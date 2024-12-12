package com.termdeposit.model;

import java.util.HashMap;
import java.util.List;

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

    public TreeNode growTree( List<HashMap<String,Object>> list){
        //need code;

        return null;
    }

    public TreeNode growRecursive(TreeNode node, List<HashMap<String,Object>> list){
        return null
    }

    public HashMap<String,Threshold> findBestThreshold(HashMap<String,String>  remainingFeatures, List<HashMap<String,Object>> trainingData){
        //need code;
        HashMap<String,Threshold> min;
        double minImpurity = 1;

        for(String feature: remainingFeatures.keySet()){
            String type = this.featureDataType.get(feature);
            if(type.equals("String")){
                double compareTo = 1.0;
                int yesSideCounter = 0;
                Threshold temp;

                //Find the count of instances satisfying the condition
                for(HashMap<String,Object> point : trainingData){
                    temp = new Threshold(point.get(feature), type);
                    yesSideCounter += temp.compare(compareTo) ? 1:0 ;
                }
                int noSideCounter = trainingData.size() - yesSideCounter;
                //Find Gini Impurity:
                double currentImpurity = 1 - ( ((double)yesSideCounter)/trainingData.size() - ((double)noSideCounter)/trainingData.size()); 
                if(currentImpurity < minImpurity) {
                    minImpurity = currentImpurity;
                    

                }

                //find the impurity when if the attribute is true. and remove this feature
            }else{

            }
        }
        return null;
    }

    public HashMap<String,Threshold> calcImpurity(List<HashMap<String,Object>> trainingData, String feature, Threshold threshold){
        //need code;
        return null;
    }


}
