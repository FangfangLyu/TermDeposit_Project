package com.termdeposit.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManagerTest {
    //command to run test: mvn -e test

    HashMap<String, String> dataTypeMap = new HashMap<>();
    // Populating the map with column names and their corresponding data types
    
    public DataManagerTest() {
        dataTypeMap = new HashMap<>();
        dataTypeMap.put("ID", "String");
        dataTypeMap.put("age", "Integer");
        dataTypeMap.put("job", "String");
        dataTypeMap.put("marital", "String");
        dataTypeMap.put("education", "String");
        dataTypeMap.put("default", "String");
        dataTypeMap.put("balance", "Double");
        dataTypeMap.put("housing", "String");
        dataTypeMap.put("loan", "String");
        dataTypeMap.put("contact", "String");
        dataTypeMap.put("day", "Integer");
        dataTypeMap.put("month", "String");
        dataTypeMap.put("campaign", "Integer");
        dataTypeMap.put("pdays", "Integer");
        dataTypeMap.put("previous", "Integer");
        dataTypeMap.put("poutcome", "String");
        dataTypeMap.put("y", "String");
    }
    
    // Method to read CSV file and return the list of maps
    public List<HashMap<String, Object>> readCSVFile(String trainingSetUrl) {
        List<HashMap<String, Object>> data = new ArrayList<>(); // This will hold the parsed data

        // Read all lines from the file
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(trainingSetUrl);

        if (inputStream == null) {
            System.err.println("File not found!");
            return data;  // Return an empty list if the file is not found
        }

        List<String> lines = new ArrayList<>();
        // Wrap the InputStream in an InputStreamReader and BufferedReader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            // Read each line and add it to the list
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return data; // Return empty list in case of error
        }

        if (lines.isEmpty()) {
            return data; // Return an empty list if the file is empty
        }

        // Extract headers from the first line
        String[] headers = lines.get(0).split(",");

        // Parse each subsequent line as a row of data
        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");
            HashMap<String, Object> row = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                if (j < values.length) {
                    if (values[j].isEmpty()) {
                        row.put(headers[j], null); // Assign null if the value is empty
                    } else {
                        row.put(headers[j], values[j].trim()); // Assign the actual value if not empty
                    }
                } else {
                    row.put(headers[j], null); // If no value for the header, assign null
                }
            }
            data.add(row);
        }
        System.out.println(data);
        return data;
    }

    @Test
    public void testPreprocessWithAllFieldsFilled() throws Exception{

        DataContainer instance = new DataContainer(dataTypeMap);

        //Arrange: Input data (all fields filled)
        HashMap<String, Object> inputData = new HashMap<>();
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
        inputData.put("y", "yes");

        // Expected output (with one-hot encoding applied)
        HashMap<String, Object> expectedOutput = new HashMap<>();
        expectedOutput.put("ID", "1");
        expectedOutput.put("age", 42.0);
        expectedOutput.put("job", "technician");
        expectedOutput.put("marital", "single");
        expectedOutput.put("education", "primary");
        expectedOutput.put("default", "yes");
        expectedOutput.put("balance", 10000.0);
        expectedOutput.put("housing", "yes");
        expectedOutput.put("loan", "yes");
        expectedOutput.put("contact", "cellular");
        expectedOutput.put("day", 4.0);
        expectedOutput.put("month", "jun");
        expectedOutput.put("campaign", 1.0);
        expectedOutput.put("pdays", 2.0);
        expectedOutput.put("previous", 14.0);
        expectedOutput.put("poutcome", "unknown");
        expectedOutput.put("y", "yes");

        // Act: Apply one-hot encoding
        HashMap<String, Object> result = instance.preprocessSingleData(inputData);


        // Assert: Compare each key-value pair
        for (String key : expectedOutput.keySet()) {
            assertEquals(expectedOutput.get(key), result.get(key), "Mismatch for key: " + key);
        }
    }

    @Test
    public void testPreprocessMissing() throws Exception{
        dataTypeMap = new HashMap<>();
        dataTypeMap.put("ID", "String");
        dataTypeMap.put("age", "Integer");
        dataTypeMap.put("job", "String");
        dataTypeMap.put("marital", "String");
        dataTypeMap.put("education", "String");
        dataTypeMap.put("y","String");
        

        DataContainer instance = new DataContainer(dataTypeMap);
        DataContainer.KNN knn = instance.new KNN();
        

        //input
        instance.preprocessData("test/test_with_missing.csv",false);
        
        //expected outputs
        List<HashMap<String,Object>> expectedResult= readCSVFile("test/test_with_missing_expected.csv");


        knn.train();
        List<HashMap<String,Object>> result = knn.imputeMissingValues(instance.gettrainingDataWithMissing(),false);
        instance.addTrainingData(result); 

        assertTrue(compareListOfMapsByValue(instance.getTrainingData(), expectedResult));

    }
    @Test
    public void testPreprocessMissingNum() throws Exception{
        dataTypeMap = new HashMap<>();
        dataTypeMap.put("ID", "String");
        dataTypeMap.put("age", "Integer");
        dataTypeMap.put("job", "String");
        dataTypeMap.put("marital", "String");
        dataTypeMap.put("education", "String");
        dataTypeMap.put("y","String");
        

        DataContainer instance = new DataContainer(dataTypeMap);
        DataContainer.KNN knn = instance.new KNN();
        

        //input
        instance.preprocessData("test/test_with_missing_num.csv",false);
        
        //expected outputs
        List<HashMap<String,Object>> expectedResult= readCSVFile("test/test_with_missing_expected_num.csv");


        knn.train();
        List<HashMap<String,Object>> result = knn.imputeMissingValues(instance.gettrainingDataWithMissing(),false);
        instance.addTrainingData(result); 

        assertTrue(compareListOfMapsByValue(instance.getTrainingData(), expectedResult));

    }
    

    private boolean compareListOfMapsByValue(List<HashMap<String, Object>> list1, List<HashMap<String, Object>> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        System.out.println(list1);
        System.out.println(list2);

        // Compare each HashMap in the lists
        for (int i = 0; i < list1.size(); i++) {
            HashMap<String, Object> map1 = list1.get(i);
            HashMap<String, Object> map2 = list2.get(i);

            // Compare the HashMaps based on their contents, not memory address
            
        // Compare each entry (key-value pair) in the maps by their string representation
        for (String key : map1.keySet()) {
            Object value1 = map1.get(key);
            Object value2 = map2.get(key);

            // Convert both values to their string representation
            String strValue1 = String.valueOf(value1);
            String strValue2 = String.valueOf(value2);

            // Compare the string representations
            if (!strValue1.equals(strValue2)) {
                System.out.println("Expected value for key '" + key + "' in map2: " + strValue2);
                System.out.println("Actual value for key '" + key + "' in map1: " + strValue1);
                return false; // Return false if any value is not equal based on string representation
            }
        }
        }
        return true; // Return true if all maps are equal
    }

    
}
