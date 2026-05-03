package model;

import java.util.*;
import java.util.function.Function;

public class BoundedQueue<T> implements Iterable<T> {
    private final LinkedList<T> elements;
    private final int capacity;

    public BoundedQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = capacity;
        this.elements = new LinkedList<>();
    }

    public synchronized boolean offer(T item) {
        elements.addLast(item);
        return true;
    }

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

    public synchronized <R> List<R> transform(Function<T, R> mapper) {
        var result = new ArrayList<R>();
        for (T item : elements) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    public synchronized List<T> asList() {
        return Collections.unmodifiableList(new ArrayList<>(elements));
    }

    @Override
    public synchronized Iterator<T> iterator() {
        return new ArrayList<>(elements).iterator();
    }
}
