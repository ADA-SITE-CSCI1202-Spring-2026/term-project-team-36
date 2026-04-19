package exception;

public class QueueOverflowException extends RuntimeException {

    private final int currentSize;
    private final int maxCapacity;

    public QueueOverflowException(int currentSize, int maxCapacity) {
        super("Holding pattern exceeded " + maxCapacity + " aircraft (current: " + currentSize + ")");
        this.currentSize = currentSize;
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentSize() { return currentSize; }
    public int getMaxCapacity() { return maxCapacity; }
}
