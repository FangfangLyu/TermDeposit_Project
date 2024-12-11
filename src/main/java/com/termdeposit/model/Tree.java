package com.termdeposit.model;

import java.util.HashMap;

public class Tree {
    private TreeNode root;
    private int treeMinSampleSplit;
    private int treeMaxLayer;
    private HashMap<String,String> featureDataType;

    public Tree(int treeMinSampleSplit, int treeMaxLayer){
        this.treeMinSampleSplit = treeMinSampleSplit;
        this.treeMaxLayer = treeMaxLayer;
        this.root = null;
    }

    public void setDatatype(HashMap<String,String> featureDataType){
        this.featureDataType = featureDataType;
    }

    public boolean predictPreorderTraversal(HashMap<String,Object> predictionInput){
        return false;
        
    }
    public boolean traverse(TreeNode current, HashMap<String,Object> predictionInput){
        if(!current.isLeaf()){ //if not result node
            String feature = current.getFeatName();
            Threshold valueObj = current.getThreshold();
            Object inputValue = predictionInput.get(feature);
            if(valueObj.compare(inputValue)){
                traverse(current.getRight(),predictionInput);
            }else{
                traverse(current.getLeft(), predictionInput);
            }
        }else{
            return current.getResult();
        }
        return false;
    }

    public TreeNode growTree(List<HashMap<String,Object>> list){
        //need code;
        return null;
    }

    public HashMap<String,Threshold> findBestThreshold(List<HashMap<String,Object>> trainingData){
        //need code;
        return null;
    }

    public HashMap<String,Threshold> calcImpurity(List<HashMap<String,Object>> trainingData, String feature, Threshold threshold){
        //need code;
        return null;
    }


}
