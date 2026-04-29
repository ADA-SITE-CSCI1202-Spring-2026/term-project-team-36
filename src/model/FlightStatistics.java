package model;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

// Week 11: Stream API for computing statistics
// Week 9:  SortedMap (TreeMap) for ordered data
// Week 8:  ArrayList, Collections
public class FlightStatistics {

    private final List<FlightEvent> events = new ArrayList<>();  // Week 8: ArrayList

    public void record(FlightEvent event) {
        events.add(event);
    }

    // Week 11: Stream — total revenue using reduce
    public BigDecimal totalRevenue() {
        return events.stream()
            .map(FlightEvent::revenue)                                      // method reference
            .reduce(BigDecimal.ZERO, BigDecimal::add);                      // reduce
    }

    // Week 11: Stream — count flights grouped by aircraft type
    public Map<String, Long> countByType() {
        return events.stream()
            .collect(Collectors.groupingBy(FlightEvent::aircraftType,       // groupingBy
                                           Collectors.counting()));         // downstream
    }

    // Week 9: TreeMap — revenue by type, sorted alphabetically
    public SortedMap<String, BigDecimal> revenueByType() {
        return events.stream()
            .collect(Collectors.groupingBy(
                FlightEvent::aircraftType,
                TreeMap::new,                                               // Week 9: TreeMap supplier
                Collectors.reducing(BigDecimal.ZERO,
                                    FlightEvent::revenue,
                                    BigDecimal::add)));
    }

    // Week 11: Stream — filter flights above a revenue threshold, sorted descending
    public List<FlightEvent> highValueFlights(BigDecimal threshold) {
        return events.stream()
            .filter(e -> e.revenue().compareTo(threshold) >= 0)             // lambda predicate
            .sorted(Comparator.comparing(FlightEvent::revenue).reversed())  // sorting
            .collect(Collectors.toList());
    }

    // Week 11: Stream — average revenue using mapToDouble
    public double averageRevenue() {
        return events.stream()
            .mapToDouble(e -> e.revenue().doubleValue())                    // mapToDouble
            .average()
            .orElse(0.0);
    }

    public int totalCleared() {
        return events.size();
    }

    // Week 8: Unmodifiable view
    public List<FlightEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}
