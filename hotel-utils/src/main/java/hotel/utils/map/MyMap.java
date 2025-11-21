package hotel.utils.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple Map implementation with two lists that maintain index correspondence between keys and values. The insertion order of keys is preserved.
 * Null keys and values are not allowed. For {@link #put(Object, Object)} with null arguments this implementation returns false. Other methods will return null/false if null keys are provided.
 */
public class MyMap<K, V> implements Map<K, V> {

    private final List<K> keys = new ArrayList<>();
    private final List<V> values = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean put(K key, V value) {
        if (key == null || value == null) {
            return false;
        }
        int idx = indexOfKey(key);
        if (idx >= 0) {
            values.set(idx, value);
            return true;
        }
        keys.add(key);
        values.add(value);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(K key) {
        if (key == null) {
            return false;
        }
        int idx = indexOfKey(key);
        if (idx >= 0) {
            keys.remove(idx);
            values.remove(idx);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        int idx = indexOfKey(key);
        return idx >= 0 ? values.get(idx) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<K> keys() {
        return Collections.unmodifiableList(new ArrayList<>(keys));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(K key) {
        if (key == null) return false;
        return indexOfKey(key) >= 0;
    }

    private int indexOfKey(K key) {
        for (int i = 0; i < keys.size(); i++) {
            K k = keys.get(i);
            if (k == null) {
                // shouldn't happen since null keys are not allowed
                continue;
            }
            if (k.equals(key)) {
                return i;
            }
        }
        return -1;
    }
}
