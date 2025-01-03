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
            List<String> builtInCommands = List.of("echo", "exit", "type");
            String path = System.getenv("PATH");
//            System.out.println(path);
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
                        String filePath = fileExists(paths, argument);
                        if (filePath != null){
                            System.out.println(argument + " is " + filePath);
                        }
                        else {
                            System.out.println(input + ": command not found");
                        }
                    }
                    break;
                default:
                    String filePath = fileExists(paths, argument);
                    if (filePath != null){
                        Process process = Runtime.getRuntime().exec(new String[] {filePath, argument});
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }

                        // Wait for the process to complete and check the exit code
                        int exitCode = process.waitFor();
                        System.out.println("Process exited with code: " + exitCode);
                    }


                    else {
                        System.out.println(input + ": command not found");
                    }
            }
        }
    }
    public static String fileExists(String[] paths, String fileName){
        for (String p : paths){
            File file = new File(p + "/" + fileName);
            if (file.exists()){
                return p + "/" + fileName;
            }
        }
        return null;
    }
}
