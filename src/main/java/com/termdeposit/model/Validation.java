package com.termdeposit.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Validation {

    private int true_positive;
    private int false_positive;
    private int true_negative;
    private int false_negative;
    private DataContainer data;
    private DataContainer.KNN knn_model;
    private RandomForest forest;

    public Validation(DataContainer data, DataContainer.KNN knn_model, RandomForest forest){
        this.forest = forest;
        this.knn_model = knn_model;
        this.data = data;
        this.true_positive = 0;
        this.false_positive = 0;
        this.true_negative = 0;
        this.false_negative = 0;
    }

    public void getInSampleAccuracy(){
        //assume the data is property preprocessed
        List<HashMap<String,Object>> insample_inputs = this.data.getTrainingData();
        int size = 1000;
        int count = 0;
         // Iterate through each HashMap in the List
        for (HashMap<String, Object> row : insample_inputs) {
            // Get the value of key "y"
            Object labelValue = row.get("y");
            boolean predictedLabel = this.forest.randomForestPrediction(row);

            // Check the value of the label and add corresponding Boolean to the result
            if (labelValue != null) {
                if ("yes".equalsIgnoreCase(labelValue.toString())) {
                    if(predictedLabel){
                        this.true_positive++;
                    }else if(!predictedLabel){
                        this.false_negative++;
                    }
                } else if ("no".equalsIgnoreCase(labelValue.toString())) {
                    if(predictedLabel){
                        this.false_positive ++;
                    }else if(!predictedLabel){
                        this.true_negative++;
                    }
                }else{
                    throw new IllegalArgumentException("Something wrong with the in-sample input. Otherthing other  than yes or no appeared.");
                }
            } else {
                throw new IllegalArgumentException("Something wrong with the in-sample input");
            }
            count++;
            if(count%size==0 || count == insample_inputs.size()){
                printResult();
            }

        }
        

    }

    public void getTestAccuracy (String testFile, String testLabel) throws Exception{

        // Read all lines from the file
        InputStream testInputStream = getClass().getClassLoader().getResourceAsStream(testFile);
        InputStream testLabelInputStream = getClass().getClassLoader().getResourceAsStream(testLabel);

        List<String> inputLines = new ArrayList<>();
        List<String> labelLines = new ArrayList<>();

        String inputLine;
        String labelLine;

        int batchSize = 1000;  // Process in chunks of 1000 lines
        
        if (testInputStream == null || testLabelInputStream == null) {
            System.err.println("File not found!");
        } else {
            // Wrap the InputStream in an InputStreamReader and BufferedReader
            BufferedReader testInputReader = new BufferedReader(new InputStreamReader(testInputStream));
            BufferedReader testLabelInputReader = new BufferedReader(new InputStreamReader(testInputStream));
            String header = testInputReader.readLine(); //consume the header
            String[] headers = header.split(",");


            try {
                // Read each line and add it to the list
                while ((inputLine = testInputReader.readLine()) != null && (labelLine = testLabelInputReader.readLine())!=null) {
                    if (!inputLine.trim().isEmpty() & !labelLine.trim().isEmpty()) {
                        inputLines.add(inputLine);
                        labelLines.add(labelLine);
                    }

                    // If we've read 1000 lines (batch size), process them
                    if (inputLines.size() == batchSize) {
                        List<HashMap<String,Object>> testInput_form = processBatch(headers, inputLines, labelLines);
                        List<Boolean> labelBooleanList = processLabelBatch(labelLines);

                        inputLines.clear();  // Clear the list for the next batch
                        labelLines.clear();
                        pauseForProcessing(testInput_form,labelBooleanList);  // Pause for further processing
                    }
                }

                // Process any remaining lines if they're fewer than 1000
                if (!inputLines.isEmpty()) {
                    //processBatch(headers, inputLines, labelLines);
                }
                
                // Close the readers
                testInputReader.close();
                testLabelInputReader.close();
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }

    }

    private List<HashMap<String,Object>> processBatch(String[] headers, List<String> inputLines, List<String> labelLines) throws Exception {
        List<HashMap<String,Object>> processedInput = this.data.preprocessData(headers, inputLines, true);
        return processedInput;
    }
    //TODO: Should use Id to map, should read the whole label file, and process the test block by block
    public List<Boolean> processLabelBatch(List<String> labelLines) {
        List<Boolean> processedLabels = new ArrayList<>();

        for (String line : labelLines) {
            String[] parts = line.split(",");            
            // Check if the line has exactly 2 parts (ID and label)
            if (parts.length == 2) {
                String label = parts[1].trim().toLowerCase(); // Normalize to lowercase

                // Process the label
                if ("yes".equals(label)) {
                    processedLabels.add(true);  // "yes" -> true
                } else if ("no".equals(label)) {
                    processedLabels.add(false); // "no" -> false
                } else {
                    throw new IllegalArgumentException("invalid test label");
                }
            } else {
                // Handle malformed lines (e.g., missing comma)
                processedLabels.add(null); // Or log an error
                System.out.println("Malformed line: " + line);
                throw new IllegalArgumentException("invalid test label");
            }
        }

        return processedLabels;
    }

    private void pauseForProcessing(List<HashMap<String,Object>> processedBatch, List<Boolean> labelLines){
        if (processedBatch.size() != labelLines.size()) {
            throw new IllegalArgumentException("Both lists must have the same size.");
        }
    
        for (int i = 0; i < processedBatch.size(); i++) {
            HashMap<String, Object> point = processedBatch.get(i);
            Boolean Label = labelLines.get(i);
            boolean predictedLabel = this.forest.randomForestPrediction(processedBatch.get(i));
            if(Label && predictedLabel){
                //is true and guessed true 
                this.true_positive++;
            }else if(Label && !predictedLabel){
                //is true and guessed false
                this.false_negative++;
            }else if( !Label && predictedLabel){
                //is false and guessed true
                this.false_positive++;
            }else if(Label && !predictedLabel){
                //is false and guessed false
                this.true_negative++;
            }
        }

        printResult();

        /* 
        // Brief explanations
        System.out.println("\nExplanation of metrics:");
        System.out.println("- **Accuracy** measures the overall correctness of the model, i.e., the proportion of correctly classified instances (both positive and negative).");
        System.out.println("- **Precision** measures how accurate the positive predictions are. It is the ratio of correctly predicted positive instances to the total predicted positives.");
        System.out.println("- **Recall** measures how well the model identifies actual positives. It is the ratio of correctly predicted positive instances to the total actual positives.");
        */

    }

    public void printResult(){
        // Calculate Accuracy, Precision, and Recall
        double accuracy = (double) (this.true_positive + this.true_negative) / (this.true_positive + this.false_positive + this.true_negative + this.false_negative);
        double precision = this.true_positive / (double) (this.true_positive + this.false_positive);
        double recall = this.true_positive / (double) (this.true_positive + this.false_negative);


        System.out.println("current process: ");


        // Print the metrics and confusion matrix values
        System.out.println("True Positive (TP): " + this.true_positive);
        System.out.println("False Positive (FP): " + this.false_positive);
        System.out.println("True Negative (TN): " + this.true_negative);
        System.out.println("False Negative (FN): " + this.false_negative);

        System.out.println("\nPerformance Metrics:");

        // Print the metrics
        System.out.println("Accuracy: " + accuracy + "(overall percentage of correct predictions made by the model)");
        System.out.println("Precision: " + precision + "(how many of the predicted positive cases were actually positive)");
        System.out.println("Recall: " + recall + "how many of the actual positive cases the model correctly identified");

    }

}
