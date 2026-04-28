package util;

public class AirportLogger {

    
    private final StringBuilder log;          
    private final String[] recentMessages;    
    private int messageIndex = 0;

    public AirportLogger(int recentCapacity) {
        this.log = new StringBuilder();
        this.recentMessages = new String[recentCapacity];  
    }

    public void append(String message) {
        log.append(message).append("\n");
        recentMessages[messageIndex % recentMessages.length] = message;
        messageIndex++;
    }

    public String getFullLog() {
        return log.toString();
    }

    public String[] getRecentMessages() {
        var count = Math.min(messageIndex, recentMessages.length);  
        var result = new String[count];  
        var start = Math.max(0, messageIndex - recentMessages.length);
        for (int i = 0; i < count; i++) {
            result[i] = recentMessages[(start + i) % recentMessages.length];
        }
        return result;
    }

    public String formatMessage(String category, String message) {
        var sb = new StringBuilder();
        sb.append(category.toUpperCase())
          .append(": ")
          .append(message.trim());
        return sb.toString();
    }

    public int messageCount() {
        return messageIndex;
    }
}
