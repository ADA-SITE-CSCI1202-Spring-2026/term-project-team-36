package exception;

public class InsufficientResourceException extends AirportException {
    private final String resourceName;
    private final int required;
    private final int available;

    public InsufficientResourceException(String resourceName, int required, int available) {
        super(String.format("Insufficient %s: need %d, have %d", resourceName, required, available));
        this.resourceName = resourceName;
        this.required = required;
        this.available = available;
    }

    public String getResourceName() { return resourceName; }
    public int getRequired()        { return required; }
    public int getAvailable()       { return available; }
}
