package utilities;
import exceptions.EmptyStackException;

/**
 * StackADT.java
 *
 * @author Santiago Pabon
 * @version 1.0
 * 
 * Class Defenition: An interface that defines the standard operations for a generic stack data structure.
 * A stack is a collection that follows the Last-In-First-Out (LIFO) principle, where elements
 * are added and removed from the top of the stack. 
 * 
 * @param <E> the type of elements held in this stack
 */
public interface StackADT<E> {
    /**
     * Constructor method to create a new stack object.
     * 
     * Precondition: None.
     * 
     * Postcondition: An empty stack is created.
     */
    public void createStack();
    /**
     * Mutator method to add an element to the top of the stack.
     * 
     * Precondition: A valid Stack object exists and an element is provided.
     * 
     * Postcondition: The element is added to the top of the stack.
     * 
     * @param element the element to be pushed onto the stack.
     */    
    public void push(E element);
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
    public E pop(E element) throws EmptyStackException;
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
}
