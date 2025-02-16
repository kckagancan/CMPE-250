/**
 * ParkingLot class manages a truck parking lot with two sections, a capacity constraint and truck limit.
 * It provides methods to accept and load the trucks. It handles the trucks between ready and waiting sections of the parking lot.
 */
public class ParkingLot{

    // Maximum load capacity constraint for the parking lot
    private int capacityConstraint;

    // Maximum number of trucks that the parking lot can handle
    private int truckLimit;

    // Current count of trucks in the parking lot
    private int truckCount = 0;

    // Queue of trucks which are waiting to be loaded
    private TruckQueue waitingSection;

    // Current count of trucks in the waiting section
    private int waitingTrucks = 0;

    // Queue of trucks which are ready for loading
    private TruckQueue readySection;

    // Current count of trucks in the ready section
    private int readyTrucks = 0;

    /**
     * Constructor to initialize a ParkingLot with the specified capacity constraint and truck limit.
     *
     * @param capacityConstraint the maximum load capacity constraint for trucks in the parking lot
     * @param truckLimit         the maximum number of trucks allowed in the parking lot
     */
    public ParkingLot(int capacityConstraint, int truckLimit) {
        this.capacityConstraint = capacityConstraint;
        this.truckLimit = truckLimit;
        waitingSection = new TruckQueue(truckLimit);
        readySection = new TruckQueue(truckLimit);
    }

    /**
     * Moves the longest waiting truck from the waiting section to the ready section and returns its ID.
     *
     * @return the ID of the truck that is moved to the ready section
     */
    public int ready() {
        Truck t = waitingSection.peek();
        waitingSection.dequeue();
        readySection.enqueue(t);

        waitingTrucks--;
        readyTrucks++;
        return t.getID();
    }

    /**
     * Loads a specified amount of load onto the truck which was ready for longest and returns it.
     * After loading, the truck leaves the parking lot.
     *
     * @param load the load amount to add to the truck
     * @return the truck after loading it
     */
    public Truck load(int load) {
        Truck t = readySection.peek();
        readySection.dequeue();
        t.addLoad(load);

        truckCount--;
        readyTrucks--;
        return t;
    }

    /**
     * Accepts a truck into the waiting section of the parking lot.
     *
     * @param t the Truck to accept
     */
    public void acceptTruck(Truck t) {
        waitingSection.enqueue(t);
        truckCount++;
        waitingTrucks++;
    }

    /**
     * Getter method for capacityConstraint variable
     *
     * @return the capacity constraint of the parking lot
     */
    public int getCapacityConstraint() {
        return capacityConstraint;
    }

    /**
     * Getter method for truckLimit variable
     *
     * @return the truck limit of the parking lot
     */
    public int getTruckLimit() {
        return truckLimit;
    }

    /**
     * Getter method for truckCount variable
     *
     * @return the number of trucks in the parking lot
     */
    public int getTruckCount() {
        return truckCount;
    }

    /**
     * Getter method for waitingTrucks variable
     *
     * @return the number of trucks in the waiting section
     */
    public int getWaitingTrucks() {
        return waitingTrucks;
    }

    /**
     * Getter method for readyTrucks variable
     *
     * @return the number of trucks in the ready section
     */
    public int getReadyTrucks() {
        return readyTrucks;
    }
}
