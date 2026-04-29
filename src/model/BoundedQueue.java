package model;

import java.util.*;
import java.util.function.Function;

// Week 10: Generic class with type parameter T
// Week 8:  Uses LinkedList internally, implements Iterable
// Week 13: All methods synchronized for thread safety
public class BoundedQueue<T> implements Iterable<T> {

    private final LinkedList<T> elements;  // Week 8: LinkedList
    private final int capacity;

    // Week 10: Constructor with bounded capacity
    public BoundedQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.elements = new LinkedList<>();
    }

    // Week 13: synchronized methods for thread-safe access
    public synchronized boolean offer(T item) {
        elements.addLast(item);
        return true;
    }

    // Week 2: Ternary operator
    public synchronized T poll() {
        return elements.isEmpty() ? null : elements.removeFirst();
    }

    public synchronized T peek() {
        return elements.isEmpty() ? null : elements.getFirst();
    }

    public synchronized boolean isFull() {
        return elements.size() > capacity;
    }

    public synchronized boolean isEmpty() {
        return elements.isEmpty();
    }

    public synchronized int size() {
        return elements.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public synchronized void clear() {
        elements.clear();
    }

    // Week 10: Generic method — transforms elements using a mapping function
    public synchronized <R> List<R> transform(Function<T, R> mapper) {
        var result = new ArrayList<R>();  // Week 2: var
        for (T item : elements) {        // Week 4: for-each
            result.add(mapper.apply(item));
        }
        return result;
    }

    // Week 8: Returns unmodifiable snapshot of current elements
    public synchronized List<T> asList() {
        return Collections.unmodifiableList(new ArrayList<>(elements));
    }

    // Week 13: Thread-safe iterator — returns snapshot to avoid ConcurrentModificationException
    @Override
    public synchronized Iterator<T> iterator() {
        return new ArrayList<>(elements).iterator();
    }
}
