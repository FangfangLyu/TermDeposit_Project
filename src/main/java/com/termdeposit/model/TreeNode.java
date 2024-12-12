package com.termdeposit.model;
public class TreeNode {


    private boolean isResultNode;
    private boolean result;

    private double impurity;
    private String splitFeatureName;
    private boolean isNumFeature;
    private TreeNode left;
    private TreeNode right;
    private Threshold threshold;

    public TreeNode() {

        
    }//constructor

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean r) {
        isResultNode = true;
        result = r;
    }

    public boolean isLeaf() {
        return isResultNode;
    }

    public double getImpurity(){
        return impurity;
    }

    public void setImpurity(double imp) {
        impurity = imp;
    }

    public String getFeatName() {
        return splitFeatureName;
    }

    public void setFeatName(String s, boolean isNum) {
        splitFeatureName = s;
        isNumFeature = isNum;
    }

    public TreeNode getRight(){
        return right;
    }

    public void setRight(TreeNode r) {
        right = r;
    }

    public TreeNode getLeft(){
        return left;
    }

    public void setLeft(TreeNode l) {
        left = l;
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold t) {
        threshold = t;
    }

}