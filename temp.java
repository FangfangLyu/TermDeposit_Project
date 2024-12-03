public class Temp {

    // Method to calculate compound interest
    public static CompoundInterestResult calculateCompoundInterest(double principal, double annualRate, int timeInYears) throws IllegalArgumentException {
        if (principal <= 0 || annualRate < 0 || timeInYears < 0) {
            throw new IllegalArgumentException("Inputs must be positive values.");
        }

        // Convert annualRate to a monthly rate
        double monthlyRate = annualRate / 12;
        int compoundingFrequency = 12; // 12 times a year (monthly)

        // Calculate compound interest
        double maturityAmount = principal * Math.pow(1 + monthlyRate, compoundingFrequency * timeInYears);
        double compoundInterest = maturityAmount - principal;

        return new CompoundInterestResult(principal, annualRate, timeInYears, compoundInterest, maturityAmount);
    }

    public static void main(String[] args) {
        try {
            CompoundInterestResult result = calculateCompoundInterest(5000, 0.045, 1);
            System.out.println(result);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }
}

// Helper class to represent the result
class CompoundInterestResult {
    double principal;
    double annualRate;
    int timeInYears;
    double compoundInterest;
    double maturityAmount;

    public CompoundInterestResult(double principal, double annualRate, int timeInYears, double compoundInterest, double maturityAmount) {
        this.principal = principal;
        this.annualRate = annualRate;
        this.timeInYears = timeInYears;
        this.compoundInterest = compoundInterest;
        this.maturityAmount = maturityAmount;
    }

    @Override
    public String toString() {
        return "Principal: " + principal +
               ", Annual Rate: " + annualRate +
               ", Time (Years): " + timeInYears +
               ", Compound Interest: " + compoundInterest +
               ", Maturity Amount: " + maturityAmount;
    }
}
