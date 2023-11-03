///// @author1: Rana Essam  20210133
///// @author2 : Nour Mohamed 20210428
///// @author3 : Noor Eyad   20210499
///// Created on: 25/10/2023
///// Last modification: 3/11/2023
//
//import java.util.Arrays;
//
//
///**
// * This class will parse the input command and divide it into commandName and args
// */
//public class Parser {
//    private String commandName;
//    private String[] args;
//
//    private String fileName;
//    private String operator;
//
//
//
//    /**
//     * This method will divide the input into commandName and args
//     * and will check if the command is valid
//     * @param input is the string command entered by the user
//     * @return true if the command is valid and false otherwise
//     */
//    public boolean parse(String input) {
//        fileName = null;
//        operator = null;
//        String[] inputArray = input.split(" ");
//        commandName = inputArray[0];
//        if(commandName.equals(">") || commandName.equals(">>")) {
//          throw new IllegalArgumentException();
//        }
//        try {
//            Commands.valueOf(commandName);
//            args = new String[inputArray.length - 1];
//            for (int i = 1; i < inputArray.length; i++) {
//                args[i - 1] = inputArray[i];
//                if (inputArray [i].equals(">") || inputArray[i].equals(">>")) {
//
//                    if(i != 1)
//                        args = Arrays.copyOfRange(inputArray, 1, i);
//                    else
//                        args = new String[0];
//
//
//                    if(i == inputArray.length - 2) {
//                        fileName = inputArray[i+1];
//                        operator = inputArray[i];
//                    }
//
//                    else {
//                        throw new IllegalArgumentException();
//                    }
//                    return true;
//
//                }
//            }
//        } catch (IllegalArgumentException ex) {
//            System.out.println("Invalid command!");
//            return false;
//        }
//        return true;
//    }
//
//
//
//
//    public String getCommandName() {
//        return commandName;
//    }
//
//    public String[] getArgs() {
//        return args;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public String getOperator() {
//        return operator;
//    }
//}
