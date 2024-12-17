package com.termdeposit.controller;

import com.termdeposit.model.AdditionalService;
import com.termdeposit.model.RandomForest;
import com.termdeposit.model.DataContainer;

import com.termdeposit.view.UserView;

import weka.core.Instance;
import weka.knowledgeflow.Data;

import java.util.HashMap;

public class Manager {
    private AdditionalService additionalService;

    private RandomForest randomForest;
    private DataContainer data;
    private UserView view;

    // TODO: for multiprocessing, 2.0
    private int multiprocessCPUCount;

    public Manager() {
        this.additionalService = null;
        this.randomForest = null;
        this.data = null;
        this.view = null;

    }

    public void startImputation(boolean isDefault, boolean isTraining, String trainingSetPath) throws Exception {

        this.data.preprocessData(trainingSetPath, !isTraining, isDefault);

    }

    public HashMap<String, String> getFeatureList() {
        return this.data.getFeatureList();
    }

    public void allowPrediction() {
        view.add
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
        inputData.put("creditDefault", creditDefault);
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
            inputData = this.data.preprocessSingleData(inputData);
            inputData = this.data.knn_model.oneHotkeyEncodingForSingle(inputData);

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

    // public void renderPredictionInput() {

    // }

    // public void restartTriggered() {

    // }

    // public void exitTriggered() {

    // }

}
