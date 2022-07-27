package fr.pandacube.lib.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A bi-direction map storing in a synchronized way a {@code forwardMap} that store the key to value mapping, and a
 * {@code backwardMap} that store the value to key mapping.
 * All the keys and value are always unique in this bi-directional map.
 * @param <K> the type of the "key"
 * @param <V> the type of the "value"
 */
public class BiMap<K, V> implements Iterable<Entry<K, V>> {

    /**
     * The backend forward map
     */
    protected final Map<K, V> forwardMap;

    /**
     * The backend bawkward map
     */
    protected final Map<V, K> backwardMap;

    private BiMap<V, K> reversedView = null;

    private final Object lock;

    /**
     * Create a new bi-directional map
     * @param mapSupplier a {@link Supplier} to create the two backend map.
     */
    @SuppressWarnings("unchecked")
	public BiMap(Supplier<Map<?, ?>> mapSupplier) {
    	forwardMap = (Map<K, V>) mapSupplier.get();
    	backwardMap = (Map<V, K>) mapSupplier.get();
        lock = new Object();
    }

    /**
     * Create a new bi-directional map with {@link HashMap} as the two backend maps.
     */
    public BiMap() {
    	this(HashMap::new);
	}

    /**
     * Create a new bi-directional map with {@link HashMap} as the two backend maps, and filled with the provided source
     * {@link Map}.
     * @param source the source to fill the new {@link BiMap}.
     */
    public BiMap(Map<K, V> source) {
    	this();
    	putAll(source);
    }

    /*
     * Only used for #reversedView()
     */
    private BiMap(BiMap<V, K> rev) {
    	forwardMap = rev.backwardMap;
    	backwardMap = rev.forwardMap;
        lock = rev.lock;
    	reversedView = rev;
    }

    /**
     * Associate the provided key and value to each other in this bi-directional map.
     * Since the {@link BiMap} cannot have duplicate keys or values: if a key is already present, the currently mapped
     * value will be removed from the map. Also if a value is already present, the currently mapped key will also be
     * removed.
     * @param k the key.
     * @param v the value.
     */
    public void put(K k, V v) {
        synchronized (lock) {
            if (containsKey(k))
                remove(k);
            if (containsValue(v))
                removeValue(v);
            forwardMap.put(k, v);
            backwardMap.put(v, k);
        }
    }


    /**
     * Associate the provided key and value to each other in this bi-directional map.
     * Since the {@link BiMap} cannot have duplicate keys or values: if a key is already present, the currently mapped
     * value will be removed from the map. Also if a value is already present, the currently mapped key will also be
     * removed.
     * @param entry the entry with a key and value.
     */
    public void put(Entry<? extends K, ? extends V> entry) {
        put(entry.getKey(), entry.getValue());
    }

    /**
     * Put the content of the provided map into this bi-directional map.
     * This methods will call the {@link #put(Entry)} method successively in the order of the provided Map’s iterator.
     * @param source the source map.
     */
    public void putAll(Map<? extends K, ? extends V> source) {
        synchronized (lock) {
            source.entrySet().forEach(this::put);
        }
    }

    /**
     * Gets the mapped value for the provided key.
     * @param k the key.
     * @return the value mapped with the key.
     */
    public V get(K k) {
        synchronized (lock) {
            return forwardMap.get(k);
        }
    }

    /**
     * Gets the mapped key for the provided value.
     * @param v the value.
     * @return the key mapped with the value.
     */
    public K getKey(V v) {
        synchronized (lock) {
            return backwardMap.get(v);
        }
    }

    /**
     * Tells if this {@link BiMap} contains the provided key.
     * @param k the key to test if it’s present.
     * @return true if this bimap contains the provided key, false otherwise.
     */
    public boolean containsKey(K k) {
        synchronized (lock) {
            return forwardMap.containsKey(k);
        }
    }

    /**
     * Tells if this {@link BiMap} contains the provided value.
     * @param v the value to test if it’s present.
     * @return true if this bimap contains the provided value, false otherwise.
     */
    public boolean containsValue(V v) {
        synchronized (lock) {
            return backwardMap.containsKey(v);
        }
    }

    /**
     * Remove the mapping of the provided key from this map.
     * The mapped value is also removed in the internal backward map.
     * @param k the key whose mapping is to be removed from the map.
     * @return the value that was mapped with the provided key.
     */
    public V remove(K k) {
        synchronized (lock) {
            V v = forwardMap.remove(k);
            backwardMap.remove(v);
            return v;
        }
    }

    /**
     * Remove the mapping of the provided value from this map.
     * The mapped key is also removed in the internal forward map.
     * @param v the value whose mapping is to be removed from the map.
     * @return the key that was mapped with the provided value.
     */
    public K removeValue(V v) {
        synchronized (lock) {
            K k = backwardMap.remove(v);
            forwardMap.remove(k);
            return k;
        }
    }

    /**
     * Returns an unmodifiable {@link Set} view of this map.
     * It’s iteration order will depends on the implementation of the {@code forwardMap}.
     * @return an unmodifiable {@link Set} view of this map.
     * @see Map#entrySet()
     */
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(forwardMap.entrySet());
    }

    /**
     * Returns an iterator of this map.
     * It’s iteration order will depends on the implementation of the {@code forwardMap}.
     * @return an iterator of this map.
     * @see Map#entrySet()
     * @see Set#iterator()
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
    	return entrySet().iterator();
    }

    /**
     * Returns an unmodifiable {@link Set} view of the keys contained in this map.
     * It’s iteration order will depends on the implementation of the {@code forwardMap}’s key set.
     * @return an unmodifiable {@link Set} view of the keys contained in this map.
     * @see Map#keySet()
     */
    public Set<K> keySet() {
    	return Collections.unmodifiableSet(forwardMap.keySet());
    }

    /**
     * Returns an unmodifiable {@link Set} view of the values contained in this map.
     * It’s iteration order will depends on the implementation of the {@code backwardMap}’s key set.
     * @return an unmodifiable {@link Set} view of the values contained in this map.
     * @see Map#keySet()
     */
    public Set<V> valuesSet() {
    	return Collections.unmodifiableSet(backwardMap.keySet());
    }

    /**
     * Returns an unmodifiable {@link Map} view of the {@code forwardMap} of this bi-directional map.
     * It’s iteration order will depends on the implementation of the {@code forwardMap}.
     * @return an unmodifiable {@link Map} view of the {@code forwardMap} of this bi-directional map.
     */
    public Map<K, V> asMap() {
    	return Collections.unmodifiableMap(forwardMap);
    }

    /**
     * Create a reversed view of this bi-directional map.
     * Since the returned {@link BiMap} is a view of this {@link BiMap}, any change to either of those will affect both
     * of them. Also, calling {@code bimap.reversedView().reversedView()} will return the original instance
     * {@code bimap} since calling this method will cache each map into the respective reversed view.
     * @return the reversed view of this {@link BiMap}.
     */
    public BiMap<V, K> reversedView() {
        synchronized (lock) {
            if (reversedView == null)
                reversedView = new BiMap<>(this);
            return reversedView;
        }
    }

    /**
     * Performs the provided action for each entry of this map, following the iteration order of the internal {@code forwardMap}.
     * @param action the action to perform on each entry.
     * @see Map#forEach(BiConsumer)
     */
    public void forEach(BiConsumer<K, V> action) {
        synchronized (lock) {
            forwardMap.forEach(action);
        }
    }

    /**
     * Returns the number of key-value mapping in this map.
     * @return the number of key-value mapping in this map.
     * @see Map#size()
     */
    public int size() {
        synchronized (lock) {
            return forwardMap.size();
        }
    }

    /**
     * Removes all the mapping from this map.
     */
    public void clear() {
        synchronized (lock) {
            forwardMap.clear();
            backwardMap.clear();
        }
    }
    

}
