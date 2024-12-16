package com.termdeposit.model;

import java.io.FileWriter;
import java.io.IOException;
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
    private int minPointToSplit;
    private int maxLayer;
    private String forestOutputPath;
    private DataContainer dataContainer; // Reference to DataManager object
    private Random random; // Initialize Random with a seed

    List<List<HashMap<String, Object>>> randomSubsets;

    

    // Constructor
    public RandomForest(DataContainer dataContainer, int randomSeed, int treeNum, int minPointToSplit, int maxLayer, int featureSplitCount) {
        this.dataContainer = dataContainer; // Set the reference to DataManager

        if(featureSplitCount != 0){
            this.treeFeatureSelectCount = featureSplitCount;
        }else{
            this.treeFeatureSelectCount = dataContainer.getFeatureAfterTrain().size();
        }
        this.random = new Random(randomSeed); // Initialize Random with a seed
        this.forest = new ArrayList<>();

        this.minPointToSplit = minPointToSplit;
        this.maxLayer = maxLayer;
        this.treeNum = treeNum;
        this.treeSubsetSize = Math.max(1, dataContainer.getTrainingData().size() / treeNum);
    }

    // Get random training subset
    public List<List<HashMap<String, Object>>> getRandomTrainingSubset( ) {

        // Implement logic to get the random training subset
        randomSubsets = new ArrayList<>();
        List<HashMap<String, Object>>  trainingData = this.dataContainer.getTrainingData(); // Get the training data

        for (int i = 0; i < treeNum; i++) {
            List<HashMap<String, Object>> shuffledData = new ArrayList<>(trainingData);
            Collections.shuffle(shuffledData, random);

            // Take the first `treeSubsetSize` elements to create the subset
            List<HashMap<String, Object>> subset = shuffledData.subList(0, Math.min(treeSubsetSize, shuffledData.size()));
            printSubsetIds(subset);

            // Add the subset to the list of random subsets
            randomSubsets.add(new ArrayList<>(subset));
        }

        return this.randomSubsets; // Placeholder
    }
    
    // Print IDs in each subset
    public void printSubsetIds() {
        List<List<HashMap<String, Object>>> subsets = randomSubsets;

        for (int i = 0; i < subsets.size(); i++) {
            System.out.println("Subset " + (i + 1) + ":");
            for (HashMap<String, Object> record : subsets.get(i)) {
                if (record.containsKey("ID")) {
                    System.out.print(record.get("ID") + " ");
                }
            }
            System.out.println(); // Line break after each subset
        }
    }
    public void printSubsetIds(List<HashMap<String, Object>> input) {

            System.out.println("Subset "+ ":");
            for (HashMap<String, Object> record : input) {
                if (record.containsKey("ID")) {
                    System.out.print(record.get("ID") + " ");
                }
            }
            System.out.println(); // Line break after each subset
    }

    // Grow a single tree by calling this method, which this method will activate the  corresponding method in the Tree class
    public Tree growTreeInitial( List<HashMap<String, Object>> trainingData) {
        // Implement logic to grow an initial tree
        Tree tree = new Tree(this.minPointToSplit, this.maxLayer, trainingData);
        tree.setDatatype(this.dataContainer.getFeatureAfterTrain());
        //tree.setDatatype(this.dataContainer.getFeatureAfterTrain());
        String content = tree.growTree(treeFeatureSelectCount).toString(); //number of random features to split on
        String filename = "./tree";
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(content);
            System.out.println("Successfully wrote to the file: " + filename);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    
        return tree;
    }

    // Intiates to grow all trees by calling this method, which this underying method will activate the corresponding method in the Tree class
    public List<Tree> growTreeForest() {

        getRandomTrainingSubset(); //TEMP HYPERPARAMETER
        System.out.println("Random subset of size "+ treeSubsetSize + " generated.");

        printSubsetIds();

        for (List<HashMap<String, Object>> subset : randomSubsets) {
            // Grow a tree for each subset
            Tree tree = growTreeInitial(subset);
            forest.add(tree); // Add the tree to the forest
        }
        return forest;

    }

    public boolean randomForestPrediction(HashMap<String,Object> onehot_input){
        List<Boolean> results = new ArrayList<>();
        System.out.println("Result for prediction in a single Tree: begin:");

        for(Tree tree : forest){
            boolean predict = tree.predictPreorderTraversal(onehot_input);
            System.out.println("Result for prediction in a single Tree: " + predict);
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
        System.out.println("Result for true in the predictions of the forest: "+ trueVotes + " / "+ results.size());

        return trueVotes > results.size() / 2;
    }

    //TODO: we could include store trees and future reference and to avoid retraining the tree
    //But since we currently have it in memory, let's just use it for now.


}
