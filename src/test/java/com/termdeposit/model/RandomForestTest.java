package com.termdeposit.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

public class RandomForestTest {
    @Test
    public void generateDifferentTrees() throws Exception{
        //test to see if two trees with different random data will /will not generate different set

        HashMap<String, String> dataTypeMap = new HashMap<>();

        // Populating the map with column names and their corresponding data types
        dataTypeMap.put("ID", "String");           
        dataTypeMap.put("age", "Integer");         // Age is a numerical value
        dataTypeMap.put("job", "String");          // Job is a categorical feature (String)
        dataTypeMap.put("marital", "String");      // Marital status is a categorical feature (String)
        dataTypeMap.put("education", "String");    // Education level is a categorical feature (String)
        dataTypeMap.put("default", "String");     // Default is a binary feature (Boolean)
        dataTypeMap.put("balance", "Double");       // Balance is a continuous numerical value (Float)
        dataTypeMap.put("housing", "String");     // Housing is a binary feature (Boolean)
        dataTypeMap.put("loan", "String");        // Loan is a binary feature (Boolean)
        dataTypeMap.put("contact", "String");      // Contact type is a categorical feature (String)
        dataTypeMap.put("day", "Integer");         // Day of the month is a numerical value (Integer)
        dataTypeMap.put("month", "String");        // Month is a categorical feature (String)
        dataTypeMap.put("campaign", "Integer");    // Campaign is a numerical value (Integer)
        dataTypeMap.put("pdays", "Integer");       // Pdays is a numerical value (Integer)
        dataTypeMap.put("previous", "Integer");    // Previous is a numerical value (Integer)
        dataTypeMap.put("poutcome", "String");     // Poutcome is a categorical feature (String)*/
        dataTypeMap.put("y", "String");            // Target variable (y) is typically a String or Boolean

        DataContainer data = new DataContainer(dataTypeMap);
        //DataContainer.KNN knn = data.new KNN();
        Tree tree = new Tree(2,5,data.getTrainingData()); //reference passed
        RandomForest forest = new RandomForest(data,42,2,2,5,3);

        String trainingSetPath = "test/forestTest1.csv";
        
        data.preprocessData(trainingSetPath, false,true);
        
        List<Tree> trees = forest.growTreeForest();
        System.out.println("0iei12\n"+trees);
        
        boolean result = trees.get(0).equals(trees.get(1));            


        assertTrue(!result); //if not true
        
        //treeInstance.growTree(3);
            // public SplitResult findBestThreshold(int layer, TreeNode currentNode, HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> remainingTrainingData, double inputImpurity){


    }

}
