package de.dwslab.dwslib.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Michael Schuhmacher
 *
 */
public class MapUtils {
	
	/**
	 * Sorts a map by values using comparator of the value object.
	 * @param map
	 * @return The sorted map as {@link LinkedHashMap}.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K extends Object, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map) {
		LinkedHashMap<K, V> orderedMap = new LinkedHashMap<K, V>();
		List list = new LinkedList(map.entrySet());
		
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			orderedMap.put((K) entry.getKey(), (V) entry.getValue());
		}
		return orderedMap;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println("Demonstrating usage of sortByValue() ...");
        
		//creating Hashtable for sorting
        Map<String, Integer> olympic2012 = new HashMap<String, Integer>();
     
        olympic2012.put("England", 3);
        olympic2012.put("USA", 1);
        olympic2012.put("China", 2);
        olympic2012.put("Russia", 4);
        olympic2012.put("Australia", 4); //adding duplicate value
     
        //printing hashtable without sorting
        System.out.println("Unsorted Map in Java : " + olympic2012);
     
        //sorting Map e.g. HashMap, Hashtable by keys in Java
        Map<String, Integer> sorted = sortByValue(olympic2012);
        System.out.println("Sorted Map in Java by key: " + sorted);

	}

}
