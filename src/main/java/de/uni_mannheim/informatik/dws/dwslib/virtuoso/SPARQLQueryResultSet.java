package de.uni_mannheim.informatik.dws.dwslib.virtuoso;

import java.io.Serializable;
import java.util.*;

/**
 * Provides access to the result of a SPARQL query executed using {@link Query}.
 */
public class SPARQLQueryResultSet implements List<HashMap<String, String>>, Serializable, java.util.RandomAccess, java.lang.Cloneable {
    private static final long serialVersionUID = 2016266566233785509L;
    private String[] columnNames;
    private ArrayList<HashMap<String,String>> list;

    public String[] getColumnNames() {
        return columnNames;
    }

    public SPARQLQueryResultSet(int initialCapacity, String[] columnNames) {
        list = new ArrayList<HashMap<String, String>>(initialCapacity);
        this.columnNames = columnNames;
    }

    public SPARQLQueryResultSet(String[] columnNames) {
        list = new ArrayList<HashMap<String, String>>();
        this.columnNames = columnNames;
    }

    public SPARQLQueryResultSet(Collection<? extends HashMap<String, String>> c,
                                String[] columnNames) {
        list = new ArrayList<HashMap<String, String>>(c);
        this.columnNames = columnNames;
    }

    public void trimToSize() {
        list.trimToSize();
    }

    public void ensureCapacity(int minCapacity) {
        list.ensureCapacity(minCapacity);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public Object clone() {
        return list.clone();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    @Override
    public HashMap<String, String> get(int index) {
        return list.get(index);
    }

    @Override
    public HashMap<String, String> set(int index, HashMap<String, String> element) {
        return list.set(index, element);
    }

    @Override
    public boolean add(HashMap<String, String> stringStringHashMap) {
        return list.add(stringStringHashMap);
    }

    @Override
    public void add(int index, HashMap<String, String> element) {
        list.add(index, element);
    }

    @Override
    public HashMap<String, String> remove(int index) {
        return list.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean addAll(Collection<? extends HashMap<String, String>> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index,
                          Collection<? extends HashMap<String, String>> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public ListIterator<HashMap<String, String>> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public ListIterator<HashMap<String, String>> listIterator() {
        return list.listIterator();
    }

    @Override
    public Iterator<HashMap<String, String>> iterator() {
        return list.iterator();
    }

    @Override
    public List<HashMap<String, String>> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public String toString() {
        return list.toString();
    }


}
