/// Created at: 25/10/2023
/// Last modification: 25/10/2023

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Terminal {
    Parser parser = new Parser();
    //Implement each command in a method, for example:

    /**
     * This is the main function where the terminal is running and takes input from the user
     *
     * @param args the arguments passed to the program
     */
    public static void main(String[] args) {
        Terminal terminal = new Terminal();
        while (true) {
            System.out.print("user@user:~$ ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine(); // here we get the input from the terminal
            if (input.equals("exit")) // exit the program in case the user enters exit in the terminal
                break;
            if (terminal.parser.parse(input)) { // if the paring is done correctly then choose the suitable action else continue the program without any action after print an error message
                terminal.chooseCommandAction();
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
        // TODO
    }

    /***
     * This method will choose the suitable command method to be called
     */
    public void chooseCommandAction() {
        switch (parser.getCommandName()) {
            case "pwd":
                pwd();
            case "ls":
                ls();
            default:
        }
    }
}
