/**
 * @author Santiago, Asad, Dylan, Kaley
 * Southern Alberta Institute of Technology: CPRG-304-B
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 03.28.2026
 *
 * MyDLL — a generic doubly linked list backed by MyDLLNode<E>.
 * Each node holds a reference to both its predecessor and successor,
 * so insertion and removal at any position can rewire pointers without
 * shifting elements. Implements the full ListADT contract.
 */

package implementations;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

import utilities.Iterator;
import utilities.ListADT;

public class MyDLL<E> implements ListADT<E>
{
	// the two anchor points — everything else lives between them
	private MyDLLNode<E> head;
	private MyDLLNode<E> tail;
	private int size;
	

	/**
	 * Builds an empty doubly linked list.
	 */
	public MyDLL()
	{
		head = null;
		tail = null;
		size = 0;
	}

	/**
	 * Returns the number of elements currently in the list.
	 *
	 * @return the current element count
	 */
	@Override
	public int size()
	{
		return size;
	}

	/**
	 * Removes all elements and severs every node's links so they can be
	 * garbage collected. The list is empty after this call.
	 */
	@Override
	public void clear()
	{
		MyDLLNode<E> current = head;
		
		// walk forward and null out every reference before moving on
		while( current != null )
		{
			MyDLLNode<E> next = current.getNext();
			current.setElement( null );
			current.setPrevious( null );
			current.setNext( null );
			current = next;
		}

		head = null;
		tail = null;
		size = 0;
	}

	/**
	 * Inserts the given element at the specified position, shifting everything
	 * from that index onward one spot to the right.
	 *
	 * @param index the position to insert at (0 to size, inclusive)
	 * @param toAdd the element to insert
	 * @return true if the insertion succeeded
	 * @throws NullPointerException      if toAdd is null
	 * @throws IndexOutOfBoundsException if index is out of range
	 */
	@Override
	public boolean add( int index, E toAdd ) throws NullPointerException, IndexOutOfBoundsException
	{
		validateElement( toAdd );
		validatePositionIndex( index );

		 
		// appending to the end is just a linkLast — no need to find a successor
		if( index == size )
		{
			linkLast( toAdd );
		}
		else
		{
			linkBefore( toAdd, nodeAt( index ) );
		}

		return true;
	}

	/**
	 * Appends the element to the end of the list.
	 *
	 * @param toAdd the element to append
	 * @return true if the append succeeded
	 * @throws NullPointerException if toAdd is null
	 */
	@Override
	public boolean add( E toAdd ) throws NullPointerException
	{
		validateElement( toAdd );
		linkLast( toAdd );
		return true;
	}

	/**
	 * Appends every element from the given list, in iteration order.
	 *
	 * @param toAdd the list of elements to add
	 * @return true if all elements were added
	 * @throws NullPointerException if toAdd is null
	 */
	@Override
	public boolean addAll( ListADT<? extends E> toAdd ) throws NullPointerException
	{
		if( toAdd == null )
		{
			throw new NullPointerException();
		}

		Iterator<? extends E> iterator = toAdd.iterator();

		while( iterator.hasNext() )
		{
			add( iterator.next() );
		}

		return true;
	}

	/**
	 * Returns the element at the given index without removing it.
	 *
	 * @param index the position to look up
	 * @return the element stored there
	 * @throws IndexOutOfBoundsException if index is out of range
	 */
	@Override
	public E get( int index ) throws IndexOutOfBoundsException
	{
		return nodeAt( index ).getElement();
	}

	/**
	 * Removes and returns the element at the given index.
	 *
	 * @param index the position of the element to remove
	 * @return the removed element
	 * @throws IndexOutOfBoundsException if index is out of range
	 */
	@Override
	public E remove( int index ) throws IndexOutOfBoundsException
	{
		return unlink( nodeAt( index ) );
	}

	/**
	 * Removes the first occurrence of the given element, if present.
	 * Returns null if nothing matched.
	 *
	 * @param toRemove the element to search for and remove
	 * @return the removed element, or null if it wasn't found
	 * @throws NullPointerException if toRemove is null
	 */
	@Override
	public E remove( E toRemove ) throws NullPointerException
	{
		validateElement( toRemove );

		MyDLLNode<E> current = head;
		
		// scan forward until we find a match
		while( current != null )
		{
			if( current.getElement().equals( toRemove ) )
			{
				return unlink( current );
			}

			current = current.getNext();
		}
		
		// nothing matched
		return null;
	}

	/**
	 * Replaces the element at the given index and returns what was there before.
	 *
	 * @param index    the position to update
	 * @param toChange the new value to store
	 * @return the element that was replaced
	 * @throws NullPointerException      if toChange is null
	 * @throws IndexOutOfBoundsException if index is out of range
	 */
	@Override
	public E set( int index, E toChange ) throws NullPointerException, IndexOutOfBoundsException
	{
		validateElement( toChange );
		MyDLLNode<E> node = nodeAt( index );
		E previousValue = node.getElement();
		node.setElement( toChange );
		return previousValue;
	}

	/**
	 * Returns true if the list has no elements.
	 *
	 * @return true if size is zero
	 */
	@Override
	public boolean isEmpty()
	{
		return size == 0;
	}

	/**
	 * Returns true if the list contains at least one element equal to toFind.
	 *
	 * @param toFind the element to search for
	 * @return true if a match was found
	 * @throws NullPointerException if toFind is null
	 */
	@Override
	public boolean contains( E toFind ) throws NullPointerException
	{
		validateElement( toFind );

		MyDLLNode<E> current = head;

		while( current != null )
		{
			if( current.getElement().equals( toFind ) )
			{
				return true;
			}

			current = current.getNext();
		}

		return false;
	}
	
	/**
	 * Copies the list's elements into the provided array. If the array is too
	 * small, a new one of the same component type is allocated. The element
	 * right after the last copied value is set to null when the array is larger
	 * than the list, matching the standard Java contract.
	 *
	 * @param toHold the array to fill (must not be null)
	 * @return the filled array
	 * @throws NullPointerException if toHold is null
	 */
	@Override
	@SuppressWarnings( "unchecked" )
	public E[] toArray( E[] toHold ) throws NullPointerException
	{
		if( toHold == null )
		{
			throw new NullPointerException();
		}

		E[] array = toHold;
		
		// allocate a properly sized array if the one we got is too small
		if( toHold.length < size )
		{
			array = (E[]) Array.newInstance( toHold.getClass().getComponentType(), size );
		}

		MyDLLNode<E> current = head;
		int index = 0;

		while( current != null )
		{
			array[index++] = current.getElement();
			current = current.getNext();
		}

		// null-terminate when there's leftover space
		if( array.length > size )
		{
			array[size] = null;
		}

		return array;
	}

	/**
	 * Returns a plain Object array containing all elements in order.
	 *
	 * @return an Object[] snapshot of the list
	 */
	@Override
	public Object[] toArray()
	{
		Object[] array = new Object[size];
		MyDLLNode<E> current = head;
		int index = 0;

		while( current != null )
		{
			array[index++] = current.getElement();
			current = current.getNext();
		}

		return array;
	}

	/**
	 * Returns an iterator that walks the list from head to tail.
	 * The iterator works on a snapshot, so structural changes mid-iteration
	 * won't affect it.
	 *
	 * @return a forward iterator over the list's elements
	 */
	@Override
	public Iterator<E> iterator()
	{
		return new DLLIterator();
	}

	/**
	 * Throws NullPointerException if the element is null.
	 * Called at the top of every public method that accepts an element.
	 *
	 * @param element the value to check
	 */
	private void validateElement( E element )
	{
		if( element == null )
		{
			throw new NullPointerException();
		}
	}

	/**
	 * Validates that an index is legal for an insertion (0 to size inclusive).
	 *
	 * @param index the position index to check
	 */
	private void validatePositionIndex( int index )
	{
		if( index < 0 || index > size )
		{
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Validates that an index points to an existing element (0 to size - 1).
	 *
	 * @param index the element index to check
	 */
	private void validateElementIndex( int index )
	{
		if( index < 0 || index >= size )
		{
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * Returns the node at the given index. Searches from whichever end is
	 * closer to minimize the number of hops.
	 *
	 * @param index the position to retrieve
	 * @return the node sitting at that position
	 */
	private MyDLLNode<E> nodeAt( int index )
	{
		validateElementIndex( index );
		
		// start from the front if the index is in the first half
		if( index < ( size / 2 ) )
		{
			MyDLLNode<E> current = head;

			for( int i = 0; i < index; i++ )
			{
				current = current.getNext();
			}

			return current;
		}
		
		// otherwise approach from the back
		MyDLLNode<E> current = tail;

		for( int i = size - 1; i > index; i-- )
		{
			current = current.getPrevious();
		}

		return current;
	}

	/**
	 * Wires a new node containing element onto the end of the list.
	 *
	 * @param element the value for the new tail node
	 */
	private void linkLast( E element )
	{
		MyDLLNode<E> last = tail;
		MyDLLNode<E> newNode = new MyDLLNode<>( element, last, null );
		tail = newNode;
		
		// if the list was empty, this node is also the head
		if( last == null )
		{
			head = newNode;
		}
		else
		{
			last.setNext( newNode );
		}

		size++;
	}
	
	/**
	 * Wires a new node containing element immediately before an existing node.
	 *
	 * @param element   the value for the new node
	 * @param successor the node that will come right after the new one
	 */
	private void linkBefore( E element, MyDLLNode<E> successor )
	{
		MyDLLNode<E> predecessor = successor.getPrevious();
		MyDLLNode<E> newNode = new MyDLLNode<>( element, predecessor, successor );
		successor.setPrevious( newNode );

		// if there was no predecessor, the new node is now the head
		if( predecessor == null )
		{
			head = newNode;
		}
		else
		{
			predecessor.setNext( newNode );
		}

		size++;
	}
	
	/**
	 * Detaches a node from the list, reconnects its neighbors, and returns
	 * the value that was stored inside it.
	 *
	 * @param node the node to remove
	 * @return the element that was in the node
	 */
	private E unlink( MyDLLNode<E> node )
	{
		E element = node.getElement();
		MyDLLNode<E> previous = node.getPrevious();
		MyDLLNode<E> next = node.getNext();

		// patch the predecessor's forward pointer (or update head)
		if( previous == null )
		{
			head = next;
		}
		else
		{
			previous.setNext( next );
		}
		
		// patch the successor's back pointer (or update tail)
		if( next == null )
		{
			tail = previous;
		}
		else
		{
			next.setPrevious( previous );
		}
		
		// null out the dead node's references so it can be GC'd
		node.setElement( null );
		node.setPrevious( null );
		node.setNext( null );
		size--;

		return element;
	}
	
	/**
	 * Snapshot iterator — takes a picture of the list at construction time
	 * and walks through it. Changes to the list after creation won't affect
	 * the iteration.
	 */
	private class DLLIterator implements Iterator<E>
	{
		private final Object[] snapshot;
		private int current;

		/**
		 * Captures the current state of the list as an Object array.
		 */
		public DLLIterator()
		{
			snapshot = MyDLL.this.toArray();
			current = 0;
		}

		/**
		 * Returns true if there are more elements to visit.
		 *
		 * @return true if the cursor hasn't reached the end
		 */
		@Override
		public boolean hasNext()
		{
			return current < snapshot.length;
		}

		/**
		 * Returns the next element and advances the cursor.
		 *
		 * @return the next element in iteration order
		 * @throws NoSuchElementException if there are no more elements
		 */
		@Override
		@SuppressWarnings( "unchecked" )
		public E next() throws NoSuchElementException
		{
			if( !hasNext() )
			{
				throw new NoSuchElementException();
			}

			return (E) snapshot[current++];
		}
	}
}
