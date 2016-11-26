package fr.pandacube.java.util;

import java.util.HashMap;

public class BiMap<K,V> {

    HashMap<K, V> map = new HashMap<>();
    HashMap<V, K> inversedMap = new HashMap<>();

    public synchronized void put(K k, V v) {
    	if (map.containsKey(k) || inversedMap.containsKey(v)) {
    		map.remove(k);
    		inversedMap.remove(v);
    	}
        map.put(k, v);
        inversedMap.put(v, k);
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

}
