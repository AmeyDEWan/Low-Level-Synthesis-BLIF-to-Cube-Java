import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        /* handle BLIF file argument */
        if (args[0] == null || args[0].isEmpty())
            System.out.println("No BLIF file given.");

        System.out.println("Processing " + args[0]);

        /* parse BLIF */
        List<Function> parsedFunctions = parseBLIF(args[0]);

        System.out.println("\n===== TASK 1 =====\n");

        System.out.println("Parsed functions:\n");
        // Task 1
        verifyTask(parsedFunctions, "golden-output/task1.txt");

        /* calculate intersection of functions */
        List<Function> intersectedFunctions = new ArrayList<>();
        int count = 0;
        // Task 2
        /***** YOUR CODE HERE *****/
        /* intersect all parsed functions by intersecting all cubes of one function with each cube of the other function */
        /* store the results in intersectedFunctions */
        for (int i = 0; i < parsedFunctions.size(); i ++) {
            Function fi = parsedFunctions.get(i);

            Function intersectedFunction = new Function(fi);

            for (int j = 0; j < parsedFunctions.size(); j ++) {
                if (i != j) {
                    Function fj = parsedFunctions.get(j);
                    for (Cube cube_fi : fi.getCubes()) {
                        for (Cube cube_fj : fj.getCubes()) {
                            Cube resCube = cube_fi.intersect(cube_fj);
                            if (resCube.isLegal()) {
                                intersectedFunction.addCube(resCube);
                            }
                            if (i == 0 && !resCube.isLegal()) {
                                count ++;
                            }
                        }
                    }
                }
            }
            intersectedFunctions.add(intersectedFunction);
        }
        
        // count = 10; 10 illegal cubes
        count += 1; 
        /***** END CODE *****/

        System.out.println("===== TASK 2 =====\n");

        System.out.println("Intersected functions:\n");
        verifyTask(intersectedFunctions, "golden-output/task2.txt");
    }

    /**
     * Parses a simple BLIF file into a list of functions
     * @param fileName the path to the BLIF file
     * @return a list of functions in order of their appearance
     */
    private static List<Function> parseBLIF(String fileName) {

        BufferedReader reader;
        List<Function> functions = new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            Function currentFunction = null;

            while (line != null) {

                /* .names marks the start of a new function */
                if (line.startsWith(".names")) {

                    /* terminate previous function */
                    if (currentFunction != null) {
                        functions.add(currentFunction);
                        currentFunction = null;
                    }

                    /* parse names of inputs and output */
                    List<String> namesLine = new ArrayList<>(Arrays.asList(line.split(" ")));
                    currentFunction = new Function(namesLine.subList(1, namesLine.size() - 1), namesLine.get(namesLine.size() - 1));

                /* .end or a blank line marks the end of a function */
                } else if (line.startsWith(".end") || line.isBlank()) {
                    functions.add(currentFunction);
                    currentFunction = null;

                /* inside a function, lines represent cubes */
                } else if (currentFunction != null) {
                    currentFunction.addLine(line);
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return functions;
    }

    private static void verifyTask(List<Function> functionsToVerify, String outputFileName) {

        List<String> lines;

        try {
            lines = Files.readAllLines(Paths.get(outputFileName));
            String referenceOutput = String.join("\n", lines);

            StringBuilder actualOutput = new StringBuilder();

            for (Function f : functionsToVerify) {
                String stringRepresentation = f.toString();
                actualOutput.append(stringRepresentation);
                System.out.println(stringRepresentation);
            }

            if (referenceOutput.contentEquals(actualOutput)) {
                System.out.println("==> CORRECT\n");
            } else {
                System.out.println("==> NOT CORRECT\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
