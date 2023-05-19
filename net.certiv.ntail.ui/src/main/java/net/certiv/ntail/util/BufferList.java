package net.certiv.ntail.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A max-size limited list of Objects. If an item is inserted into a full list the
 * oldest/left-most item will be removed. The insert is performed before the list is
 * trimmed to the max-size limit.
 */
public class BufferList extends ArrayList<Object> {

	private static final long serialVersionUID = -3006340427893792599L;
	private int sizeLimit = 1;

	public BufferList(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public synchronized int getSizeLimit() {
		return sizeLimit;
	}

	public synchronized void setSizeLimit(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public synchronized boolean isFull() {
		return (size() >= sizeLimit);
	}

	@Override
	public synchronized Object get(int idx) {
		return super.get(idx);
	}

	@Override
	public synchronized void clear() {
		super.clear();
	}

	@Override
	public synchronized boolean add(Object element) {
		if (isFull()) remove(0);
		return super.add(element);
	}

	@Override
	public synchronized void add(int idx, Object element) {
		if (isFull()) remove(0);
		super.add(idx, element);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public synchronized boolean addAll(Collection c) {
		if (super.addAll(c)) {
			if (isFull()) removeRange(0, size() - sizeLimit);
			return true;
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public synchronized boolean addAll(int index, Collection c) {
		if (super.addAll(index, c)) {
			if (isFull()) removeRange(0, size() - sizeLimit);
			return true;
		}
		return false;
	}

	@Override
	public synchronized void ensureCapacity(int minCapacity) {
		super.ensureCapacity(minCapacity > sizeLimit ? sizeLimit : minCapacity);
	}

	/**
	 * Return the contents of the list formatted as a string. Each item in the list
	 * contributes one line to the string by calling it's toString() method.
	 * 
	 * @return String
	 */
	public synchronized String getFormattedText() {
		StringBuffer sb = new StringBuffer();
		for (Object o : this) {
			sb.append(o + "\n");
		}
		return sb.toString();
	}

	/** Debugging method. */
	public synchronized void dump() {
		System.out.println("List contents");
		int count = 1;
		for (Object o : this) {
			System.out.println(count++ + ": " + o);
		}
	}
}
