/**
 * Represents a Node with 2D coordinates and a distance from a source point.
 * This class is used for dijkstra's algorithm implementation with priority queue.
 */
public class Node implements Comparable<Node> {

    private int x; // x coordinate of the node
    private int y; // y coordinate of the node
    private double distanceFromSrc; // distance from the source node of the dijkstra's algorithm

    /**
     * Constructs a Node with specified coordinates and distance from the source.
     *
     * @param x the x-coordinate of the node
     * @param y the y-coordinate of the node
     * @param distanceFromSrc the distance of the node from the source point
     */
    public Node(int x, int y, double distanceFromSrc) {
        this.x = x;
        this.y = y;
        this.distanceFromSrc = distanceFromSrc;
    }

    /**
     * Returns the x-coordinate of the node.
     *
     * @return the x-coordinate of the node
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the node.
     *
     * @return the y-coordinate of the node
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the distance of the node from the source.
     *
     * @return the distance from the source
     */
    public double getDistanceFromSrc() {
        return distanceFromSrc;
    }

    /**
     * Compares this node to another node based on the distance from the source node.
     *
     * @param node the node to compare to
     * @return a negative integer, zero, or a positive integer as this node is less than,
     * equal to, or greater than the specified node based on distance
     */
    @Override
    public int compareTo(Node node) {
        return Double.compare(this.distanceFromSrc, node.distanceFromSrc);
    }
}
