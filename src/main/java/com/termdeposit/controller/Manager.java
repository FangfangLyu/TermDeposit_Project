package com.termdeposit.controller;

import com.termdeposit.model.AdditionalService;
import com.termdeposit.model.RandomForest;
import com.termdeposit.model.DataContainer;

import com.termdeposit.view.UserView;

import weka.core.Instance;
import weka.knowledgeflow.Data;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private AdditionalService additionalService;

    private RandomForest randomForest;
    private DataContainer data;
    private UserView view;

    // TODO: for multiprocessing, 2.0. Discard for now. No need for it.
    private int multiprocessCPUCount;

    public Manager() {

        HashMap<String, String> dataTypeMap = new HashMap<>();

        // Populating the map with column names and their corresponding data types
        dataTypeMap.put("ID", "String");
        dataTypeMap.put("age", "Integer"); // Age is a numerical value
        dataTypeMap.put("job", "String"); // Job is a categorical feature (String)
        dataTypeMap.put("marital", "String"); // Marital status is a categorical feature (String)
        dataTypeMap.put("education", "String"); // Education level is a categorical feature (String)
        dataTypeMap.put("default", "String"); // Default is a binary feature (Boolean)
        dataTypeMap.put("balance", "Double"); // Balance is a continuous numerical value (Float)
        dataTypeMap.put("housing", "String"); // Housing is a binary feature (Boolean)
        dataTypeMap.put("loan", "String"); // Loan is a binary feature (Boolean)
        dataTypeMap.put("contact", "String"); // Contact type is a categorical feature (String)
        dataTypeMap.put("day", "Integer"); // Day of the month is a numerical value (Integer)
        dataTypeMap.put("month", "String"); // Month is a categorical feature (String)
        dataTypeMap.put("campaign", "Integer"); // Campaign is a numerical value (Integer)
        dataTypeMap.put("pdays", "Integer"); // Pdays is a numerical value (Integer)
        dataTypeMap.put("previous", "Integer"); // Previous is a numerical value (Integer)
        dataTypeMap.put("poutcome", "String"); // Poutcome is a categorical feature (String)*/
        dataTypeMap.put("y", "String"); // Target variable (y) is typically a String or Boolean

        this.data = new DataContainer(dataTypeMap);
        this.view = new UserView(this); // userView will request action from the Model part so pass this object of the
                                        // manager to UI

        this.additionalService = null; // TODO: do we need to initialize first?
    }

    public void startImputation(boolean isDefault, boolean isTraining, String trainingSetPath) throws Exception {
        boolean isTesting = !isTraining;
        this.data.preprocessData(trainingSetPath, isTesting, isDefault);

        try {
            // RandomForest forest = new
            // RandomForest(this.data,randomSeed,treeNum,minDataToSplit,minDataToSplit,featureNum);
            this.randomForest = new RandomForest(this.data, 42, 1, 10, 1, 0);
            randomForest.growTreeForest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("Random Forest not trained.");
            e.printStackTrace();
        }

        // bootstrapBuildRandomForest(42,200,10,5,0);

        System.out.println("Data Trained");
        // TODO: We could allow user to define these hyperparameters
    }

    public HashMap<String, Object> initAdditionalService() {
        additionalService = new AdditionalService(data);
        return additionalService.recordImprovements();
    }

    public HashMap<String, Object> getPredictionInput() {
        return this.data.getPredictionInput();
    }

    public HashMap<String, Object> getImputedPredictionInput() {
        return this.data.getImputedPredictionInput();
    }

    public HashMap<String, String> getFeatureList() {
        return this.data.getFeatureList();
    }

    // public void trigger() {

    // }

    // public void bootstrapView() {

    // }

    public boolean predictionTriggered(int age, String job, String marital, String education, String creditDefault,
            double balance, String housing, String loan, String contact,
            int day, String month, int campaign, int pdays, int previous,
            String poutcome) {
        HashMap<String, Object> inputData = new HashMap<>();

        inputData.put("age", age);
        inputData.put("job", job);
        inputData.put("marital", marital);
        inputData.put("education", education);
        inputData.put("default", creditDefault);
        inputData.put("balance", balance);
        inputData.put("housing", housing);
        inputData.put("loan", loan);
        inputData.put("contact", contact);
        inputData.put("day", day);
        inputData.put("month", month);
        inputData.put("campaign", campaign);
        inputData.put("pdays", pdays);
        inputData.put("previous", previous);
        inputData.put("poutcome", poutcome);

        try {
            // Make the prediction
            // Save raw data
            this.data.setPredictionInput(inputData);

            // impute user's input
            inputData = this.data.preprocessSingleData(inputData);
            inputData = this.data.knn_model.oneHotkeyEncodingForSingle(inputData);
            List<HashMap<String, Object>> tempList = new ArrayList<>();
            tempList.add(inputData);
            inputData = this.data.knn_model.imputeMissingValues(tempList, true).get(0);
            System.out.println(inputData);
            // save imputed data
            this.data.setImputedPredictionInput(inputData);

            // get prediction
            boolean prediction = this.randomForest.randomForestPrediction(inputData);

            // Return the result as a boolean based on the model's prediction
            return prediction; // Assuming class 1 means subscription, class 0 means no subscription

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    // public void addServiceTriggered() {

    // }

    // public void restartTriggered() {

    // }

    // public void exitTriggered() {

    // }

}
