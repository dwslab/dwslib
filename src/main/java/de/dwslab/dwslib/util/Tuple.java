package de.dwslab.dwslib.util;

/**
 * A generic tuple class. Contains an (ordered) pair of values. This implementation uses uncached hashcode and toString
 * methods and thus is safe to use with mutable element values.
 *
 * @author Daniel Fleischhacker
 * @version 1.1 (Michael)
 */
public class Tuple<T, S> {
    private T firstElement;
    private S secondElement;

    /**
     * Initialize the tuple with the given elements.
     * @param firstElement first element of this tuple object
     * @param secondElement second element of this tuple object
     */
    public Tuple(T firstElement, S secondElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
    }

    /**
     * Returns the first element of this tuple.
     * @return first element
     */
    public T getFirstElement() {
        return firstElement;
    }

    /**
     * Returns the second element of this tuple.
     * @return second element
     */
    public S getSecondElement() {
        return secondElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tuple tuple = (Tuple) o;

        if (firstElement != null ? !firstElement.equals(tuple.firstElement) : tuple.firstElement != null) {
            return false;
        }
        if (secondElement != null ? !secondElement.equals(tuple.secondElement) : tuple.secondElement != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstElement != null ? firstElement.hashCode() : 0;
        result = 31 * result + (secondElement != null ? secondElement.hashCode() : 0);
        return result;
    }

	@Override
	public String toString() {
		return "Tuple <" + firstElement + ", " + secondElement + ">";
	}
    
    
}
