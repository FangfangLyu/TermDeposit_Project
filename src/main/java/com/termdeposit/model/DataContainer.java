package com.termdeposit.model;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private HashMap<String, String> featureDatatype;

    private HashMap<String, String> featureDatatype_afterTrain;

    private HashMap<String, LinkedHashSet<String>> oneHotkeyValues;
    private boolean isTrained;

    // Note: Array is an interface, can not be instantiated, but it can store
    // ArrayList later.
    private List<HashMap<String, Object>> trainingData;

    private List<HashMap<String, Object>> trainingDataWithMissing;

    private List<HashMap<String, Object>> testingData;


    
    private KNN knn_model = null;

    private HashMap<String, Object> predictionInput;
    private List<HashMap<String, Object>> addServiceOptions;

    private float minGain;
    private boolean hasMinGain;

    public DataContainer(HashMap<String, String> featureDatatype) {
        /**
         * This method instantiate the DataContainer with featureDatatype.
         * 
         * @param featureDatatype
         * @throws IllegalArgumentException
         */
        if (featureDatatype == null) {
            throw new IllegalArgumentException("featureDatatype cannot be null");
        }

        // Initializing other fields with default values
        this.oneHotkeyValues = new HashMap<>();

        // Check if all values in featureDatatype are of type String, Integer, or Float
        for (String key : featureDatatype.keySet()) {
            String value = featureDatatype.get(key);
            if (!(value.equals("String") || value.equals("Integer") || value.equals("Double"))) {
                throw new IllegalArgumentException(
                        "Invalid type for feature: " + key + ". Allowed types are String, Integer, or Float.");
                // this is an unchecked exception
            }
            if (value.equals("String")) {
                this.oneHotkeyValues.put(key, new LinkedHashSet<>());
            }
        }
        this.featureDatatype = featureDatatype;
        this.featureDatatype_afterTrain = new HashMap<String, String>();

        this.trainingData = new ArrayList<>();
        this.trainingDataWithMissing = new ArrayList<>();

        this.testingData = new ArrayList<>();
        this.predictionInput = new HashMap<>();

        this.isTrained = false;
    }

    public List<HashMap<String, Object>> preprocessData(String trainingSetUrl, boolean isTesting) throws Exception {
        /**
         * Preprocess a dataset from a file.
         *
         * @param trainingSet Path to the dataset.
         * @param isTesting   Flag indicating if this is testing data.
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
        if (!isTesting) {
            this.trainingData = data;
        }
        return data;

    }

    public List<HashMap<String, Object>> preprocessData(String[] headers, List<String> inputStringList,
            boolean isTesting) throws Exception {
        /**
         * Preprocess a dataset from a file.
         *
         * @param trainingSet Path to the dataset.
         * @param isTesting   Flag indicating if this is testing data.
         * @return A HashMap containing preprocessed data.
         */
        System.out.println("Test label start count:" + inputStringList.size());

        List<HashMap<String, Object>> data = new ArrayList<>();

        // Parse each subsequent line as a row of data
        for (int i = 0; i < inputStringList.size(); i++) {
            String[] values = inputStringList.get(i).split(",");
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
        data = preprocessData(data, isTesting);
        if (!isTesting) {
            this.trainingData = data;
        }
        return data;

    }


    public List<HashMap<String, Object>> preprocessData(List<HashMap<String,Object>> inputs, boolean isTesting) throws Exception{
        // Output list is not needed in this case since we modify the input list in place
        List<HashMap<String, Object>> rowsToFill = new ArrayList<>();

        
        // Iterate over the list using index-based for loop
        for (int i = 0; i < inputs.size(); i++) {
            HashMap<String, Object> row = inputs.get(i);
            
            try {
                preprocessSingleData(row); // Process the data row (this method should be defined elsewhere)
            } catch (IllegalArgumentException e) {
                // Handle missing data (e.g., if some values are missing)
                if (e.getMessage().equals("Fill")) {
                    if (!isTesting) {
                        // If not testing, add row to the missing data list and remove it from the inputs
                        System.out.println("Missing value during training.");
                        this.trainingDataWithMissing.add(row);
                        inputs.remove(i); // Remove the row with missing data
                        i--; // Adjust the index because we removed an element from the list
                    } else {
                        // If testing, we need to fill the missing values
                        System.out.println("Testing missing value. Finding and filling.");
                        HashMap<String, Object> filledRow = knn_nearestSearch(row, isTesting);
                        inputs.set(i, filledRow);  // Replace the row at position i with the filled row
                    }
                }
            }
        }

        return inputs;

    }

    public HashMap<String,Object> knn_nearestSearch(HashMap<String, Object> input, boolean isTesting) throws Exception{
        List<HashMap<String,Object>> input_list = new ArrayList<HashMap<String,Object>>();

        if (this.knn_model == null) {
            throw new IllegalStateException("knn_model is not initialized");
        }
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        HashMap<String, Object> encoded = this.knn_model.oneHotkeyEncodingForSingle(input);
        if (encoded == null) {
            throw new IllegalStateException("Encoding returned null for the input");
        }
        input_list.add(encoded);
        List<HashMap<String, Object>> imputedList = this.knn_model.imputeMissingValues(input_list,isTesting);
        if (imputedList == null || imputedList.isEmpty()) {
            throw new IllegalStateException("Imputed list is null or empty");
        }
        return imputedList.get(0);

    }

    public HashMap<String, Object> preprocessSingleData(HashMap<String, Object> row) {
        // preprocess a single data point input parameters
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
                // Generate unique categorical values from the training if not yet trainined
                // (expecting training)
                if ("String".equals(this.featureDatatype.get(feature))) {
                    // Check if the feature has a String datatype, do the proper preprocess
                    // Preprocess the value (e.g., convert to lowercase and trim spaces)
                    String processedValue = value.toString().trim().toLowerCase();
                    if (!this.isTrained) { // collect unique features only if this is the training data.
                        // If the categorical feature doesn't exist in oneHotkeyValues, create an empty
                        // set
                        this.oneHotkeyValues.computeIfAbsent(feature, k -> new LinkedHashSet<String>());
                        // Add the value to the feature's set of unique values
                        this.oneHotkeyValues.get(feature).add(processedValue);
                    } else {
                        // For testing or other data, ensure the value is in the trained set
                        if (!(this.oneHotkeyValues.getOrDefault(feature, new LinkedHashSet<String>()))
                                .contains(processedValue)) { // TODO: could avoid getOrDefault, by just try get.
                            System.err.println(
                                    "Warning: Skipping row because the data contains values that's the trained model has never seen before:  '"
                                            + feature + "': " + processedValue);
                            throw new IllegalArgumentException("Skip");
                        }
                    }
                    row.put(feature, processedValue);
                } else if ("Integer".equals(this.featureDatatype.get(feature))) {
                    try {
                        Integer processedValue = Integer.parseInt(value.toString());
                        Double doubleValue = processedValue.doubleValue();
                        row.put(feature, doubleValue);
                    } catch (NumberFormatException e) {
                        hasMissingValue = true; // Mark as missing if parsing fails
                    }
                } else if ("Double".equals(this.featureDatatype.get(feature))) {
                    try {
                        Double processedValue = Double.parseDouble(value.toString());
                        row.put(feature, processedValue);
                    } catch (NumberFormatException e) {
                        hasMissingValue = true; // Mark as missing if parsing fails
                    }
                }
            } else {
                if (feature.equals("y") && !isTrained) {
                    System.err.println("Warning: Skipping row because the data contains no y value.");
                    throw new IllegalArgumentException("Skip");
                }
                hasMissingValue = true;
            }
        }
        // If the row has missing values, move it to the list of data points with
        // missing
        if (hasMissingValue) {
            throw new IllegalArgumentException("Fill");
        }
        return row;
    }

    public HashMap<String, Object> preprocessSingleAddServiceInput(HashMap<String, Object> input) {
        HashMap<String, Object> resultMap = new HashMap<>();
        // I want all addService input, in the UI make all inputs mandatory and limit
        // the data type, thus not checking here.
        // Make string lowercase,

        for (String key : input.keySet()) {
            Object value = input.get(key);

            if (value instanceof String) {
                resultMap.put(key, ((String) value).toLowerCase());
            } else {
                resultMap.put(key, value); // Just copy the value if not a String
            }
        }
        return resultMap;
    }

    public Double preprocessDesiredGain(Double input) {
        // Create a BigDecimal from the input, setting the desired scale (2 decimal
        // places)
        BigDecimal bigDecimal = new BigDecimal(input);

        // Set the rounding mode to HALF_UP and round to 2 decimal places (round up
        // here)
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);

        // Return the result as a Double
        return bigDecimal.doubleValue();
    }

    // TODO: I didn't include clearMemory Part, because seems no need, but be
    // alerted about it please.

    public boolean testMatchTestLabel(String filenameTesting, String filenameTestingWith_y) {
        try {
            String resourcePath = getClass().getClassLoader().getResource("").getPath(); // default repository
            String filePath1 = resourcePath + filenameTesting;
            String filePath2 = resourcePath + filenameTestingWith_y;

            // Read the number of rows in both files
            int sizeFile1 = countRowsInFile(filePath1);
            int sizeFile2 = countRowsInFile(filePath2);

            // Return true if both files have the same number of rows, false otherwise
            return sizeFile1 == sizeFile2;

        } catch (IOException e) {
            e.printStackTrace();
            return false; // Return false if there was an issue reading the files
        }
    }

    private int countRowsInFile(String filePath) throws IOException {
        int rowCount = 0;

        // Open the file and count the number of lines (entries)
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                rowCount++;
            }
        }
        return rowCount;
    }

    public void addTrainingData(List<HashMap<String, Object>> data) {
        this.trainingData.addAll(data);
    }

    public List<HashMap<String, Object>> getTrainingData() {
        return this.trainingData;
    }

    public HashMap<String, String> getFeatureList() {
        return this.featureDatatype;
    }

    public List<HashMap<String, Object>> gettrainingDataWithMissing() {
        return this.trainingDataWithMissing;
    }

    public HashMap<String, String> getFeatureAfterTrain() {
        return this.featureDatatype_afterTrain;
    }

    public void setKnn(KNN knn) {
        // Have to set up knn after data is trained.
        this.knn_model = knn;
    }

    public HashMap<String, Object> getPredictionInput() { // TODO: NEW
        return this.predictionInput;
    }

    public List<HashMap<String, Object>> getTestingData() { // TODO: NEW
        return this.testingData;
    }

    // nested inner class (non-static nested class)
    public class KNN {
        private NearestNeighbourSearch m_NNSearch; // for KNN
        private Instances trainingInstances;
        private Instances trainingMissingInstances;
        private HashMap<String, String> featureList;

        private ArrayList<Attribute> attributes;

        private Set<String> usedIds;

        private String modelFilename;
        private int id_counter;

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
                            if (feature.equals("ID") || feature.equals("id") || feature.equals("y")) {
                                transformedRow.put(feature, value);
                                DataContainer.this.featureDatatype_afterTrain.put(feature, "String");
                            } else {
                                transformedRow.put(newFeatureName, uniqueValue.equals(value) ? 1.0 : 0.0);
                                DataContainer.this.featureDatatype_afterTrain.put(newFeatureName, "String");
                            }

                        }
                    } else {
                        if (feature.equals("ID") || feature.equals("id")) {
                            transformedRow.put(feature, value);
                            // this will not occur (should be)
                        } else {
                            transformedRow.put(feature, value);
                            DataContainer.this.featureDatatype_afterTrain.put(feature,
                                    DataContainer.this.featureDatatype.get(feature)); // store the truth feature type
                            // TODO: downside of the approach is that the featureDatatype_afterTrain is
                            // getting refreshed each time. Should have a separate loop of handling this

                        }
                    }
                } else {
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
         * public List<HashMap<String, Object>> oneHotkeyEncoding(List<HashMap<String,
         * Object>> data){
         * List<HashMap<String,Object>> updatedTrainingData = new ArrayList<>();
         * 
         * for (HashMap<String, Object> row : data) {
         * HashMap<String, Object> transformedRow = new HashMap<>();
         * 
         * for (Map.Entry<String, Object> entry : row.entrySet()) {
         * String feature = entry.getKey();
         * Object value = entry.getValue();
         * 
         * if ("String".equals(DataContainer.this.featureDatatype.get(feature))) {
         * // Perform one-hot encoding
         * LinkedHashSet<String> uniqueValues =
         * DataContainer.this.oneHotkeyValues.get(feature);
         * for (String uniqueValue : uniqueValues) {
         * String newFeatureName = feature + "_" + uniqueValue;
         * if(value != null){
         * transformedRow.put(newFeatureName, uniqueValue.equals(value) ? 1.0 : 0.0);
         * }else{
         * transformedRow.put(newFeatureName, null);
         * }
         * }
         * } else{
         * if(feature.equals("ID") || feature.equals("id")){
         * transformedRow.put(feature, value);
         * }else{
         * transformedRow.put(feature, ((Number)value).doubleValue());
         * }
         * }
         * }
         * updatedTrainingData.add(transformedRow);
         * }
         * 
         * // Modify the original data list
         * data.clear(); // Clear the original data list
         * data.addAll(updatedTrainingData); // Add all transformed rows to the original
         * list
         * return updatedTrainingData;
         * }
         */

        public Instance toInstance(HashMap<String, Object> row, Instances destInstances) {
            // Add complete data instances to Weka Instances object
            Instance instance = new DenseInstance(this.attributes.size());
            /**
             * Instance object in Weka represents a single data point.
             * DenseInstance (vs SpareInstance) is subclass of Instance where all attributes
             * are defined, no missing
             */
            instance.setDataset(destInstances); // set up empty instance with correct number of attributes without ID
                                                // and y.

            for (int j = 0; j < this.attributes.size(); j++) {
                String feature = this.attributes.get(j).name(); // get the attribute name
                Object value = row.get(feature); // get the value under the attribute(feature)

                if (value != null) {
                    // If the value is not null, process it
                    if (!feature.equals("y")) {
                        // Check if the value is already a Number
                        if (value instanceof Number) {
                            instance.setValue(j, ((Number) value).doubleValue());
                        }
                        // If it's a String that can be parsed into a Number, attempt to parse it
                        else if (value instanceof String) {
                            try {
                                // Try parsing the string into a double (you can use other numeric types if needed)
                                double parsedValue = Double.parseDouble((String) value);
                                instance.setValue(j, parsedValue);
                            } catch (NumberFormatException e) {
                                // Handle parsing failure (you can log this or handle as a missing value)
                                System.err.println("Error parsing value for feature " + feature + ": " + value);
                                instance.setMissing(j); // Set missing if parsing fails
                            }
                        } else {
                            // If it's not a Number or String, you may want to handle it differently
                            System.err.println("Unexpected data type for feature " + feature + ": " + value.getClass().getName());
                            instance.setMissing(j); // Set missing if the type is unexpected
                        }
                    } else {
                        // Handle the "y" feature (class) if necessary
                        instance.setValue(j, 0.0); // Setting a default value for the class feature
                    }
                } else {
                    // Handle missing data for the feature
                    System.err.println("Missing data for feature " + feature);
                    instance.setMissing(j); // Set missing if the value is null
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
            // id and y plays no role in KNN, and it should not interfere the KNN, thus
            // remove them from the attribute list.

            // Iterate through features to construct Weka attributes(after the one hot key)
            for (String feature : this.featureList.keySet()) {
                if ("String".equals(this.featureList.get(feature)) && !feature.equals("y")) {
                    // handle categorical
                    for (String uniqueValue : DataContainer.this.oneHotkeyValues.get(feature)) {
                        this.attributes.add(new Attribute(feature + "_" + uniqueValue)); // because after one hot
                                                                                         // encoding, the categorical
                                                                                         // attributes get expanded.
                    }
                } else {
                    // handle numeric features
                    this.attributes.add(new Attribute(feature));
                }
            }

            // this.attributes.add(new Attribute("class")); // Add dummy class attribute

            // Create the Weka Instances object for KNN imputation, where features
            this.trainingInstances = new Instances("TrainingData", this.attributes,
                    DataContainer.this.trainingData.size());

            // Add complete data instances to Weka Instances object
            for (HashMap<String, Object> row : DataContainer.this.trainingData) {
                toInstance(row, this.trainingInstances);
            }

            this.m_NNSearch = new LinearNNSearch(this.trainingInstances);
            System.out.println("KNN model has been trained.");
            // saveModel();
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
            System.out.println("Neighbor index at " + maxIndex);
            return maxIndex;
        }

        private String findUnusedId() {

            // Find an ID that is not in the used IDs set
            String newId = "id_" + this.id_counter++;
            System.out.println("New id generated: " + newId);
            return newId;
        }

        // Perform KNN imputation for missing values in the dataset
        public List<HashMap<String, Object>> imputeMissingValues(List<HashMap<String, Object>> missingData, Boolean isTesting)
                throws Exception {

            List<HashMap<String, Object>> encodedData = this.oneHotkeyEncoding(missingData);

            // Create the Weka Instances object for KNN imputation, where features
            this.trainingMissingInstances = new Instances("TrainingDataMissing", this.attributes, encodedData.size());

            // Add complete data instances to Weka Instances object
            for (HashMap<String, Object> row : encodedData) {
                toInstance(row, this.trainingMissingInstances);
                // System.out.println(this.trainingMissingInstances.toString());
            }

            for (int i = 0; i < this.trainingMissingInstances.numInstances(); i++) {
                Instance instance = this.trainingMissingInstances.instance(i);

                // Check if the instance has missing values
                if (instance.hasMissingValue()) {
                    Instances neighbours = this.m_NNSearch.kNearestNeighbours(instance, 1); // get nearest the neighbors
                    double[] distances = this.m_NNSearch.getDistances();
                    System.out.println(neighbours.size());

                    /*
                     * int counter = 0; // Initialize the counter
                     * for(Instance neighbor : neighbours){
                     * System.out.println("Nearest neighbor: ");
                     * System.out.println(neighbor.toString() + " & " + distances[counter]);
                     * counter++;
                     * }
                     */
                    int maxIndex = getIndex(distances); // Find the class with the highest probability => this step is
                                                        // not necessary if we get one nearest neighbor, but if all
                                                        // neighbors have the same distance, there are many
                    System.out.println("Closet Neighbor" + this.trainingInstances.get(maxIndex));

                    System.out.println("**has Missing.");
                    // Iterate over all attributes
                    /*
                     * for (int j = 0; j < instance.numAttributes(); j++) {
                     * if(instance.isMissing(j)) { //TODO: IF ID IS MISSING, I WOULD PICK another
                     * not used ID.
                     * String featureName = instance.attribute(j).name();
                     * // Handle missing "ID" separately
                     * if ("ID".equals(featureName)) {
                     * // Find another unused ID from the dataset
                     * String newId = findUnusedId();
                     * instance.setValue(j, newId); // Set the missing ID to a new unique value
                     * }else{
                     * double predicted = neighbours.get(maxIndex).value( instance.attribute(j) );
                     * System.out.println("The missing feature " + featureName +
                     * " is imputed to be "+ predicted);
                     * instance.setValue(j, predicted); // Set the missing value to the predicted
                     * class
                     * }
                     * 
                     * }
                     * }
                     */
                    for (int j = 0; j < instance.numAttributes(); j++) {
                        String featureName = instance.attribute(j).name();

                        if ("ID".equals(featureName)) {
                            // Handle ID column
                            if (instance.isMissing(j)) {
                                String newId = findUnusedId(); // Custom method to find an unused ID
                                instance.setValue(j, newId);
                                System.out.println("Missing ID replaced with: " + newId);
                            }
                        } else if ("y".equals(featureName)) {
                            // Keep the y column as-is (no changes needed here)

                        } else {
                            // Replace other attributes with their predicted or existing feature values
                            if (instance.isMissing(j)) {
                                double predicted = neighbours.get(maxIndex).value(instance.attribute(j));
                                System.out.println(
                                        "The missing feature " + featureName + " is imputed to be " + predicted);
                                instance.setValue(j, predicted); // Set missing feature to predicted value
                            } else {
                                double originalValue = instance.value(instance.attribute(j));
                                System.out
                                        .println("Retaining original value for " + featureName + ": " + originalValue);
                                instance.setValue(j, originalValue); // Retain the original feature value
                            }
                        }
                    }
                    //System.out.println(instance);

                }

            }

            //System.out.println("Imputation completed.");

            // Reintroduce "id" and "y" back into the data
            List<HashMap<String, Object>> results = new LinkedList<>();
            for (int i = 0; i < this.trainingMissingInstances.numInstances(); i++) {
                Instance instance = this.trainingMissingInstances.instance(i);
                // Get the original data for this instance
                HashMap<String, Object> originalRow = missingData.get(i);
                HashMap<String, Object> newRow = new HashMap<>();

                // Iterate through all attributes to check for missing values and impute them
                //System.out.println("Before update: " + originalRow);

                // Get the ID and y from the originalRow
                if (originalRow == null) {
                    throw new IllegalStateException("Original row is null.");
                }
                Object ID = originalRow.get("ID");
                Object y = originalRow.get("y");

                if(!isTesting && y == null){
                    throw new IllegalStateException("'y' is missing in the original row.");
                }


                //System.out.println("Before update: " + originalRow);

                if (ID == null) {
                    // Handle ID column

                    String newId = findUnusedId(); // Custom method to find an unused ID
                    System.out.println("Before update: " + originalRow);

                    newRow.put("ID", newId);

                    System.out.println("Missing ID replaced with: " + newId);
                } else {
                    newRow.put("ID", ID);
                }

                //System.out.println("Before update: " + originalRow);

                /* */
                for (String uniqueValue : DataContainer.this.oneHotkeyValues.get("y")) {
                    newRow.put("y", y); // because after one hot encoding, the categorical attributes get expanded.
                }

                for (int j = 0; j < instance.numAttributes(); j++) {
                    String feature = instance.attribute(j).name(); // Get the feature name
                    Object imputedValue = instance.value(j); // Get the imputed value

                    newRow.put(feature, imputedValue); // Substitute the missing value back
                }

                //System.out.println("After update: " + newRow);

                results.add(newRow);
            }
            return results;
        }
    }
}