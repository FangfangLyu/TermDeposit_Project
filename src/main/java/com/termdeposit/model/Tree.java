package com.termdeposit.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Tree {
    private TreeNode root;
    private int treeMinSampleSplit;
    private int treeMaxLayer;
    
    private HashMap<String,String> featureDataType;
    private List<HashMap<String,Object>> trainingData;

    private Random random;

    public Tree(int treeMinSampleSplit, int treeMaxLayer, Random random, List<HashMap<String,Object>> trainingData){
        this.treeMinSampleSplit = treeMinSampleSplit;
        this.treeMaxLayer = treeMaxLayer;
        this.root = null;
        this.random = random;
        this.trainingData = trainingData;
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

    public TreeNode growTree(){
        //need code;
        this.featureDataType.remove("ID");
        grow(1, root, featureDataType,trainingData,false);
        System.out.println("Tree grown");

        return root;
    }


    public void grow(int layer, TreeNode currentNode, HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> remainingTrainingData, boolean resultNode) {
        // Initialize the best impurity, best threshold, and best splits
        double minImpurity = 1;

        HashMap<String, Threshold> min = new HashMap<>();
        List<HashMap<String, Object>> trainingDataLeft_final = new ArrayList<>();
        List<HashMap<String, Object>> trainingDataRight_final = new ArrayList<>();
    
        Object result_threshold_value = null;
        boolean featureTypeIsNum = false;

        boolean isLeftNull=false, isRightNull = false;

        if( (remainingTrainingData.size() < this.treeMinSampleSplit) || treeMaxLayer <= layer || (remainingFeatures.size() <= 1 || resultNode)){
            int yesCount = 0;
            int noCount = 0;
            
            for(HashMap<String,Object> data: remainingTrainingData){
                if(data.get("y").equals("yes")){
                    yesCount++;
                }else{
                    noCount++;
                }
            }
            currentNode.setResult(yesCount>noCount);
            
            System.out.println("result node.");

            return;
        }

        if (remainingTrainingData.size() >= this.treeMinSampleSplit || remainingFeatures.size() > 1) {
            for (String feature : remainingFeatures.keySet()) {

                // Skip ID or target column (y)
                if (feature.equals("ID") || feature.equals("y")) {
                    continue;
                }
                System.out.println("String feature:"+feature);

    
                // Get the data type of the feature
                String type = this.featureDataType.get(feature);
                int dataSize = remainingTrainingData.size();
    
                double currentImpurity = 1; // Initialize the impurity for this feature
                
                System.out.println("type:"+type);
                System.out.println("dataSize:"+dataSize);


                List<HashMap<String, Object>> trainingDataLeft = new ArrayList<>();
                List<HashMap<String, Object>> trainingDataRight = new ArrayList<>();
                int yesSideCounter = 0;  // For Gini impurity calculation
                

                Object compareTo = null;
    
                if (type.equals("String")) {
                    // Handling categorical features (String type)
                    for (HashMap<String, Object> point : remainingTrainingData) {
                        compareTo = point.get(feature); // The threshold is the category value
                        Threshold temp = new Threshold(point.get(feature), type);
    
                        // Split data based on comparison
                        if (temp.compare(compareTo)) {
                            trainingDataRight.add(point);
                            if (point.get("y").equals("yes")) {
                                yesSideCounter++;
                            }
                        } else {
                            trainingDataLeft.add(point);
                        }
                    }
    
                    // Calculate Gini Impurity for both sides (Left and Right)
                    int leftYes = 0, leftNo = 0, rightYes = yesSideCounter, rightNo = dataSize - yesSideCounter;
                    
                    System.out.println("Size of left data:"+trainingDataLeft.size());

                    //TODO: if empty
                    if(trainingDataLeft.size() == 0){
                        isLeftNull_temp = true;
                        System.out.println("Left branch is empty");
                        
                    }else{
                        for (HashMap<String, Object> leftPoint : trainingDataLeft) {
                            System.out.println(leftPoint);
                            if (leftPoint.get("y").equals("yes")) {
                                leftYes++;
                                System.out.println("Left got a yes");
                            } else {
                                leftNo++;
                                System.out.println("Left got a no");

                            }
                        } 
                    }

                    //TODO: if empty
                    if(trainingDataRight.size() == 0){
                        isRightNull = true;
                        System.out.println("Right branch is empty");


                    }else{
                        for (HashMap<String, Object> rightPoint : trainingDataRight) {
                            if (rightPoint.get("y").equals("yes")) {
                                rightYes++;
                            } else {
                                rightNo++;
                            }
                        } 
                    }
    
                    // Calculate Gini Impurity for both splits

                    double leftGini = 1 - Math.pow( ((double)leftYes )/ trainingDataLeft.size(), 2) - Math.pow(((double) leftNo) / trainingDataLeft.size(), 2);
                    double rightGini = 1 - Math.pow((double) rightYes / trainingDataRight.size(), 2) - Math.pow((double) rightNo / trainingDataRight.size(), 2);
                    
                    if(isLeftNull){
                        leftGini = 1;
                    }
                    if(isRightNull){
                        rightGini = 1;
                    }

                    System.out.println("Left Gini" + leftGini);
                    System.out.println("Right Gini" + rightGini);



                    // Weighted Gini Impurity for the split
                    currentImpurity = (trainingDataLeft.size() / (double) dataSize) * leftGini + (trainingDataRight.size() / (double) dataSize) * rightGini;
                    
                    if(currentImpurity==0){
                        featureTypeIsNum = false;
                        minImpurity = currentImpurity;
                        result_threshold_value = compareTo;
                        trainingDataLeft_final = new ArrayList<>(trainingDataLeft);
                        trainingDataRight_final = new ArrayList<>(trainingDataRight);
                        min.clear();
                        min.put(feature, new Threshold(compareTo, type));
                        break;
                    }

                    // Update the best split if the current split has lower impurity
                    if (currentImpurity < minImpurity) {
                        featureTypeIsNum = false;
                        minImpurity = currentImpurity;
                        result_threshold_value = compareTo;
                        trainingDataLeft_final = new ArrayList<>(trainingDataLeft);
                        trainingDataRight_final = new ArrayList<>(trainingDataRight);
                        min.clear();
                        min.put(feature, new Threshold(compareTo, type));
                    }
    
                } else {
                    // Handling numerical features (non-String type)
                    // Generate random samples to check for potential splits
                    int testNum = Math.max((int) Math.min(50.0, Math.sqrt(dataSize)),1);
                    double localMinImpurity = 1;
                    Object tmp_result_threshold_value = null;
                    boolean isLeftNull_temp = false, isRightNull_temp = false;

    
                    for (int i = 0; i < testNum; i++) {
                        int randomIndex = random.nextInt(dataSize);
                        compareTo = remainingTrainingData.get(randomIndex).get(feature);
    
                        trainingDataLeft.clear();
                        trainingDataRight.clear();
    
                        // Counters for Gini Impurity calculation
                        int localYesSideCounter = 0;
    
                        // Split data based on the threshold value
                        for (HashMap<String, Object> point : remainingTrainingData) {
                            Threshold temp = new Threshold(point.get(feature), type);
                            if (temp.compare(compareTo)) {
                                trainingDataRight.add(point);
                                if (point.get("y").equals("yes")) {
                                    localYesSideCounter++;
                                }
                            } else {
                                trainingDataLeft.add(point);
                            }
                        }
    
                        // Gini Impurity calculation for the left and right sides
                        int localLeftYes = 0, localLeftNo = 0, localRightYes = localYesSideCounter, localRightNo = dataSize - localYesSideCounter;
    
                        for (HashMap<String, Object> leftPoint : trainingDataLeft) {
                            if (leftPoint.get("y").equals("yes")) {
                                localLeftYes++;
                            } else {
                                localLeftNo++;
                            }
                        }
    
                        double leftGini = 1 - Math.pow((double) localLeftYes / trainingDataLeft.size(), 2) - Math.pow((double) localLeftNo / trainingDataLeft.size(), 2);
                        double rightGini = 1 - Math.pow((double) localRightYes / trainingDataRight.size(), 2) - Math.pow((double) localRightNo / trainingDataRight.size(), 2);
    
                        // Weighted Gini Impurity for this test split
                        double testImpurity = (trainingDataLeft.size() / (double) dataSize) * leftGini + (trainingDataRight.size() / (double) dataSize) * rightGini;
    
                        
                        // Update the best threshold if the current test impurity is lower
                        if (testImpurity < localMinImpurity) {
                            featureTypeIsNum = true;
                            localMinImpurity = testImpurity;
                            tmp_result_threshold_value = compareTo;
                        }
                    }
    
                    // After checking all test splits, update if necessary
                    currentImpurity = localMinImpurity;
                    result_threshold_value = tmp_result_threshold_value;
    
                    if (currentImpurity < minImpurity) {
                        minImpurity = currentImpurity;
                        trainingDataLeft_final = new ArrayList<>(trainingDataLeft);
                        trainingDataRight_final = new ArrayList<>(trainingDataRight);
                        min.clear();
                        min.put(feature, new Threshold(result_threshold_value, type));
                    }
                }
    
            }
        }
        // Recursively set the left and right children of the current node 
        if(!isLeftNull){
            TreeNode leftChild = new TreeNode();
            leftChild.setFeatName(min.keySet().iterator().next(), featureTypeIsNum); // get feature Name
        
            remainingFeatures.remove(min.keySet().iterator().next());
            currentNode.setLeft(leftChild);


            if(isRightNull){
                grow(layer+1, leftChild, remainingFeatures, trainingDataLeft_final,true);
            }else{
                grow(layer+1, leftChild, remainingFeatures, trainingDataLeft_final,false);
            }
        }
        if(!isRightNull){
            TreeNode rightChild = new TreeNode();
            rightChild.setFeatName(min.keySet().iterator().next(), featureTypeIsNum); // get feature Name
        
            remainingFeatures.remove(min.keySet().iterator().next());

            currentNode.setRight(rightChild);
            if(isLeftNull){
                grow(layer+1, rightChild, remainingFeatures, trainingDataRight_final,true);
            }else{
                grow(layer+1, rightChild, remainingFeatures, trainingDataRight_final,false);
            }
        }
        

    }
    


}
