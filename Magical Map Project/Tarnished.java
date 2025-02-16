import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a wanderer, namely "Tarnished" from the elden ring game, who is
 * trying to navigate through a magical map.
 */
public class Tarnished {

    // Contains the travel times of each edge for each node located at i,j. Left, Up, Right, Down respectively.
    // Edge nodes are handled within the algorithms such as left edge of the node located at 0,0.
    private final double[][][] magicalMap;
    private final int[][] magicalNodes; // Contains the type of each node located at i,j.
    private MyHashSet<Integer> changedNodes; // Contains the node types that are changed to 0.
    private boolean[][] revealedNodes; // Contains the nodes that are revealed.
    private String[] offeredNumbers; // Contains the numbers that the wizard have offered most recently.
    private final int revealRadius; // Reveal radius of the tarnished
    private int currentObjective; // Current objective number
    private final int totalRows; // Total rows in the magical map
    private final int totalColumns; // Total columns in the magical map
    private int xPosition; // Current x-position of the Tarnished
    private int yPosition; // Current y-position of the Tarnished
    private final int[] deltaRow; // Row direction deltas for navigation (Left, Up, Right, Down respectively)
    private final int[] deltaCol; // Column direction deltas for navigation (Left, Up, Right, Down respectively)
    FileWriter writer; // FileWriter for logging navigation and actions

    /**
     * Constructs a Tarnished instance with the specified parameters.
     *
     * @param magicalMap the 3D array representing travel times of edges
     * @param magicalNodes the 2D array representing node types
     * @param revealRadius the reveal radius of the Tarnished
     * @param xStart the initial x-coordinate of the Tarnished
     * @param yStart the initial y-coordinate of the Tarnished
     * @param writer the FileWriter for logging actions
     */
    public Tarnished(double[][][] magicalMap, int[][] magicalNodes, int revealRadius, int xStart, int yStart, FileWriter writer) {
        this.magicalMap = magicalMap;
        this.magicalNodes = magicalNodes;
        this.writer = writer;
        this.revealRadius = revealRadius;
        xPosition = xStart;
        yPosition = yStart;
        changedNodes = new MyHashSet<>();
        totalRows = magicalNodes.length;
        totalColumns = magicalNodes[0].length;
        revealedNodes = new boolean[totalRows][totalColumns];
        offeredNumbers = null;
        deltaCol = new int[]{-1, 0, 1, 0};
        deltaRow = new int[]{0, -1, 0, 1};
        currentObjective = 1;

        // Reveal the nodes within the line of sight of the tarnished initially.
        revealLineOfSight();
    }

    /**
     * Travels to the specified destination coordinates without or with wizard assistance.
     *
     * @param destinationX the x-coordinate of the destination
     * @param destinationY the y-coordinate of the destination
     * @throws IOException if an I/O error occurs
     */
    public void travelTo(int destinationX, int destinationY) throws IOException {
        if (offeredNumbers == null){
            travelWithoutHelp(destinationX, destinationY);
        }
        else {
            travelWithHelp(destinationX, destinationY);
            offeredNumbers = null;
        }
    }

    /**
     * Travels to the specified destination coordinates without or with wizard assistance.
     * It sets the offered numbers so that the wanderer uses the wizard assistance in the next objective.
     *
     * @param destinationX the x-coordinate of the destination
     * @param destinationY the y-coordinate of the destination
     * @param options the options provided by the wizard
     * @throws IOException if an I/O error occurs
     */
    public void travelTo(int destinationX, int destinationY, String[] options) throws IOException {
        if (offeredNumbers == null){
            travelWithoutHelp(destinationX, destinationY);
        }
        else {
            travelWithHelp(destinationX, destinationY);
        }
        offeredNumbers = options;
    }

    /**
     * Travels to the destination using wizard assistance and by choosing the optimal option.
     *
     * @param destinationX the x-coordinate of the destination
     * @param destinationY the y-coordinate of the destination
     * @throws IOException if an I/O error occurs
     */
    private void travelWithHelp(int destinationX, int destinationY) throws IOException {

        double minDistance = 1e9;
        int chosen = -1;
        for (String option : offeredNumbers){
            int currentOption = Integer.parseInt(option);
            // If the offered number is not already changed, try the number.
            if (!changedNodes.contains(currentOption)) {
                changedNodes.insert(currentOption);
                double currentDistance = findShortestPathLength(destinationX, destinationY);
                if (currentDistance < minDistance) {
                    minDistance = currentDistance;
                    chosen = currentOption;
                }
                changedNodes.remove(currentOption);
            }
        }

        // We are actually travelling with help here.
        writer.write("Number " + chosen + " is chosen!\n");
        changedNodes.insert(chosen);
        travelWithoutHelp(destinationX, destinationY);
    }

    /**
     * Travels to the destination without wizard assistance.
     *
     * @param destinationX the x-coordinate of the destination
     * @param destinationY the y-coordinate of the destination
     * @throws IOException if an I/O error occurs
     */
    private void travelWithoutHelp(int destinationX, int destinationY) throws IOException {

        // Runs until the wanderer finds a path without a single impassable node encounter.
        boolean pathFound = false;
        while(!pathFound) {
            int[][][] path = generatePath(destinationX, destinationY);

            ArrayList<int[]> pathList = new ArrayList<>();
            pathList.add(new int[]{destinationX, destinationY});

            ArrayList<int[]> impassableCandidates = new ArrayList<>();

            // Trace the path array from the reverse
            int xFrom = destinationX;
            int yFrom = destinationY;

            while (true) {

                int xTo = path[xFrom][yFrom][0];
                int yTo = path[xFrom][yFrom][1];
                int[] nodeCoordinates = new int[]{xTo, yTo};

                // Move on the generated path after creating the path list and holding the impassable nodes on our path.
                if (xTo == xPosition && yTo == yPosition) {
                    if (impassableCandidates.isEmpty()) {
                        moveOnPath(pathList);
                        pathFound = true;
                    } else {
                        moveOnPath(pathList, impassableCandidates);
                    }
                    break;
                }

                // Add the node to our path list and add to the impassable candidates if it's type is nonzero and not changed to 0.
                pathList.add(nodeCoordinates);
                int nodeType = magicalNodes[xTo][yTo];
                if (nodeType != 0 && !changedNodes.contains(nodeType)) {
                    impassableCandidates.add(nodeCoordinates);
                }

                xFrom = xTo;
                yFrom = yTo;
            }

        }
    }

    /**
     * Moves along the specified path and reveals nodes within line of sight.
     *
     * @param pathList the list of coordinates representing the path
     * @throws IOException if an I/O error occurs
     */
    private void moveOnPath(ArrayList<int[]> pathList) throws IOException {

        for (int i = pathList.size() - 1; i >= 0; i--) {
            xPosition = pathList.get(i)[0];
            yPosition = pathList.get(i)[1];
            revealLineOfSight();
            writer.write("Moving to " + xPosition + "-" + yPosition + "\n");
        }

        writer.write("Objective " + (currentObjective++) + " reached!\n");
    }

    /**
     * Moves along the specified path until encountering an impassable node.
     *
     * @param pathList the list of coordinates representing the path
     * @param impassableCandidates the list of impassable node candidates
     * @throws IOException if an I/O error occurs
     */
    private void moveOnPath(ArrayList<int[]> pathList, ArrayList<int[]> impassableCandidates) throws IOException {

        // Runs until an impassable node in our path enters the wanderer's line of sight.
        int i = pathList.size() - 1;
        while(!withinLineOfSight(impassableCandidates)){
            xPosition = pathList.get(i)[0];
            yPosition = pathList.get(i)[1];
            revealLineOfSight();
            i--;
            writer.write("Moving to " + xPosition + "-" + yPosition + "\n");
        }

        writer.write("Path is impassable!\n");
    }

    /**
     * Calculates the optimal path from the current position of the wanderer to the specified destination on the map.
     * Uses Dijkstra's algorithm to compute the shortest path considering node types and distances.
     *
     @param destinationX The X-coordinate of the destination
     @param destinationY The Y-coordinate of the destination
     @return A 3D array where path[x][y][0] and path[x][y][1] store the coordinates of the node wanderer came from
     */
    private int[][][] generatePath(int destinationX, int destinationY){

        int[][][] path = new int[totalRows][totalColumns][2];
        double[][] distances = new double[totalRows][totalColumns];

        for (int i = 0; i < totalRows; i++){
            for (int j = 0; j < totalColumns; j++){
                distances[i][j] = 1e9;
            }
        }

        distances[xPosition][yPosition] = 0.0;
        path[xPosition][yPosition][0] = xPosition;
        path[xPosition][yPosition][1] = yPosition;

        MinHeap<Node> priorityQueue = new MinHeap<Node>();
        priorityQueue.insert(new Node(xPosition, yPosition, 0));

        while (!priorityQueue.isEmpty()){

            Node current = priorityQueue.deleteMin();
            int x = current.getX();
            int y = current.getY();

            // The wanderer calculated the shortest path to its destination node already if the condition is true.
            if (x == destinationX && y == destinationY){
                break;
            }

            double distance = current.getDistanceFromSrc();

            for(int i = 0; i < 4; i++){
                int newX = x + deltaRow[i];
                int newY = y + deltaCol[i];

                if (newX < 0 || newX >= totalRows || newY < 0 || newY >= totalColumns){
                    continue;
                }

                // Previously visited node, no need to keep going for this one.
                if (path[x][y][0] == newX && path[x][y][1] == newY){
                    continue;
                }

                int newNodeType = magicalNodes[newX][newY];
                if (newNodeType == 1){
                    continue;
                }

                // The conditions are: new distance should be less and (the node type should be 0 or changed to 0 or the node should be not revealed)
                double newDistance = distance + magicalMap[x][y][i];
                if (newDistance < distances[newX][newY] && (newNodeType == 0 || !revealedNodes[newX][newY] || changedNodes.contains(newNodeType))){
                    distances[newX][newY] = newDistance;
                    path[newX][newY][0] = x;
                    path[newX][newY][1] = y;
                    priorityQueue.insert(new Node(newX, newY, newDistance));
                }
            }
        }

        return path;
    }

    /**
     * Calculates the shortest path length from the current position of the wanderer to the specified destination on the map.
     * Uses Dijkstra's algorithm. It's the same algorithm as generatePath method but without the path array.
     *
     * @param destinationX The X-coordinate of the destination
     * @param destinationY The Y-coordinate of the destination
     * @return The shortest path distance to the destination
     */
    private double findShortestPathLength(int destinationX, int destinationY){

        double[][] distances = new double[totalRows][totalColumns];

        for (int i = 0; i < totalRows; i++){
            for (int j = 0; j < totalColumns; j++){
                distances[i][j] = 1e9;
            }
        }

        distances[xPosition][yPosition] = 0.0;

        MinHeap<Node> priorityQueue = new MinHeap<Node>();
        priorityQueue.insert(new Node(xPosition, yPosition, 0));

        while (!priorityQueue.isEmpty()){

            Node current = priorityQueue.deleteMin();
            int x = current.getX();
            int y = current.getY();

            // The wanderer calculated the shortest path to its destination node already if the condition is true.
            if (x == destinationX && y == destinationY){
                break;
            }

            double distance = current.getDistanceFromSrc();

            for(int i = 0; i < 4; i++){
                int newX = x + deltaRow[i];
                int newY = y + deltaCol[i];

                if (newX < 0 || newX >= totalRows || newY < 0 || newY >= totalColumns){
                    continue;
                }

                int newNodeType = magicalNodes[newX][newY];
                if (newNodeType == 1){
                    continue;
                }

                // The conditions are: new distance should be less and (the node type should be 0 or changed to 0 or the node should be not revealed)
                double newDistance = distance + magicalMap[x][y][i];
                if (newDistance < distances[newX][newY] && (newNodeType == 0 || !revealedNodes[newX][newY] || changedNodes.contains(newNodeType))){
                    distances[newX][newY] = newDistance;
                    priorityQueue.insert(new Node(newX, newY, newDistance));
                }
            }
        }

        return distances[destinationX][destinationY];
    }

    /**
     * Reveals all nodes within a circular radius of the current position that are in the line of sight.
     */
    private void revealLineOfSight() {
        for (int i = xPosition-revealRadius; i <=xPosition+revealRadius; i++) {
            for (int j = yPosition-revealRadius; j <=yPosition+revealRadius; j++){

                if (i < 0 || i >= totalRows || j < 0 || j >= totalColumns){
                    continue;
                }

                if (withinLineOfSight(i, j)){
                    revealedNodes[i][j] = true;
                }
            }
        }
    }

    /**
     * Checks if any of the impassable nodes are within the line of sight of the current position.
     *
     * @param candidates A list of candidate coordinates represented as integer arrays
     * @return true if at least one candidate is within the line of sight; false otherwise
     */
    private boolean withinLineOfSight(ArrayList<int[]> candidates){

        int size = candidates.size();
        for (int i = size-1; i >= 0; i--){

            int x = candidates.get(i)[0];
            int y = candidates.get(i)[1];

            if(withinLineOfSight(x, y)){
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether a specific node is within the line of sight of the wanderer.
     *
     * @param x The X-coordinate of the node.
     * @param y The Y-coordinate of the node.
     * @return true if the node is within the line of sight, false otherwise
     */
    private boolean withinLineOfSight(int x, int y){
        return Math.sqrt(Math.pow(xPosition - x, 2) + Math.pow(yPosition - y, 2)) <= revealRadius;
    }
}
