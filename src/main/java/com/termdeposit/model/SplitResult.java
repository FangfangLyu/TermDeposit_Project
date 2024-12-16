package com.termdeposit.model;

import java.util.HashMap;
import java.util.List;

public class SplitResult {
    private double minImpurity;
    private boolean featureTypeIsNum;
    private String featureName;
    private Threshold threshold;
    private HashMap<String, String> remainingFeatures;
    private List<HashMap<String, Object>> trainingDataLeft;
    private List<HashMap<String, Object>> trainingDataRight;
    private double leftImpurity;
    private double rightImpurity;
    private boolean isLeftNull;
    private boolean isRightNull;

    public SplitResult(double minImpurity, boolean featureTypeIsNum, String featureName, Threshold threshold,
                       HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> trainingDataLeft,
                       List<HashMap<String, Object>> trainingDataRight, double leftImpurity, double rightImpurity,
                       boolean isLeftNull, boolean isRightNull) {
        this.minImpurity = minImpurity;
        this.featureTypeIsNum = featureTypeIsNum;
        this.featureName = featureName;
        this.threshold = threshold;
        this.remainingFeatures = remainingFeatures;
        this.trainingDataLeft = trainingDataLeft;
        this.trainingDataRight = trainingDataRight;
        this.leftImpurity = leftImpurity;
        this.rightImpurity = rightImpurity;
        this.isLeftNull = isLeftNull;
        this.isRightNull = isRightNull;
    }

    // Getters and Setters
    public double getMinImpurity() {
        return minImpurity;
    }

    public void setMinImpurity(double minImpurity) {
        this.minImpurity = minImpurity;
    }

    public boolean isFeatureTypeIsNum() {
        return featureTypeIsNum;
    }

    public void setFeatureTypeIsNum(boolean featureTypeIsNum) {
        this.featureTypeIsNum = featureTypeIsNum;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

    public HashMap<String, String> getRemainingFeatures() {
        return remainingFeatures;
    }

    public void setRemainingFeatures(HashMap<String, String> remainingFeatures) {
        this.remainingFeatures = remainingFeatures;
    }

    public List<HashMap<String, Object>> getTrainingDataLeft() {
        return trainingDataLeft;
    }

    public void setTrainingDataLeft(List<HashMap<String, Object>> trainingDataLeft) {
        this.trainingDataLeft = trainingDataLeft;
    }

    public List<HashMap<String, Object>> getTrainingDataRight() {
        return trainingDataRight;
    }

    public void setTrainingDataRight(List<HashMap<String, Object>> trainingDataRight) {
        this.trainingDataRight = trainingDataRight;
    }

    public double getLeftImpurity() {
        return leftImpurity;
    }

    public void setLeftImpurity(double leftImpurity) {
        this.leftImpurity = leftImpurity;
    }

    public double getRightImpurity() {
        return rightImpurity;
    }

    public void setRightImpurity(double rightImpurity) {
        this.rightImpurity = rightImpurity;
    }

    public boolean isLeftNull() {
        return isLeftNull;
    }

    public void setLeftNull(boolean leftNull) {
        isLeftNull = leftNull;
    }

    public boolean isRightNull() {
        return isRightNull;
    }

    public void setRightNull(boolean rightNull) {
        isRightNull = rightNull;
    }
}
