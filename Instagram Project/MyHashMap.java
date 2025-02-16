import java.util.ArrayList;
import java.util.Objects;

/**
 * Implementation of a generic hash map using seperate chaining for collision handling.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class MyHashMap<K, V> {

    /**
     * Represents a single node in the hash map chain.
     *
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    private static class HashNode<K, V> {
        K key;                // The key for this node
        V value;              // The value associated with the key
        final int hashCode;   // The hash code of the key
        HashNode<K, V> next;  // Pointer to the next node in the chain

        /**
         * Constructs a new hash node.
         *
         * @param key the key of the node
         * @param value the value of the node
         * @param hashCode the hash code of the key
         */
        public HashNode(K key, V value, int hashCode) {
            this.key = key;
            this.value = value;
            this.hashCode = hashCode;
        }
    }

    private ArrayList<HashNode<K, V>> bucketArray; // Array list for hash nodes
    private int numBuckets; // Current capacity of the array list
    private int size; // Number of elements in the hash map

    /**
     * Constructs a new hash map with an initial capacity of 10.
     */
    public MyHashMap() {
        bucketArray = new ArrayList<>();
        numBuckets = 10;
        size = 0;

        for (int i = 0; i < numBuckets; i++)
            bucketArray.add(null);
    }

    /**
     * Retrieves all values stored in the hash map.
     *
     * @return an array list containing all values in the hash map
     */
    public ArrayList<V> getValues() {
        ArrayList<V> vals = new ArrayList<V>();
        for (HashNode<K, V> headNode : bucketArray) {
            while (headNode != null) {
                vals.add(headNode.value);
                headNode = headNode.next;
            }
        }
        return vals;
    }

    /**
     * Checks if the hash map is empty.
     *
     * @return true if the hash map contains no elements, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Computes the hash code for a given key.
     *
     * @param key the key whose hash code is to be computed
     * @return the hash code of the key
     */
    private int hashCode(K key) {
        return Objects.hashCode(key);
    }

    /**
     * Determines the bucket index for a given key.
     *
     * @param key the key to find the bucket index for
     * @return the index of the bucket
     */
    private int getBucketIndex(K key) {
        int hashCode = hashCode(key);
        int index = hashCode % numBuckets;
        index = index < 0 ? index * -1 : index; // Ensure non-negative index
        return index;
    }

    /**
     * Removes a key-value pair from the hash map.
     *
     * @param key the key to be removed
     * @return the value associated with the removed key, or null if the key is not found
     */
    public V remove(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        HashNode<K, V> head = bucketArray.get(bucketIndex);
        HashNode<K, V> prev = null;

        // Search for the key in the chain
        while (head != null) {
            if (head.key.equals(key) && hashCode == head.hashCode)
                break;

            prev = head;
            head = head.next;
        }
        // Key not found
        if (head == null)
            return null;

        size--;

        // Remove the node
        if (prev != null)
            prev.next = head.next;
        else
            bucketArray.set(bucketIndex, head.next);

        return head.value;
    }

    /**
     * Retrieves the value associated with a given key.
     *
     * @param key the key whose value is to be retrieved
     * @return the value associated with the key, or null if the key is not found
     */
    public V get(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        HashNode<K, V> head = bucketArray.get(bucketIndex);

        // Search for the key in the chain
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode)
                return head.value;
            head = head.next;
        }

        return null; // Key not found
    }

    /**
     * Adds a key-value pair to the hash map. If the key already exists, updates its value.
     *
     * @param key the key to be added
     * @param value the value to be associated with the key
     */
    public void add(K key, V value) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);
        HashNode<K, V> head = bucketArray.get(bucketIndex);

        // Check if the key already exists
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode) {
                head.value = value; // Update value
                return;
            }
            head = head.next;
        }

        // Insert the new key-value pair
        size++;
        head = bucketArray.get(bucketIndex);
        HashNode<K, V> newNode = new HashNode<K, V>(key, value, hashCode);
        newNode.next = head;
        bucketArray.set(bucketIndex, newNode);

        // Rehash if the load factor exceeds 1.0
        if ((1.0 * size) / numBuckets >= 1.0) {
            rehash();
        }
    }

    /**
     * Doubles the capacity of the hash map and rehashes all key-value pairs.
     */
    public void rehash(){
        ArrayList<HashNode<K, V>> temp = bucketArray;
        bucketArray = new ArrayList<>();
        numBuckets = 2 * numBuckets;
        size = 0;

        for (int i = 0; i < numBuckets; i++)
            bucketArray.add(null);

        for (HashNode<K, V> headNode : temp) {
            while (headNode != null) {
                add(headNode.key, headNode.value);
                headNode = headNode.next;
            }
        }
    }
}
