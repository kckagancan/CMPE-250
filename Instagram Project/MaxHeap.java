import java.util.ArrayList;

/**
 * Implementation of a generic MaxHeap data structure.
 *
 * @param <AnyType> the type of elements stored in the heap, which must implement Comparable
 */
public class MaxHeap<AnyType extends Comparable<? super AnyType>> {

    private int capacity;              // Initial capacity of the heap
    ArrayList<AnyType> heap;           // Internal array representation of the heap
    int currentSize;                   // Current number of elements in the heap
    boolean built;                     // Flag indicating if the heap has been built

    /**
     * Constructs a new MaxHeap with an initial capacity of 1024.
     */
    public MaxHeap() {
        currentSize = 0;
        capacity = 1024;
        built = false;
        heap = new ArrayList<>(capacity);

        for (int i = 0; i < capacity; i++) {
            heap.add(null);
        }
    }

    /**
     * Enlarges the heap's internal storage to the specified size and copies the elements.
     *
     * @param size the new capacity of the heap
     */
    private void enlargeHeap(int size) {
        ArrayList<AnyType> newHeap = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            heap.add(null);
        }

        newHeap.addAll(heap);
        heap = newHeap;
    }

    /**
     * Returns the current number of elements in the heap.
     *
     * @return the size of the heap
     */
    public int size() {
        return currentSize;
    }

    /**
     * Checks if the heap is empty.
     *
     * @return true if the heap is empty, false otherwise
     */
    public boolean isEmpty() {
        return currentSize == 0;
    }

    /**
     * Percolates down the element at the specified index to restore the max-heap property.
     *
     * @param hole the index of the element to percolate down
     */
    private void percolateDown(int hole) {
        int child;
        AnyType temp = heap.get(hole);

        // Percolate down
        for (; hole * 2 <= currentSize; hole = child) {
            child = hole * 2;

            // Find the bigger child
            if (child != currentSize && heap.get(child + 1).compareTo(heap.get(child)) > 0)
                child++;

            if (heap.get(child).compareTo(temp) > 0)
                heap.set(hole, heap.get(child));
            else
                break;
        }

        heap.set(hole, temp);
    }

    /**
     * Inserts a new element into the heap. If the heap is not built, it simply adds the element.
     *
     * @param x the element to insert
     */
    public void insert(AnyType x) {
        // Enlarge the heap if necessary
        if (currentSize == heap.size() - 1)
            enlargeHeap(heap.size() * 2 + 1);

        int hole = ++currentSize;

        // If not built, heapify first
        if (!built) {
            heap.set(hole, x);
            return;
        }

        // Percolate up
        for (heap.set(0, x); x.compareTo(heap.get(hole / 2)) > 0; hole /= 2)
            heap.set(hole, heap.get(hole / 2));
        heap.set(hole, x);
    }

    /**
     * Removes and returns the maximum element from the heap.
     *
     * @return the maximum element in the heap
     */
    public AnyType deleteMax() {
        // If not built, heapify first
        if (!built) {
            buildHeap();
        }

        AnyType maxItem = getMax();
        heap.set(1, heap.get(currentSize--));
        percolateDown(1);
        return maxItem;
    }

    /**
     * Returns the maximum element in the heap without removing it.
     *
     * @return the maximum element in the heap
     */
    public AnyType getMax() {
        // If not built, heapify first
        if (!built) {
            buildHeap();
        }
        return heap.get(1);
    }

    /**
     * Builds the heap (heapify) by ensuring all elements satisfy the max-heap property.
     */
    private void buildHeap() {
        for (int i = currentSize / 2; i > 0; i--)
            percolateDown(i);
        built = true;
    }
}
