package bg.sofia.uni.fmi.mjt.splitwise.validations;

public class CastToDoubleValidation  {
    public static boolean check(String[] args) {
        String number = args[0];
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
