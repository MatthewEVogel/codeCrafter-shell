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
                    File directory = new File(argument);
                    if (directory.exists() && directory.isDirectory()) {
                        System.setProperty("user.dir", argument);
                    }
                    else {
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
                    else {
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
