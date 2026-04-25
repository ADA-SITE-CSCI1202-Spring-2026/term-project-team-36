package persistence;
import model.*;
import java.io.*;
import java.util.Queue;
public class SaveLoadManager {
    private static final String FILE_NAME = "airport_state.csv";
    public static void save(DepotManager depot, Queue<Aircraft> queue) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            pw.println("BUDGET," + depot.getBudget());
            for (Resource r : Resource.values()) {
                pw.println("RESOURCE," + r.name() + "," + depot.getResource(r));
            }
            for (Aircraft ac : queue) {
                pw.println("TASK," + ac.getType() + "," + ac.getFlightNumber() + ","
                    + ac.getRequiredFuel() + "," + ac.getRequiredMeals() + "," + ac.getReward());
            }
        }
    }
    public static void load(DepotManager depot, Queue<Aircraft> queue) throws IOException {
        File file = new File(FILE_NAME);
        if (!file.exists()) throw new FileNotFoundException("Save file not found: " + FILE_NAME);
        queue.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                switch (parts[0]) {
                    case "BUDGET" -> depot.setBudget(Double.parseDouble(parts[1]));
                    case "RESOURCE" -> depot.setResource(Resource.valueOf(parts[1]), Integer.parseInt(parts[2]));
                    case "TASK" -> {
                        String type = parts[1];
                        String flightNum = parts[2];
                        Aircraft ac = switch (type) {
                            case "CommercialJet" -> new CommercialJet(flightNum);
                            case "CargoFreighter" -> new CargoFreighter(flightNum);
                            case "PrivateCharter" -> new PrivateCharter(flightNum);
                            default -> throw new IllegalArgumentException("Unknown type: " + type);
                        };
                        queue.add(ac);
                    }
                }
            }
        }
    }
}
