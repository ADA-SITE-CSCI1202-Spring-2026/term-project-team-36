package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightEvent(
    String flightNumber,
    String aircraftType,
    BigDecimal revenue,
    LocalDateTime timestamp
) {
    public FlightEvent {
        if (flightNumber == null || flightNumber.isBlank()) {
            throw new IllegalArgumentException("Flight number cannot be blank");
        }
        if (revenue == null || revenue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Revenue cannot be negative");
        }
    }
}
