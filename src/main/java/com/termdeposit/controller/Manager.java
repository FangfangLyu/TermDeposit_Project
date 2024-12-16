package com.termdeposit.controller;

import com.termdeposit.model.AdditionalService;
import com.termdeposit.model.RandomForest;
import com.termdeposit.model.DataContainer;

import com.termdeposit.view.UserView;

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

        this.data.preprocessData(trainingSetPath, !isTraining,isDefault);


    }

    public HashMap<String, String> getFeatureList() {
        return this.data.getFeatureList();
    }
    // public void bootstrapBuildRandomForest() {

    // }

    // public void bootstrapView() {

    // }

    // public void predictionTriggered() {

    // }

    // public void addServiceTriggered() {

    // }

    // public void renderPredictionInput() {

    // }

    // public void restartTriggered() {

    // }

    // public void exitTriggered() {

    // }

}
