package implementations;

public class MyDLLNode<E>
{
	private E element;
	private MyDLLNode<E> previous;
	private MyDLLNode<E> next;

	public MyDLLNode( E element )
	{
		this( element, null, null );
	}

	public MyDLLNode( E element, MyDLLNode<E> previous, MyDLLNode<E> next )
	{
		this.element = element;
		this.previous = previous;
		this.next = next;
	}

	public E getElement()
	{
		return element;
	}

	public void setElement( E element )
	{
		this.element = element;
	}

	public MyDLLNode<E> getPrevious()
	{
		return previous;
	}

	public void setPrevious( MyDLLNode<E> previous )
	{
		this.previous = previous;
	}

	public MyDLLNode<E> getNext()
	{
		return next;
	}

	public void setNext( MyDLLNode<E> next )
	{
		this.next = next;
	}
}
