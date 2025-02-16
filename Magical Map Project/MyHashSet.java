import java.util.ArrayList;
import java.util.Objects;

/**
 * Implementation of a generic hash set using seperate chaining for collision handling.
 *
 * @param <K> the type of keys maintained by this map
 */
public class MyHashSet<K> {

    /**
     * Represents a single node in the hash set chain.
     *
     * @param <K> the type of the key
     */
    private static class HashNode<K> {
        K key;                // The key for this node
        final int hashCode;   // The hash code of the key
        HashNode<K> next;     // Pointer to the next node in the chain

        /**
         * Constructs a new hash node.
         *
         * @param key the key of the node
         * @param hashCode the hash code of the key
         */
        public HashNode(K key, int hashCode) {
            this.key = key;
            this.hashCode = hashCode;
        }
    }

    private ArrayList<HashNode<K>> bucketArray; // Array list for hash nodes
    private int numBuckets; // Current capacity of the array list
    private int size; // Number of elements in the hash map

    /**
     * Constructs a new hash map with an initial capacity of 10.
     */
    public MyHashSet() {
        bucketArray = new ArrayList<>();
        numBuckets = 31;
        size = 0;

        for (int i = 0; i < numBuckets; i++)
            bucketArray.add(null);
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
     * Retrieves the value associated with a given key.
     *
     * @param key the key whose value is to be retrieved
     * @return the value associated with the key, or null if the key is not found
     */
    public boolean contains(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        HashNode<K> head = bucketArray.get(bucketIndex);

        // Search for the key in the chain
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode)
                return true;
            head = head.next;
        }

        return false; // Key not found
    }

    /**
     * Adds a key-value pair to the hash map. If the key already exists, updates its value.
     *
     * @param key the key to be added
     */
    public void insert(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);
        HashNode<K> head = bucketArray.get(bucketIndex);

        // Check if the key already exists
        while (head != null) {
            if (head.key.equals(key) && head.hashCode == hashCode) {
                return;
            }
            head = head.next;
        }

        // Insert the new key
        size++;
        head = bucketArray.get(bucketIndex);
        HashNode<K> newNode = new HashNode<K>(key, hashCode);
        newNode.next = head;
        bucketArray.set(bucketIndex, newNode);

        // Rehash if the load factor exceeds 1.0
        if ((1.0 * size) / numBuckets >= 1.0) {
            rehash();
        }
    }

    /**
     * Removes a key-value pair from the hash map.
     *
     * @param key the key to be removed
     * @return the value associated with the removed key, or null if the key is not found
     */
    public K remove(K key) {
        int bucketIndex = getBucketIndex(key);
        int hashCode = hashCode(key);

        HashNode<K> head = bucketArray.get(bucketIndex);
        HashNode<K> prev = null;

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

        return head.key;
    }

    /**
     * Doubles the capacity of the hash map and rehashes all key-value pairs.
     */
    public void rehash(){
        ArrayList<HashNode<K>> temp = bucketArray;
        bucketArray = new ArrayList<>();
        numBuckets = 2 * numBuckets;
        size = 0;

        for (int i = 0; i < numBuckets; i++)
            bucketArray.add(null);

        for (HashNode<K> headNode : temp) {
            while (headNode != null) {
                insert(headNode.key);
                headNode = headNode.next;
            }
        }
    }
}