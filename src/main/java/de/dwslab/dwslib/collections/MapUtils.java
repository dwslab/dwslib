package de.dwslab.dwslib.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.dwslab.dwslib.models.SortingOrderTypes;

/**
 * Frequently used static Utils methods for {@link Map}
 * @author Michael Schuhmacher
 *
 */
public class MapUtils {	
	
	/**
	 * Sorts a map by their values using the comparator of the value object.
	 * @param map The map to be sorted
	 * @param sortingOrder The {@link SortingOrderTypes}: <br> 
	 * {@code SortingOrderTypes.ASCENDING} -> 1,2,3,4 <br>
	 * {@code SortingOrderTypes.DESCENDING} -> 4,3,2,1
	 * @return The sorted map as {@code LinkedHashMap}.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K extends Object, V extends Comparable<V>> Map<K, V> sortByValue(
			Map<K, V> map, final SortingOrderTypes sortingOrder) {
		LinkedHashMap<K, V> orderedMap = new LinkedHashMap<K, V>();
		List list = new LinkedList(map.entrySet());
		
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				int val = ((Comparable) ((Map.Entry) (o1)).getValue()) .compareTo(((Map.Entry) (o2)).getValue());
				if (sortingOrder == SortingOrderTypes.ASCENDING)
					return val;
				else if (sortingOrder == SortingOrderTypes.DESCENDING)
					return -val;
				else 
					return 0;
			}
		});
		
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			orderedMap.put((K) entry.getKey(), (V) entry.getValue());
		}
		return orderedMap;
	}
	
	
	public static void main(String[] args) {
		
		System.out.println("Demonstrating usage of sortByValueAsc() and sortByValueDsc() ...");
        
		//creating Hashtable for sorting
        Map<String, Integer> olympic2012 = new HashMap<String, Integer>();
     
        olympic2012.put("England", 3);
        olympic2012.put("USA", 1);
        olympic2012.put("China", 2);
        olympic2012.put("Russia", 4);
        olympic2012.put("Australia", 4); //adding duplicate value
     
        //printing hashtable without sorting
        System.out.println("Unsorted Map in Java : " + olympic2012);
     
        //sorting Map by keys
        Map<String, Integer> sortedAsc = sortByValue(olympic2012, SortingOrderTypes.ASCENDING);
        System.out.println("Ascending sorted Map in Java by key: " + sortedAsc);
        
        //sorting Map by keys
        Map<String, Integer> sortedDsc = sortByValue(olympic2012, SortingOrderTypes.DESCENDING);
        System.out.println("Descending sorted Map in Java by key: " + sortedDsc);

	}

}
