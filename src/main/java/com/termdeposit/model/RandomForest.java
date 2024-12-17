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
    private HashMap<String,String> featureAfterTrain;
    public DataContainer dataContainer; // Reference to DataManager object

    private String forestOutputPath;
    private Random random; // Initialize Random with a seed

    List<List<HashMap<String, Object>>> randomSubsets;

    private void defineFeature(){
        HashMap<String, String> dataTypeMap = new HashMap<>();

        dataTypeMap.put("y", "String");
        dataTypeMap.put("job_technician", "String");
        dataTypeMap.put("job_services", "String");
        dataTypeMap.put("job_management", "String");
        dataTypeMap.put("job_admin.", "String");
        dataTypeMap.put("job_student", "String");
        dataTypeMap.put("job_blue-collar", "String");
        dataTypeMap.put("job_housemaid", "String");
        dataTypeMap.put("job_retired", "String");
        dataTypeMap.put("job_unemployed", "String");
        dataTypeMap.put("job_self-employed", "String");
        dataTypeMap.put("job_unknown", "String");
        dataTypeMap.put("job_entrepreneur", "String");
        dataTypeMap.put("marital_single", "String");
        dataTypeMap.put("marital_married", "String");
        dataTypeMap.put("marital_divorced", "String");
        dataTypeMap.put("education_tertiary", "String");
        dataTypeMap.put("education_secondary", "String");
        dataTypeMap.put("education_unknown", "String");
        dataTypeMap.put("education_primary", "String");
        dataTypeMap.put("default_no", "String");
        dataTypeMap.put("default_yes", "String");
        dataTypeMap.put("housing_no", "String");
        dataTypeMap.put("housing_yes", "String");
        dataTypeMap.put("loan_no", "String");
        dataTypeMap.put("loan_yes", "String");
        dataTypeMap.put("contact_cellular", "String");
        dataTypeMap.put("contact_unknown", "String");
        dataTypeMap.put("contact_telephone", "String");
        dataTypeMap.put("month_may", "String");
        dataTypeMap.put("month_jun", "String");
        dataTypeMap.put("month_aug", "String");
        dataTypeMap.put("month_jul", "String");
        dataTypeMap.put("month_sep", "String");
        dataTypeMap.put("month_nov", "String");
        dataTypeMap.put("month_mar", "String");
        dataTypeMap.put("month_apr", "String");
        dataTypeMap.put("month_jan", "String");
        dataTypeMap.put("month_feb", "String");
        dataTypeMap.put("month_oct", "String");
        dataTypeMap.put("month_dec", "String");
        dataTypeMap.put("poutcome_unknown", "String");
        dataTypeMap.put("poutcome_failure", "String");
        dataTypeMap.put("poutcome_other", "String");
        dataTypeMap.put("poutcome_success", "String");
        
        dataTypeMap.put("ID", "String"); 

        dataTypeMap.put("age", "Integer");
        dataTypeMap.put("balance", "Double");  // balance is a continuous value, so it's a Double
        dataTypeMap.put("day", "Integer");  // day is a numerical value, so it's an Integer
        dataTypeMap.put("campaign", "Integer");  // campaign is a numerical value, so it's an Integer
        dataTypeMap.put("pdays", "Integer");  // pdays is a numerical value, so it's an Integer
        dataTypeMap.put("previous", "Integer");
        featureAfterTrain = dataTypeMap;
    }
    
    // Constructor
    public RandomForest(DataContainer dataContainer, int randomSeed, int treeNum, int minPointToSplit, int maxLayer, int featureSplitCount) {
        this.dataContainer = dataContainer; // Set the reference to DataManager
        //this.featureAfterTrain = dataContainer.getFeatureAfterTrain();

        this.treeFeatureSelectCount = dataContainer.getFeatureAfterTrain().size();
        this.random = new Random(randomSeed); // Initialize Random with a seed
        this.forest = new ArrayList<>();

        this.minPointToSplit = minPointToSplit;
        this.maxLayer = maxLayer;
        this.treeNum = treeNum;
        defineFeature();
    }

    // Get random training subset
    public List<List<HashMap<String, Object>>> getRandomTrainingSubset( ) {

        this.treeSubsetSize = Math.max(2, dataContainer.getTrainingData().size() / treeNum);

        // Ensure the subset size is at least 2
        if (treeSubsetSize < 2) {
            // Throw an exception if the subset size is less than 2
            throw new IllegalArgumentException("Tree subset size cannot be less than 2. " +
                                            "The number of trees (" + treeNum + ") is too large for the available data.");
        }
        // Implement logic to get the random training subset
        //treeSubsetSize = 100; //temp
        this.randomSubsets = new ArrayList<>();
        List<HashMap<String, Object>>  trainingData = this.dataContainer.getTrainingData(); // Get the training data

        for (int i = 0; i < treeNum; i++) {
            Random treeRandom = new Random(System.currentTimeMillis() + i*5); // Different seed per tree

            List<HashMap<String, Object>> shuffledData = new ArrayList<>(trainingData);
            Collections.shuffle(shuffledData, treeRandom);

            // Take the first `treeSubsetSize` elements to create the subset
            List<HashMap<String, Object>> subset = shuffledData.subList(0, Math.min(treeSubsetSize, shuffledData.size()));
            //printSubsetIds(subset);

            // Add the subset to the list of random subsets
            this.randomSubsets.add(new ArrayList<>(subset));
        }
                defineFeature();

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
    public Tree growTreeInitial( List<HashMap<String, Object>> trainingData) throws Exception {
        // Implement logic to grow an initial tree
        Tree tree = new Tree(this.minPointToSplit, this.maxLayer, trainingData);
        tree.setDatatype(featureAfterTrain);
        System.out.println(featureAfterTrain);
        
        
        String content = tree.growTree(treeFeatureSelectCount).toString(); //number of random features to split on
        
        /*String filename = "./tree";
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(content);
            System.out.println("Successfully wrote to the file: " + filename);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }*/
    
        if(trainingData.size()!=0 & tree == null){
            throw new NullPointerException("Error, tree generated is null. ");
        }else{
            return tree;
        }
    }

    // Intiates to grow all trees by calling this method, which this underying method will activate the corresponding method in the Tree class
    public List<Tree> growTreeForest() throws Exception {
        getRandomTrainingSubset(); //TEMP HYPERPARAMETER
        printSubsetIds();

        System.out.println("This many subsets:  "+ randomSubsets.size() + " generated.");

        System.out.println("Random subset of size "+  treeSubsetSize + " generated.");


        for (List<HashMap<String, Object>> subset : randomSubsets) {
            // Grow a tree for each subset
            Tree tree = growTreeInitial(subset);
            System.out.println(tree);
            forest.add(tree); // Add the tree to the forest
        }
        return forest;

    }

    public boolean randomForestPrediction(HashMap<String,Object> onehot_input){
        List<Boolean> results = new ArrayList<>();
        //System.out.println("Result for prediction in a single Tree: begin:");

        for(Tree tree : forest){
            boolean predict = tree.predictPreorderTraversal(onehot_input);
            //System.out.println("Result for prediction in a single Tree: " + predict);
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
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
    
        sb.append("Forest{");
        sb.append("size=").append(forest.size()).append(", ");
        sb.append("trees=[");
    
        for (Tree tree : forest) {
            sb.append(tree.toString()).append(", ");
        }
    
        // Remove the last comma and space
        if (!forest.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
    
        sb.append("]}");
    
        return sb.toString();
    }

}
