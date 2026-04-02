/**
 * @author Santiago, Kaley Wood, Asad, Dylan
 * Southern Alberta Institute of Technology: CPRG-304-B
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 03.26.2026
 *
 * MyArrayList -- a generic, resizable array-backed list that implements ListADT.
 * Starts at a capacity of 10 and doubles whenever it runs out of room.
 */
package implementations;

import utilities.Iterator;
import utilities.ListADT;
import java.util.NoSuchElementException;
import java.util.Arrays;


public class MyArrayList<E> implements ListADT<E> {

	private static final int DEFAULT_CAPACITY = 10;

	private E[] elements;
	private int size;

    /**
     * Creates an empty list with a default capacity of 10.
     */
	public MyArrayList()
	{
		elements = (E[]) new Object[DEFAULT_CAPACITY];
		size = 0;
	}

    /**
     * Returns the number of elements currently in the list.
     *
     * @return current size
     */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

    /**
     * Removes all elements and resets the list to its default capacity.
     * Reinitializes the array so the list stays usable after clearing.
     */
	@Override
	public void clear() {
        elements = (E[]) new Object[DEFAULT_CAPACITY];
        size = 0;
		
	}

    /**
     * Inserts an element at a specific index, shifting everything after it right.
     *
     * @param index  the position to insert at (0 to size, inclusive)
     * @param toAdd  the element to insert
     * @return true if successful
     * @throws NullPointerException      if toAdd is null
     * @throws IndexOutOfBoundsException if index is out of range
     */
	@Override
	public boolean add(int index, E toAdd) throws NullPointerException, IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		if (toAdd == null) {
			throw new NullPointerException();
		}
		if(index < 0 || index > size()) {
			throw new IndexOutOfBoundsException();
		}
		if (size == elements.length) {
			elements = Arrays.copyOf(elements, elements.length * 2);
		}
		for (int i = size; i > index; i--)
		{
			elements[i] = elements[i - 1];
		}
		elements[index] = toAdd;
		size++;
		return true;
	}

    /**
     * Appends an element to the end of the list.
     *
     * @param toAdd  the element to append
     * @return true if successful
     * @throws NullPointerException if toAdd is null
     */
	@Override
	public boolean add(E toAdd) throws NullPointerException {
		if (toAdd == null) {
			throw new NullPointerException();
		}
		if (size == elements.length) {
			elements = Arrays.copyOf(elements, elements.length * 2);
		}
		
		elements[size] = toAdd;
		size++;
		return true;
	}

    /**
     * Appends all elements from another list to the end of this one.
     *
     * @param toAdd  the list of elements to append
     * @return true if successful
     * @throws NullPointerException if toAdd is null
     */
	@Override
	public boolean addAll(ListADT<? extends E> toAdd) throws NullPointerException {
		if (toAdd == null)
		{
			throw new NullPointerException();
		}

		Iterator<? extends E> it = toAdd.iterator();

		while (it.hasNext())
		{
			add(it.next());
		}

		return true;
	}

    /**
     * Returns the element at the given index without removing it.
     *
     * @param index  the position to look up
     * @return the element at that position
     * @throws IndexOutOfBoundsException if index is out of range
     */
	@Override
	public E get(int index) throws IndexOutOfBoundsException {
		if(index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		return elements[index]; 
	}

    /**
     * Removes and returns the element at the given index, shifting everything after it left.
     *
     * @param index  the position to remove
     * @return the element that was removed
     * @throws IndexOutOfBoundsException if index is out of range
     */
	@Override
	public E remove(int index) throws IndexOutOfBoundsException {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException();
		}

		E element = elements[index];

		for (int i = index; i < size - 1; i++) {
			elements[i] = elements[i + 1];
		}

		elements[size - 1] = null;
		size--;

		return element;
	}

    /**
     * Removes the first occurrence of the given element, if it exists.
     *
     * @param toRemove  the element to search for and remove
     * @return the removed element, or null if not found
     * @throws NullPointerException if toRemove is null
     */
	@Override
	public E remove(E toRemove) throws NullPointerException {
		if (toRemove == null) {
			throw new NullPointerException();
		}

		for (int i = 0; i < size; i++) {
			if (elements[i].equals(toRemove)) {
				return remove(i);
			}
		}

		return null;
	}

    /**
     * Replaces the element at the given index with a new value.
     *
     * @param index     the position to update
     * @param toChange  the new value to place there
     * @return the element that was replaced
     * @throws NullPointerException      if toChange is null
     * @throws IndexOutOfBoundsException if index is out of range
     */
	@Override
	public E set(int index, E toChange) throws NullPointerException, IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		if(index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		if(toChange == null) {
			throw new NullPointerException();
		}
		
		E elementToReturn = elements[index]; 
		elements[index] = toChange;
		return elementToReturn;
	}

    /**
     * Returns true if the list has no elements.
     *
     * @return true if size is 0
     */
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		if(size == 0) {
			return true;
		}
		return false;
	}

    /**
     * Returns true if the list contains the given element.
     *
     * @param toFind  the element to search for
     * @return true if found, false otherwise
     * @throws NullPointerException if toFind is null
     */
	@Override
	public boolean contains(E toFind) throws NullPointerException {
		if(toFind == null) {
			throw new NullPointerException();		
		}

		for (int i = 0; i < size; i++) {
			if (elements[i].equals(toFind)) {
				return true;
			}
		}
		return false;
		
	}

    /**
     * Fills the provided array with the list's elements in order.
     * If the array is too small, a new one of the correct size is created.
     *
     * @param toHold  the array to fill
     * @return the filled array
     * @throws NullPointerException if toHold is null
     */
	@Override
	public E[] toArray(E[] toHold) throws NullPointerException {
		if (toHold == null) {
			throw new NullPointerException();
		}

		if (toHold.length < size) {
			toHold = Arrays.copyOf(toHold, size);
		}

		for (int i = 0; i < size; i++) {
			toHold[i] = elements[i];
		}

		return toHold;
	}

    /**
     * Returns a plain Object array containing all elements in order.
     *
     * @return Object array of current elements
     */
	@Override
	public Object[] toArray() {
		Object[] arr = new Object[size];

		for (int i = 0; i < size; i++) {
			arr[i] = elements[i];
		}

		return arr;
	}

    /**
     * Returns an iterator that traverses the list from index 0 to the end.
     * Works on a snapshot of the list taken at the moment it's created.
     *
     * @return an Iterator over the current elements
     */
	@Override
	public Iterator<E> iterator() {
		return new ArrayListIterator();
	}
	private class ArrayListIterator implements Iterator<E>
	{
		private E[] copy;
		private int current;

		public ArrayListIterator()
		{
			copy = (E[]) new Object[size];

			for (int i = 0; i < size; i++)
			{
				copy[i] = elements[i];
			}

			current = 0;
		}

		@Override
		public boolean hasNext()
		{
			return current < copy.length;
		}

		@Override
		public E next() throws NoSuchElementException
		{
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}

			return copy[current++];
		}
	}
}

