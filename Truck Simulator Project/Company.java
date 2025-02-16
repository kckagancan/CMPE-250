import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A class representing a company, which manages a fleet of trucks.
 */
public class Company {

    FileWriter writer; // a writer object for outputting the operations' information.
    AVLTree readyTree; // a tree containing the nodes with parking lots that has at least one ready truck
    AVLTree waitingTree; // a tree containing the nodes with parking lots that has at least one waiting truck
    AVLTree unFullTree; // a tree containing the nodes with parking lots that has at least one remaining truck space
    AVLTree tree; // a tree containing all the nodes and parking lots

    /**
     * Constructs a company object with 4 AVL trees and a file writer.
     *
     * @param writer a file writer object
     */
    public Company(FileWriter writer){
        this.writer = writer;
        this.readyTree = new AVLTree();
        this.waitingTree = new AVLTree();
        this.unFullTree = new AVLTree();
        this.tree = new AVLTree();
    }

    /**
     * Creates a new parking lot if a parking lot with the same capacity constraint does not already exist.
     *
     * @param pl a parking lot object
     */
    public void addParkingLot(ParkingLot pl){
        unFullTree.insert(pl);
        tree.insert(pl);
    }

    /**
     * Deletes a parking lot with the specified capacity constraint if exists.
     *
     * @param capacityConstraint the capacity constraint of the parking lot to be deleted
     */
    public void deleteParkingLot(int capacityConstraint){
        unFullTree.delete(capacityConstraint);
        waitingTree.delete(capacityConstraint);
        readyTree.delete(capacityConstraint);
        tree.delete(capacityConstraint);
    }

    /**
     * Moves a truck from the waiting section of the most suitable parking lot to its ready section.
     * It outputs either the capacity constraint of the lot or -1 if no suitable lot exists.
     *
     * @param capacityConstraint the capacity constraint of the target parking lot
     * @throws IOException if an I/O error occurs while writing to the output file
     */
    public void ready(int capacityConstraint) throws IOException {

        Node current = waitingTree.findNextGreater(capacityConstraint);

        if (current == null) {
            // If there are no suitable parking lots, output -1.
            writer.write("-1\n");
            return;
        }

        int id = current.pl.ready();
        writer.write(String.format("%d %d\n", id, current.pl.getCapacityConstraint()));

        // Insert the node into ready AVL tree if it was not already in there.
        if (current.pl.getReadyTrucks() == 1){
            readyTree.insert(current.pl);
        }

        // Delete the node from the waiting AVL tree if there are no remaining waiting trucks.
        if (current.pl.getWaitingTrucks() == 0){
            waitingTree.delete(current.pl.getCapacityConstraint());
        }
    }

    /**
     * Counts and outputs the number of trucks in parking lots with a capacity constraint greater than the specified value.
     *
     * @param capacityConstraint the capacity constraint threshold for counting trucks
     * @throws IOException if an I/O error occurs while writing to the output file
     */
    public void count(int capacityConstraint) throws IOException {

        Node current = tree.countHelper(capacityConstraint);
        int truckCount = 0;

        while (current != null)
        {

            truckCount += current.pl.getTruckCount();
            current = tree.findNextGreater(current);

        }

        writer.write(String.format("%d\n", truckCount));
    }

    /**
     * Loads a specified amount onto trucks in the ready section of parking lots that meet the minimum capacity constraint.
     * Outputs the truck IDs and the capacity constraint of the parking lots that trucks transferred to after loading.
     *
     * @param capacityConstraint the minimum capacity constraint required for loading
     * @param loadAmount the amount of load to be distributed among trucks
     * @throws IOException if an I/O error occurs while writing to the output file
     */
    public void load(int capacityConstraint, int loadAmount) throws IOException {

        // array lists to hold the output elements and nodes to be deleted after the load operation.
        ArrayList<int[]> outputElements = new ArrayList<int[]>();
        ArrayList<Integer> nodesToDelete = new ArrayList<Integer>();

        Node current = readyTree.findNextGreater(capacityConstraint);

        while (loadAmount > 0 && current != null)
        {

            while (loadAmount > 0 && current.pl.getReadyTrucks() > 0){

                int load = Math.min(loadAmount, current.pl.getCapacityConstraint());
                loadAmount -= load;
                Truck t = current.pl.load(load);

                // Insert the node into unfull AVL tree if it was not already in there.
                if (unFullTree.hashTable[current.pl.getCapacityConstraint()] == null){
                    unFullTree.insert(current.pl);
                }

                int newCapacityConstraint = transferTruck(t);
                outputElements.add(new int[]{t.getID(), newCapacityConstraint});

            }

            // If there are no ready trucks left in the parking lot, add the capacity to the list for deletion.
            if (current.pl.getReadyTrucks() == 0){
                nodesToDelete.add(current.pl.getCapacityConstraint());
            }

            current = readyTree.findNextGreater(current);
        }

        // cc stands for capacity constraint
        for (int cc : nodesToDelete){
            readyTree.delete(cc);
        }

        // Output the requested information.
        int size = outputElements.size();
        if (size == 0){ // Handle the case if we did not load a truck
            writer.write("-1\n");
            return;
        }
        for(int i = 0; i < size; i++) {

            int[] outputElement = outputElements.get(i);
            int info1 = outputElement[0];
            int info2 = outputElement[1];

            // Output a different string when it comes to the last output element
            if (i == size - 1) {
                writer.write(String.format("%d %d\n", info1, info2));
            }
            else{
                writer.write(String.format("%d %d - ", info1, info2));
            }
        }
    }

    /**
     * Transfers a truck to a new parking lot based on its remaining capacity.
     * This method is a dependency for load method.
     *
     * @param t the truck to transfer
     * @return the capacity constraint of the parking lot, to which the given truck is transferred
     */
    private int transferTruck(Truck t) throws IOException {

        int capacityConstraint = t.getMaxCapacity() - t.getLoad();
        Node current = unFullTree.findNextSmaller(capacityConstraint);

        // If we weren't able to transfer the truck, return -1.
        if (current == null) {
            return -1;
        }

        current.pl.acceptTruck(t);

        // Hold the capacity constraint of the current node before deletion to prevent potential bugs.
        int newCapacityConstraint = current.pl.getCapacityConstraint();

        // Insert the node into waiting AVL tree if it was not already in there.
        if  (current.pl.getWaitingTrucks() == 1){
            waitingTree.insert(current.pl);
        }

        // Delete the node from the unfull AVL tree if the parking lot reached its truck limit.
        if (current.pl.getTruckLimit() == current.pl.getTruckCount()){
            unFullTree.delete(current.pl.getCapacityConstraint());
        }

        return newCapacityConstraint;
    }

    /**
     * Adds a truck to the most suitable parking lot based on capacity constraints.
     * Outputs the capacity constraint of the selected parking lot or -1 if no lot is suitable.
     *
     * @param ID the ID of the truck
     * @param maxCapacity the maximum capacity of the truck
     * @throws IOException if an I/O error occurs while writing to the output file
     */
    public void addTruck(int ID, int maxCapacity) throws IOException {

        Node current = unFullTree.findNextSmaller(maxCapacity);

        if (current == null) {
            // If there are no suitable parking lots, output -1.
            writer.write("-1\n");
            return;
        }

        current.pl.acceptTruck(new Truck(ID, maxCapacity));
        writer.write(String.format("%d\n", current.pl.getCapacityConstraint()));

        // Insert the node into waiting AVL tree if it was not already in there.
        if  (current.pl.getWaitingTrucks() == 1){
            waitingTree.insert(current.pl);
        }

        // Delete the node from the unfull AVL tree if the parking lot reached its truck limit.
        if (current.pl.getTruckLimit() == current.pl.getTruckCount()){
            unFullTree.delete(current.pl.getCapacityConstraint());
        }
    }
}
