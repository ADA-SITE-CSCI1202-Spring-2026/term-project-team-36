package model;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class FlightStatistics {
    private final List<FlightEvent> events = new ArrayList<>();

    public void record(FlightEvent event) {
        events.add(event);
    }

    public BigDecimal totalRevenue() {
        return events.stream()
            .map(FlightEvent::revenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, Long> countByType() {
        return events.stream()
            .collect(Collectors.groupingBy(FlightEvent::aircraftType,
                                           Collectors.counting()));
    }

    public SortedMap<String, BigDecimal> revenueByType() {
        return events.stream()
            .collect(Collectors.groupingBy(
                FlightEvent::aircraftType,
                TreeMap::new,
                Collectors.reducing(BigDecimal.ZERO,
                                    FlightEvent::revenue,
                                    BigDecimal::add)));
    }

    public List<FlightEvent> highValueFlights(BigDecimal threshold) {
        return events.stream()
            .filter(e -> e.revenue().compareTo(threshold) >= 0)
            .sorted(Comparator.comparing(FlightEvent::revenue).reversed())
            .collect(Collectors.toList());
    }

    public double averageRevenue() {
        return events.stream()
            .mapToDouble(e -> e.revenue().doubleValue())
            .average()
            .orElse(0.0);
    }

    public int totalCleared() {
        return events.size();
    }

    public List<FlightEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
