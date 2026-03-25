package implementations;

import utilities.Iterator;
import utilities.ListADT;
import java.util.NoSuchElementException;
import java.util.Arrays;


public class MyArrayList<E> implements ListADT<E> {

	private static final int DEFAULT_CAPACITY = 10;

	private E[] elements;
	private int size;

	public MyArrayList()
	{
		elements = (E[]) new Object[DEFAULT_CAPACITY];
		size = 0;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public void clear() {
		elements = null;
		size = 0;
		
	}

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

	@Override
	public E get(int index) throws IndexOutOfBoundsException {
		if(index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		return elements[index]; 
	}

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

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		if(size == 0) {
			return true;
		}
		return false;
	}

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

	@Override
	public Object[] toArray() {
		Object[] arr = new Object[size];

		for (int i = 0; i < size; i++) {
			arr[i] = elements[i];
		}

		return arr;
	}

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

