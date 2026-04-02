/**
 * @author Santiago, Asad, Dylan, Kaley
 * Southern Alberta Institute of Technology: CPRG-304-B
 * Assignment 2: Creating ADTs, Implementing DS and an XML Parser
 * Created: 03.28.2026
 *
 * MyDLLNode — stores one element in the doubly linked list along with
 * references to the node before it and the node after it.
 */
package implementations;

public class MyDLLNode<E>
{
	private E element;
	private MyDLLNode<E> previous;
	private MyDLLNode<E> next;
	/**
	 * Builds a node with the given element and no links yet.
	 *
	 * @param element the value this node should store
	 */
	public MyDLLNode( E element )
	{
		this( element, null, null );
	}
	/**
	 * Builds a node with its element and both surrounding links already set.
	 *
	 * @param element  the value this node should store
	 * @param previous the node that comes before this one
	 * @param next     the node that comes after this one
	 */
	public MyDLLNode( E element, MyDLLNode<E> previous, MyDLLNode<E> next )
	{
		this.element = element;
		this.previous = previous;
		this.next = next;
	}
	/**
	 * Returns the element stored in this node.
	 *
	 * @return the node's current value
	 */
	public E getElement()
	{
		return element;
	}
	/**
	 * Replaces the element stored in this node.
	 *
	 * @param element the new value to store here
	 */
	public void setElement( E element )
	{
		this.element = element;
	}

	/**
	 * Returns the node linked before this one.
	 *
	 * @return the previous node, or null if there isn't one
	 */
	public MyDLLNode<E> getPrevious()
	{
		return previous;
	}

	/**
	 * Updates the node linked before this one.
	 *
	 * @param previous the node that should come before this one
	 */
	public void setPrevious( MyDLLNode<E> previous )
	{
		this.previous = previous;
	}
	/**
	 * Returns the node linked after this one.
	 *
	 * @return the next node, or null if there isn't one
	 */
	public MyDLLNode<E> getNext()
	{
		return next;
	}
	/**
	 * Updates the node linked after this one.
	 *
	 * @param next the node that should come after this one
	 */
	public void setNext( MyDLLNode<E> next )
	{
		this.next = next;
	}
}
