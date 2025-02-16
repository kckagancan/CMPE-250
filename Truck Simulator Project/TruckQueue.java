/**
 * TruckQueue class is a circular queue implementation that manages a fixed number of trucks.
 */
public class TruckQueue {

    // front of the queue
    private int front;
    // back of the queue
    private int rear;
    // capacity of the queue
    private int capacity;
    // array for circular queue
    private Truck[] queue;

    /**
     * Constructor for TruckQueue.
     *
     * @param capacity the maximum number of trucks the queue can hold
     */
    TruckQueue(int capacity) {
        this.front = 0;
        this.rear = -1;
        this.capacity = capacity;
        this.queue = new Truck[capacity];
    }

    /**
     * Adds a truck to the end of the queue.
     *
     * @param t the Truck to enqueue
     */
    void enqueue(Truck t) {
        rear = (rear + 1) % capacity;
        queue[rear] = t;
    }

    /**
     * Removes the truck at the front of the queue.
     */
    void dequeue() {
        queue[front] = null;
        front = (front + 1) % capacity;
    }

    /**
     * Returns the truck at the front of the queue without removing it.
     *
     * @return the Truck at the front of the queue
     */
    Truck peek() {
        return queue[front];
    }
}
