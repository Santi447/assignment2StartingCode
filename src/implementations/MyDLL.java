package implementations;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

import utilities.Iterator;
import utilities.ListADT;

public class MyDLL<E> implements ListADT<E>
{
	private MyDLLNode<E> head;
	private MyDLLNode<E> tail;
	private int size;

	public MyDLL()
	{
		head = null;
		tail = null;
		size = 0;
	}

	@Override
	public int size()
	{
		return size;
	}

	@Override
	public void clear()
	{
		MyDLLNode<E> current = head;

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

	@Override
	public boolean add( int index, E toAdd ) throws NullPointerException, IndexOutOfBoundsException
	{
		validateElement( toAdd );
		validatePositionIndex( index );

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

	@Override
	public boolean add( E toAdd ) throws NullPointerException
	{
		validateElement( toAdd );
		linkLast( toAdd );
		return true;
	}

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

	@Override
	public E get( int index ) throws IndexOutOfBoundsException
	{
		return nodeAt( index ).getElement();
	}

	@Override
	public E remove( int index ) throws IndexOutOfBoundsException
	{
		return unlink( nodeAt( index ) );
	}

	@Override
	public E remove( E toRemove ) throws NullPointerException
	{
		validateElement( toRemove );

		MyDLLNode<E> current = head;

		while( current != null )
		{
			if( current.getElement().equals( toRemove ) )
			{
				return unlink( current );
			}

			current = current.getNext();
		}

		return null;
	}

	@Override
	public E set( int index, E toChange ) throws NullPointerException, IndexOutOfBoundsException
	{
		validateElement( toChange );
		MyDLLNode<E> node = nodeAt( index );
		E previousValue = node.getElement();
		node.setElement( toChange );
		return previousValue;
	}

	@Override
	public boolean isEmpty()
	{
		return size == 0;
	}

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

	@Override
	@SuppressWarnings( "unchecked" )
	public E[] toArray( E[] toHold ) throws NullPointerException
	{
		if( toHold == null )
		{
			throw new NullPointerException();
		}

		E[] array = toHold;

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

		if( array.length > size )
		{
			array[size] = null;
		}

		return array;
	}

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

	@Override
	public Iterator<E> iterator()
	{
		return new DLLIterator();
	}

	private void validateElement( E element )
	{
		if( element == null )
		{
			throw new NullPointerException();
		}
	}

	private void validatePositionIndex( int index )
	{
		if( index < 0 || index > size )
		{
			throw new IndexOutOfBoundsException();
		}
	}

	private void validateElementIndex( int index )
	{
		if( index < 0 || index >= size )
		{
			throw new IndexOutOfBoundsException();
		}
	}

	private MyDLLNode<E> nodeAt( int index )
	{
		validateElementIndex( index );

		if( index < ( size / 2 ) )
		{
			MyDLLNode<E> current = head;

			for( int i = 0; i < index; i++ )
			{
				current = current.getNext();
			}

			return current;
		}

		MyDLLNode<E> current = tail;

		for( int i = size - 1; i > index; i-- )
		{
			current = current.getPrevious();
		}

		return current;
	}

	private void linkLast( E element )
	{
		MyDLLNode<E> last = tail;
		MyDLLNode<E> newNode = new MyDLLNode<>( element, last, null );
		tail = newNode;

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

	private void linkBefore( E element, MyDLLNode<E> successor )
	{
		MyDLLNode<E> predecessor = successor.getPrevious();
		MyDLLNode<E> newNode = new MyDLLNode<>( element, predecessor, successor );
		successor.setPrevious( newNode );

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

	private E unlink( MyDLLNode<E> node )
	{
		E element = node.getElement();
		MyDLLNode<E> previous = node.getPrevious();
		MyDLLNode<E> next = node.getNext();

		if( previous == null )
		{
			head = next;
		}
		else
		{
			previous.setNext( next );
		}

		if( next == null )
		{
			tail = previous;
		}
		else
		{
			next.setPrevious( previous );
		}

		node.setElement( null );
		node.setPrevious( null );
		node.setNext( null );
		size--;

		return element;
	}

	private class DLLIterator implements Iterator<E>
	{
		private final Object[] snapshot;
		private int current;

		public DLLIterator()
		{
			snapshot = MyDLL.this.toArray();
			current = 0;
		}

		@Override
		public boolean hasNext()
		{
			return current < snapshot.length;
		}

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
