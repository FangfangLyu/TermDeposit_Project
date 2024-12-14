package com.termdeposit.model;
public class TreeNode {
    private Boolean isResultNode;
    private Boolean result;

    private double impurity;
    private String splitFeatureName;
    private Boolean isNumFeature;
    private TreeNode left;
    private TreeNode right;
    private Threshold threshold;

        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTreeNode {")
            .append("\n  isResultNode: ").append(isResultNode)
            .append(",\n  result: ").append(result)
            .append(",\n  impurity: ").append(impurity)
            .append(",\n  splitFeatureName: ").append(splitFeatureName)
            .append(",\n  isNumFeature: ").append(isNumFeature)
            .append(",\n  threshold: ").append(threshold != null ? threshold.toString() : "null")
            .append(",\n  left: ").append(left != null ? left.toString() : "null")
            .append(",\n  right: ").append(right != null ? right.toString() : "null")
            .append("\n}");
        return sb.toString();

    }
    public TreeNode() {
        this.isResultNode = null;
        this.left = null;
        this.right = null;
        this.isNumFeature = false;

        this.result = null;
        this.impurity = 1.0;
        this.splitFeatureName = "";


    }//constructor

    public Boolean getResult() {
        return result;
    }

    public void setResult(boolean r) {
        result = r;
    }
    public void setIsResultNode(Boolean r){
        isResultNode = r;
    }

    public Boolean isLeaf() {
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
        this.right = r;
    }

    public TreeNode getLeft(){
        return left;
    }

    public void setLeft(TreeNode l) {
        this.left = l;
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold t) {
        threshold = t;
    }

}