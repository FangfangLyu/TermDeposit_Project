package com.termdeposit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class RandomForest {
    private List<Tree> forest;
    private int treeFeatureSelectCount; 
    //I will temporarily remove the feature selection for diversity enhancement, becasue currently the categorical data is separated into separate columns.
    private int treeNum;
    private int treeSubsetSize;
    private String forestOutputPath;
    private DataContainer dataContainer; // Reference to DataManager object
    private Random random; // Initialize Random with a seed

    List<List<HashMap<String, Object>>> randomSubsets;



    // Constructor
    public RandomForest(DataContainer dataContainer, int randomSeed) {
        this.dataContainer = dataContainer; // Set the reference to DataManager
        //this.treeFeatureSelectCount = treeFeatureCount;
        this.random = new Random(randomSeed); // Initialize Random with a seed

    }

    // Get random training subset
    public List<List<HashMap<String, Object>>> getRandomTrainingSubset( int treeNum, int treeSubsetSize) {
        this.treeNum = treeNum;
        this.treeSubsetSize = treeSubsetSize;

        // Implement logic to get the random training subset
        randomSubsets = new ArrayList<>();
        List<HashMap<String, Object>>  trainingData = this.dataContainer.getTrainingData(); // Get the training data

        for (int i = 0; i < treeNum; i++) {
            List<HashMap<String, Object>> shuffledData = new ArrayList<>(trainingData);
            Collections.shuffle(shuffledData, random);

            // Take the first `treeSubsetSize` elements to create the subset
            List<HashMap<String, Object>> subset = shuffledData.subList(0, Math.min(treeSubsetSize, shuffledData.size()));

            // Add the subset to the list of random subsets
            randomSubsets.add(new ArrayList<>(subset));
        }

        return this.randomSubsets; // Placeholder
    }
    

    /*  TODO: Random feature selection subset 
    (not applicable, but it might be a good thing to include in future 
    when trainingData is converted back from one hot encoding to numbers and Strings)
    due to time constraint at this points, I decide to leave it out of our way first.
    */
        private List randomFeatureSelectionSubset(int featureSize) {
        // Implement logic to select random features
        return null; // Placeholder
    }

    // Grow a single tree by calling this method, which this method will activate the  corresponding method in the Tree class
    public Tree growTreeInitial(List<List<HashMap<String, Object>>> trainingData) {
        // Implement logic to grow an initial tree
        Tree tree = new Tree();
        tree.setDatatype(this.dataContainer.getFeatureAfterTrain());
        tree.growTree(trainingData);
        return tree;
    }

    // Intiates to grow all trees by calling this method, which this underying method will activate the corresponding method in the Tree class
    public void growTreeForest() {

        for (List<HashMap<String, Object>> subset : randomSubsets) {
            // Grow a tree for each subset
            Tree tree = growTreeInitial(subset);
            forest.add(tree);  // Add the tree to the forest
        }

    }

    public boolean randomForestPrediction(HashMap<String,Object> onehot_input){
        List<Boolean> results = new ArrayList();
        for(Tree tree : forest){
            boolean predict = tree.predictPreorderTraversal(onehot_input);
            results.add(predict);
        }

        // Majority voting - the class predicted by the most trees
        int trueVotes = 0;
        for (boolean prediction : results) {
            if (prediction) {
                trueVotes++;
            }
            }

        // If the majority of trees predict true, return true, otherwise false
        return trueVotes > results.size() / 2;
    }

    //TODO: we could include store trees and future reference and to avoid retraining the tree
    //But since we currently have it in memory, let's just use it for now.


}
