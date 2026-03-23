package utilities;

import exceptions.EmptyQueueException;

/**
 * QueueADT.java
 * 
 * @author Asad Arif
 * @version 1.0
 * 
 * Class Definition: An interface that defines the standard operations for a generic queue data structure.
 * A queue is a collection that follows the First-In-First-Out (FIFO) principle, where elements
 * are added at the rear and removed from the front.
 * 
 * @param <E> the type of elements held in this queue
 */
public interface QueueADT<E>
{
	/**
	 * Accessor method to return the number of elements in the queue.
	 * 
	 * @return the number of elements in the queue.
	 */
	public int size();

	/**
	 * Mutator method to remove all elements from the queue.
	 */
	public void dequeueAll();

	/**
	 * Mutator method to add an element to the rear of the queue.
	 * 
	 * Precondition: A valid Queue object exists and an element is provided.
	 * 
	 * Postcondition: The element is added to the rear of the queue.
	 * 
	 * @param toAdd the element to be enqueued.
	 * @throws NullPointerException if the element is null.
	 */
	public void enqueue( E toAdd ) throws NullPointerException;

	/**
	 * Mutator method to remove and return the front element of the queue.
	 * 
	 * Precondition: A valid Queue object exists and is not empty.
	 * 
	 * Postcondition: The front element is removed from the queue and returned.
	 * 
	 * @return the element that was removed from the front of the queue.
	 * @throws EmptyQueueException if the queue is empty.
	 */
	public E dequeue() throws EmptyQueueException;

	/**
	 * Accessor method to view the front element of the queue without removing it.
	 * 
	 * Precondition: A valid Queue object exists and is not empty.
	 * 
	 * Postcondition: The front element is returned but not removed.
	 * 
	 * @return the front element of the queue.
	 * @throws EmptyQueueException if the queue is empty.
	 */
	public E peek() throws EmptyQueueException;

	/**
	 * Accessor method to check if the queue is empty.
	 * 
	 * @return true if the queue contains no elements.
	 */
	public boolean isEmpty();

	/**
	 * Accessor method to check if the queue is full.
	 * 
	 * @return true if the queue is full.
	 */
	public boolean isFull();

	/**
	 * Accessor method to check whether the queue contains a specific element.
	 * 
	 * @param toFind the element to search for.
	 * @return true if the element is found in the queue.
	 * @throws NullPointerException if the element is null.
	 */
	public boolean contains( E toFind ) throws NullPointerException;

	/**
	 * Accessor method to search for an element in the queue.
	 * 
	 * @param toFind the element to search for.
	 * @return the 1-based position from the front, or -1 if not found.
	 * @throws NullPointerException if the element is null.
	 */
	public int search( E toFind ) throws NullPointerException;

	/**
	 * Accessor method to return the queue elements in an array.
	 * 
	 * @param toHold the array used to hold the elements.
	 * @return an array containing the queue from front to rear.
	 * @throws NullPointerException if the array passed is null.
	 */
	public E[] toArray( E[] toHold ) throws NullPointerException;

	/**
	 * Accessor method to return the queue elements in an Object array.
	 * 
	 * @return an Object array containing the queue from front to rear.
	 */
	public Object[] toArray();

	/**
	 * Accessor method to return an iterator for the queue.
	 * 
	 * @return an iterator for the queue from front to rear.
	 */
	public Iterator<E> iterator();

	/**
	 * Accessor method to compare this queue with another queue.
	 * 
	 * @param that the queue to compare with.
	 * @return true if both queues contain the same elements in the same order.
	 */
	public boolean equals( QueueADT<E> that );
}
