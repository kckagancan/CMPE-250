/**
 * A class representing an AVL tree with nodes containing parking lot data.
 */
public class AVLTree {

    private Node root; // Root node of the AVL tree
    public Node[] hashTable; // Hash table containing the nodes of the AVL tree

    /**
     * Constructor initializes the AVL tree with a null root and a hash table.
     */
    public AVLTree() {
        root = null;
        hashTable = new Node[500001]; // Bad practice but sufficient for the project
    }

    /**
     * Inserts a new parking lot into the AVL tree and hash table.
     *
     * @param pl the ParkingLot object to insert
     */
    public void insert(ParkingLot pl){
        root = insertNode(root, null, pl);
    }

    /**
     * Recursive helper method to insert a parking lot node into the AVL tree and hash table.
     *
     * @param node the current node in the tree
     * @param parent the parent node of the current node
     * @param pl the ParkingLot object to insert
     * @return the balanced node after insertion
     */
    private Node insertNode(Node node, Node parent, ParkingLot pl){

        // Add the parking lot to the tree.
        if (node == null){
            Node newNode = new Node(pl, parent);
            hashTable[pl.getCapacityConstraint()] = newNode;
            return newNode;
        }

        // Find where to insert the node.
        if (pl.getCapacityConstraint() < node.pl.getCapacityConstraint()){
            node.left = insertNode(node.left, node, pl);
        }
        else if (pl.getCapacityConstraint() > node.pl.getCapacityConstraint()){
            node.right = insertNode(node.right, node, pl);
        }
        // Don't take an action if a parking lot with the same capacity constraint already exists.
        else{
            return node;
        }

        // Update the height of the node, re-balance it, then return it.
        updateHeight(node);
        return balanceInsertion(node, pl);
    }

    /**
     * Deletes a parking lot node from the AVL tree and hash table based on its capacity constraint.
     *
     * @param capacityConstraint the capacity constraint of the parking lot to delete
     */
    public void delete(int capacityConstraint){
        root = deleteNode(root, capacityConstraint);
        hashTable[capacityConstraint] = null;
    }

    /**
     * Recursive helper method to delete a parking lot node from the AVL tree and hash table.
     *
     * @param node the current node in the tree
     * @param capacityConstraint the capacity constraint of the parking lot to delete
     * @return the balanced node after deletion
     */
    private Node deleteNode(Node node, int capacityConstraint) {

        // Base case for recursion
        if (node == null)
            return node;

        // Find the node to delete and assign parents if necessary.
        if (capacityConstraint < node.pl.getCapacityConstraint()){
            node.left = deleteNode(node.left, capacityConstraint);
            if (node.left != null) node.left.parent = node;
        }
        else if (capacityConstraint > node.pl.getCapacityConstraint()){
            node.right = deleteNode(node.right, capacityConstraint);
            if (node.right != null) node.right.parent = node;
        }
        // At this point, "node" is the node to be deleted.
        else {

            // If node has no children, just delete it.
            if (node.left == null && node.right == null) {
                node = null;
            }

            // If node has only one child, replace the node with its child.
            else if (node.left == null) {
                node.right.parent = node.parent;
                node = node.right;
            } else if (node.right == null) {
                node.left.parent = node.parent;
                node = node.left;
            }

            // If the node has two children
            else {

                // Find the nextGreater node and copy its parking lot to this node.
                Node nextGreater = findNextGreater(node);
                node.pl = nextGreater.pl;

                // Delete the nextGreater node recursively.
                node.right = deleteNode(node.right, nextGreater.pl.getCapacityConstraint());
                if (node.right != null) node.right.parent = node;
            }

        }

        // If the tree had only one node and that node is deleted, return.
        if (node == null){
            return node;
        }

        // Update the height of the node, re-balance it, then return it.
        updateHeight(node);
        return balanceDeletion(node);
    }

    /**
     * Finds the next greater node in the tree for a given node.
     *
     * @param node the node whose next greater node is to be found
     * @return the next greater node in the tree
     */
    public Node findNextGreater(Node node){

        // Case 1: Node has a right subtree.
        if (node.right != null) {

            Node current = node.right;
            while (current.left != null) {
                current = current.left;
            }
            return current;
        }
        // Case 2: Node does not have a right subtree.
        else {

            Node current = node;
            Node parent = current.parent;
            while (parent != null && current == parent.right) {
                current = parent;
                parent = parent.parent;
            }
            return parent;
        }
    }

    /**
     * Finds the next greater node based on the capacity constraint.
     * This method is only used for count operation.
     *
     * @param capacityConstraint the capacity constraint to compare
     * @return the next greater node in the tree
     */
    public Node countHelper(int capacityConstraint){

        Node current = root;
        Node nextGreater = null;

        while(current!=null){

            if (capacityConstraint < current.pl.getCapacityConstraint()){
                nextGreater = current;
                current = current.left;
            }
            else{
                current = current.right;
            }
        }

        return nextGreater;
    }

    /**
     * Finds the next greater or equal node based on the capacity constraint.
     *
     * @param capacityConstraint the capacity constraint to compare
     * @return the next greater node in the tree
     */
    public Node findNextGreater(int capacityConstraint){

        // Check if there exists a node with the given capacity constraint.
        if (hashTable[capacityConstraint] != null){
            return hashTable[capacityConstraint];
        }

        // Find the node that has the parking lot with next greater capacity constraint.
        Node current = root;
        Node nextGreater = null;

        while(current!=null){

            if (capacityConstraint <= current.pl.getCapacityConstraint()){
                nextGreater = current;
                current = current.left;
            }
            else{
                current = current.right;
            }
        }

        return nextGreater;
    }

    /**
     * Finds the next smaller or equal node based on the capacity constraint.
     *
     * @param capacityConstraint the capacity constraint to compare
     * @return the next smaller node in the tree
     */
    public Node findNextSmaller(int capacityConstraint){

        // Check if there exists a node with the given capacity constraint.
        if (hashTable[capacityConstraint] != null){
            return hashTable[capacityConstraint];
        }

        // Find the node that has the parking lot with next smaller capacity constraint.
        Node current = root;
        Node nextSmaller = null;

        while(current!=null){

            if (capacityConstraint >= current.pl.getCapacityConstraint()){
                nextSmaller = current;
                current = current.right;
            }
            else{
                current = current.left;
            }
        }

        return nextSmaller;
    }

    /**
     * Gets the height of a node.
     *
     * @param node the node whose height is to be obtained
     * @return the height of the node, or -1 if the node is null
     */
    private int height(Node node){
        return node == null ? -1 : node.height;
    }

    /**
     * Calculates the balance factor of a node.
     *
     * @param node the node whose balance factor is to be calculated
     * @return the balance factor of the node
     */
    private int balanceFactor(Node node){
        return height(node.left) - height(node.right);
    }

    /**
     * Performs a right rotation on the given node.
     *
     * @param node the node to rotate
     * @return the new root after rotation
     */
    private Node rightRotate(Node node) {

        // Perform rotation.
        Node leftChild = node.left;
        Node leftRightChild = leftChild.right;
        node.left = leftRightChild;
        leftChild.right = node;

        // Update parents
        leftChild.parent = node.parent;
        node.parent = leftChild;
        if (leftRightChild != null){
            leftRightChild.parent = node;
        }

        // Update heights.
        updateHeight(node);
        updateHeight(leftChild);

        // Return the new root.
        return leftChild;
    }

    /**
     * Performs a left rotation on the given node.
     *
     * @param node the node to rotate
     * @return the new root after rotation
     */
    private Node leftRotate(Node node) {

        // Perform rotation.
        Node rightChild = node.right;
        Node rightLeftChild = rightChild.left;
        node.right = rightLeftChild;
        rightChild.left = node;

        // Update parents
        rightChild.parent = node.parent;
        node.parent = rightChild;
        if (rightLeftChild != null){
            rightLeftChild.parent = node;
        }

        // Update heights.
        updateHeight(node);
        updateHeight(rightChild);

        // Return the new root.
        return rightChild;
    }

    /**
     * Updates the height of a node based on its children's heights.
     *
     * @param node the node whose height is to be updated
     */
    private void updateHeight(Node node){
        node.height = Math.max(height(node.left), height(node.right)) + 1;
    }

    /**
     * Balances the tree after an insertion operation.
     *
     * @param node the node to balance
     * @param pl the inserted ParkingLot object
     * @return the balanced node
     */
    private Node balanceInsertion(Node node, ParkingLot pl){

        // If this node is unbalanced, handle the four cases.
        int balanceFactor = balanceFactor(node);

        // Left-Left Case
        if (balanceFactor > 1 && pl.getCapacityConstraint() < node.left.pl.getCapacityConstraint())
            return rightRotate(node);

        // Right-Right Case
        if (balanceFactor < -1 && pl.getCapacityConstraint() > node.right.pl.getCapacityConstraint())
            return leftRotate(node);

        // Left-Right Case
        if (balanceFactor > 1 && pl.getCapacityConstraint() > node.left.pl.getCapacityConstraint()) {
            node.left = leftRotate(node.left);
            node.left.parent = node;
            return rightRotate(node);
        }

        // Right-Left Case
        if (balanceFactor < -1 && pl.getCapacityConstraint() < node.right.pl.getCapacityConstraint()) {
            node.right = rightRotate(node.right);
            node.right.parent = node;
            return leftRotate(node);
        }

        // Return the node if this node is balanced.
        return node;
    }

    /**
     * Balances the tree after a deletion operation.
     *
     * @param node the node to balance
     * @return the balanced node
     */
    private Node balanceDeletion(Node node){

        // If this node is unbalanced, handle the four cases.
        int balanceFactor = balanceFactor(node);

        // Left-Left Case
        if (balanceFactor > 1 && balanceFactor(node.left) >= 0)
            return rightRotate(node);

        // Left-Right Case
        if (balanceFactor > 1 && balanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            node.left.parent = node;
            return rightRotate(node);
        }

        // Right-Right Case
        if (balanceFactor < -1 && balanceFactor(node.right) <= 0)
            return leftRotate(node);

        // Right-Left Case
        if (balanceFactor < -1 && balanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            node.right.parent = node;
            return leftRotate(node);
        }

        // Return the node if this node is balanced.
        return node;
    }
}
