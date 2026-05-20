# Extension Readiness

## Which extension would your design support best?
The current design best supports **adding a new card effect or rule variant**.

## Where would that change be implemented?
Because the responsibilities are now separated, adding a new card (like a "Skip Everyone" card) would only require two clean changes:
1. Updating `UnoRules.java` so `rank()` and `points()` know how to identify and score the new card string.
2. Updating the `applyCardEffect()` method in `Main.java` to execute the specific turn-manipulation logic (e.g., advancing the `currentPlayer` index multiple times).
   We no longer have to hunt through a massive game loop or update duplicate bot logic to make this happen.

## What part of your design still makes change difficult?
The system's reliance on global static arrays for the `deck`, `discard`, and player `hands` in `Main.java` makes structural changes difficult. For instance, adding a "Replay Log" extension or a "Smarter Bot Strategy" would be risky because any method can accidentally overwrite the global deck state. To fully support those features, the next refactoring step would be to extract a `GameState` object to encapsulate and protect the deck and player hands.
