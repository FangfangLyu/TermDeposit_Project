package com.termdeposit.model;
public class Threshold {
    private Object value;
    private String type;

    public Threshold(Object v, String t) {
        value = v;
        if ( t.equals("String") || t.equals("Integer") || (t.equals("Double"))) {
            type = t;
        }
    }//constructor

    public Object getValue() {
        return value;
    }

    public void setValue(Object v) {
        value = v;
    }

    public String getType() {
        return type;
    }

    public boolean compare(Object featureValue){
        if ((this.type.equals("Integer") || this.type.equals("Double")) && this.value instanceof Number) {
            return subNumericGivenStrictlyGreater(featureValue); //TODO: I changed the method's name and functionality a bit, but doesn't affect the usage.
            //is the given greater or not
        } else if (type.equals("String") && value instanceof String) {
            return subCategoryEquivalent(featureValue);
        }else{
            return false;
        }

    }

    // Checks if the featureValue is equivalent for a categorical threshold
    public boolean subCategoryEquivalent(Object featureValue) {
        if (type.equals("String") && featureValue instanceof String) {
            System.out.println("String featurevalue in threadhold:"+featureValue);
            return featureValue.equals(value);
        }else if (type.equals("String") && featureValue instanceof Number) { //due to current implementation all String are expressed in double
            System.out.println("String in number featurevalue in threadhold:"+featureValue);

            double numericValue = ((Number) value).doubleValue();
            double numericValueGiven = ((Number) featureValue).doubleValue();

            return Double.toString(numericValue).equals(Double.toString(numericValueGiven));
        }
        return false;
    }

    // Checks if a numeric featureValue is greater than the threshold
    public boolean subNumericGivenStrictlyGreater(Object featureValue) {
        if ((type.equals("Integer")||type.equals("Double"))) {
            System.out.println("Number featurevalue in threadhold:"+featureValue);
            return ((Number) featureValue).doubleValue() <= ((Number) value).doubleValue();
        }
        return false;
    }


    // Converts the Threshold object to a String representation
    @Override
    public String toString() {
        return "Threshold{" +
                "value=" + value +
                ", type='" + type + '\'' +
                '}';
    }

    // Checks equality between two Threshold objects
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Threshold threshold = (Threshold) obj;
        return value.equals(threshold.value) && type.equals(threshold.type);

    }



}
