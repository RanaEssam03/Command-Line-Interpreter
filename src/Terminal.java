/// @author1: Rana Essam  20210133
/// @author2 : Nour Mohamed 20210428
/// @author3 : Noor Eyad   20210499
/// Created on: 25/10/2023
/// Last modification: 3/11/2023

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Scanner;

public class Terminal {
    final String homeDic = System.getProperty("user.home");
    Parser parser = new Parser();

    /**
     * _______________________________________________________________________
     * This is the constructor of the class
     * it sets the current directory to the home directory
     */
    Terminal() {
        System.setProperty("user.dir", homeDic);
    }


    /**
     * _______________________________________________________________________
     * This is the main function where the terminal is running and takes input from the user
     *
     * @param args the arguments passed to the program
     */
    public static void main(String[] args) throws IOException {
        try {

            Terminal terminal = new Terminal();
            ArrayList<String> historyArray = new ArrayList<>(); // array list that saves the commands the user writes
            while (true) {
                System.out.print(System.getProperty("user.dir") + " > ");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine(); // here we get the input from the terminal
                historyArray.add(input);
                if (input.equals("exit")) // exit the program in case the user enters exit in the terminal
                    break;
                terminal.executeCommand(historyArray, input);

            }
        } catch (Exception e) {
            if (e.getMessage() != null)
                System.out.println(e.getMessage());
            else
                System.out.println("Something went wrong, try again!");
        }


    }

    /**
     * ____________________________________________________________________________
     * This method will execute the command the user entered
     *
     * @param historyArray the array list that contains the history of the commands
     * @param command      the command the user entered
     * @throws IOException in case of an error in the file
     */
    public void executeCommand(ArrayList<String> historyArray, String command) throws IOException {
        if (parser.parse(command)) {
            if (parser.getFileName() != null) {
                File file;
                File current = new File(parser.getFileName());
                if (!current.isAbsolute())
                    file = new File(System.getProperty("user.dir") + "\\\\" + parser.getFileName());
                else
                    file = current;
                try {
                    if (!file.exists() && parser.getOperator().equals(">>")) {
                        System.out.println("ERROR :  " + parser.getFileName() + " doesn't exists! ");
                        return;
                    } else if (!file.exists() && parser.getOperator().equals(">")) {
                        file.createNewFile();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
            // if the paring is done correctly then choose the suitable action else continue the program without any action after print an error message
            chooseCommandAction(historyArray);
        }

    }

    /***________________________________________________________________
     * This method will choose the suitable command method to be called
     */
    public void chooseCommandAction(ArrayList<String> historyArray) throws IOException {

        try {

            switch (parser.getCommandName()) {
                case "cat":
                    cat();
                    break;
                case "echo":
                    echo();
                    break;
                case "mkdir":
                    mkdir();
                    break;
                case "rmdir":
                    rmdir();
                    break;
                case "pwd":
                    pwd();
                    break;
                case "ls":
                    ls();
                    break;
                case "cd":
                    cd(parser.getArgs());
                    break;
                case "history":
                    history(historyArray);
                    break;
                case "rm":
                    rm();
                    break;
                case "wc":
                    wc();
                    break;
                case "cp":
                    cp();
                    break;
                case "touch":
                    touch();
                    break;
                default:
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This method will print the current directory
     */
    public void pwd() throws IOException {
        if (parser.getArgs().length != 0) {
            System.out.println("Invalid number of arguments, expected 0 arguments");
            return;
        }
        String currentDic = System.getProperty("user.dir");

        if (parser.getFileName() == null)
            System.out.println(currentDic);
        else
            writeToFile(currentDic);

    }

    /**
     * _________________________________________________________________________
     * This method will print the files and directories in the current directory in alphabetical order or reverse order
     */
    public void ls() throws IOException {
        boolean reverse = false;
        if (parser.getArgs().length > 1) {
            System.out.println("Invalid number of arguments, expected 0 arguments");
            return;
        } else if (parser.getArgs().length == 1) {
            if (parser.getArgs()[0].equals("-r")) {
                reverse = true;
            } else {
                System.out.println("Invalid number of arguments, expected 0 arguments");
                return;
            }
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        assert files != null;
        ArrayList<String> ans = new ArrayList<>();
        for (File file : files)
            ans.add(file.getName());

        if (reverse)
            ans.sort(Comparator.reverseOrder());
        else
            ans.sort(String::compareTo);

        String finalStr = "";
        for (String s : ans)
            finalStr += s + "\n";

        if (parser.getFileName() == null)
            System.out.println(finalStr);
        else
            writeToFile(finalStr);

    }

    /**
     * _________________________________________________________________________
     * This method will change the current directory
     *
     * @param args the new directory
     */
    public void cd(String[] args) {
        CheckContentClearing(); // clears the content of the file if the operator is ">"

        if (args.length > 1) {
            System.out.println("Invalid number of arguments");
            return;
        }
        if (args.length == 0) {
            System.setProperty("user.dir", homeDic);
            return;
        }
        String[] path = args[0].split("\\\\");
        if (path[0].equals("..")) {  //relative path after going back to the parent directory
            for (String folder : path) {
                if (folder.equals("..")) {
                    File file = new File(System.getProperty("user.dir"));
                    System.setProperty("user.dir", file.getParent());
                } else {
                    String fullPath = System.getProperty("user.dir") + folder;
                    File file = new File(fullPath);
                    if (file.exists()) {
                        System.setProperty("user.dir", fullPath);
                    } else {
                        System.out.println("ERROR: Directory doesn't exist");
                        return;
                    }
                }
            }
        } else {
            File file = new File(args[0]);

            if (!file.isAbsolute()) {

                Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                Path relPath = Paths.get(args[0]);
                args[0] = currentDir.resolve(relPath).toString();// if the dic is short path then add the current directory to the path

                file = new File(args[0]);
                if (!file.exists()) {
                    System.out.println("ERROR : " + args[0] + "\\" +
                            "" + " is not found !");
                    return;
                }
                if (!file.isDirectory()) {
                    throw new IllegalArgumentException();
                }
            }
            File finalPath = new File(args[0]);

            if (finalPath.isDirectory())
                System.setProperty("user.dir", args[0]);
            else {
                System.out.println("ERROR: Invalid Directory!");
            }
        }
    }

    /**
     * _________________________________________________________________________
     * This method will create a directory or more in the current path or in a given path
     */
    public void mkdir() {
        CheckContentClearing(); // clears the content of the file if the operator is ">"

        if (parser.getArgs().length == 0) {
            System.out.println("Expected an argument");
        } else {
            for (String dir : parser.getArgs()) {
                File file = new File(dir);
                if (!file.isAbsolute()) {
                    Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                    Path relPath = Paths.get(dir);
                    Path newResultPath = currentDir.resolve(relPath);
                    file = new File(newResultPath.toString());
                }
                if (!file.exists()) {
                    file.mkdir();
                } else {
                    System.out.println("Directory already exists");
                }
            }
        }
    }

    /**
     * _________________________________________________________________________
     * This method will delete either a specific empty directory or all empty directories in the current path
     */
    public void rmdir() {
        CheckContentClearing(); // clears the content of the file if the operator is ">"
        if (parser.getArgs().length == 0) {
            System.out.println("Expected an argument");
        } else if (parser.getArgs().length > 1) {
            System.out.println("Command takes only one argument");
        } else {
            String givenPath;
            File targetDir;
            if (Objects.equals(parser.getArgs()[0], "*")) {
                givenPath = System.getProperty("user.dir");
                targetDir = new File(givenPath);
                File[] listOfFiles = targetDir.listFiles();
                assert listOfFiles != null;
                for (File f : listOfFiles) {
                    if (f.isDirectory()) {
                        File[] fList = f.listFiles();
                        assert fList != null;
                        if (fList.length == 0) {
                            f.delete();
                        }
                    }
                }
            } else {
                givenPath = parser.getArgs()[0];
                targetDir = new File(givenPath);
                if (!targetDir.isAbsolute()) {
                    Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                    Path relPath = Paths.get(givenPath);
                    Path newResultPath = currentDir.resolve(relPath);
                    targetDir = new File(newResultPath.toString());
                }
                if (!targetDir.exists()) {
                    System.out.println("Directory doesn't exist");
                    return;
                }
                File[] listOfFiles = targetDir.listFiles();
                assert listOfFiles != null;
                if (listOfFiles.length == 0) {
                    targetDir.delete();
                }
            }
        }
    }

    /**
     * _________________________________________________________________________
     * This method will print the content of a file or more
     */
    public void cat() throws IOException {
        if (parser.getArgs().length == 0) {
            System.out.println("Expected an argument");
        } else if (parser.getArgs().length > 2) {
            System.out.println("Command takes no more than two arguments");
        } else {
            Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
            Path targetFile = Paths.get(parser.getArgs()[0]);
            Path newResultPath = currentDir.resolve(targetFile);
            File file1 = new File(newResultPath.toString());
            if (parser.getArgs().length == 1) {
                if (file1.exists()) {
                    BufferedReader read = new BufferedReader(new FileReader(file1));
                    String finalStr = "";
                    String line;
                    while ((line = read.readLine()) != null) {
                        finalStr += line + "\n";
                    }
                    if (parser.getFileName() == null)
                        System.out.println(finalStr);
                    else
                        writeToFile(finalStr);
                } else {
                    System.out.println(parser.getArgs()[0] + " doesn't exist");
                }
            } else {
                Path targetFile2 = Paths.get(parser.getArgs()[1]);
                Path newResultPath2 = currentDir.resolve(targetFile2);
                File file2 = new File(newResultPath2.toString());
                if (file1.exists() && file2.exists()) {
                    BufferedReader read = new BufferedReader(new FileReader(file1));
                    String finalStr = "";
                    String line;
                    while ((line = read.readLine()) != null) {
                        finalStr += line + "\n";
                    }
                    BufferedReader read2 = new BufferedReader(new FileReader(file2));
                    while ((line = read2.readLine()) != null) {
                        finalStr += line + "\n";
                    }
                    if (parser.getFileName() == null)
                        System.out.println(finalStr);
                    else
                        writeToFile(finalStr);
                } else if (!file1.exists()) {
                    System.out.println(parser.getArgs()[0] + " doesn't exist");
                } else if (!file2.exists()) {
                    System.out.println(parser.getArgs()[1] + " doesn't exist");
                } else {
                    System.out.println(parser.getArgs()[0] + " and " + parser.getArgs()[1] + " don't exist");
                }
            }
        }
    }

    /**
     * _________________________________________________________________________
     * This method will print the arguments passed to it
     */
    public void echo() throws IOException {
        if (parser.getArgs().length == 0) {
            System.out.println("Expected an argument");
        } else {
            String finalStr = "";
            for (String arg : parser.getArgs()) {
                finalStr += arg + "\n";
            }
            if (parser.getFileName() == null)
                System.out.println(finalStr);
            else
                writeToFile(finalStr);
        }
    }


    /**
     * _________________________________________________________________________
     * This method will print the history of the commands the user wrote
     *
     * @param historyArray the array list that contains the history of the commands
     */
    public void history(ArrayList<String> historyArray) throws IOException {
        if (parser.getArgs().length > 0) {
            System.out.println("Invalid number of arguments, expected 0 arguments");
            return;
        }
        StringBuilder finalStr = new StringBuilder();
        for (int i = 1; i <= historyArray.size(); i++) {
            finalStr.append(i).append(" ");
            finalStr.append(historyArray.get(i - 1)).append("\n");
        }
        if (parser.getFileName() == null)
            System.out.println(finalStr);
        else
            writeToFile(finalStr.toString());
    }

    /**
     * _________________________________________________________________________
     * This method will delete a file from the current directory
     */
    public void rm() {
        CheckContentClearing();
        // checks for the number of arguments written
        if (parser.getArgs().length != 1) {
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        File file = new File(parser.getArgs()[0]);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[0]);
        }
        //makes sure that the files in the directory the user is currently working on is not empty
        assert files != null;
        boolean del = false;
        if (file.exists() && !file.isDirectory()) {
            del = file.delete();
            if (del)
                return;
            else
                throw new RuntimeException();
        }

        // else display that it has not been found/ does not exist
        System.out.println("can't remove file: No such file ");
    }

    /**
     * _________________________________________________________________________
     * This method will count the number of lines, words and characters in a file
     */
    void wc() throws IOException {
        // checks for the number of arguments written
        if (parser.getArgs().length != 1) {
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        File currentDic = new File(System.getProperty("user.dir"));

        File[] files = currentDic.listFiles();
        File file = new File(parser.getArgs()[0]);
        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[0]);
        }
        assert files != null; //makes sure that the files in the directory the user is currently working on is not empty
        if (file.exists() && !file.isDirectory()) {
            String finalStr = "";
            int countLines = 0, countWords = 0, countChars = 0;
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                countLines++;
                String[] words = line.split(" ");
                countWords += words.length;
                for (String word : words) {
                    countChars += word.length();
                }
            }
            finalStr = (countLines + " " + countWords + " " + countChars + " " + file.getName() + "\n");
            sc.close();

            if (parser.getFileName() == null)
                System.out.println(finalStr);
            else
                writeToFile(finalStr);
            return;

        }
        System.out.println("No such file or directory");
    }

    /**
     * _________________________________________________________________________
     * This method will copy the content of a file to another file
     */
    void cp() throws IOException {
        CheckContentClearing();
        // checks for the number of arguments written
        if (parser.getArgs().length != 2) {
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();

        File file1 = new File(parser.getArgs()[0]);
        if (!file1.isAbsolute())
            file1 = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[0]);
        File file2 = new File(parser.getArgs()[1]);
        if (!file2.isAbsolute())
            file2 = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[1]);
        assert files != null; //makes sure that the files in the directory the user is currently working on is not empty
        boolean f1 = false, f2 = false;

        if (file1.exists() && !file1.isDirectory()) {
            f1 = true;
        }

        if (file2.exists() && !file2.isDirectory()) {
            f2 = true;
        }

        if (f1 && f2) {
            FileInputStream inFile = new FileInputStream(file1);
            FileOutputStream outFile = new FileOutputStream(file2);

            try {
                int n;
                while ((n = inFile.read()) != -1) {
                    outFile.write(n);
                }
            } finally {
                if (inFile != null) {
                    inFile.close();
                }
                if (outFile != null) {
                    outFile.close();
                }
            }
        } else if (f1) {
            System.out.println(parser.getArgs()[1] + "does not exist");
        } else if (f2) {
            System.out.println(parser.getArgs()[0] + "does not exist");
        } else {
            System.out.println("None of the files exist");
        }
    }

    public void touch() throws IOException {
        CheckContentClearing();
        // checks for the number of arguments written
        if (parser.getArgs().length != 1) {
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        // touch ..\Admin\test100.txt
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        File searchFile = new File(parser.getArgs()[0]);
        File file;
        if (searchFile.isAbsolute()) {
            file = searchFile;
        } else {
            file = new File(System.getProperty("user.dir") + "\\\\" + searchFile.getName());
        }
        //makes sure that the files in the directory the user is currently working on is not empty
        assert files != null;
        for (File f : files) {
            if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
                System.out.println("This file already exists in this path");
                return;
            }
        }
        Path newFile = Paths.get(file.toURI());
        Files.createFile(newFile);
    }

    /**
     * _________________________________________________________________________
     * This method will clear the content of a file
     * if the operator is ">" and the command returns nothing
     */
    public void CheckContentClearing() {
        if (!Objects.equals(parser.getOperator(), ">"))
            return;

        try {
            // Defining the file name of the file
            Path fileName = Path.of(System.getProperty("user.dir") + "\\\\" + parser.getFileName());
            // Writing into the file
            Files.writeString(fileName, "");

        } catch (IOException e) {

        }
    }

    /***_________________________________________________________________________
     * This method will write the content to a file
     * if the operator is ">" or ">>" and the command returns something
     * @param content the content to be written to the file
     * @throws IOException in case of an error in the file
     */
    void writeToFile(String content) throws IOException {

        boolean append = parser.getOperator().equals(">>");
        File file = new File(parser.getFileName());
        Path fileName;
        if (!file.isAbsolute())
            fileName = Path.of(System.getProperty("user.dir") + "\\\\" + parser.getFileName());
        else
            fileName = Path.of(parser.getFileName());

        if (append) {
            String fullContent = Files.readString(fileName);
            fullContent += content;
            Files.writeString(fileName, fullContent);

        } else {
            Files.writeString(fileName, content);
        }

    }


}
