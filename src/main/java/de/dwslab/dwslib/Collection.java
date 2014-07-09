package de.dwslab.dwslib;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Collection {

	/**
	 * @param <T>
	 * @param args
	 */
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K extends Object, V extends Comparable<V>> LinkedHashMap<K, V> sortHashMapByValue(HashMap<K, V> map) {
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
		// TODO Auto-generated method stub

	}

}
