package implementations;

import java.util.NoSuchElementException;

import exceptions.EmptyQueueException;
import utilities.Iterator;
import utilities.QueueADT;

public class MyQueue<E> implements QueueADT<E>
{
    private MyDLL<E> list;

    public MyQueue()
    {
        list = new MyDLL<>();
    }

    @Override
    public void enqueue( E toAdd ) throws NullPointerException
    {
        if( toAdd == null )
        {
            throw new NullPointerException( "Cannot enqueue a null element." );
        }

        list.add( toAdd );
    }

    @Override
    public E dequeue() throws EmptyQueueException
    {
        if( isEmpty() )
        {
            throw new EmptyQueueException( "Cannot dequeue from an empty queue." );
        }

        return list.remove( 0 );
    }

    @Override
    public E peek() throws EmptyQueueException
    {
        if( isEmpty() )
        {
            throw new EmptyQueueException( "Cannot peek at an empty queue." );
        }

        return list.get( 0 );
    }

    @Override
    public void dequeueAll()
    {
        list.clear();
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public boolean contains( E toFind ) throws NullPointerException
    {
        if( toFind == null )
        {
            throw new NullPointerException( "Cannot search for a null element." );
        }

        return list.contains( toFind );
    }

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

    @Override
    public Iterator<E> iterator()
    {
        return new QueueIterator();
    }

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

    @Override
    public Object[] toArray()
    {
        return list.toArray();
    }

    @Override
    public E[] toArray( E[] holder ) throws NullPointerException
    {
        if( holder == null )
        {
            throw new NullPointerException( "The provided array must not be null." );
        }

        return list.toArray( holder );
    }

    @Override
    public boolean isFull()
    {
        return false;
    }

    @Override
    public int size()
    {
        return list.size();
    }

    private class QueueIterator implements Iterator<E>
    {
        private final Object[] snapshot;
        private int current;

        public QueueIterator()
        {
            snapshot = list.toArray();
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
                throw new NoSuchElementException( "No more elements in the queue." );
            }

            return (E) snapshot[current++];
        }
    }
}