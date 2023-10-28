/// Created at: 25/10/2023
/// Last modification: 25/10/2023


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * This class will parse the input command and divide it into commandName and args
 */
public class Parser {
    private String commandName;
    private String[] args;


    /**
     * This method will divide the input into commandName and args
     *
     * @param input is the string command entered by the user
     * @return true if the command is valid and false otherwise
     */
    public boolean parse(String input) {
        String[] inputArray = input.split(" ");
        commandName = inputArray[0];
        try {
            Commands.valueOf(commandName);
            args = new String[inputArray.length - 1];
            for (int i = 1; i < inputArray.length; i++) {
                args[i - 1] = inputArray[i];
            }

        } catch (IllegalArgumentException ex) {
            System.out.println("Invalid command!");
            return false;
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}
