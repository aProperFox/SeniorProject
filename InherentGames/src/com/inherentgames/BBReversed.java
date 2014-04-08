package com.inherentgames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class BBReversed<T> implements Iterable<T> {
    private final ArrayList<T> original;

    public BBReversed( ArrayList<T> original ) {
        this.original = original;
    }

    public Iterator<T> iterator() {
        final ListIterator<T> i = original.listIterator( original.size() );

        return new Iterator<T>() {
            public boolean hasNext() { return i.hasPrevious(); }
            public T next() { return i.previous(); }
            public void remove() { i.remove(); }
        };
    }

    public static <T> BBReversed<T> reversed( ArrayList<T> original ) {
        return new BBReversed<T>( original );
    }
}
