package exceptions;

/**
 * Thrown when a queue operation requiring at least one element is performed on
 * an empty queue.
 */
public class EmptyQueueException extends Exception
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new exception with no detail message.
	 */
	public EmptyQueueException()
	{
		super();
	}

	/**
	 * Creates a new exception with the specified detail message.
	 *
	 * @param message The detail message.
	 */
	public EmptyQueueException( String message )
	{
		super( message );
	}
}
