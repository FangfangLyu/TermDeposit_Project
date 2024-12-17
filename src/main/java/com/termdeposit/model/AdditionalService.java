package com.termdeposit.model;

import java.util.*;

//TODO: Amend 3rd functional requirement to this
public class AdditionalService {

    private DataContainer dataContainer; // get reference to DataContainer

    public AdditionalService(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public HashMap<String, Object> findClosestPositive() {
        double minDistance = Double.MAX_VALUE;

        HashMap<String, Object> closestPositive = null;

        // combine data into one structure to make it easier to iterate through
        List<HashMap<String, Object>> totalData = new ArrayList<>();
        totalData.addAll(dataContainer.getTrainingData());
        totalData.addAll(dataContainer.getTestingData());

        // get user input (single line)
        HashMap<String, Object> userInput = dataContainer.getImputedPredictionInput();

        // iterate through all the data row by row
        for (HashMap<String, Object> row : totalData) {
            // only calculate distance for rows where target is "yes"
            if ("yes".equalsIgnoreCase((String) row.get("y"))) {

                double distance = calculateDistance(userInput, row);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestPositive = row;
                } // if: update min
            }
        } // end-for
        return closestPositive;
    }// end-findClosestPositive

    private double calculateDistance(HashMap<String, Object> userInput, HashMap<String, Object> row) {
        double distance = 0.0;

        // iterate through the key-value pairs in HashMap
        for (String key : userInput.keySet()) {
            // Leave out target column and ID column (ID is unique identifier)
            if (!key.equals("y") || !key.equals("ID")) {
                Object userValue = userInput.get(key);
                Object dataValue = row.get(key);

                if ((userValue.equals(0.0) || userValue.equals(1.0))
                        && (dataValue.equals(0.0) || dataValue.equals(1.0))) {
                    if (!userValue.equals(dataValue)) {
                        distance += 1.0;
                    } // end-if: one hot key encoding, if not equal, just add 1
                } else if (userValue instanceof Float && dataValue instanceof Float) {
                    Float difference = (Float) userValue - (Float) dataValue;
                    distance += difference * difference; // Euclidean distance
                } // end-elseif

            } // end-if
        } // end-for
        return Math.sqrt(distance);
    }// end-calculateDistance

    public HashMap<String, Object> recordImprovements() {
        HashMap<String, Object> closestPositive = findClosestPositive();
        HashMap<String, Object> predictionInput = dataContainer.getPredictionInput();

        if (closestPositive == null)
            return null;

        HashMap<String, Object> improvements = new HashMap<>();

        for (String key : predictionInput.keySet()) {
            Object userValue = predictionInput.get(key);
            Object closestValue = closestPositive.get(key);

            if (!userValue.equals(closestValue)) {
                improvements.put(key, closestValue);
            }
        } // end-for

        return improvements;
    }// end-method recordImprovements

}// end-class AdditionalService
