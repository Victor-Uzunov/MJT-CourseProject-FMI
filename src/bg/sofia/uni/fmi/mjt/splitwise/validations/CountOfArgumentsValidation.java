package bg.sofia.uni.fmi.mjt.splitwise.validations;

public class CountOfArgumentsValidation  {

    private static final int ONE_ARGUMENT = 1;
    private static final int TWO_ARGUMENTS = 2;
    private static final int TREE_ARGUMENTS = 3;

    public static boolean checkForTwo(String[] args) {
        return args.length >= TWO_ARGUMENTS;
    }

    public static boolean checkForThree(String[] args) {
        return args.length >= TREE_ARGUMENTS;
    }

    public static boolean checkForOne(String[] args) {
        return args.length >= ONE_ARGUMENT;
    }
}
