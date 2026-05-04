# Skyways Airport Dispatch Tycoon

Real-time airport ground operations simulation. JavaFX desktop app.  
CSCI 1202 — Programming Principles II, Spring 2026.

---

## How to Run

### Windows
Double-click **`run.bat`** or run in terminal:
```
.\mvnw.cmd javafx:run
```

### macOS / Linux
Double-click **`run.sh`** or run in terminal:
```
./run.sh
```

> First launch downloads Maven + JavaFX automatically (~50MB). Needs internet once.  
> Java 21 must be installed. Check: `java -version`  
> macOS install: `brew install openjdk@21`

---

## Gameplay

1. Pick a city on the welcome screen
2. Aircraft arrive in the **Holding Pattern** automatically
3. Click **CLEAR NEXT FLIGHT** to dispatch it
4. Each flight consumes **Jet Fuel** and/or **Meals** from the depot
5. Use **SUPPLY REQUISITION** to restock before running dry
6. Operational costs drain budget every interval — stay profitable

**WIN** → reach the budget target shown in the progress bar  
**LOSE** → queue overflows OR budget goes bankrupt

---

## Difficulty

| City   | Level  | Interval | Start Budget | Win Target |
|--------|--------|----------|--------------|------------|
| Baku   | Easy   | 10s      | $80,000      | $200,000   |
| Moscow | Medium | 6s       | $50,000      | $150,000   |
| Tokyo  | Hard   | 3s       | $30,000      | $100,000   |

---

## Save / Load

- **SAVE STATE** → writes `airport_state.csv` in the project folder  
- **LOAD STATE** → restores exact queue, resources, and budget

---

## Screenshots

| | | |
|:---:|:---:|:---:|
| ![Gameplay 1](docs/gameplay_1.png) | ![Gameplay 2](docs/gameplay_2.png) | ![Gameplay 3](docs/gameplay_3.png) |

---

## UML Class Diagram

![UML Class Diagram](docs/uml_diagram.png)
