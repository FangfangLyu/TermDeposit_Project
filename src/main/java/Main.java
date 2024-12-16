import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.termdeposit.model.DataContainer;
import com.termdeposit.model.RandomForest;
import com.termdeposit.model.Tree;
import com.termdeposit.model.Validation;
import com.termdeposit.view.UserView;



public class Main {

    public static void main(String[] args) {
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
        //RandomForest forest = new RandomForest(data,42,1,5,10,5);
        String trainingSetPath = "data/train.csv";

        //String trainingSetPath = "data/train.csv";
        
        try{
            data.preprocessData(trainingSetPath, false);
            knn.train();
            knn.saveModel("knn.bin");
            data.getTrainingData().addAll(knn.imputeMissingValues(data.gettrainingDataWithMissing(),false));
            System.out.println(data.getTrainingData());
            System.out.println(data.getFeatureAfterTrain()); //TODO: to enahnce the overall structure, this can be map the new variable name back to the original by sdtoring it part of the value.
            System.out.println("RandomForest-------------");

            /*Tree treeInstance = new Tree(2,5, new Random(42), data.getTrainingData());
            treeInstance.setDatatype(data.getFeatureAfterTrain());



            HashMap<String,String> selectedFeatures = new HashMap<>();
            selectedFeatures.put("job_technician", "String");
            selectedFeatures.put("age", "Integer");
            selectedFeatures.put("balance", "Double");

            //treeInstance.growTree(3);

            System.out.println(treeInstance.growTree(selectedFeatures)); //TODO: This is probably needed for Test cases to run
            //treeInstance.growTree(selectedFeatures);

            System.out.println("Tree-------------");
            //treeInstance.printTree();
            */

            RandomForest forest = new RandomForest(data,42,200,5,5,0);

            //HashMap<String, Object> inputData = new HashMap<>();
            /* 
            inputData.put("ID", "1");
            inputData.put("age", 42);
            inputData.put("job", "technician");
            inputData.put("marital", "single");
            inputData.put("education", "primary");
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
            inputData.put("y", "yes");*/
    
            //predictInput.put("y", "no");

            //inputData= knn.oneHotkeyEncodingForSingle(data.preprocessSingleData(inputData));            
            
            //System.out.printf("%s: %b\n",inputData.toString(),treeInstance.predictPreorderTraversal(inputData));
            
            //forest.getRandomTrainingSubset();
            /*Tree treeInstance = new Tree(2,5, new Random(42), data.getTrainingData());
            treeInstance.setDatatype(data.getFeatureAfterTrain());

            System.out.println(treeInstance.growTree(3));
//            System.out.println(treeInstance.predictPreorderTraversal(inputData));*/
            
            forest.growTreeForest();
            //System.out.printf("%s: %b\n",inputData.toString(), forest.randomForestPrediction(inputData));

            //forest.randomForestPrediction(inputData);
            System.out.println("*********Accuracy test in sample:");
            
            Validation tester = new Validation(data, knn, forest); 
            tester.getInSampleAccuracy();

            //forest.randomForestPrediction(inputData);
            System.out.println("**********Accuracy test out sample:");
            
            tester.getTestAccuracy("data/test.csv", "data/test_label.csv");

            //new UserView();

        }catch(Exception e){
            System.out.println(" Main: something failed.");
            e.printStackTrace(); // Prints the exception and the call stack
        }

        System.out.println("Test file and test label size matched: " + data.testMatchTestLabel("data/test.csv", "data/test_label.csv"));
        
    }
        //System.out.println(data.gettrainingDataWithMissing());
}

