package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SupplyOrder(
    Resource resource,
    int quantity,
    BigDecimal cost,
    LocalDateTime timestamp
) {
    public SupplyOrder {
        assert quantity > 0 : "Quantity must be positive";
        assert cost.compareTo(BigDecimal.ZERO) > 0 : "Cost must be positive";
    }

    public String toLogString() {
        var sb = new StringBuilder();
        sb.append("ORDER: ").append(quantity).append(resource.getUnit())
          .append(" of ").append(resource.getDisplayName())
          .append(" for $").append(cost.toPlainString());
        return sb.toString();
    }
}
