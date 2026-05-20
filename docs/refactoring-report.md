# Refactoring Report

## What behavior did you characterize before refactoring?
Before modifying the architecture, I expanded the `selfTest()` method to characterize 20 distinct behaviors of the existing implementation. This included standard game properties (scoring, legal play matching by color/number/action), but more importantly, it locked down the game's specific quirks:
* The bot's specific logic prioritization (Draw Two > Skip > Number > Wild).
* The bot's method for picking a color after a Wild card (based on maximum color count in hand).
* The edge-case fallback behavior where drawing from an empty deck/discard pile yields a "W" card.

## What were the worst design problems you found?
The original `Main.java` was a "God Class" suffering from severe Mixed Responsibilities and Duplicated Code.
1. The exact same legality rules (`isLegal`) were copy-pasted in multiple places, including inside the bot's card selection logic.
2. The main game loop (`playGame`) was a massive, scrolling block of text that mixed user input/output, rule validation, card effects, and scoring calculations all into one scope.

## Which refactorings did you perform?
1. **Extract Class:** I pulled the core rule logic out of `Main.java` and into a dedicated `UnoRules.java` class. This gave the rules a clear home and made them testable without running the console game.
2. **Extract Method:** I pulled the large `if/else` block handling action cards out of the loop into an `applyCardEffect()` method.
3. **Extract Method:** I pulled the point-tallying logic out of the game end condition into a `processWin()` method.
4. **Remove Duplication:** I updated `chooseBotCard()` to rely on `UnoRules.isLegal()` instead of reinventing the wheel.

## What behavior did you intentionally preserve?
I strictly preserved the existing gameplay quirks. The bot still cannot play Reverse cards (it falls back to drawing). Humans can still type "draw" even when they hold a legal card. Hand arrays are still printed openly to the terminal. Illegal index inputs still result in a penalty draw and skipped turn.

## What risks remain?
The application still relies heavily on global, mutable static variables (`hands`, `deck`, `discard`, `currentPlayer`). While the rules are separated, the game state itself is tightly coupled to the `Main` class, meaning we could not easily run two games in parallel or easily swap out the console view for a GUI without further decoupling.
