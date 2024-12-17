package com.termdeposit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class TreeNodeTest {

    @Test
    public void TreeGrow(){
        
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
        DataContainer.KNN knn = data.new KNN();
        Tree tree = new Tree(2,5,data.getTrainingData()); //reference passed

        String trainingSetPath = "test/treeTest1.csv";
        
        try{        
            data.preprocessData(trainingSetPath, false,true);

            LinkedHashMap<String,String> selectedFeatures = new LinkedHashMap<>();
            selectedFeatures.put("job_technician", "String");
            selectedFeatures.put("balance", "Double");
            selectedFeatures.put("age", "Integer");

            //treeInstance.growTree(3);
               // public SplitResult findBestThreshold(int layer, TreeNode currentNode, HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> remainingTrainingData, double inputImpurity){

            TreeNode resultRoot = new TreeNode();
            SplitResult result = tree.findBestThreshold(1, resultRoot,selectedFeatures, data.getTrainingData(), 1);

            System.out.println(result);
            assertEquals("job_technician", result.getFeatureName());
            assertEquals("1.0", result.getThreshold().getValue().toString());


            //new UserView();

        }catch(Exception e){
            System.out.println(" Main: something failed.");
            e.printStackTrace(); // Prints the exception and the call stack
        }
    }
    
}
