package hotel.utils.map;

import java.util.List;

/**
 * A small map-like interface made with lists.
 * Implementations are expected to maintain the insertion order of keys.
 * Null keys or values are not allowed; operations with nulls should return false or null as documented.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface Map<K, V> {

    /**
     * Put a key-value pair into the map. If the key already exists, replace its value.
     *
     * @param key   non-null key
     * @param value non-null value
     * @return true if the operation succeeded; false if key or value is null or the operation failed
     */
    boolean put(K key, V value);

    /**
     * Remove the entry associated with the given key.
     *
     * @param key non-null key
     * @return true if the key existed and was removed; false if the key was not found
     */
    boolean remove(K key);

    /**
     * Get the value for the given key.
     *
     * @param key non-null key
     * @return the value associated with the key, or null if not present
     */
    V get(K key);

    /**
     * Return a snapshot of all keys in insertion order.
     *
     * @return unmodifiable list or copy of keys
     */
    List<K> keys();

    /**
     * Check whether the given key exists in the map.
     *
     * @param key non-null key
     * @return true if present, false otherwise
     */
    boolean contains(K key);
}
