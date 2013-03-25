package de.uni_mannheim.informatik.dws.dwslib;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Counter{

	/**
	 * @param args
	 */
	
	private HashMap<Object, Integer> map = null;
	
	public Counter(Iterable<?> i) {
		this();
		addAll(i);
	}
	
	public Counter() {
		this.map = new HashMap<Object, Integer>();
	}
	
	public void addAll(Iterable<?> i) {
		for (Object o: i) {
			if (map.containsKey(o)) {
				map.put(o, map.get(o) + 1);
			} else {map.put(o, 1);}
		}
	}
	
	public void add(Object o) {
		if (map.containsKey(o)) {
			map.put(o, map.get(o) + 1);
		} else {map.put(o, 1);}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LinkedHashMap<Object,Integer> mostCommon() {
		LinkedHashMap<Object, Integer> orderedMap = new LinkedHashMap<Object, Integer>();
		List list = new LinkedList(this.map.entrySet());
		
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			orderedMap.put(entry.getKey(), (Integer) entry.getValue());
		}
		return orderedMap;
			
	}
	
	@Override
	public String toString() {
		return mostCommon().toString();
	}

	public static void main(String[] args) {
		Vector<String> v = new Vector<String>();
		v.add("String");
		v.add("Micha");
		v.add("Micha");
		v.add("Daniel");
		v.add("Daniel");
		v.add("Cilli");
		v.add("Micha");
		Counter c = new Counter(v);
		System.out.println(c);
	}

}
