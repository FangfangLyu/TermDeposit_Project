import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.termdeposit.model.DataContainer;


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
        dataTypeMap.put("balance", "Float");       // Balance is a continuous numerical value (Float)
        dataTypeMap.put("housing", "String");     // Housing is a binary feature (Boolean)
        dataTypeMap.put("loan", "String");        // Loan is a binary feature (Boolean)
        dataTypeMap.put("contact", "String");      // Contact type is a categorical feature (String)
        dataTypeMap.put("day", "Integer");         // Day of the month is a numerical value (Integer)
        dataTypeMap.put("month", "String");        // Month is a categorical feature (String)
        dataTypeMap.put("campaign", "Integer");    // Campaign is a numerical value (Integer)
        dataTypeMap.put("pdays", "Integer");       // Pdays is a numerical value (Integer)
        dataTypeMap.put("previous", "Integer");    // Previous is a numerical value (Integer)
        dataTypeMap.put("poutcome", "String");     // Poutcome is a categorical feature (String)
        dataTypeMap.put("y", "String");            // Target variable (y) is typically a String or Boolean

        DataContainer data = new DataContainer(dataTypeMap);
        DataContainer.KNN knn = data.new KNN();

        String trainingSetPath = "data/FakeTrain.csv";
        
        data.preprocessData(trainingSetPath, false);
        try{
            knn.train();
            knn.saveModel("knn.bin");
            knn.imputeMissingValues();
        }catch(Exception e){
            System.out.println("Knn failed.");
            e.printStackTrace(); // Prints the exception and the call stack
        }
        

        //System.out.println(data.gettrainingDataWithMissing());



    }
}

