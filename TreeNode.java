public class TreeNode {


    private boolean isResultNode;
    private boolean result;

    private float impurity;
    private String splitFeatureName;
    private boolean isNumFeature;
    private TreeNode left = null;
    private TreeNode right = null;
    private Threshold threshold;

    public TreeNode() {
        
    }//constructor

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean r) {
        result = r;
    }

    public boolean isLeaf() {
        return isResultNode;
    }

    public float getPurity(){
        return impurity;
    }

    public void setPurity(float imp) {
        impurity = imp;
    }

    public String getFeatName() {
        return splitFeatureName;
    }

    public void setFeatName(String s, boolean isNum) {
        splitFeatureName = s;
        isNumFeature = isNum;
    }

    public TreeNode getRight(){
        return right;
    }

    public void setRight(TreeNode r) {
        right = r;
    }

    public TreeNode getLeft(){
        return left;
    }

    public void setLeft(TreeNode l) {
        left = l;
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold t) {
        threshold = t;
    }

}