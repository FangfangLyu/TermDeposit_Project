package com.termdeposit.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Tree {
    private TreeNode root;
    private int treeMinSampleSplit;
    private int treeMaxLayer;
    
    private HashMap<String,String> featureDataType;
    private List<HashMap<String,Object>> trainingData;

    private Random random;


    public Tree(List<HashMap<String,Object>> trainingData2){
        this.treeMinSampleSplit = 2;
        this.treeMaxLayer = 5;
        this.root = null;
        this.random = new Random(42);
        this.trainingData = trainingData2;
    }

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
        return traverse(root,predictionInput);
        
    }
    public boolean traverse(TreeNode current, HashMap<String,Object> predictionInput){
        if (!current.isLeaf()) {
            String feature = current.getFeatName();
            Threshold valueObj = current.getThreshold();
            System.out.println("Testing "+feature);
            Object inputValue = predictionInput.get(feature);
            System.out.println("Input value "+inputValue);

            String type = featureDataType.get(feature);
            System.out.println("Input type "+type);


            if(type.equals("String")){
                if (valueObj.compare(inputValue.toString())) {
                    if(current.getRight()!=null){
                        return traverse(current.getRight(), predictionInput);
                    }else{
                        return current.getResult();
                    }
                } else {
                    if(current.getRight()!=null){
                        return traverse(current.getLeft(), predictionInput);
                    }else{
                        return current.getResult();
                    }
                }
            }else{
                if (valueObj.compare((double)inputValue)) {
                    if(current.getLeft()!=null){
                        return traverse(current.getRight(), predictionInput);
                    }else{
                        return current.getResult();
                    }
                } else {
                    if(current.getLeft()!=null){
                        return traverse(current.getLeft(), predictionInput);
                    }else{
                        return current.getResult();
                    }
                }
            }

        } else {
            System.out.println("Leaf reached.");

            return current.getResult();  // Return the predicted result at the leaf node
        }
    }

    // Helper method to print indentation based on tree depth
    private void printIndentation(int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("    "); // Indentation for better readability
        }
    }
    public void printTree() {
        // Helper method for recursive traversal and printing
        if(root != null){
            printTreeRecursive(root, 0);
        }else{
            System.out.println("ERROR: ROOT IS NULL");
        }

    }
    
    // Recursive method to print the tree
    private void printTreeRecursive(TreeNode node, int level) {
        if (node == null) {
            printIndentation(level);
            System.out.println("Node is null");

            return; // Base case: if the node is null, do nothing
        }
        // Print the current node with indentation based on tree level
    
        if (node.isLeaf()) {
            // If it's a leaf node, print its label
            printIndentation(level);
            System.out.println("Impurity: " + node.getImpurity());
            printIndentation(level);
            System.out.println("Leaf " + "Result: "+ node.getResult());
        } else {
            // If it's an internal node, print feature and threshold
            printIndentation(level);
            System.out.println("IsLeaf: " + node.isLeaf() );

            printIndentation(level);
            System.out.println("Impurity: " + node.getImpurity() );
            printIndentation(level);
            System.out.println("Feature: " + node.getFeatName() + " <= " + node.getThreshold().toString());
            printIndentation(level);
            printIndentation(level);

            System.out.println("Left Child:");
            // Recursively print the left child
            printTreeRecursive(node.getLeft(), level + 1);

            printIndentation(level);

            System.out.println("Right Child: ");
            // Recursively print the right child
            printTreeRecursive(node.getRight(), level + 1);
        }
    }

    public TreeNode growTree(int featureNum){
        //need code;
        this.featureDataType.remove("ID");
        TreeNode initialNode = new TreeNode();
        grow(1, initialNode, selectRandomFeatures(featureNum),trainingData,1);

        System.out.println("Tree grown complete");

        root = initialNode;
        return root;
    }
    public TreeNode growTree(HashMap<String,String> featureList){

        //need code;
        //TODO: has bug here, intend for testing, but report error when using a featureList. 
        //Code has to be modified
        //this.featureDataType.remove("ID");

        TreeNode initialNode = new TreeNode();

        grow(1, initialNode, featureList, trainingData,1);

        System.out.println("Tree grown complete");

        root = initialNode;


        return root;
    }

    public HashMap<String, String>  selectRandomFeatures(int num){
        HashMap<String, String> selectedFeatures = new HashMap<>();


        // Define the list of keys to exclude
        Set<String> excludedKeys = new HashSet<>(Arrays.asList("ID", "y"));

        // Convert the featureDataType map entries into a list
        List<Map.Entry<String, String>> entries = new ArrayList<>();
        for (Map.Entry<String, String> entry : featureDataType.entrySet()) {
            if (!excludedKeys.contains(entry.getKey())) {
                entries.add(entry);
            }
        }

        num = Math.min(num, entries.size());

        for (int i = 0; i < num; i++) {
            int randomIndex = random.nextInt(entries.size());
            Map.Entry<String, String> randomEntry = entries.get(randomIndex);
            selectedFeatures.put(randomEntry.getKey(), randomEntry.getValue());
            entries.remove(randomIndex); // Remove to avoid duplicates
        }

        return selectedFeatures;
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

        double leftImpurity=1, rightImpurity=1;

        boolean conditionForResult = (remainingTrainingData.size() < this.treeMinSampleSplit) 
                                    || treeMaxLayer < layer || remainingFeatures.size() <= 1 || inputImpurity == 0; //only ID column remains

        int yesCount = 0;
        int noCount = 0;
        for(HashMap<String,Object> data: remainingTrainingData){
            if(data.get("y").equals("yes")){
                yesCount++;
            }else{
                noCount++;
            }
        }
        currentNode.setImpurity(inputImpurity); //probably
        currentNode.setResult(yesCount>noCount);
        if( (conditionForResult)){
            currentNode.setIsResultNode(true);
            currentNode.setLeft(null);
            currentNode.setRight(null);

            System.out.println("result node.:impurity input"+inputImpurity);
            return;
        }else{
            currentNode.setIsResultNode(false);

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

                // Get the data type of the feature
                String type = remainingFeatures.get(feature);
                int dataSize = remainingTrainingData.size();
    
                double currentImpurity = 1; // Initialize the impurity for this feature
                
                System.out.println("type:"+type);
                System.out.println("dataSize:"+dataSize);


                List<HashMap<String, Object>> trainingDataLeft = new ArrayList<>();
                List<HashMap<String, Object>> trainingDataRight = new ArrayList<>();
            
                // Calculate Gini Impurity for both sides (Left and Right)
                int leftYes = 0, leftNo = 0, rightYes = 0, rightNo = 0;
                //speical handling needed if any side is empty                

                double lImpurity =1, rImpurity = 1;

                Object compareTo = null;
    
                    //////////////////////////
                if (type.equals("String")) {
                    System.out.println("***finding the best for String feature:***"+feature);

                    // Handling categorical features (String type)
                    for (HashMap<String, Object> point : remainingTrainingData) {
                        compareTo = 1.0; // The threshold is the category value
                        Threshold temp = new Threshold(point.get(feature).toString(), type);
    
                        // Split data based on comparison
                        if (temp.compare(compareTo.toString())) {
                            isRightNull_temp = false;
                            //calculate the right branch's impurity
                            trainingDataRight.add(point);
                            if (point.get("y").equals("yes")) {
                                rightYes++;
                                System.out.println("Comapred to " + temp.getValue());
                                System.out.println("Right got a yes");

                            }else{
                                rightNo++;
                                System.out.println("Comapred to " + temp.getValue());
                                System.out.println("Right got a No");


                            }
                        } else {
                            isLeftNull_temp = false; //TODO: this is actually not needed, it could be fone with numerical comprate of left counts
                            //calculate the right branch's impurity
                            trainingDataLeft.add(point);
                            if (point.get("y").equals("yes")) {
                                leftYes++;
                                System.out.println("Comapred to " + temp.getValue());
                                System.out.println("left got a yes");

                            }else{
                                leftNo++;
                                System.out.println("Comapred to " + temp.getValue());

                                System.out.println("left got a no");

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

                    lImpurity = leftGini;
                    rImpurity = rightGini;

                    System.out.println("Left Gini" + leftGini);
                    System.out.println("Right Gini" + rightGini);

                    // Weighted Gini Impurity for the split
                    currentImpurity = (trainingDataLeft.size() / (double) dataSize) * leftGini + (trainingDataRight.size() / (double) dataSize) * rightGini;
                    
                    System.out.println("Weighted Impurity: "+ currentImpurity+"****");
                    System.out.println("current min Impurity: "+ minImpurity+"****");


                } else {
                    // Handling numerical features (non-String type)
                    // Generate random samples to check for potential splits
                    int testNum = Math.max((int) Math.min(20.0, Math.sqrt(dataSize)),1);

                    HashMap<String, Threshold> min_local = new HashMap<>();
                    double localMinImpurity_best = 1;
                    System.out.println("***finding the best for Numerical feature:***"+feature);


                    //Object result_threshold_value_best = null;

                    List<HashMap<String, Object>> trainingDataLeft_best= new ArrayList<>();
                    List<HashMap<String, Object>> trainingDataRight_best = new ArrayList<>();
                
                    boolean isNullLeft_localBest=true, isNullRight_localBest=true;
                    Object local_compareTo = null;

                    HashSet<Double> visitedDoubles = new HashSet<>();


                    for (int i = 0; i < testNum; i++) {
                        Object comparedNum;
                        int randomIndex = random.nextInt(dataSize);
                        comparedNum = remainingTrainingData.get(randomIndex).get(feature);
                        System.out.println("CompareTo for numerical: "+ comparedNum);


                        if (!visitedDoubles.contains((Double)comparedNum)) {
                            // Process value2 as it hasn't been visited
                            visitedDoubles.add((Double)comparedNum);
                        }else{
                            continue;
                        }
                        
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
                                    System.out.println("Comapred to " + point.get(feature));

                                    System.out.println("Right got a yes");
                                }else{
                                    local_rightNo++;
                                    System.out.println("Comapred to " + point.get(feature));
                                    System.out.println("Right got a no");

                                }
                            } else {
                                isNullLeft_local = false;
                                trainingDataLeft_local.add(point);
                                if (point.get("y").equals("yes")) {
                                    local_leftYes++;
                                    System.out.println("Comapred to " + point.get(feature));
                                    System.out.println("Left got a yes");
    
                                }else{
                                    local_leftNo++;
                                    System.out.println("Comapred to " + point.get(feature));
                                    System.out.println("Left got a no");
    
                                }
                            }
                        }
                        System.out.println("Size of Right data:"+trainingDataRight_local.size());

                        System.out.println("Size of left data:"+trainingDataLeft_local.size());
    
                        // Calculate Gini Impurity for both splits
                        double leftGini;
                        double rightGini;
                            
                        if(isNullLeft_local){
                            leftGini = 1;
                            System.out.println("Left is null " + isNullLeft_local);

                        }else{
                            leftGini = 1 - Math.pow((double) local_leftYes / trainingDataLeft_local.size(), 2) - Math.pow((double) local_leftNo / trainingDataLeft_local.size(), 2);

                        }
                        if(isNullRight_local){
                            rightGini = 1;
                            System.out.println("Right is null " + isNullRight_local);
                        }else{
                            rightGini = 1 - Math.pow((double) local_rightYes / trainingDataRight_local.size(), 2) - Math.pow((double) local_rightNo / trainingDataRight_local.size(), 2);

                        }


                        System.out.println("Left Gini" + leftGini);
                        System.out.println("Right Gini" + rightGini);

    
                        // Weighted Gini Impurity for this test split
                        double testImpurity = (trainingDataLeft_local.size() / (double) dataSize) * leftGini + (trainingDataRight_local.size() / (double) dataSize) * rightGini;
                        System.out.println("Weighted Impurity: "+ testImpurity+"****");
                        System.out.println("current min Impurity: "+ minImpurity+"****");


                        if(testImpurity < localMinImpurity_best){
                            //featureTypeIsNum = true;
                            //First make this the local best
                            lImpurity = leftGini;
                            rImpurity = rightGini;
                            local_compareTo = comparedNum;
                            localMinImpurity_best = testImpurity;

                            trainingDataLeft_best = new ArrayList<>(trainingDataLeft_local);
                            trainingDataRight_best = new ArrayList<>(trainingDataRight_local);

                            isNullLeft_localBest = isNullLeft_local;
                            isNullRight_localBest = isNullRight_local;

                            min_local.clear();

                            min_local.put(feature, new Threshold(compareTo, type));

                        }
                    }

                    trainingDataLeft = trainingDataLeft_best;
                    trainingDataRight = trainingDataRight_best;

                    isLeftNull_temp = isNullLeft_localBest;
                    isRightNull_temp = isNullRight_localBest;

                    compareTo = local_compareTo;
                    currentImpurity = localMinImpurity_best;

                }
                //for both data type same handling: String anf Number, where the variable sued in following are all saved under the same name.
                /*if(Math.abs(currentImpurity - 0.0) < 1e-9){
                    bestType = type;
                    System.out.println(feature+" - Feature datatype:"+bestType);

                    if(bestType.equals("String")){
                        featureTypeIsNum = false;
                    }else{
                        featureTypeIsNum = true;
                    }

                    leftImpurity = lImpurity;
                    rightImpurity = rImpurity;

                    minImpurity = currentImpurity;

                    trainingDataLeft_final = trainingDataLeft;
                    trainingDataRight_final = trainingDataRight;

                    min.clear();

                    if(featureTypeIsNum){
                        min.put(feature, new Threshold((Double)compareTo, bestType));
                    }else{
                        min.put(feature, new Threshold(compareTo.toString(), bestType));

                    }
                    isLeftNull = isLeftNull_temp;
                    isRightNull = isRightNull_temp;
                    break;
                }*/
                // Update the best split if the current split has lower impurity
                if (currentImpurity < minImpurity) {
                    bestType = type;

                    leftImpurity = lImpurity;
                    rightImpurity = rImpurity;

                    isLeftNull = isLeftNull_temp;
                    isRightNull = isRightNull_temp;

                    minImpurity = currentImpurity;

                    trainingDataLeft_final = trainingDataLeft;
                    trainingDataRight_final = trainingDataRight;

                    min.clear();
                    if(featureTypeIsNum){
                        min.put(feature, new Threshold((Double)compareTo, bestType));

                    }else{
                        min.put(feature, new Threshold(compareTo.toString(), bestType));
                    }

                }
            }
        }
        //define the node here
        currentNode.setImpurity(minImpurity);

        System.out.println(min);

        //String featureName = min.keySet().iterator().next();
        String featureName = min.keySet().stream().findFirst().orElse(null);

        currentNode.setFeatName(featureName, featureTypeIsNum);
        currentNode.setThreshold(min.get(featureName));
        // Recursively set the left and right children of the current node 
        remainingFeatures.remove(featureName);
        System.out.println(remainingFeatures);


        if(!isLeftNull){
            TreeNode leftChild = new TreeNode();        
            currentNode.setLeft(leftChild);
            grow(layer+1, leftChild, remainingFeatures, trainingDataLeft_final, leftImpurity);
            System.out.println("leftChild grown for" );
            
        }else{
            currentNode.setLeft(null);
        }
        if(!isRightNull){
            TreeNode rightChild = new TreeNode();        
            currentNode.setRight(rightChild);
            grow(layer+1, rightChild, remainingFeatures, trainingDataRight_final, rightImpurity);
            System.out.println("rightChild grown: " );
        }else{
            currentNode.setLeft(null);
        }
        

    }
    


}
