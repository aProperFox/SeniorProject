package com.inherentgames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * @author Tyler
 * A function that takes an array list and reverses it
 */
public class BBReversed<T> implements Iterable<T> {
    private final ArrayList<T> original;

    /**
     * The constructor for BBReversed.
     * 
     * @param original - the ArrayList to be reversed
     */
    public BBReversed( ArrayList<T> original ) {
        this.original = original;
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<T> iterator() {
        final ListIterator<T> i = original.listIterator( original.size() );

        return new Iterator<T>() {
            public boolean hasNext() { return i.hasPrevious(); }
            public T next() { return i.previous(); }
            public void remove() { i.remove(); }
        };
    }

    /**
     * Reversed the given ArrayList and returns it
     * 
     * @param original - the original ArrayList
     * @return - the reversed version of the ArrayList
     */
    public static <T> BBReversed<T> reversed( ArrayList<T> original ) {
        return new BBReversed<T>( original );
    }
}
