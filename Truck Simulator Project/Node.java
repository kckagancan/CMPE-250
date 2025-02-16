/**
 * A class representing a node in a binary tree structure, where each node
 * contains a reference to a ParkingLot and pointers to left, right, and
 * parent nodes, as well as its height within the tree.
 */
public class Node {

    ParkingLot pl; // parking lot object
    Node right; // right child node
    Node left; // left child node
    Node parent; // parent node
    int height; // height of the node

    /**
     * Constructs a new Node with a specified ParkingLot and parent node.
     * Initializes the left and right child nodes to null and sets the height to 0.
     *
     * @param pl     The ParkingLot associated with this node
     * @param parent The parent node of this node
     */
    public Node(ParkingLot pl, Node parent) {
        this.pl = pl;
        this.right = null;
        this.left = null;
        this.parent = parent;
        this.height = 0;
    }
}