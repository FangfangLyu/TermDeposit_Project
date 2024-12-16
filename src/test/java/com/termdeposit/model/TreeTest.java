package com.termdeposit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

public class TreeTest {

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
        RandomForest forest = new RandomForest(data,42,100,5,20,5);

        String trainingSetPath = "test/treeTest1.csv";
        
        try{        
            data.preprocessData(trainingSetPath, false,true);

            Tree treeInstance = new Tree(2,3, data.getTrainingData());
            treeInstance.setDatatype(data.getFeatureAfterTrain());
            
            LinkedHashMap<String,String> selectedFeatures = new LinkedHashMap<>();
            selectedFeatures.put("job_technician", "String");
            selectedFeatures.put("balance", "Double");
            selectedFeatures.put("age", "Integer");

            treeInstance.growTree(3);
            // public SplitResult findBestThreshold(int layer, TreeNode currentNode, HashMap<String, String> remainingFeatures, List<HashMap<String, Object>> remainingTrainingData, double inputImpurity){

            System.out.println("******TEST::*****");

            //Arrange: Input data (all fields filled)
            HashMap<String, Object> inputData = new HashMap<>();
            inputData.put("ID", "1");
            inputData.put("age", 42);
            inputData.put("job", "technician");
            inputData.put("marital", "single");
            inputData.put("education", "secondary");
            inputData.put("default", "yes");
            inputData.put("balance", 10000.0);
            inputData.put("housing", "yes");
            inputData.put("loan", "yes");
            inputData.put("contact", "cellular");
            inputData.put("day", 4);
            inputData.put("month", "jun");
            inputData.put("campaign", 1);
            inputData.put("pdays", 2);
            inputData.put("previous", 14);
            inputData.put("poutcome", "unknown");

            inputData = knn.oneHotkeyEncodingForSingle(data.preprocessSingleData(inputData));            


            System.out.println("Tree-------------");

            treeInstance.printTree();
            assertTrue(true==treeInstance.predictPreorderTraversal(inputData));

            //new UserView();

        }catch(Exception e){
            System.out.println(" Main: something failed.");
            e.printStackTrace(); // Prints the exception and the call stack
        }
    }
    
}
