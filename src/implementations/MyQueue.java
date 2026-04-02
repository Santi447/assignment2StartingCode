package implementations;

import java.util.NoSuchElementException;

import exceptions.EmptyQueueException;
import utilities.Iterator;
import utilities.QueueADT;

/**
 * @author Santiago, Asad, Dylan, Kaley
 * Southern Alberta Institute of Technology: CPRG-304-B
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 03.28.2026
 *
 * MyQueue — implements a queue using a doubly linked list to store
 * elements in first-in, first-out order.
 */
public class MyQueue<E> implements QueueADT<E>
{
    private MyDLL<E> list;
    /**
     * Builds an empty queue backed by a doubly linked list.
     */
    public MyQueue()
    {
        list = new MyDLL<>();
    }
    /**
     * Adds an element to the rear of the queue.
     *
     * @param toAdd the element to place at the back of the queue
     * @throws NullPointerException if the given element is null
     */
    @Override
    public void enqueue( E toAdd ) throws NullPointerException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot enqueue a null element." );
        }

        list.add( toAdd );
    }
    /**
     * Removes and returns the element at the front of the queue.
     *
     * @return the element removed from the front
     * @throws EmptyQueueException if the queue is empty
     */
    @Override
    public E dequeue() throws EmptyQueueException
    {
        if( isEmpty() )
        {
            throw new EmptyQueueException( "Cannot dequeue from an empty queue." );
        }

        return list.remove( 0 );
    }
    /**
     * Returns the element at the front of the queue without removing it.
     *
     * @return the element currently at the front
     * @throws EmptyQueueException if the queue is empty
     */
    @Override
    public E peek() throws EmptyQueueException
    {
        if( isEmpty() )
        {
            throw new EmptyQueueException( "Cannot peek at an empty queue." );
        }

        return list.get( 0 );
    }
    /**
     * Removes all elements from the queue.
     */
    @Override
    public void dequeueAll()
    {
        list.clear();
    }
    /**
     * Checks whether the queue currently has no elements.
     *
     * @return true if the queue is empty, otherwise false
     */
    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }
/**
     * Checks whether the queue contains the given element.
     *
     * @param toFind the element to search for
     * @return true if the element is found, otherwise false
     * @throws NullPointerException if the given element is null
     */

    @Override
    public boolean contains( E toFind ) throws NullPointerException
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for a null element." );
        }

        return list.contains( toFind );
    }
    /**
     * Searches for the given element and returns its 1-based position.
     *
     * @param toFind the element to locate in the queue
     * @return the 1-based position if found, otherwise -1
     */
    @Override
    public int search( E toFind )
    {
        if( toFind == null )
        {
            return -1;
        }
        
        Iterator<E> it = iterator();
        int position = 1;

        while( it.hasNext() )
        {
            if( it.next().equals( toFind ) )
            {
                return position;
            }

            position++;
        }

        return -1;
    }
    /**
     * Creates and returns an iterator for traversing the queue.
     *
     * @return an iterator over the queue elements
     */
    @Override
    public Iterator<E> iterator()
    {
        return new QueueIterator();
    }
    /**
     * Compares this queue to another queue for equal size and element order.
     *
     * @param that the other queue to compare against
     * @return true if both queues contain the same elements in the same order
     */
    @Override
    public boolean equals( QueueADT<E> that )
    {
        if( that == null )
        {
            return false;
        }

        if( this.size() != that.size() )
        {
            return false;
        }

        Iterator<E> thisIt = this.iterator();
        Iterator<E> thatIt = that.iterator();

        while( thisIt.hasNext() )
        {
            if( !thisIt.next().equals( thatIt.next() ) )
            {
                return false;
            }
        }

        return true;
    }
    /**
     * Returns the queue contents as an Object array.
     *
     * @return an array containing all queue elements
     */
    @Override
    public Object[] toArray()
    {
        return list.toArray();
    }
    /**
     * Copies the queue contents into the provided array type.
     *
     * @param holder the array to hold the queue elements
     * @return an array containing all queue elements
     * @throws NullPointerException if the provided array is null
     */
    @Override
    public E[] toArray( E[] holder ) throws NullPointerException
    {
        if( holder == null )
        {
            throw new NullPointerException( "The provided array must not be null." );
        }

        return list.toArray( holder );
    }
    /**
     * Indicates whether the queue has reached capacity.
     *
     * @return false because this queue is not capacity restricted
     */
    @Override
    public boolean isFull()
    {
        return false;
    }
    /**
     * Returns the number of elements currently stored in the queue.
     *
     * @return the queue size
     */
    @Override
    public int size()
    {
        return list.size();
    }
    /**
     * QueueIterator — iterates through the queue using a snapshot of its
     * current contents.
     */
    private class QueueIterator implements Iterator<E>
    {
        private final Object[] snapshot;
        private int current;
        /**
         * Builds an iterator using a snapshot of the queue contents.
         */
        public QueueIterator()
        {
            snapshot = list.toArray();
            current = 0;
        }
        /**
         * Checks whether there are more elements left in the iteration.
         *
         * @return true if another element is available, otherwise false
         */
        @Override
        public boolean hasNext()
        {
            return current < snapshot.length;
        }
        /**
         * Returns the next element in the iteration.
         *
         * @return the next queued element in the snapshot
         * @throws NoSuchElementException if there are no more elements
         */
        @Override
        @SuppressWarnings( "unchecked" )
        public E next() throws NoSuchElementException
        {
            if( !hasNext() )
            {
                throw new NoSuchElementException( "No more elements in the queue." );
            }

            return (E) snapshot[current++];
        }
    }
}