package model;

public enum Difficulty {

    // Defining places 
    BAKU(
        "BAKU", "Heydar Aliyev International", "EASY",
        10.0, 80_000.0, 5000, 500,
        200_000.0, 15, 300.0,
        "#39ff14", "#003a00",
        "Low traffic. Generous resources. Perfect for learning GAA protocols."
    ),
    MOSCOW(
        "MOSCOW", "Sheremetyevo International", "MEDIUM",
        6.0, 50_000.0, 3000, 250,
        150_000.0, 12, 500.0,
        "#ffaa00", "#3a2a00",
        "Standard load. Flights arrive faster. Manage your depot carefully."
    ),
    TOKYO(
        "TOKYO", "Haneda International", "HARD",
        3.0, 30_000.0, 2000, 150,
        100_000.0, 10, 800.0,
        "#ff4444", "#3a0000",
        "Maximum throughput. Near-zero margin for error. For veterans only."
    );
}
