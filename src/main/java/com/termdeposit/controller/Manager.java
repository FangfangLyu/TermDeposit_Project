package com.termdeposit.controller;

import com.termdeposit.model.AdditionalService;
import com.termdeposit.model.RandomForest;
import com.termdeposit.model.DataContainer;

import com.termdeposit.view.UserView;

import weka.knowledgeflow.Data;

public class Manager {
    private AdditionalService additionalService;

    private RandomForest randomForest;
    private DataContainer data;
    private UserView view;

    // TODO: for multiprocessing, 2.0
    private int multiprocessCPUCount;

    public Manager(AdditionalService additionalService, RandomForest randomForest, DataContainer data, UserView view) {
        this.additionalService = additionalService;
        this.randomForest = randomForest;
        this.data = data;
        this.view = view;
    }

    public void startView() {

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