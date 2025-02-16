import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {

        // Initialize the file handles and scanners for the input, a file writer for the output.
        // They are in the same order given in the input-output description.
        File nodeFile = new File(args[0]);
        File edgeFile = new File(args[1]);
        File objectiveFile = new File(args[2]);

        Scanner nodeScanner = new Scanner(nodeFile);
        Scanner edgeScanner = new Scanner(edgeFile);
        Scanner objectiveScanner = new Scanner(objectiveFile);

        FileWriter writer = new FileWriter(args[3]);

        // Take some of the inputs beforehand
        int gridRow = nodeScanner.nextInt();
        int gridColumn = nodeScanner.nextInt();

        int revealRadius = objectiveScanner.nextInt();
        int xStart = objectiveScanner.nextInt();
        int yStart = objectiveScanner.nextInt();

        // deltaRow[i] and deltaCol[i] represents left, right, up, down respectively.
        int[] deltaCol = new int[]{-1, 0, 1, 0};
        int[] deltaRow = new int[]{0, -1, 0, 1};

        // Initialize the adjacency list and node type array.
        int[][] magicalNodes = new int[gridRow][gridColumn];
        double[][][] magicalMap = new double[gridRow][gridColumn][4];

        try {
            while (nodeScanner.hasNextLine()) {
                int x = nodeScanner.nextInt();
                int y = nodeScanner.nextInt();
                int type = nodeScanner.nextInt();
                magicalNodes[x][y] = type;
            }
        }
        catch (NoSuchElementException ignored) {
        }

        try {
            while (edgeScanner.hasNextLine()) {
                String[] allCoords = edgeScanner.next().split(",");
                double time = edgeScanner.nextDouble();

                String[] coords1 = allCoords[0].split("-");
                String[] coords2 = allCoords[1].split("-");

                int x1 = Integer.parseInt(coords1[0]);
                int y1 = Integer.parseInt(coords1[1]);
                int x2 = Integer.parseInt(coords2[0]);
                int y2 = Integer.parseInt(coords2[1]);

                // Add the time between nodes to the adjacency list.
                for (int i = 0; i < 4; i++){
                    // Find if the x2,y2 node is to the left, up, right, or down of the x1,y1 node.
                    if (x2 == x1 + deltaRow[i] && y2 == y1 + deltaCol[i]) {
                        magicalMap[x1][y1][i] = time;
                        // Add the same time to the x2,y2 node's adjacency list.
                        // There exists an index difference of 2 between opposite movements (left-right and up-down).
                        if (i < 2){
                            magicalMap[x2][y2][i+2] = time;
                        }
                        else{
                            magicalMap[x2][y2][i-2] = time;
                        }
                    }
                }
            }
        }
        catch (NoSuchElementException ignored) {
        }

        Tarnished tarnished = new Tarnished(magicalMap, magicalNodes, revealRadius, xStart, yStart, writer);
        try {
            while (objectiveScanner.hasNextLine()) {

                int destinationX = objectiveScanner.nextInt();
                int destinationY = objectiveScanner.nextInt();

                String remainingLine = "";
                // Check if we are at the very end of the objective input file.
                if (objectiveScanner.hasNext()){
                    remainingLine = objectiveScanner.nextLine().stripLeading();
                }

                // Check if there exists another integer in the same line.
                if (!remainingLine.isEmpty()) {
                    String[] options = remainingLine.split(" ");
                    tarnished.travelTo(destinationX, destinationY, options);
                }
                else {
                    tarnished.travelTo(destinationX, destinationY);
                }
            }
        }
        catch (NoSuchElementException ignored) {
        }

        writer.close();
    }
}