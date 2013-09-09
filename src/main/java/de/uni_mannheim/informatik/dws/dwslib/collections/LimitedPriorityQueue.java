package de.uni_mannheim.informatik.dws.dwslib.collections;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 
 * This is the implementation of a {@link PriorityQueue} which has a limited
 * size. The queue will hold always the top elements based on the given
 * {@link Comparator} or the natural ordering of the Elements.
 * 
 * @author Robert Meusel (robert@informatik.uni-mannheim.de
 */
public class LimitedPriorityQueue<E> extends PriorityQueue<E> {

	private static final long serialVersionUID = 1L;
	private int limit;
	private E head;

	public LimitedPriorityQueue(int limit) {
		super();
		this.limit = limit;
	}

	public LimitedPriorityQueue(int limit, Comparator<E> comp) {
		super(limit, comp);
		this.limit = limit;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E e) {
		if (super.size() < limit
				|| ((super.comparator() != null ? super.comparator().compare(
						head, e) : ((Comparable<E>) head).compareTo(e)) < 0)) {
			boolean ret = super.add(e);
			resize();
			return ret;
		}
		return false;
	}

	@Override
	public E peek() {
		resize();
		return super.peek();
	}

	@Override
	public E poll() {
		resize();
		E ret = super.poll();
		head = super.peek();
		return ret;
	}

	private void resize() {
		while (super.size() > limit) {
			super.poll();
		}
		head = super.peek();
	}

	/*
	 * Output is: 4, 5, 8, 10, 13
	 */
	public static void main(String[] args) {
		LimitedPriorityQueue<Integer> q = new LimitedPriorityQueue<Integer>(5);
		q.add(1);
		q.add(2);
		q.add(13);
		q.add(3);
		q.add(8);
		q.add(4);
		q.add(10);
		q.add(5);
		while (!q.isEmpty()) {
			System.out.println(q.poll());
		}
	}
}
