# Extension Readiness

## Which extension would your design support best?
The current design best supports **adding a smarter bot strategy** or **adding a replay log**. 

## Where would that change be implemented?
Because the architecture is now fully separated:
1. **Smarter Bots:** We would only need to update the `BotStrategy.java` class. Since the bots now have their own isolated decision-making logic, we could easily pass the `GameState` to them to allow for card-counting or predictive strategies without touching the core game loop.
2. **Replay Log:** Because all mutable state is now safely encapsulated inside the `GameState` object, we could easily track every turn by logging snapshots of `GameState` after each move in `GameEngine`, without worrying about rogue methods mutating global arrays.

## What part of your design still makes change difficult?
The biggest remaining friction point is replacing the CLI view with a graphical user interface (GUI). Right now, console printing (`System.out.println`) and user input (`Scanner`) are directly hardcoded inside the `GameEngine.java` orchestration loop. To add a GUI, we would need to further decouple the engine by extracting a dedicated `View` interface so the engine doesn't care whether it is talking to a terminal or a visual window.
