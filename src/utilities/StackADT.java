package utilities;

import java.util.EmptyStackException;

/**
 * StackADT.java
 *
 * @author Santiago Pabon
 * @version 1.0
 * 
 * Class Definition: An interface that defines the standard operations for a generic stack data structure.
 * A stack is a collection that follows the Last-In-First-Out (LIFO) principle, where elements
 * are added and removed from the top of the stack. 
 * 
 * @param <E> the type of elements held in this stack
 */
public interface StackADT<E>
{
	/**
	 * Accessor method to return the number of elements in the stack.
	 * 
	 * @return the number of elements in the stack.
	 */
	public int size();

	/**
	 * Mutator method to remove all elements from the stack.
	 */
	public void clear();

	/**
	 * Mutator method to add an element to the top of the stack.
	 * 
	 * Precondition: A valid Stack object exists and an element is provided.
	 * 
	 * Postcondition: The element is added to the top of the stack.
	 * 
	 * @param toAdd the element to be pushed onto the stack.
	 * @throws NullPointerException if the element is null.
	 */
	public void push( E toAdd ) throws NullPointerException;

	/**
	 * Mutator method to remove and return the top element of the stack.
	 * 
	 * Precondition: A valid Stack object exists and is not empty.
	 * 
	 * Postcondition: The top element is removed from the stack and returned.
	 * 
	 * @return the element that was removed from the top of the stack.
	 * @throws EmptyStackException if the stack is empty.
	 */
	public E pop() throws EmptyStackException;

	/**
	 * Accessor method to view the top element of the stack without removing it.
	 * 
	 * Precondition: A valid Stack object exists and is not empty.
	 * 
	 * Postcondition: The top element is returned but not removed.
	 * 
	 * @return the top element of the stack.
	 * @throws EmptyStackException if the stack is empty.
	 */
	public E peek() throws EmptyStackException;

	/**
	 * Accessor method to check if the stack is empty.
	 * 
	 * @return true if the stack contains no elements.
	 */
	public boolean isEmpty();

	/**
	 * Accessor method to check whether the stack contains a specific element.
	 * 
	 * @param toFind the element to search for.
	 * @return true if the element is found in the stack.
	 * @throws NullPointerException if the element is null.
	 */
	public boolean contains( E toFind ) throws NullPointerException;

	/**
	 * Accessor method to search for an element in the stack.
	 * 
	 * @param toFind the element to search for.
	 * @return the 1-based position from the top, or -1 if not found.
	 * @throws NullPointerException if the element is null.
	 */
	public int search( E toFind ) throws NullPointerException;

	/**
	 * Accessor method to return the stack elements in an array.
	 * 
	 * @param toHold the array used to hold the elements.
	 * @return an array containing the stack from top to bottom.
	 * @throws NullPointerException if the array passed is null.
	 */
	public E[] toArray( E[] toHold ) throws NullPointerException;

	/**
	 * Accessor method to return the stack elements in an Object array.
	 * 
	 * @return an Object array containing the stack from top to bottom.
	 */
	public Object[] toArray();

	/**
	 * Accessor method to return an iterator for the stack.
	 * 
	 * @return an iterator for the stack from top to bottom.
	 */
	public Iterator<E> iterator();

	/**
	 * Accessor method to check if the stack is full.
	 * 
	 * @return true if the stack is full.
	 */
	public boolean stackOverflow();

	/**
	 * Accessor method to compare this stack with another stack.
	 * 
	 * @param that the stack to compare with.
	 * @return true if both stacks contain the same elements in the same order.
	 */
	public boolean equals( StackADT<E> that );
}
