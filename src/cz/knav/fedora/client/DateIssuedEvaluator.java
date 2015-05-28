package cz.knav.fedora.client;

public class DateIssuedEvaluator extends DateAuthorEvaluator {
    
    public static boolean isDateIssuedOk(String date, int years) {
        return (new DateIssuedEvaluator(date, years)).isDateOk();
    }
    
    protected int getIncrementForYearOfBirth() {
        return 0;
    }
    
    protected boolean inspectOnlyOneYear() {
        return evaluateYear(false, yearLast);
    }

    private DateIssuedEvaluator(String date, int years) {
        super(date, years);
    }

}










