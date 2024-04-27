package bg.sofia.uni.fmi.mjt.splitwise.command;

import bg.sofia.uni.fmi.mjt.splitwise.SplitWise;
import bg.sofia.uni.fmi.mjt.splitwise.exception.GroupAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.InvalidLoginDataException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchFriendException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.NoSuchUserInGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserAlreadyExitsException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.UserNotLoggedInException;
import bg.sofia.uni.fmi.mjt.splitwise.exception.logger.ExceptionLogger;
import bg.sofia.uni.fmi.mjt.splitwise.validations.CastToDoubleValidation;
import bg.sofia.uni.fmi.mjt.splitwise.validations.CountOfArgumentsValidation;

import java.nio.channels.SocketChannel;

public class CommandExecutor {
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String ADD_FRIEND = "add-friend";
    private static final String CREATE_GROUP = "create-group";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String GET_STATUS = "get-status";
    private static final String PAYED = "payed";
    private static final String PAYED_IN_GROUP = "payed-in-group";
    private static final String HELP = "HELP";
    private static final String HISTORY = "history";
    private static final String LOG_OUT = "logout";
    private static final String ERROR = "Error: ";
    private static final String DISCONNECT = "quit";
    private static final String UNKNOWN_COMMAND = "Unknown command";
    private static final String INCORRECT_COMMAND_FORMAT = "Incorrect command format";
    private final SplitWise splitWise;

    public CommandExecutor(SplitWise splitWise) {
        this.splitWise = splitWise;
    }

    public String execute(SocketChannel key, Command cmd) {
        try {
            if (!splitWise.getUsers().containsKey(key)) {
                return handleUnregisteredUserCommand(key, cmd);
            } else {
                return handleRegisteredUserCommand(key, cmd);
            }
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            return  ERROR + e.getMessage();
        }
    }

    private String handleUnregisteredUserCommand(SocketChannel key, Command cmd) {
        try {
            if (!splitWise.getUsers().containsKey(key)) {
                return switch (cmd.command()) {
                    case REGISTER -> register(key, cmd.arguments());
                    case LOGIN -> login(key, cmd.arguments());
                    case DISCONNECT -> saveInformation(key);
                    case HELP -> help();
                    default -> UNKNOWN_COMMAND;
                };
            } else {
                throw new IllegalStateException("User already registered");
            }
        } catch (InvalidLoginDataException | UserAlreadyExitsException e) {
            return ERROR + e.getMessage();
        }
    }

    private String handleRegisteredUserCommand(SocketChannel key, Command cmd) {
        try {
            if (splitWise.getUsers().containsKey(key)) {
                return switch (cmd.command()) {
                    case ADD_FRIEND -> addFriend(key, cmd.arguments());
                    case CREATE_GROUP -> createGroup(key, cmd.arguments());
                    case SPLIT -> split(key, cmd.arguments());
                    case SPLIT_GROUP -> splitGroup(key, cmd.arguments());
                    case GET_STATUS -> getStatus(key);
                    case PAYED -> payed(key, cmd.arguments());
                    case PAYED_IN_GROUP -> payedGroup(key, cmd.arguments());
                    case LOG_OUT -> logout(key);
                    case HISTORY -> printHistory(key);
                    case HELP -> help();
                    case DISCONNECT -> saveInformation(key);
                    default -> UNKNOWN_COMMAND;
                };
            } else {
                throw new IllegalStateException("User not registered");
            }
        } catch (NoSuchFriendException | NoSuchGroupException | NoSuchUserInGroupException |
                 UserDoesNotExistException | UserNotLoggedInException | GroupAlreadyExistsException e) {
            return ERROR + e.getMessage();
        }
    }

    private String printHistory(SocketChannel key) throws UserNotLoggedInException {
        return splitWise.printHistory(key);
    }

    public String saveInformation(SocketChannel key) {
        splitWise.exit(key);
        return "Disconnect successfully";
    }

    private String register(SocketChannel key, String[] args) throws UserAlreadyExitsException {
        if (CountOfArgumentsValidation.checkForTwo(args)) {
            splitWise.register(key, args[0], args[1]);
            return String.format("User with username %s registered", args[0]);
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String login(SocketChannel key, String[] args) throws InvalidLoginDataException {
        if (CountOfArgumentsValidation.checkForTwo(args)) {
            String answer = splitWise.login(key, args[0], args[1]);
            return "Successfully logged in!\n"  + answer;
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String addFriend(SocketChannel key, String[] args)
            throws UserDoesNotExistException, UserNotLoggedInException {
        if (CountOfArgumentsValidation.checkForOne(args)) {
            splitWise.addFriend(key, splitWise.getUser(args[0]));
            return String.format("%s is successfully added as a friend!", args[0]);
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String createGroup(SocketChannel key, String[] args) throws UserNotLoggedInException,
            UserDoesNotExistException, GroupAlreadyExistsException {
        if (CountOfArgumentsValidation.checkForTwo(args)) {
            String[] users = new String[args.length - 1];
            System.arraycopy(args, 1, users, 0, users.length);
            splitWise.createGroup(key, args[0], users);
            return String.format("The group with name %s is successfully created", args[0]);
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String split(SocketChannel key, String[] args)
            throws UserNotLoggedInException,
            NoSuchFriendException, UserDoesNotExistException {
        if (CountOfArgumentsValidation.checkForThree(args) && CastToDoubleValidation.check(args)) {
            String reason = createReason(args);
            splitWise.splitWithFriend(key, args[1], Double.parseDouble(args[0]), reason);
            return String.format("Successfully split money with %s", args[1]);
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String splitGroup(SocketChannel key, String[] args)
            throws UserNotLoggedInException, NoSuchGroupException, NoSuchUserInGroupException {
        if (CountOfArgumentsValidation.checkForThree(args) && CastToDoubleValidation.check(args)) {
            String reason = createReason(args);
            splitWise.splitWithGroup(key, args[1], Double.parseDouble(args[0]), reason);
            return String.format("Successfully split with group %s", args[1]);
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String createReason(String[] args) {
        StringBuilder reason = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            reason.append(args[i]).append(" ");
        }
        reason.deleteCharAt(reason.length() - 1);
        return reason.toString();
    }

    private String getStatus(SocketChannel key) throws UserNotLoggedInException {
        return splitWise.getStatus(key);
    }

    private String payed(SocketChannel key, String[] args) throws UserNotLoggedInException,
            NoSuchFriendException, UserDoesNotExistException {
        if (CountOfArgumentsValidation.checkForTwo(args) && CastToDoubleValidation.check(args)) {
            splitWise.payed(key, args[1], Double.parseDouble(args[0]));
            return String.format("%s has payed successfully", args[1]);
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String payedGroup(SocketChannel key, String[] args)
            throws UserNotLoggedInException, NoSuchGroupException,
            NoSuchUserInGroupException, UserDoesNotExistException {
        if (CountOfArgumentsValidation.checkForThree(args) && CastToDoubleValidation.check(args)) {
            splitWise.payedInGroup(key, args[1], args[2], Double.parseDouble(args[0]));
            return String.format("%s has payed successfully in the group", args[2]);
        } else {
            return INCORRECT_COMMAND_FORMAT;
        }
    }

    private String logout(SocketChannel key) {
        splitWise.logout(key);
        return "Successfully logged out";
    }

    private String help() {
        return "register <username> <password>" + System.lineSeparator() +
                "login <username> <password" + System.lineSeparator() +
                "add-friend <username>" + System.lineSeparator() +
                "create-group <groupname> <username> <username> ... <username>" + System.lineSeparator() +
                "split <amount> <username> <reason>" + System.lineSeparator() +
                "split-group <amount> <groupname> <username>" + System.lineSeparator() +
                "get-status" + System.lineSeparator() +
                "payed <amount> <username>" + System.lineSeparator() +
                "payed-in-group <amount> <groupname> <username>" + System.lineSeparator() +
                "logout" + System.lineSeparator() +
                "history" + System.lineSeparator() +
                "enter quit to disconnect from the server" + System.lineSeparator();
    }

    public void saveAll() {
        splitWise.save();
    }
}
