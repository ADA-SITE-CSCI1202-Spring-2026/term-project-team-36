package persistence;

import model.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveLoadManager {
    private static final String CSV_FILE = "airport_state.csv";
    private static final String BIN_FILE = "airport_state.dat";

    public static void save(DepotManager depot, BoundedQueue<Aircraft> queue)
            throws IOException {
        var lines = new ArrayList<String>();

        lines.add("BUDGET," + depot.getBudget().toPlainString());

        for (Resource r : Resource.values()) {
            lines.add("RESOURCE," + r.name() + "," + depot.getResource(r));
        }

        for (Aircraft ac : queue) {
            var sb = new StringBuilder("TASK,");
            sb.append(ac.getType()).append(",")
              .append(ac.getFlightNumber()).append(",")
              .append(ac.getRequiredFuel()).append(",")
              .append(ac.getRequiredMeals()).append(",")
              .append(ac.getReward().toPlainString());
            lines.add(sb.toString());
        }

        Path path = Path.of(CSV_FILE);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static void load(DepotManager depot, BoundedQueue<Aircraft> queue)
            throws IOException {
        Path path = Path.of(CSV_FILE);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Save file not found: " + CSV_FILE);
        }

        queue.clear();

        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");

            switch (parts[0]) {
                case "BUDGET" -> depot.setBudget(new BigDecimal(parts[1]));
                case "RESOURCE" -> depot.setResource(
                    Resource.valueOf(parts[1]), Integer.parseInt(parts[2]));
                case "TASK" -> {
                    var type = parts[1];
                    var flightNum = parts[2];

                    Aircraft ac = switch (type) {
                        case "CommercialJet"  -> new CommercialJet(flightNum);
                        case "CargoFreighter" -> new CargoFreighter(flightNum);
                        case "PrivateCharter" -> new PrivateCharter(flightNum);
                        default -> throw new IllegalArgumentException(
                            "Unknown aircraft type: " + type);
                    };
                    queue.offer(ac);
                }
            }
        }
    }

    public static void saveBinary(DepotManager depot, BoundedQueue<Aircraft> queue)
            throws IOException {
        try (var oos = new ObjectOutputStream(
                new BufferedOutputStream(
                    Files.newOutputStream(Path.of(BIN_FILE))))) {
            oos.writeObject(depot.getBudget());
            oos.writeObject(depot.getAllResources());
            oos.writeObject(new ArrayList<>(queue.asList()));
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadBinary(DepotManager depot, BoundedQueue<Aircraft> queue)
            throws IOException, ClassNotFoundException {
        Path path = Path.of(BIN_FILE);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Binary save file not found: " + BIN_FILE);
        }

        try (var ois = new ObjectInputStream(
                new BufferedInputStream(
                    Files.newInputStream(path)))) {
            var budget = (BigDecimal) ois.readObject();
            var resources = (Map<Resource, Integer>) ois.readObject();
            var aircraftList = (List<Aircraft>) ois.readObject();

            depot.setBudget(budget);
            for (var entry : resources.entrySet()) {
                depot.setResource(entry.getKey(), entry.getValue());
            }
            queue.clear();
            for (Aircraft ac : aircraftList) {
                queue.offer(ac);
            }
        }
    }
}
