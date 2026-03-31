/**
 * @author Kaley Wood, Santiago, Asad, Dylan
 * Southern Alberta Institute of Technology: CPRG-304
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 03.26.2026
 *
 * MyStack -- a LIFO stack backed by MyArrayList. The top of the stack
 * lives at the end of the list, so push and pop both work at that end.
 */

package implementations;

import utilities.Iterator;
import utilities.StackADT;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;

public class MyStack<E> implements StackADT<E>
{
    private MyArrayList<E> list;

    /**
     * Creates an empty stack.
     */
    public MyStack()
    {
        list = new MyArrayList<>();
    }

    /**
     * Adds an element to the top of the stack.
     *
     * @param element  the value to push on
     * @throws NullPointerException if element is null
     */
    @Override
    public void push( E element ) throws NullPointerException
    {
        if ( element == null )
        {
            throw new NullPointerException();
        }
        list.add( element );
    }

    /**
     * Removes and returns the top element of the stack.
     *
     * @return the element that was on top
     * @throws EmptyStackException if the stack is empty
     */
    @Override
    public E pop() throws EmptyStackException
    {
        if ( isEmpty() )
        {
            throw new EmptyStackException();
        }

        // top of stack is the last element in the list
        return list.remove( list.size() - 1 );
    }

    /**
     * Returns the top element without removing it.
     *
     * @return the element currently on top
     * @throws EmptyStackException if the stack is empty
     */
    @Override
    public E peek() throws EmptyStackException
    {
        if ( isEmpty() )
        {
            throw new EmptyStackException();
        }

        return list.get( list.size() - 1 );
    }

    /**
     * Returns true if the stack has no elements.
     *
     * @return true if empty, false otherwise
     */
    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    /**
     * Returns the number of elements in the stack.
     *
     * @return current stack size
     */
    @Override
    public int size()
    {
        return list.size();
    }

    /**
     * Removes all elements from the stack.
     */
    @Override
    public void clear()
    {
        list.clear();
    }

    /**
     * Returns true if the stack contains the given element.
     *
     * @param element  the value to search for
     * @return true if found, false otherwise
     * @throws NullPointerException if element is null
     */
    @Override
    public boolean contains( E element ) throws NullPointerException
    {
        if ( element == null )
        {
            throw new NullPointerException();
        }
        return list.contains( element );
    }

    /**
     * Returns the 1-based position of an element from the top of the stack.
     * Top of stack is position 1. Returns -1 if not found.
     *
     * @param element  the value to search for
     * @return 1-based position from top, or -1 if not present
     */
    @Override
    public int search( E element )
    {
        // walk from the top (end of list) down
        for ( int position = list.size() - 1; position >= 0; position-- )
        {
            if ( list.get( position ).equals( element ) )
            {
                // distance from top: top is index size-1, so distance = size - position
                return list.size() - position;
            }
        }
        return -1;
    }

    /**
     * Returns true if the two stacks contain the same elements in the same order.
     * Comparison goes top to bottom using iterators.
     *
     * @param otherStack  the stack to compare against
     * @return true if both stacks are equal
     */
    @Override
    public boolean equals( StackADT<E> otherStack )
    {
        if ( otherStack == null || this.size() != otherStack.size() )
        {
            return false;
        }

        Iterator<E> thisIterator = this.iterator();
        Iterator<E> otherIterator = otherStack.iterator();

        while ( thisIterator.hasNext() )
        {
            if ( !thisIterator.next().equals( otherIterator.next() ) )
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns an array of all elements in the stack, top to bottom.
     *
     * @return Object array ordered from top to bottom
     */
    @Override
    public Object[] toArray()
    {
        Object[] result = new Object[list.size()];

        // fill result in reverse so index 0 = top of stack
        for ( int position = list.size() - 1; position >= 0; position-- )
        {
            result[list.size() - 1 - position] = list.get( position );
        }
        return result;
    }

    /**
     * Fills the provided array with stack elements top to bottom.
     * If the array is too small, a new one of the correct size is created.
     *
     * @param holder  the array to fill
     * @return the filled array
     * @throws NullPointerException if holder is null
     */
    @Override
    public E[] toArray( E[] holder ) throws NullPointerException
    {
        if ( holder == null )
        {
            throw new NullPointerException();
        }

        if ( holder.length < list.size() )
        {
            holder = Arrays.copyOf(holder, this. size());
        }

        // fill holder in reverse so index 0 = top of stack
        for ( int position = list.size() - 1; position >= 0; position-- )
        {
            holder[list.size() - 1 - position] = list.get( position );
        }
        return holder;
    }

    /**
     * Always returns false -- this stack has no fixed size limit.
     *
     * @return false
     */
    @Override
    public boolean stackOverflow()
    {
        return false;
    }

    /**
     * Returns an iterator that traverses the stack from top to bottom.
     *
     * @return iterator starting at the top
     */
    @Override
    public Iterator<E> iterator()
    {
        return new StackIterator();
    }

    private class StackIterator implements Iterator<E>
    {
        // snapshot of the stack in top-to-bottom order
        private Object[] snapshot;
        private int current;

        public StackIterator()
        {
            snapshot = toArray();
            current = 0;
        }

        @Override
        public boolean hasNext()
        {
            return current < snapshot.length;
        }

        @Override
        public E next() throws NoSuchElementException
        {
            if ( !hasNext() )
            {
                throw new NoSuchElementException();
            }
            return (E) snapshot[current++];
        }
    }
}