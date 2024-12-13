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
        /*dataTypeMap.put("default", "String");     // Default is a binary feature (Boolean)
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
        RandomForest forest = new RandomForest(data,42);

        String trainingSetPath = "data/fake2.csv";
        
        data.preprocessData(trainingSetPath, false);
        try{
            knn.train();
            knn.saveModel("knn.bin");
            data.getTrainingData().addAll(knn.imputeMissingTrainingValues(data.gettrainingDataWithMissing()));
            System.out.println(data.getTrainingData());
            System.out.println(data.getFeatureAfterTrain()); //TODO: to enahnce the overall structure, this can be map the new variable name back to the original by sdtoring it part of the value.
            System.out.println("RandomForest-------------");
            System.out.println(forest.getRandomTrainingSubset(2,2));

            Tree treeInstance = new Tree(2,3, new Random(42), data.getTrainingData());
            treeInstance.setDatatype(data.getFeatureAfterTrain());



            HashMap<String,String> selectedFeatures = new HashMap<>();
            selectedFeatures.put("job_technician", "String");
            selectedFeatures.put("balance", "Double");
            selectedFeatures.put("age", "Integer");

            treeInstance.growTree(3);

            //treeInstance.growTree(selectedFeatures); //TODO: This is probably needed for Test cases to run
            //treeInstance.growTree(3);

            System.out.println("Tree-------------");
            treeInstance.printTree();

            HashMap<String, Object> predictInput = new HashMap<>();
        
            predictInput.put("ID", 1);
            predictInput.put("age", 29);
            predictInput.put("job", "cashier");
            predictInput.put("marital", "single");
            predictInput.put("education", "tertiary");
            predictInput.put("y", "no");

            predictInput= knn.oneHotkeyEncodingForSingle(data.preprocessSingleData(predictInput));            
            
            System.out.printf("%s: %b\n",predictInput.toString(),treeInstance.predictPreorderTraversal(predictInput));
            


            //new UserView();

        }catch(Exception e){
            System.out.println(" Main: something failed.");
            e.printStackTrace(); // Prints the exception and the call stack
        }

        System.out.println("Test file and test label size matched: "+data.testMatchTestLabel("data/test.csv", "data/test_label.csv"));
        
    }
        //System.out.println(data.gettrainingDataWithMissing());
}

