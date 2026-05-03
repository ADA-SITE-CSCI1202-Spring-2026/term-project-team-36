package model;

public enum Difficulty {
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

    private final String city;
    private final String airport;
    private final String tier;
    private final double spawnIntervalSec;
    private final double initialBudget;
    private final int    initialFuel;
    private final int    initialMeals;
    private final double winTargetBudget;
    private final int    maxQueueSize;
    private final double operationalCostPerInterval;
    private final String accentColor;
    private final String bgColor;
    private final String description;

    Difficulty(String city, String airport, String tier,
               double spawnIntervalSec, double initialBudget,
               int initialFuel, int initialMeals,
               double winTargetBudget, int maxQueueSize,
               double operationalCostPerInterval,
               String accentColor, String bgColor, String description) {
        this.city = city;
        this.airport = airport;
        this.tier = tier;
        this.spawnIntervalSec = spawnIntervalSec;
        this.initialBudget = initialBudget;
        this.initialFuel = initialFuel;
        this.initialMeals = initialMeals;
        this.winTargetBudget = winTargetBudget;
        this.maxQueueSize = maxQueueSize;
        this.operationalCostPerInterval = operationalCostPerInterval;
        this.accentColor = accentColor;
        this.bgColor = bgColor;
        this.description = description;
    }

    public String getCity()                      { return city; }
    public String getAirport()                   { return airport; }
    public String getTier()                      { return tier; }
    public double getSpawnIntervalSec()          { return spawnIntervalSec; }
    public double getInitialBudget()             { return initialBudget; }
    public int    getInitialFuel()               { return initialFuel; }
    public int    getInitialMeals()              { return initialMeals; }
    public double getWinTargetBudget()           { return winTargetBudget; }
    public int    getMaxQueueSize()              { return maxQueueSize; }
    public double getOperationalCostPerInterval(){ return operationalCostPerInterval; }
    public String getAccentColor()               { return accentColor; }
    public String getBgColor()                   { return bgColor; }
    public String getDescription()               { return description; }
}
