import exceptions.DuplicateKeyException;
import exceptions.NotFoundException;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.function.Function;

public class HM<K,V> {
    private int capacity, size;
    private PList<Pair<K,V>>[] table;
    private Function<K,Integer> hashFunction;

    HM () {
        this(11);
    }
    @SuppressWarnings("unchecked")
    HM (int capacity) {
        this.capacity = capacity;
        this.table = (PList<Pair<K,V>>[]) Array.newInstance(PList.class, capacity);
        for (int i = 0; i < capacity; i++) table[i] = new EmptyPList<>();
        this.hashFunction = k -> k.hashCode() % capacity;
        this.size = 0;
    }

    /**
     * Returns the number of key-value mappings in this map.
     * O(1) operation
     */
    public int size () {
        return this.size;
    }

    /**
     * Returns the ratio of size / capacity
     * O(1) operation
     */
    public double loadFactor () {
        return (double) size / capacity;
    }

    /**
     * Removes all the key-value pairs from this map.
     * O(n) operation where n is the capacity of the map
     */
    public void clear () {
        for(int i = 0; i < capacity; i++) table[i] = new EmptyPList<>();
        size = 0;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     * O(1) operation
     */
    public boolean isEmpty () {
        return size == 0;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or throws NotFoundException if this map contains no mapping
     * for the key.
     * <p>
     * The complexity depends on the number of collisions for this
     * key in the hash table.
     */
    public V get (K key) throws NotFoundException {
        for(Pair<K,V> pair : table[hashFunction.apply(key)]) {
            if (pair.key().equals(key)) return pair.value();
        }
        throw new NotFoundException();
    }

    /**
     * Checks if the map contains the given key.
     * <p>
     * The complexity depends on the number of collisions for this
     * key in the hash table.
     */
    /**
     * Checks if the map contains the given key.
     * <p>
     * The complexity depends on the number of collisions for this
     * key in the hash table.
     */
    boolean containsKey(K key) {
        int index = hashFunction.apply(key); // Calculate the hash code of the key
        PList<Pair<K, V>> list = table[index]; // Get the linked list at the index

        // Check if the list is empty (no collisions)
        if (list.isEmpty()) {
            // If the list is empty, check the key at that index
            for (Pair<K, V> pair : list) {
                if (pair.key().equals(key)) {
                    return true; // Found a matching key
                }
            }
        } else {
            // If there are collisions (non-empty list), traverse the linked list
            for (Pair<K, V> pair : list) {
                if (pair.key().equals(key)) {
                    return true; // Found a matching key within the collision chain
                }
            }
        }

        // Key not found in the map
        return false;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, an exception
     * is thrown.
     * If the load factor exceeds 0.75, the map is rehashed.
     * <p>
     * The complexity depends on the number of collisions for this
     * key in the hash table.
     */
    void put (K key, V value) throws DuplicateKeyException {
        // check load factor...
        if (loadFactor() > 0.75) rehash();
        //System.out.printf("Hashing %s to %d\n", key, hashFunction.apply(key));
        int index = hashFunction.apply(key);
        // check if key already exists...
        for(Pair<K,V> pair : table[index]) {
            if (pair.key().equals(key)) throw new DuplicateKeyException();
        }
        PList<Pair<K,V>> list = table[index];
        PList<Pair<K,V>> newList = list.addFront(new Pair<>(key, value));
        table[index] = newList;
        size++;
    }

    /**
     * Checks if the map contains the given value.
     * <p>
     * O(n) operation where n depends on both the capacity of the
     * table and the total number of key-value pairs in the map.
     */
    boolean containsValue(V value) {
        for (int i = 0; i < capacity; i++) {
            PList<Pair<K, V>> list = table[i]; // Get the linked list at the index

            // Traverse the linked list at the current index
            for (Pair<K, V> pair : list) {
                if (pair.value().equals(value)) {
                    return true; // Found a matching value
                }
            }
        }

        // Value not found in the map
        return false;
    }

    /**
     * Allocates a new table of approximately twice the capacity (the next
     * prime after twice the current capacity) and rehashes the key-value
     * pairs from the old table to the new table.
     * <p>
     * O(n) operation where n depends on both the capacity of the
     * table and the total number of key-value pairs in the map.
     */
    @SuppressWarnings("unchecked")
    void rehash() {
        int newCapacity = BigInteger.valueOf(capacity * 2).nextProbablePrime().intValue();
        PList<Pair<K, V>>[] newTable = (PList<Pair<K, V>>[]) Array.newInstance(PList.class, newCapacity);
        for (int i = 0; i < newCapacity; i++) newTable[i] = new EmptyPList<>();

        int newSize = 0; // Initialize the size for the new table

        for (int i = 0; i < capacity; i++) {
            for (Pair<K, V> pair : table[i]) {
                int index = hashFunction.apply(pair.key());
                newTable[index] = newTable[index].addFront(pair);
                newSize++; // Increment the size for each added pair
            }
        }

        capacity = newCapacity;
        table = newTable;
        size = newSize; // Update the size based on the number of key-value pairs in the new table
    }

    /**
     * For convenience
     */
    public String toString () {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            sb.append(String.format("table[%d]: ", i));
            for (Pair<K,V> pair : table[i]) {
                sb.append(String.format("{%s , %s} ", pair.key(), pair.value()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
