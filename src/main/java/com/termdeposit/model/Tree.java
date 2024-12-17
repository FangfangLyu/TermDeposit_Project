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
    private TreeNode root = new TreeNode();
    private int treeMinSampleSplit;
    private int treeMaxLayer;
    
    private HashMap<String,String> featureDataType;
    private List<HashMap<String,Object>> trainingData;

    private Random random;
    // Getter for root
    public TreeNode getRoot() {
        return root;
    }

    // Getter for treeMinSampleSplit
    public int getTreeMinSampleSplit() {
        return treeMinSampleSplit;
    }

    // Getter for treeMaxLayer
    public int getTreeMaxLayer() {
        return treeMaxLayer;
    }

    // Getter for featureDataType
    public HashMap<String, String> getFeatureDataType() {
        return featureDataType;
    }

    // Getter for trainingData
    public List<HashMap<String, Object>> getTrainingData() {
        return trainingData;
    }

    // Getter for random
    public Random getRandom() {
        return random;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
    
        sb.append("Tree{");
        sb.append("treeMinSampleSplit=").append(treeMinSampleSplit).append(", ");
        sb.append("treeMaxLayer=").append(treeMaxLayer).append(", ");
        sb.append("featureDataType=").append(featureDataType).append(", ");
        sb.append("trainingDataSize=").append(trainingData != null ? trainingData.size() : "null").append(", ");
        sb.append("root=").append(root).append(", ");
        sb.append("random=").append(random); // Optionally, you can avoid printing the Random object itself as it's hard to represent meaningfully
        sb.append("}");
    
        return sb.toString();
    }


    @Override
    public boolean equals(Object obj){

        if (this == obj) {
            return true;
        }
    
        // Check if obj is null or not an instance of Tree
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Tree other = (Tree)obj;
        // Compare tree parameters
        System.out.println("Comparing tree parameters...");
        
        if (this.treeMinSampleSplit != other.getTreeMinSampleSplit()) {
            System.out.println("Different min sample split: " + this.treeMinSampleSplit + " vs " + other.getTreeMinSampleSplit());
            return false;
        } else {
            System.out.println("Min sample split is the same: " + this.treeMinSampleSplit);
        }
    
        if (this.treeMaxLayer != other.getTreeMaxLayer()) {
            System.out.println("Different max layer: " + this.treeMaxLayer + " vs " + other.getTreeMaxLayer());
            return false;
        } else {
            System.out.println("Max layer is the same: " + this.treeMaxLayer);
        }
    
        // Compare the tree structure (nodes)
        System.out.println("Comparing tree structures...");
        return compareNodes(this.root, other.root);
    }        


    private boolean compareNodes(TreeNode node1, TreeNode node2) {
        // If both nodes are null, they are considered equal
        if (node1 == null && node2 == null) {
            System.out.println("Both nodes are null.");
            return true; // Both are null, so they are equal
        }
    
        // If one node is null and the other is not, they are not equal
        if (node1 == null || node2 == null) {
            System.out.println("One node is null and the other is not.");
            return false; // One is null, the other is not
        }
    
        // Compare isResultNode
        if (node1.isLeaf() != node2.isLeaf()) {
            System.out.println("Different isResultNode: " + node1.isLeaf() + " vs " + node2.isLeaf());
            return false;
        }
    
        // Compare result
        if (node1.getResult() != node2.getResult()) {
            System.out.println("Different result: " + node1.getResult() + " vs " + node2.getResult());
            return false;
        }
    
        // Compare impurity (double comparison)
        if (Double.compare(node1.getImpurity(), node2.getImpurity()) != 0) {
            System.out.println("Different impurity: " + node1.getImpurity() + " vs " + node2.getImpurity());
            return false;
        }
    
        // Compare splitFeatureName (String comparison)
        if (node1.getFeatName() == null && node2.getFeatName() != null || 
            node1.getFeatName() != null && !node1.getFeatName().equals(node2.getFeatName())) {
            System.out.println("Different splitFeatureName: " + node1.getFeatName() + " vs " + node2.getFeatName());
            return false;
        }
    
        // Recursively compare left and right children
        System.out.println("Comparing left children...");
        boolean leftCompare = compareNodes(node1.getLeft(), node2.getLeft());
        if (!leftCompare) {
            return false;
        }
    
        System.out.println("Comparing right children...");
        boolean rightCompare = compareNodes(node1.getRight(), node2.getRight());
        if (!rightCompare) {
            return false;
        }
    
        // Compare threshold (custom comparison, assuming Threshold is a class)
        if (node1.getThreshold() == null && node2.getThreshold() != null || 
            node1.getThreshold() != null && !node1.getThreshold().equals(node2.getThreshold())) {
            System.out.println("Different threshold.");
            return false;
        }
    
        // If all comparisons passed, the nodes are considered equal
        return true;
    }
    

    public Tree(int treeMinSampleSplit, int treeMaxLayer, List<HashMap<String,Object>> trainingData2){
        this.treeMinSampleSplit = 5;
        this.treeMaxLayer = 20;
        this.root = null;
        this.random = new Random(42);
        this.trainingData = trainingData2;
        //this.featureDataType = featureDataType;
    }

    public Tree(int treeMinSampleSplit, int treeMaxLayer, Random random, List<HashMap<String,Object>> trainingData){
        this.treeMinSampleSplit = treeMinSampleSplit;
        this.treeMaxLayer = treeMaxLayer;
        this.root = null;
        this.random = random;
        this.trainingData = trainingData;
        //this.featureDataType = featureDataType;

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
                    if(current.getLeft()!=null){
                        return traverse(current.getLeft(), predictionInput);
                    }else{
                        return current.getResult();
                    }
                }
            }else{
                if (valueObj.compare((double)inputValue)) {
                    if(current.getRight()!=null){
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
            //System.out.println("Leaf reached.");

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

        if(featureNum == 0){
            featureNum = this.featureDataType.size();
        }
        grow(1, initialNode, selectRandomFeatures(featureNum),trainingData,1);

        System.out.println("Tree grown complete");

        this.root = initialNode;
        //System.out.println(root);


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

        //System.out.println(root.toString());

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


    private double calculateGini(int yesCount, int noCount, int size) {
        if (size == 0) return 1.0;
        return 1 - Math.pow(((double) yesCount) / size, 2) - Math.pow(((double) noCount) / size, 2);
    }
    
    private double weightedGini(double leftGini, double rightGini, int leftSize, int rightSize, int dataSize){
        return(leftSize/ (double) dataSize) * leftGini + (rightSize / (double) dataSize) * rightGini;

    }
    public SplitResult findBestThreshold(int layer, TreeNode currentNode, HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> remainingTrainingData, double inputImpurity){
        // Initialize the best impurity, best threshold, and best splits
        double minImpurity = 1;
        String bestType = null;

        HashMap<String, Threshold> min = new HashMap<>();
        boolean featureTypeIsNum = false;


        List<HashMap<String, Object>> trainingDataLeft_final = new ArrayList<>();
        List<HashMap<String, Object>> trainingDataRight_final = new ArrayList<>();
        boolean isLeftNull=true, isRightNull=true;
        double leftImpurity=1, rightImpurity=1;


     
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
            System.out.println("featyre:"+feature);

            System.out.println("dataSize:"+dataSize);
            //System.out.println("Remaining training:"+remainingTrainingData);


            List<HashMap<String, Object>> trainingDataLeft = new ArrayList<>();
            List<HashMap<String, Object>> trainingDataRight = new ArrayList<>();
        
            // Calculate Gini Impurity for both sides (Left and Right)
            int leftYes = 0, leftNo = 0, rightYes = 0, rightNo = 0;
            //speical handling needed if any side is empty                

            double lImpurity =1, rImpurity = 1;

            Object compareTo = null;

                //////////////////////////
            if (type.equals("String")) {
                //System.out.println("***finding the best for String feature:***"+feature);

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
                            //System.out.println("Comapred to " + temp.getValue());
                            //System.out.println("Right got a yes");

                        }else{
                            rightNo++;
                            //System.out.println("Comapred to " + temp.getValue());
                            //System.out.println("Right got a No");


                        }
                    } else {
                        isLeftNull_temp = false; //TODO: this is actually not needed, it could be fone with numerical comprate of left counts
                        //calculate the right branch's impurity
                        trainingDataLeft.add(point);
                        if (point.get("y").equals("yes")) {
                            leftYes++;
                            //System.out.println("Comapred to " + temp.getValue());
                           //System.out.println("left got a yes");

                        }else{
                            leftNo++;
                            //System.out.println("Comapred to " + temp.getValue());

                            //System.out.println("left got a no");

                        }
                    }
                }

                //System.out.println("Size of left data:"+trainingDataLeft.size());
                //System.out.println("Size of right data:"+trainingDataRight.size());

                // Calculate Gini Impurity for both splits
                double leftGini;
                double rightGini;

                //System.out.println("~NULL Left :" + isLeftNull_temp);
                //System.out.println("~NULL Right : " + isRightNull_temp);

                leftGini = calculateGini(leftYes,leftNo,trainingDataLeft.size());
                //1 - Math.pow( ((double)leftYes )/ trainingDataLeft.size(), 2) - Math.pow(((double) leftNo) / trainingDataLeft.size(), 2);
            
                rightGini = calculateGini(rightYes, rightNo, trainingDataRight.size());
                //1 - Math.pow((double) rightYes / trainingDataRight.size(), 2) - Math.pow((double) rightNo / trainingDataRight.size(), 2);

                lImpurity = leftGini;
                rImpurity = rightGini;

                //System.out.println("Left Gini" + leftGini);
                //System.out.println("Right Gini" + rightGini);

                // Weighted Gini Impurity for the split
                currentImpurity = weightedGini(leftGini, rightGini,trainingDataLeft.size(),trainingDataRight.size(), dataSize);
                //(trainingDataLeft.size() / (double) dataSize) * leftGini + (trainingDataRight.size() / (double) dataSize) * rightGini;
                
                //System.out.println("Weighted Impurity: "+ currentImpurity+"****");
                //System.out.println("current min Impurity: "+ minImpurity+"****");


            } else {
                // Handling numerical features (non-String type)
                // Generate random samples to check for potential splits
                int testNum = Math.max((int) Math.min(10.0, Math.sqrt(dataSize)),1);
                

                HashMap<String, Threshold> min_local = new HashMap<>();
                double localMinImpurity_best = 1;
                //System.out.println("***finding the best for Numerical feature:***"+feature);


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
                    //System.out.println("CompareTo for numerical: "+ comparedNum);


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
                                //System.out.println("Comapred to " + point.get(feature));

                                //System.out.println("Right got a yes");
                            }else{
                                local_rightNo++;
                                //System.out.println("Comapred to " + point.get(feature));
                                //System.out.println("Right got a no");

                            }
                        } else {
                            isNullLeft_local = false;
                            trainingDataLeft_local.add(point);
                            if (point.get("y").equals("yes")) {
                                local_leftYes++;
                                //System.out.println("Comapred to " + point.get(feature));
                                //System.out.println("Left got a yes");

                            }else{
                                local_leftNo++;
                                //System.out.println("Comapred to " + point.get(feature));
                                //System.out.println("Left got a no");

                            }
                        }
                    }
                    //System.out.println("Size of Right data:"+trainingDataRight_local.size());

                    //System.out.println("Size of left data:"+trainingDataLeft_local.size());

                    // Calculate Gini Impurity for both splits
                    double leftGini;
                    double rightGini;

                    leftGini = calculateGini(local_leftYes, local_leftNo, trainingDataLeft_local.size());
                    rightGini = calculateGini(local_rightYes, local_rightNo, trainingDataRight_local.size());
                    // 1 - Math.pow((double) local_rightYes / trainingDataRight_local.size(), 2) - Math.pow((double) local_rightNo / trainingDataRight_local.size(), 2);



                    //System.out.println("Left Gini" + leftGini);
                    //System.out.println("Right Gini" + rightGini);


                    // Weighted Gini Impurity for this test split
                    double testImpurity = weightedGini(leftGini, rightGini, trainingDataLeft_local.size(), trainingDataRight_local.size(), dataSize);
                    // (trainingDataLeft_local.size() / (double) dataSize) * leftGini + (trainingDataRight_local.size() / (double) dataSize) * rightGini;
                    //System.out.println("Weighted Impurity: "+ testImpurity+"****");
                    //System.out.println("current min Impurity: "+ localMinImpurity_best+"****");


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
        String featureName = min.keySet().stream().findFirst().orElse(null);

        Threshold obj = min.get(featureName);

        remainingFeatures.remove(featureName);
        SplitResult result = new SplitResult(minImpurity, featureTypeIsNum, featureName,obj,remainingFeatures, trainingDataLeft_final,trainingDataRight_final,leftImpurity,rightImpurity,isLeftNull, isRightNull);

        return result;
    }

    public void grow(int layer, TreeNode currentNode, HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> remainingTrainingData, double inputImpurity) {        
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

            //System.out.println("result node.:impurity input"+inputImpurity);
        }else{

            SplitResult splitResult = findBestThreshold(layer, currentNode, remainingFeatures, remainingTrainingData, inputImpurity);

            //define the node here
            currentNode.setImpurity(splitResult.getMinImpurity());

            currentNode.setFeatName(splitResult.getFeatureName(), splitResult.isFeatureTypeIsNum());
            currentNode.setThreshold(splitResult.getThreshold());
            // Recursively set the left and right children of the current node 
            //System.out.println(remainingFeatures);

            if(!splitResult.isLeftNull()){
                TreeNode leftChild = new TreeNode();        
                currentNode.setLeft(leftChild);
                grow(layer+1, leftChild, remainingFeatures, splitResult.getTrainingDataLeft(), splitResult.getLeftImpurity());
                //System.out.println("leftChild grown for" );
                
            }else{
                currentNode.setLeft(null);
            }
            if(!splitResult.isRightNull()){
                TreeNode rightChild = new TreeNode();        
                currentNode.setRight(rightChild);
                grow(layer+1, rightChild, remainingFeatures, splitResult.getTrainingDataRight(), splitResult.getRightImpurity());
                //System.out.println("rightChild grown: " );
            }else{
                currentNode.setLeft(null);
            }
        }
        
    }
}
