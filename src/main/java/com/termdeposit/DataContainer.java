package com.termdeposit;


import java.util.*;
import java.io.*;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk; //this IBk package from weka contains KNN method
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
// import weka.knowledgeflow.steps.DataCollector;


public class DataContainer {
    private HashMap<String,String> featureDatatype;  // TODO: For assignement 2, reflect the variable name changed from inputDatatype to featureDatatype

    private HashMap<String,LinkedHashSet<String>> oneHotkeyValues;  //TODO: NEW
    private boolean isTrained; // TODO: NEW


    // Note: Array is an interface, can not be instantiated, but it can store ArrayList later.
    private List<HashMap<String,Object>> trainingData;
    private List<HashMap<String,Object>> trainingDataWithMissing; //TODO: NEW
    private List<HashMap<String,Object>> testingData;
    
    private HashMap<String,Object> predictionInput; 
    private List<HashMap<String,Object>> addServiceOptions; 

    private float minGain;
    private boolean hasMinGain;

    public DataContainer(HashMap<String, String> featureDatatype){
       /**
         * This method instantiate the DataContainer with featureDatatype.
         * 
         * @param featureDatatype
         * @throws IllegalArgumentException
        */
        if (featureDatatype == null) {
            throw new IllegalArgumentException("featureDatatype cannot be null");
        }

        // Check if all values in featureDatatype are of type String, Integer, or Float
        for (String key : featureDatatype.keySet()) {
            String value = featureDatatype.get(key);
            if (!(value.equals("String") || value.equals("Integer")  || value.equals("Float")) ) {
                throw new IllegalArgumentException("Invalid type for feature: " + key + ". Allowed types are String, Integer, or Float.");
                // this is an unchecked exception
            }
        }
        this.featureDatatype = featureDatatype;

        // Initializing other fields with default values
        this.oneHotkeyValues = new HashMap<>();
        this.trainingData = new ArrayList<>();
        this.trainingDataWithMissing = new ArrayList<>(); 

        this.testingData = new ArrayList<>();
        this.predictionInput = new HashMap<>();
        this.addServiceOptions = new ArrayList<>();
        this.minGain = 0.0f;
        this.hasMinGain = false;
        this.isTrained = false; 
    }

    public void preprocessData(String trainingSetUrl, boolean isTesting){
        /**
         * Preprocess a dataset from a file.
         *
         * @param trainingSet Path to the dataset.
         * @param isTesting Flag indicating if this is testing data.
         * @return A HashMap containing preprocessed data.
         */
        List<HashMap<String, Object>> data = new ArrayList<>();

        // Read all lines from the file
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(trainingSetUrl);

        List<String> lines = new ArrayList<>();

        if (inputStream == null) {
            System.err.println("File not found!");
        } else {
            // List to hold lines read from the CSV

            // Wrap the InputStream in an InputStreamReader and BufferedReader
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                // Read each line and add it to the list
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }

        // Extract headers from the first line
        String[] headers = lines.get(0).split(",");

        // Parse each subsequent line as a row of data
        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");
            HashMap<String, Object> row = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                if (j < values.length) {
                    if (values[j].isEmpty()) {
                        row.put(headers[j], null); // Assign null if the value is empty
                    } else {
                        row.put(headers[j], values[j]); // Assign the actual value if not empty
                    }
                } else {
                    row.put(headers[j], null); // If no value for the header, assign null
                }
            }
            data.add(row);
        }
        // Above step parse the input file
        preprocessData(data, isTesting);
        this.trainingData = data;


    }


    public void preprocessData(List<HashMap<String,Object>> inputs, boolean isTesting){
        //if this object has never trainined yet, this should be preprocessing training data
        Iterator<HashMap<String, Object>> iterator = inputs.iterator();

        while (iterator.hasNext()) {
            HashMap<String, Object> row = iterator.next();
            try{
                preprocessSingleData(row, iterator);
            }catch (IllegalArgumentException e) {
                // TODO: Possible enhancement here is to create custom Exceptions. 
                // Handle the error gracefully
                if (e.getMessage().equals("Fill")) {
                    System.out.println("once");
                    this.trainingDataWithMissing.add(row);
                }
               iterator.remove();
            }

        }
    }

    public HashMap<String, Object> preprocessSingleData( HashMap<String,Object> row, Iterator<HashMap<String, Object>> iterator){
        //preprocess a single data point input parameters
        /**
         * Preprocess a single data point.
         *
         * @param input A row represented as a HashMap.
         * @return The preprocessed row.
         */
        boolean hasMissingValue = false;
        for (String feature : this.featureDatatype.keySet()) {
            Object value = row.get(feature);
            if (value != null) {
                //Generate unique categorical values from the training if not yet trainined (expecting training)
                if ("String".equals(this.featureDatatype.get(feature))) {
                    // Check if the feature has a String datatype, do the proper preprocess
                        // Preprocess the value (e.g., convert to lowercase and trim spaces)
                        String processedValue = value.toString().trim().toLowerCase();
                        if(!this.isTrained){ //collect unique features only if this is the training data.
                            // If the categorical feature doesn't exist in oneHotkeyValues, create an empty set
                            this.oneHotkeyValues.computeIfAbsent(feature, k -> new LinkedHashSet<String>());
                            // Add the value to the feature's set of unique values
                            this.oneHotkeyValues.get(feature).add(processedValue);
                        }else{
                            // For testing or other data, ensure the value is in the trained set
                            if (!(this.oneHotkeyValues.getOrDefault(feature, new LinkedHashSet<String>())).contains(processedValue)) { //TODO: could avoid getOrDefault, by just try get.
                                System.err.println("Warning: Skipping row because the data contains values that's the trained model has never seen before:  '" + feature + "': " + processedValue);
                                throw new IllegalArgumentException("Skip");
                            }
                        }
                        row.put(feature, processedValue);
                }else if("Integer".equals(this.featureDatatype.get(feature))) {
                    try {
                        Integer processedValue = Integer.parseInt(value.toString());
                        row.put(feature, processedValue);
                    } catch (NumberFormatException e) {
                        hasMissingValue = true; // Mark as missing if parsing fails
                    }
                }else if("Float".equals(this.featureDatatype.get(feature))){
                    try {
                        Float processedValue = Float.parseFloat(value.toString());
                        row.put(feature, processedValue);
                    } catch (NumberFormatException e) {
                        hasMissingValue = true; // Mark as missing if parsing fails
                    }        
                } 
            }else{
                if(feature.equals("y") && !isTrained){
                    System.err.println("Warning: Skipping row because the data contains no y value.");
                    throw new IllegalArgumentException("Skip");
                }
                hasMissingValue = true;
            }
        }
        // If the row has missing values, move it to the list of data points with missing
        if (hasMissingValue) {
            throw new IllegalArgumentException("Fill");
        }
        return row;
    }

    public List<HashMap<String, Object>> getTrainingData(){
        return this.trainingData;
    }
    public HashMap<String, String> getFeatureList(){
        return this.featureDatatype;
    }
    public List<HashMap<String, Object>> gettrainingDataWithMissing(){
        return this.trainingDataWithMissing;
    }

    //nested inner class (non-static nested class)        
    public class KNN {
        private IBk knn;
        private Instances trainingInstances;
        private int numberNeighbors;
        private String modelFilename;


        public KNN() {
            this.numberNeighbors = 3; //defualt k set to 3
            this.knn = new IBk(numberNeighbors); 
            //TODO: Do I need to instantiate the Instances here?
        }

        public void oneHotkeyEncoding(List<HashMap<String, Object>> data){
            List<HashMap<String,Object>> updatedTrainingData = new ArrayList<>();

            for (HashMap<String, Object> row : data) {
                HashMap<String, Object> transformedRow = new HashMap<>();

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String feature = entry.getKey();
                    Object value = entry.getValue();
        
                    if ("String".equals(DataContainer.this.featureDatatype.get(feature))) {
                        // Perform one-hot encoding
                        LinkedHashSet<String> uniqueValues = DataContainer.this.oneHotkeyValues.get(feature); 
                        for (String uniqueValue : uniqueValues) {
                            String newFeatureName = feature + "_" + uniqueValue;
                            transformedRow.put(newFeatureName, uniqueValue.equals(value) ? 1.0 : 0.0);
                        }
                    } else {
                        // Retain non-categorical features
                        transformedRow.put(feature, value);
                    }
                }
                updatedTrainingData.add(transformedRow);
            }
            
            // Modify the original data list
            data.clear();  // Clear the original data list
            data.addAll(updatedTrainingData);  // Add all transformed rows to the original list
        }

        public void train() throws Exception {
            oneHotkeyEncoding(DataContainer.this.trainingData);

            // Build Weka attributes
            ArrayList<Attribute> attributes = new ArrayList<>();
            
            HashMap<String, String> featureList = DataContainer.this.featureDatatype;
            featureList.remove("id");
            featureList.remove("y");
            // id and y plays no role in KNN, and it should not interfere the KNN, thus remove them from the attribute list.

            // Iterate through features to construct Weka attributes(after the one hot key)
            for (String feature : featureList.keySet()) {
                if ("String".equals(featureList.get(feature))) {
                    //handle categorical
                    for (String uniqueValue : DataContainer.this.oneHotkeyValues.get(feature)) {
                        attributes.add(new Attribute(feature + "_" + uniqueValue)); //because after one hot encoding, the categorical attributes get expanded.
                    }
                } else if ("Integer".equals(featureList.get(feature)) || "Float".equals(featureList.get(feature))) {
                    //handle numeric features
                    attributes.add(new Attribute(feature));
                }
            }

            attributes.add(new Attribute("class")); // Add dummy class attribute


            
            // Create the Weka Instances object for KNN imputation, where features 
            this.trainingInstances = new Instances("TrainingData", attributes, DataContainer.this.trainingData.size());
            
            // Add complete data instances to Weka Instances object
            for (HashMap<String, Object> row : DataContainer.this.trainingData) {
                Instance instance = new DenseInstance(attributes.size()); 
                /**
                 * Instance object in Weka represents a single data point. 
                 * DenseInstance (vs SpareInstance) is subclass of Instance where all attributes are defined, no missing
                 */
                instance.setDataset(this.trainingInstances);

                for (int j = 0; j < attributes.size(); j++) {
                    String feature = attributes.get(j).name(); //get the attribute name
                    Object value = row.get(feature); //get the value under the attribute(feature)
                    
                    if (!feature.equals("class") && value != null) {
                        instance.setValue(j, ((Number) value).doubleValue()); 
                        //get the value under each attributes (Now, all attributes are numeric after encoding)
                        //.doubleValue (case Number type to Double type for Weka)
                    } else {
                        if(feature.equals("class")){
                            instance.setValue(j, 0.0);  // Set the dummy class value for all instances
                        }else{
                            System.err.println("Missing data from preprocess training data set." + feature + "\n");
                            // Handle missing data if any, this should not error in our program design
                            instance.setMissing(j);
                        }
                    }
                }

                // Add dummy class value (constant 'dummy' class)
                // Storing the instance object into Instances object 
                this.trainingInstances.add(instance);
            }


            // Set the class index to the dummy class
            this.trainingInstances.setClassIndex(attributes.size() - 1);

            // Train the model knn (IBk object in Weka) using the Instances object
            knn.buildClassifier(this.trainingInstances);
            System.out.println("KNN model has been trained.");
            //saveModel();

            
        }

        public void saveModel(String modelFilename) throws Exception {
            // Save the trained KNN model to the specified file
            SerializationHelper.write(modelFilename, knn);
            this.modelFilename = modelFilename;
            System.out.println("Model saved to: " + modelFilename);
        }


        public void findNeighbors(Instance instanceToClassify) throws Exception {

            Classifier classifier = (Classifier) SerializationHelper.read(modelFilename);


            // Get the neighbors for the given instance
            double[] distances = knn.getDistances(instanceToClassify);  // This returns the distances to the neighbors

            System.out.println("Neighbors for instance " + instanceToClassify);
            for (int i = 0; i < distances.length; i++) {
                // For each neighbor, you can also output the index and distance
                System.out.println("Neighbor " + i + ": " + "Distance = " + distances[i]);
            }
        }


    }

    
}
