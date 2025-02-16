/**
 * Truck class represents a truck with a unique ID, a maximum load capacity, and a current load.
 * This class provides methods to manage the load on the truck.
 */
public class Truck {

    // Unique identifier for the truck
    private int ID;
    // Maximum load capacity of the truck
    private int maxCapacity;
    // Current load of the truck
    private int load = 0;

    /**
     * Constructor to create a Truck with the specified ID and capacity.
     *
     * @param id the unique identifier for the truck
     * @param maxCapacity the maximum load capacity of the truck
     */
    public Truck(int id, int maxCapacity) {
        this.ID = id;
        this.maxCapacity = maxCapacity;
    }

    /**
     * Adds a specified amount of load to the truck.
     * If the new load reaches the truck's maximum capacity, the truck is automatically unloaded.
     *
     * @param load the amount of load to add to the truck
     */
    public void addLoad(int load) {
        this.load += load;
        if (this.load == maxCapacity) {
            unload();
        }
    }

    /**
     * Unloads the truck, setting the current load to zero.
     */
    public void unload() {
        load = 0;
    }

    /**
     * Getter method for ID variable.
     *
     * @return the ID of the truck
     */
    public int getID() {
        return ID;
    }

    /**
     * Getter method for maxCapacity variable.
     *
     * @return the maximum load capacity of the truck
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Getter method for load variable.
     *
     * @return the current load within the truck
     */
    public int getLoad() {
        return load;
    }
}
