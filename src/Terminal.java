/// Created on: 25/10/2023
/// Last modification: 26/10/2023

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Terminal {
    Parser parser = new Parser();
    String homeDic = System.getProperty("user.home");
    //Implement each command in a method, for example:

    /**
     * This is the main function where the terminal is running and takes input from the user
     *
     * @param args the arguments passed to the program
     */
    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        ArrayList<String> historyArray = new ArrayList<>(); // array list that saves the commands the user writes
        while (true) {
            System.out.print("> ");
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
                System.out.println(args[0] + " is not found !");
                return;
            }
        }
        System.setProperty("user.dir", args[0]);
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
        if (parser.getArgs().length > 1){
            System.out.println("Invalid number of arguments, expected 1 arguments");
            return;
        }
        File currentDic = new File(System.getProperty("user.dir"));
        File[] files = currentDic.listFiles();
        File file = new File(parser.getArgs()[0]);
        assert files != null;
        ArrayList<String> arr = new ArrayList<>();
        for (File f : files) {
            arr.add(f.getName());
        }
        boolean del = false;
        for (String s : arr){
            if (s.equals(parser.getArgs()[0])){
                del = file.delete();
                return;
            }
        }
        if (!del)
            System.out.println("can't remove file: No such file or directory");
    }

    /***
     * This method will choose the suitable command method to be called
     */
    public void chooseCommandAction(ArrayList<String> historyArray) {
        switch (parser.getCommandName()) {
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
            default:
        }
    }
}
