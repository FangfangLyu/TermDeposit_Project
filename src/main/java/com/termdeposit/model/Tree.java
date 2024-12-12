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

    // Helper method to print indentation based on tree depth
    private void printIndentation(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    "); // Indentation for better readability
        }
    }
    public void printTree() {
        // Helper method for recursive traversal and printing
        printTreeRecursive(root, 0);
    }
    
    // Recursive method to print the tree
    private void printTreeRecursive(TreeNode node, int level) {
        if (node == null) {
            System.out.println("Node is null");

            return; // Base case: if the node is null, do nothing
        }
        
        // Print the current node with indentation based on tree level
        printIndentation(level);
    
        if (node.isLeaf()) {
            // If it's a leaf node, print its label
            System.out.println("Leaf: " + "Result: "+ node.getResult());
        } else {
            // If it's an internal node, print feature and threshold
            System.out.println("Impurity: " + node.getImpurity() );
            System.out.println("Feature: " + node.getFeatName() + " <= " + node.getThreshold().toString());
            System.out.println("Left Child: (result:)"+node.isLeaf());
            // Recursively print the left child
            printTreeRecursive(node.getLeft(), level + 1);
            
            System.out.println("Right Child: (result) "+node.isLeaf());
            // Recursively print the right child
            printTreeRecursive(node.getRight(), level + 1);
        }
    }

    public TreeNode growTree(){
        //need code;
        this.featureDataType.remove("ID");
        TreeNode initialNode = new TreeNode();
        grow(1, initialNode, featureDataType,trainingData,1);
        System.out.println("Tree grown");

        root = initialNode;
        return root;
    }


    public void grow(int layer, TreeNode currentNode, HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> remainingTrainingData, double inputImpurity) {
        // Initialize the best impurity, best threshold, and best splits
        double minImpurity = 1;
        String bestType = null;

        HashMap<String, Threshold> min = new HashMap<>();
        List<HashMap<String, Object>> trainingDataLeft_final = new ArrayList<>();
        List<HashMap<String, Object>> trainingDataRight_final = new ArrayList<>();
    
        Object result_threshold_value = null;
        boolean featureTypeIsNum = false;

        boolean isLeftNull=true, isRightNull=true;

        boolean conditionForResult = (remainingTrainingData.size() < this.treeMinSampleSplit) 
                                    || treeMaxLayer <= layer || remainingFeatures.size() <= 1 || inputImpurity == 0; //only ID column remains

        if( (conditionForResult)){
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
            currentNode.setLeft(null);
            currentNode.setLeft(null);

            System.out.println("result node.");
            return;
        }

        if (remainingTrainingData.size() >= this.treeMinSampleSplit || remainingFeatures.size() > 1) {
            /*TODO: this method could be handled properly if we could modularize it.
            But the contrainint at this point is that we didn't specific label the name of ID row and y row in the beginning.
            So we have only fixed the row name to "ID" and "y"
            And there could be too many data passing by parameter if we don't make some of the varible more compact or store it in the class scope.
            This requires more design and thinking, due to time constraint, out goal is to create a running prototype first. 
            */
            for (String feature : remainingFeatures.keySet()) {
                boolean isLeftNull_temp=true, isRightNull_temp = true; 

                // Skip ID or target column (y)
                if (feature.equals("ID") || feature.equals("y")) {
                    continue;
                }
                System.out.println("**String feature:"+feature);

    
                // Get the data type of the feature
                String type = this.featureDataType.get(feature);
                int dataSize = remainingTrainingData.size();
    
                double currentImpurity = 1; // Initialize the impurity for this feature
                
                System.out.println("type:"+type);
                System.out.println("dataSize:"+dataSize);


                List<HashMap<String, Object>> trainingDataLeft = new ArrayList<>();
                List<HashMap<String, Object>> trainingDataRight = new ArrayList<>();
            
                // Calculate Gini Impurity for both sides (Left and Right)
                int leftYes = 0, leftNo = 0, rightYes = 0, rightNo = 0;
                //speical handling needed if any side is empty

                Object compareTo = null;
    
                    //////////////////////////
                if (type.equals("String")) {
                    // Handling categorical features (String type)
                    for (HashMap<String, Object> point : remainingTrainingData) {
                        compareTo = point.get(feature); // The threshold is the category value
                        Threshold temp = new Threshold(point.get(feature).toString(), type);
    
                        // Split data based on comparison
                        if (temp.compare(compareTo.toString())) {
                            isRightNull_temp = false;
                            //calculate the right branch's impurity
                            trainingDataRight.add(point);
                            if (point.get("y").equals("yes")) {
                                rightYes++;
                            }else{
                                rightNo++;

                            }
                        } else {
                            isLeftNull_temp = false;
                            //calculate the right branch's impurity
                            trainingDataLeft.add(point);
                            if (point.get("y").equals("yes")) {
                                leftYes++;
                            }else{
                                leftNo++;
                            }
                        }
                    }

                    System.out.println("Size of left data:"+trainingDataLeft.size());
                    System.out.println("Size of right data:"+trainingDataRight.size());
    
                    // Calculate Gini Impurity for both splits
                    double leftGini;
                    double rightGini;
                    System.out.println("~NULL Left :" + isLeftNull_temp);
                    System.out.println("~NULL Right : " + isRightNull_temp);

                    if(isLeftNull_temp){
                        leftGini = 1;
                        System.out.println("Left is null " + isLeftNull_temp);
                    }else{
                        leftGini = 1 - Math.pow( ((double)leftYes )/ trainingDataLeft.size(), 2) - Math.pow(((double) leftNo) / trainingDataLeft.size(), 2);

                    }
                    if(isRightNull_temp){
                        rightGini = 1;
                        System.out.println("Right is null " + isRightNull_temp);
                    }else{
                        rightGini = 1 - Math.pow((double) rightYes / trainingDataRight.size(), 2) - Math.pow((double) rightNo / trainingDataRight.size(), 2);

                    }

                    System.out.println("Left Gini" + leftGini);
                    System.out.println("Right Gini" + rightGini);

                    // Weighted Gini Impurity for the split
                    currentImpurity = (trainingDataLeft.size() / (double) dataSize) * leftGini + (trainingDataRight.size() / (double) dataSize) * rightGini;
                    
                    System.out.println("Weighted Impurity: "+ currentImpurity+"****");

                } else {
                    // Handling numerical features (non-String type)
                    // Generate random samples to check for potential splits
                    int testNum = Math.max((int) Math.min(50.0, Math.sqrt(dataSize)),1);

                    HashMap<String, Threshold> min_local = new HashMap<>();
                    double localMinImpurity_best = 1;

                    //Object result_threshold_value_best = null;

                    List<HashMap<String, Object>> trainingDataLeft_best= new ArrayList<>();
                    List<HashMap<String, Object>> trainingDataRight_best = new ArrayList<>();
                
                    boolean isNullLeft_localBest=true, isNullRight_localBest=true;
                    Object local_compareTo = null;

                    for (int i = 0; i < testNum; i++) {
                        Object comparedNum;
                        int randomIndex = random.nextInt(dataSize);
                        comparedNum = remainingTrainingData.get(randomIndex).get(feature);
                        System.out.println("CompareTo for numerical: "+ comparedNum);
                        
                        boolean isNullLeft_local=true, isNullRight_local=true;
                            
                        List<HashMap<String, Object>> trainingDataLeft_local = new ArrayList<>();
                        List<HashMap<String, Object>> trainingDataRight_local = new ArrayList<>();
                    
                        trainingDataLeft_local.clear();
                        trainingDataRight_local.clear();
    
                        // Counters for Gini Impurity calculation
                        int local_leftYes = 0, local_leftNo = 0,local_rightYes = 0, local_rightNo = 0;
    
                        // Split data based on the threshold value
                        for (HashMap<String, Object> point : remainingTrainingData) {
                            Threshold temp = new Threshold(point.get(feature), type);
                            if (temp.compare((Double)comparedNum)) {
                                isNullRight_local = false;

                                trainingDataRight_local.add(point);
                                if (point.get("y").equals("yes")) {
                                    local_rightYes++;
                                    System.out.println("Right got a yes");
                                }else{
                                    local_rightNo++;
                                    System.out.println("Right got a no");

                                }
                            } else {
                                isNullLeft_local = false;
                                trainingDataLeft_local.add(point);
                                if (point.get("y").equals("yes")) {
                                    leftYes++;
                                    System.out.println("Left got a yes");
    
                                }else{
                                    leftNo++;
                                    System.out.println("Left got a no");
    
                                }
                            }
                        }
    
                        System.out.println("Size of left data:"+trainingDataLeft_local.size());
    
                        // Calculate Gini Impurity for both splits
                        double leftGini;
                        double rightGini;
                        System.out.println("Left is null condition " + isNullLeft_local);
                        System.out.println("Right is null condition " + isNullRight_local);
                            
                        if(isNullLeft_local){
                            leftGini = 1;
                            System.out.println("Left is null " + isNullRight_local);

                        }else{
                            leftGini = 1 - Math.pow((double) local_leftYes / trainingDataLeft_local.size(), 2) - Math.pow((double) local_leftNo / trainingDataLeft_local.size(), 2);

                        }
                        if(isRightNull_temp){
                            rightGini = 1;
                            System.out.println("Right is null " + isNullRight_local);
                        }else{
                            rightGini = 1 - Math.pow((double) local_rightYes / trainingDataRight_local.size(), 2) - Math.pow((double) local_rightNo / trainingDataRight_local.size(), 2);

                        }
    
                        // Weighted Gini Impurity for this test split
                        double testImpurity = (trainingDataLeft_local.size() / (double) dataSize) * leftGini + (trainingDataRight_local.size() / (double) dataSize) * rightGini;
                        System.out.println("Weighted Impurity: "+ testImpurity+"****");

                        if(testImpurity==0.0 || testImpurity < localMinImpurity_best){
                            //featureTypeIsNum = true;
                            //First make this the local best
                            local_compareTo = comparedNum;
                            localMinImpurity_best = currentImpurity;
                            trainingDataLeft_best = new ArrayList<>(trainingDataLeft_local);
                            trainingDataRight_best = new ArrayList<>(trainingDataRight_local);

                            isNullLeft_localBest = isNullLeft_local;
                            isNullRight_localBest = isNullRight_local;

                            min_local.clear();

                            min_local.put(feature, new Threshold(compareTo, type));

                            if(testImpurity == 0.0){
                                break;
                            }
                        }
                    }
                    trainingDataLeft = trainingDataLeft_best;
                    trainingDataRight = trainingDataRight_best;

                    isLeftNull_temp = isNullLeft_localBest;
                    isRightNull_temp = isNullRight_localBest;
                    bestType = type;

                    compareTo = local_compareTo;
                    currentImpurity = localMinImpurity_best;

                }
                //for both data type same handling: String anf Number, where the variable sued in following are all saved under the same name.
                if(currentImpurity==0){
                    if(bestType.equals("String")){
                        featureTypeIsNum = false;
                    }else{
                        featureTypeIsNum = true;
                    }
                    minImpurity = currentImpurity;
                    trainingDataLeft_final = trainingDataLeft;
                    trainingDataRight_final = trainingDataRight;
                    min.clear();
                    if(featureTypeIsNum){
                        min.put(feature, new Threshold((Double)compareTo, type));

                    }else{
                        min.put(feature, new Threshold(compareTo.toString(), type));

                    }
                    isLeftNull = isLeftNull_temp;
                    isRightNull = isRightNull_temp;
                    break;
                }
                // Update the best split if the current split has lower impurity
                if (currentImpurity < minImpurity) {

                    isLeftNull = isLeftNull_temp;
                    isRightNull = isRightNull_temp;

                    minImpurity = currentImpurity;
                    trainingDataLeft_final = trainingDataLeft;
                    trainingDataRight_final = trainingDataRight;

                    min.clear();
                    if(featureTypeIsNum){
                        min.put(feature, new Threshold((Double)compareTo, type));

                    }else{
                        min.put(feature, new Threshold(compareTo.toString(), type));
                    }

                    isLeftNull = isLeftNull_temp;
                    isRightNull = isRightNull_temp;
                }
            }
        }
        //define the node here
        currentNode.setImpurity(minImpurity);
        String featureName = min.keySet().iterator().next();
        currentNode.setFeatName(featureName, featureTypeIsNum);
        currentNode.setThreshold(min.get(featureName));
        // Recursively set the left and right children of the current node 
        remainingFeatures.remove(featureName);
        System.out.println(remainingFeatures);


        if(!isLeftNull){
            TreeNode leftChild = new TreeNode();        
            System.out.println("Left is not null.");

            grow(layer+1, leftChild, remainingFeatures, trainingDataLeft_final, minImpurity);
            System.out.println("leftChild grown not result " + currentNode );

            printIndentation(layer);
            System.out.print("Left child of the current Node:" + leftChild);
            currentNode.setLeft(leftChild);
        }else{
            currentNode.setLeft(null);
        }
        if(!isRightNull){
            TreeNode rightChild = new TreeNode();        
            currentNode.setRight(rightChild);
            grow(layer+1, rightChild, remainingFeatures, trainingDataRight_final, minImpurity);
            System.out.println("rightChild grown for " + currentNode );
        }else{
            currentNode.setLeft(null);
        }
        

    }
    


}
