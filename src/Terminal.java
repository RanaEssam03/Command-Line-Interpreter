/// Created on: 25/10/2023
/// Last modification: 30/10/2023

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Terminal {
    Parser parser = new Parser();
    final String homeDic = System.getProperty("user.home");
    String currentDirectory = System.getProperty("user.home");

    Terminal(){
        System.setProperty("user.dir", homeDic);
    }
    //Implement each command in a method, for example:

    /**
     * This is the main function where the terminal is running and takes input from the user
     *
     * @param args the arguments passed to the program
     */
    public static void main(String[] args) throws IOException {

        Terminal terminal = new Terminal();
        ArrayList<String> historyArray = new ArrayList<>(); // array list that saves the commands the user writes
        while (true) {
            System.out.print(System.getProperty("user.dir")+" > ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine(); // here we get the input from the terminal
            historyArray.add(input);
            if (input.equals("exit")) // exit the program in case the user enters exit in the terminal
                break;
            if (terminal.parser.parse(input)) { // if the paring is done correctly then choose the suitable action else continue the program without any action after print an error message
                terminal.chooseCommandAction(historyArray);
            }
        }
    }

    /**
     * This method will print the current directory
     */
    public void pwd() {
        if (parser.getArgs().length != 0) {
            System.out.println("Invalid number of arguments, expected 0 arguments");
            return;
        }
        String currentDic = System.getProperty("user.dir");
        System.out.println("Path\n____");
        System.out.println(currentDic);
    }

    /**
     * This method will print the files and directories in the current directory in alphabetical order or reverse order
     */
    public void ls() {
        boolean reverse = false;
        if (parser.getArgs().length > 1) {
            System.out.println("Invalid number of arguments, expected 0 arguments");
            return;
        }
        else if (parser.getArgs().length == 1){
            if (parser.getArgs()[0].equals("-r")){
                reverse = true;
            }
            else {
                System.out.println("Invalid number of arguments, expected 0 arguments");
                return;
            }
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        assert files != null;
        ArrayList<String> ans = new ArrayList<>();
        for(File file : files)
                ans.add(file.getName());

        if(reverse)
            ans.sort(Comparator.reverseOrder());
        else
            ans.sort(String::compareTo);

        for(String s : ans)
            System.out.println(s);

    }

    /**
     * This method will change the current directory
     *
     * @param args the new directory
     */
    public void cd(String[] args) {
        if (args.length > 1) {
            System.out.println("Invalid number of arguments, expected 1 argument");
            return;
        }
        else if (args.length == 0){
            System.setProperty("user.dir", homeDic);
            return;
        }

        if(Objects.equals(args[0], "..")){
            File file = new File(System.getProperty("user.dir"));
            System.setProperty("user.dir", file.getParent());
            return;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            args[0] = System.getProperty("user.dir") + "\\" + args[0]; // if the dic is short path then add the current directory to the path
            file = new File(args[0]);
            if(!file.exists())
            {
                System.out.println("ERROR : "+args[0] + " is not found !");
                return;
            }
        }
        System.setProperty("user.dir", args[0]);
    }

    /**
     * This method returns the current path
     */
    String getCurrentPath(){
        Path currRelativePath = Paths.get("");
        String path = currRelativePath.toAbsolutePath().toString();
        return path;
    }

    /**
     * This method will create a directory or more in the current path or in a given path
     */
    public void mkdir(){
        if(parser.getArgs().length == 0){
            System.out.println("Expected an argument");
        }
        else{
            for(String dir : parser.getArgs()){
                File file;
                if(dir.contains(":")){
                    file = new File(dir);
                }
                else if(dir.charAt(0) == '.' && dir.charAt(1) == '.'){
                    String newDir = dir.replace(".", "");
                    file = new File(currentDirectory + newDir);
                }
                else if(dir.charAt(0) == '~'){
                    String newDir = dir.replace("~", "");
                    file = new File(homeDic + newDir);
                }
                else{
                    String path = currentDirectory + '/' + dir;
                    file = new File(path);
                }
                if(!file.exists()) {
                    file.mkdir();
                }
                else{
                    System.out.println("Directory already exists");
                }
            }
        }
    }
    /**
     * This method recursively delete all empty directories in the current directory
     */
    public void deleteDirectory(String currentDir){
        File directory = new File(currentDir);
        File[] listOfFiles = directory.listFiles();
        if(listOfFiles.length == 0){
            directory.delete();
        }
        else{
            for(int i = 0; i < listOfFiles.length; i++){
                File file = listOfFiles[i];
                if(file.isDirectory()){
                    deleteDirectory(file.getAbsolutePath());
                }
            }
        }
    }
    /**
     * This method will delete either a specific empty directory or all empty directories in the current path
     */
    public void rmdir(){
        if(parser.getArgs().length == 0){
            System.out.println("Expected an argument");
        }
        else if(parser.getArgs().length > 1){
            System.out.println("Command takes only one argument");
        }
        else{
            if(Objects.equals(parser.getArgs()[0], "*")){
                String path = getCurrentPath();
                deleteDirectory(path);
            }
            else{
                File targetDir = new File(parser.getArgs()[0]);
                File[] listOfFiles = targetDir.listFiles();
                if(listOfFiles.length == 0){
                    targetDir.delete();
                }
            }
        }
    }

    public void history(ArrayList<String> historyArray){
        if (parser.getArgs().length > 0){
            System.out.println("Invalid number of arguments, expected 0 arguments");
            return;
        }
        for (int i = 1; i <= historyArray.size(); i++){
            System.out.print(i + " ");
            System.out.println(historyArray.get(i-1));
        }
    }

    public void rm(){
        // checks for the number of arguments written
        if (parser.getArgs().length != 1){
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        File file = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[0]);
        //makes sure that the files in the directory the user is currently working on is not empty
        assert files != null;
        boolean del = false;
        for (File f : files) {
            if (f.getAbsolutePath().equals(file.getAbsolutePath())){
                del = f.delete();
                if (del)
                    return;
            }

        }
        // else display that it has not been found/ does not exist
        System.out.println("can't remove file: No such file or directory");
    }

    void wc() throws FileNotFoundException {
        // checks for the number of arguments written
        if (parser.getArgs().length != 1){
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        File file = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[0]);
        assert files != null; //makes sure that the files in the directory the user is currently working on is not empty
        for (File f : files) {
            if (f.getAbsolutePath().equals(file.getAbsolutePath())){
                int countLines = 0, countWords = 0, countChars = 0;
                Scanner sc = new Scanner(file);
                while (sc.hasNextLine()){
                    String line = sc.nextLine();
                    countLines++;
                    String[] words = line.split(" ");
                    countWords += words.length;
                    for (String word : words){
                        countChars += word.length();
                    }
                }
                System.out.println(countLines + " " + countWords + " " + countChars + " " + file.getName());
                return;
            }
        }
        System.out.println("No such file or directory");
    }

    void cp() throws IOException {
        // checks for the number of arguments written
        if (parser.getArgs().length != 2){
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        File file1 = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[0]);
        File file2 = new File(System.getProperty("user.dir") + "\\\\" + parser.getArgs()[1]);
        assert files != null; //makes sure that the files in the directory the user is currently working on is not empty
        boolean f1 = false, f2 = false;
        for (File f : files){
            if(f.getAbsolutePath().equals(file1.getAbsolutePath())){
                f1 = true;
            }
            if (f.getAbsolutePath().equals(file2.getAbsolutePath())){
                f2 = true;
            }
        }
        if (f1 && f2){
            FileInputStream inFile = new FileInputStream(file1);
            FileOutputStream outFile = new FileOutputStream(file2);

            try {
                int n;
                while ((n = inFile.read()) != -1){
                    outFile.write(n);
                }
            }
            finally {
                if (inFile != null) {
                    inFile.close();
                }
                if (outFile != null) {
                    outFile.close();
                }
            }
        }
        else if (f1){
            System.out.println(parser.getArgs()[1] + "does not exist");
        }
        else if (f2){
            System.out.println(parser.getArgs()[0] + "does not exist");
        }
        else{
            System.out.println("None of the files exist");
        }
    }

    /***
     * This method will choose the suitable command method to be called
     */
    public void chooseCommandAction(ArrayList<String> historyArray) throws IOException {
        switch (parser.getCommandName()) {
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
            default:
        }
    }
}
