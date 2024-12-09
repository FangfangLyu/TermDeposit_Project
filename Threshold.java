public class Threshold {
    private Object value;
    private String type;

    public Threshold(Object v, String t) {
        value = v;
        if (t == "Categorical || Numerical") {
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

    public void setType(String t) {
        if (t == "Categorical || Numerical") {
            type = t;
        }
    }



}
