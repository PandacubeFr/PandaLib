package fr.pandacube.lib.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class BiMap<K, V> implements Iterable<Entry<K, V>> {

    protected Map<K, V> map;
    protected Map<V, K> inversedMap;
    
    private BiMap<V, K> reversedView = null;
    
    @SuppressWarnings("unchecked")
	public BiMap(Supplier<Map<?, ?>> mapSupplier) {
    	map = (Map<K, V>) mapSupplier.get();
    	inversedMap = (Map<V, K>) mapSupplier.get();
    }
    
    public BiMap() {
    	this(HashMap::new);
	}
    
    public BiMap(Map<K, V> source) {
    	this();
    	putAll(source);
    }
    
    /*
     * Only used for #reversedView()
     */
    private BiMap(BiMap<V, K> rev) {
    	map = rev.inversedMap;
    	inversedMap = rev.map;
    	reversedView = rev;
    }

    public synchronized void put(K k, V v) {
    	if (containsKey(k))
    		remove(k);
    	if (containsValue(v))
    		removeValue(v);
        map.put(k, v);
        inversedMap.put(v, k);
    }
    
    public synchronized void putAll(Map<? extends K, ? extends V> source) {
    	for (Map.Entry<? extends K, ? extends V> e : source.entrySet()) {
    		put(e.getKey(), e.getValue());
    	}
    }

    public synchronized V get(K k) {
        return map.get(k);
    }

    public synchronized K getKey(V v) {
        return inversedMap.get(v);
    }
    
    public synchronized boolean containsKey(K k) {
    	return map.containsKey(k);
    }
    
    public synchronized boolean containsValue(V v) {
    	return inversedMap.containsKey(v);
    }
    
    public synchronized V remove(K k) {
    	V v = map.remove(k);
    	inversedMap.remove(v);
    	return v;
    }
    
    public synchronized K removeValue(V v) {
    	K k = inversedMap.remove(v);
    	map.remove(k);
    	return k;
    }
    
    @Override
    public Iterator<Entry<K, V>> iterator() {
    	return Collections.unmodifiableSet(map.entrySet()).iterator();
    }
    
    public Set<K> keySet() {
    	return Collections.unmodifiableSet(map.keySet());
    }
    
    public Set<V> valuesSet() {
    	return Collections.unmodifiableSet(inversedMap.keySet());
    }
    
    public Map<K, V> asMap() {
    	return Collections.unmodifiableMap(map);
    }
    
    public BiMap<V, K> reversedView() {
    	if (reversedView == null)
    		reversedView = new BiMap<>(this);
    	return reversedView;
    }
    
    public synchronized void forEach(BiConsumer<K, V> c) {
    	for(Entry<K, V> entry : this) {
    		c.accept(entry.getKey(), entry.getValue());
    	}
    }
    
    public int size() {
    	return map.size();
    }
    
    public synchronized void clear() {
    	map.clear();
    	inversedMap.clear();
    }
    

}
