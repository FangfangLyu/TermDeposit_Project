package com.termdeposit.model;


import java.util.*;
import java.io.*;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk; //this IBk package from weka contains KNN method
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
// import weka.knowledgeflow.steps.DataCollector;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;


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
            if (!(value.equals("String") || value.equals("Integer")  || value.equals("Double")) ) {
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
                    if (!line.trim().isEmpty()) {
                        lines.add(line);
                    }
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
                preprocessSingleData(row);
            }catch (IllegalArgumentException e) {
                // TODO: Possible enhancement here is to create custom Exceptions. 
                // Handle the error gracefully
                if (e.getMessage().equals("Fill")) {
                    System.out.println("missing once");
                    this.trainingDataWithMissing.add(row);
                }
               iterator.remove();
            }

        }
    }

    public HashMap<String, Object> preprocessSingleData( HashMap<String,Object> row){
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
                }else if("Double".equals(this.featureDatatype.get(feature))){
                    try {
                        Double processedValue = Double.parseDouble(value.toString());
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
    public void addTrainingData(List<HashMap<String,Object>> data){
        this.trainingData.addAll(data);
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
        private NearestNeighbourSearch m_NNSearch; //for KNN
        private Instances trainingInstances;
        private Instances trainingMissingInstances;
        private HashMap<String, String> featureList;

        private ArrayList<Attribute> attributes;

        private Set<String> usedIds;

        private String modelFilename;

        public KNN() {

        }

        public HashMap<String, Object> oneHotkeyEncodingForSingle(HashMap<String, Object> row) {
            HashMap<String, Object> transformedRow = new HashMap<>();
        
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String feature = entry.getKey();
                Object value = entry.getValue();


                if (value != null) {
                    if ("String".equals(DataContainer.this.featureDatatype.get(feature))) {
                        // Perform one-hot encoding for string features
                        LinkedHashSet<String> uniqueValues = DataContainer.this.oneHotkeyValues.get(feature);
                        for (String uniqueValue : uniqueValues) {
                            String newFeatureName = feature + "_" + uniqueValue;
                            if (feature.equals("ID") || feature.equals("id")) {
                                transformedRow.put(feature, value);
                            }else{
                                transformedRow.put(newFeatureName, uniqueValue.equals(value) ? 1.0 : 0.0);
                            }

                        }
                    }else {
                        if (feature.equals("ID") || feature.equals("id")) {
                            transformedRow.put(feature, value);
                        }else{
                            transformedRow.put(feature, value);
                        } 
                    }
                }else{
                    transformedRow.put(feature, null);
                }
            }
        
            return transformedRow;
        }

        public List<HashMap<String, Object>> oneHotkeyEncoding(List<HashMap<String, Object>> data) {
            List<HashMap<String, Object>> updatedTrainingData = new ArrayList<>();
        
            for (HashMap<String, Object> row : data) {
                // Use the single row method to process each HashMap

                HashMap<String, Object> transformedRow = oneHotkeyEncodingForSingle(row);
                updatedTrainingData.add(transformedRow);
            }
        
            return updatedTrainingData;
        }
        
        
        /* 
        public List<HashMap<String, Object>> oneHotkeyEncoding(List<HashMap<String, Object>> data){
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
                            if(value != null){
                                transformedRow.put(newFeatureName, uniqueValue.equals(value) ? 1.0 : 0.0);
                            }else{
                                transformedRow.put(newFeatureName, null);
                            }
                        }
                    } else{
                        if(feature.equals("ID") || feature.equals("id")){
                            transformedRow.put(feature, value);
                        }else{
                            transformedRow.put(feature, ((Number)value).doubleValue());
                        }
                    }
                }
                updatedTrainingData.add(transformedRow);
            }
            
            // Modify the original data list
            data.clear();  // Clear the original data list
            data.addAll(updatedTrainingData);  // Add all transformed rows to the original list
            return updatedTrainingData;
        }*/

        public Instance toInstance(HashMap<String, Object> row, Instances destInstances){
            // Add complete data instances to Weka Instances object
            Instance instance = new DenseInstance(this.attributes.size()); 
            /**
             * Instance object in Weka represents a single data point. 
             * DenseInstance (vs SpareInstance) is subclass of Instance where all attributes are defined, no missing
             */
            instance.setDataset(destInstances); //set up empty instance with correct number of attributes without ID and y.

            for (int j = 0; j < this.attributes.size(); j++) {
                String feature = this.attributes.get(j).name(); //get the attribute name
                Object value = row.get(feature); //get the value under the attribute(feature)
                
                if (!feature.equals("class") && value != null) {
                    instance.setValue(j, ((Number) value).doubleValue()); 
                    //get the value under each attributes (Now, all attributes are numeric after encoding)
                    //.doubleValue (case Number type to Double type for Weka)
                } else {
                    if(feature.equals("class")){
                        instance.setValue(j, 0.0);  // Set the dummy class value for all instances, no meaning for itself, but all instances share it.
                    }else{
                        System.err.println("Missing data from preprocess training data set." + feature + "\n");
                        // Handle missing data if any, this should not error in our program design
                        instance.setMissing(j);
                    }
                }
            }

            // Add dummy class value (constant 'dummy' class)
            // Storing the instance object into Instances object 
            destInstances.add(instance);            
            return instance;
            

        }

        public void train() throws Exception {

            DataContainer.this.trainingData = oneHotkeyEncoding(DataContainer.this.trainingData);
            // Build Weka attributes
            this.attributes = new ArrayList<>();
            
            this.featureList = DataContainer.this.featureDatatype;
            this.featureList.remove("ID");
            this.featureList.remove("y");
            // id and y plays no role in KNN, and it should not interfere the KNN, thus remove them from the attribute list.
            
            // Iterate through features to construct Weka attributes(after the one hot key)
            for (String feature : this.featureList.keySet()) {
                if ("String".equals(this.featureList.get(feature))) {
                    //handle categorical
                    for (String uniqueValue : DataContainer.this.oneHotkeyValues.get(feature)) {
                        this.attributes.add(new Attribute(feature + "_" + uniqueValue)); //because after one hot encoding, the categorical attributes get expanded.
                    }
                } else if ("Integer".equals(this.featureList.get(feature)) || "Double".equals(this.featureList.get(feature))) {
                    //handle numeric features
                    this.attributes.add(new Attribute(feature));
                }
            }

            //this.attributes.add(new Attribute("class")); // Add dummy class attribute
            
            // Create the Weka Instances object for KNN imputation, where features 
            this.trainingInstances = new Instances("TrainingData", this.attributes, DataContainer.this.trainingData.size());
            
            // Add complete data instances to Weka Instances object
            for (HashMap<String, Object> row : DataContainer.this.trainingData) {
                toInstance(row, this.trainingInstances);
            }

            
            this.m_NNSearch = new LinearNNSearch(this.trainingInstances);
            System.out.println("KNN model has been trained." );
            //saveModel();
        }

        public void saveModel(String modelFilename) throws Exception {
            // Save the trained KNN model to the specified file
            SerializationHelper.write(modelFilename, this.m_NNSearch);
            this.modelFilename = modelFilename;
            System.out.println("Model saved to: " + modelFilename);
        }

        // Helper method to find the index of the maximum value in an array
        private int getIndex(double[] array) {
            int maxIndex = 0;
            for (int i = 1; i < array.length; i++) {
                if (array[i] < array[maxIndex]) {
                    maxIndex = i;
                }
            }
            System.out.println("Neighbor index at "+ maxIndex);
            return maxIndex;
        }

        private String findUnusedId() {
            this.usedIds = new HashSet<>();
        
            // Collect all the used IDs from your training data
            for (int i = 0; i < trainingInstances.numInstances(); i++) {
                this.usedIds.add(trainingInstances.instance(i).stringValue(trainingInstances.attribute("ID")));
            }
        
            // Find an ID that is not in the used IDs set
            String newId = "newID"; // Replace with the logic to generate a new unused ID
            while (usedIds.contains(newId)) {
                newId = generateNewId();  // You can implement this method to generate unique IDs
            }
            return newId;
        }

        private String generateNewId() {
            // Example: Create a new ID by appending a number or UUID
            return "id_" + System.currentTimeMillis();
        }


        // Perform KNN imputation for missing values in the dataset
        public List<HashMap<String,Object>> imputeMissingTrainingValues(List<HashMap<String,Object>> missingData) throws Exception {

            List<HashMap<String,Object>> encodedData= this.oneHotkeyEncoding(missingData);

            // Create the Weka Instances object for KNN imputation, where features 
            this.trainingMissingInstances = new Instances("TrainingDataMissing", this.attributes, encodedData.size());
            
            // Add complete data instances to Weka Instances object
            for (HashMap<String, Object> row : encodedData) {
                toInstance(row, this.trainingMissingInstances);
                //System.out.println(this.trainingMissingInstances.toString());
            }

            for (int i = 0; i < this.trainingMissingInstances.numInstances(); i++) {
                Instance instance = this.trainingMissingInstances.instance(i);

                // Check if the instance has missing values
                if (instance.hasMissingValue()) {
                    Instances neighbours = this.m_NNSearch.kNearestNeighbours(instance, 1); //get nearest the neighbors
                    double [] distances = this.m_NNSearch.getDistances();
                    System.out.println(neighbours.size());

                    /* 
                    int counter = 0; // Initialize the counter
                    for(Instance neighbor : neighbours){
                        System.out.println("Nearest neighbor: ");
                        System.out.println(neighbor.toString() + " & " + distances[counter]);
                        counter++;
                    }*/
                    int maxIndex = getIndex(distances);  // Find the class with the highest probability => this step is not necessary if we get one nearest neighbor, but if all neighbors have the same distance, there are many
                    System.out.println("Closet Neighbor" + this.trainingInstances.get(maxIndex));

                    System.out.println("**has Missing.");
                    // Iterate over all attributes
                    /*for (int j = 0; j < instance.numAttributes(); j++) {
                        if(instance.isMissing(j)) { //TODO: IF ID IS MISSING, I WOULD PICK another not used ID.
                            String featureName = instance.attribute(j).name();
                            // Handle missing "ID" separately
                            if ("ID".equals(featureName)) {
                                // Find another unused ID from the dataset
                                String newId = findUnusedId();
                                instance.setValue(j, newId);  // Set the missing ID to a new unique value
                            }else{
                                double predicted = neighbours.get(maxIndex).value( instance.attribute(j) );
                                System.out.println("The missing feature " + featureName + " is imputed to be "+ predicted);
                                instance.setValue(j, predicted);  // Set the missing value to the predicted class
                            }

                        }
                    }*/
                    for (int j = 0; j < instance.numAttributes(); j++) {
                        String featureName = instance.attribute(j).name();
                    
                        if ("ID".equals(featureName)) {
                            // Handle ID column
                            if (instance.isMissing(j)) {
                                String newId = findUnusedId();  // Custom method to find an unused ID
                                instance.setValue(j, newId);
                                System.out.println("Missing ID replaced with: " + newId);
                            }
                        } else if ("y".equals(featureName)) {
                            // Keep the y column as-is (no changes needed here)

                        } else {
                            // Replace other attributes with their predicted or existing feature values
                            if (instance.isMissing(j)) {
                                double predicted = neighbours.get(maxIndex).value(instance.attribute(j));
                                System.out.println("The missing feature " + featureName + " is imputed to be " + predicted);
                                instance.setValue(j, predicted);  // Set missing feature to predicted value
                            } else {
                                double originalValue = instance.value(instance.attribute(j));
                                System.out.println("Retaining original value for " + featureName + ": " + originalValue);
                                instance.setValue(j, originalValue);  // Retain the original feature value
                            }
                        }
                    }
                    
                }
            }

            System.out.println("Imputation completed.");

            //Reintroduce "id" and "y" back into the data
            List<HashMap<String,Object>> results = new LinkedList<>();
            for (int i = 0; i < this.trainingMissingInstances.numInstances(); i++) {
                Instance instance = this.trainingMissingInstances.instance(i);
                // Get the original data for this instance
                HashMap<String, Object> originalRow = missingData.get(i);
                HashMap<String, Object> newRow = new HashMap<>();
                // Get the ID and y from the originalRow
                Object ID = originalRow.get("ID");
                Object y = originalRow.get("y");

                // Iterate through all attributes to check for missing values and impute them
                System.out.println("Before update: " + originalRow);


                for (int j = 0; j < instance.numAttributes(); j++) {
                    String feature = instance.attribute(j).name(); // Get the feature name
                    Object imputedValue = instance.value(j); // Get the imputed value
                    String expectedType = DataContainer.this.featureDatatype.get(feature);

                    if ("Integer".equals(expectedType)) {
                        newRow.put(feature, (int) Math.round((Double)imputedValue));  // Substitute the missing value back
                    }else {
                        newRow.put(feature, imputedValue);  // Substitute the missing value back

                    }

                }
                newRow.put("ID", ID);
                for (String uniqueValue : DataContainer.this.oneHotkeyValues.get("y")) {
                    newRow.put("y_" + uniqueValue, y.equals(uniqueValue)? 1.0: 0.0); //because after one hot encoding, the categorical attributes get expanded.
                }

                System.out.println("After update: " + newRow);

                results.add(newRow);
            }
            return results;
        }
    }
}
