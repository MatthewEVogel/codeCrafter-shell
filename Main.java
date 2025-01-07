import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        // Uncomment this block to pass the first stage
        //test
        while (true) {
            System.out.print("$ ");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] inputArray = input.split(" ");
            String command = inputArray[0];
            String argument = String.join(" ", Arrays.copyOfRange(inputArray, 1, inputArray.length));
            List<String> builtInCommands = List.of("echo", "exit", "type", "pwd", "cd");
            String path = System.getenv("PATH");
//            System.out.println("path: " + path);
            String[] paths = {};
            if (path != null) {
                paths = path.split(":");
            }
            switch (command){
                case "exit":
                    System.exit(Integer.parseInt(argument));
                    break;
                case "echo":
                    System.out.println(argument);
                    break;
                case "type":
                    if (builtInCommands.contains(argument)) {
                        System.out.println(argument + " is a shell builtin");
                    }
                    else {
                        String filePath = findFileInPath(paths, argument);
                        if (filePath != null){
                            System.out.println(argument + " is " + filePath);
                        }
                        else {
                            System.out.println(argument + ": not found");
                        }
                    }
                    break;
                case "pwd":
                    System.out.println(System.getProperty("user.dir"));
                    break;
                case "cd":
                    if (argument.charAt(0) == '~'){
                        if (argument.length() == 1){
                            argument = System.getenv("HOME");
                        }
                        else {
                            argument = System.getenv("HOME") + argument.split("~")[0];
                        }
                    }
                    File directory = new File(argument);
                    String[] parentLevels = argument.split("/");
                    boolean relativeDir = false;
                    if (parentLevels[0].equals(".") || parentLevels[0].equals(".."))  {
                        relativeDir = true;
                        for (int i = 0; i < parentLevels.length; i++){
                            String level = parentLevels[i];
                            if (!level.equals("..")) {
                                String dir = "";
                                for (int j = i+1; j < parentLevels.length; j++){
                                    dir = dir + "/" + parentLevels[j];
                                }
                                dir = System.getProperty("user.dir") + dir;
                                System.setProperty("user.dir", dir);
                                directory = new File(dir);
                                break;
                            }
                            Path currentDirectory = Paths.get(System.getProperty("user.dir"));
                            Path parentDirectory = currentDirectory.getParent();
                            System.setProperty("user.dir", parentDirectory.toString());
                        }
                    }
                    if (directory.exists() && directory.isDirectory() && !relativeDir) {
                        System.setProperty("user.dir", argument);
                    }
                    else if (!relativeDir){
                    System.out.println("cd: " + argument + ": No such file or directory");
                }

                    break;
                default:
                    String filePath = findFileInPath(paths, command);
                    if (filePath != null){
                        Process process = Runtime.getRuntime().exec(new String[] {filePath, argument});
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    }
                    else{
                        System.out.println(input + ": command not found");
                    }
            }
        }
    }
    public static String findFileInPath(String[] paths, String fileName){
//        System.out.println(fileName);
        for (String p : paths){
            String filePath = p + "/" + fileName;
            File file = new File(filePath);
            if (file.exists()){
                return filePath;
            }
        }
        return null;
    }
}
