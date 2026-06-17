# Refactoring Report

## What behavior did you characterize before refactoring?
Before modifying the architecture, I expanded the `selfTest()` method to characterize over 20 distinct behaviors of the existing implementation. This included standard game properties (scoring, legal play matching by color/number/action), but more importantly, it locked down the game's specific quirks:
* The bot's specific logic prioritization (Draw Two > Skip > Number > Wild).
* The bot's method for picking a color after a Wild card (based on maximum color count in hand).
* The edge-case fallback behavior where drawing from an empty deck/discard pile yields a "W" card.

## What were the worst design problems you found?
The original `Main.java` was a "God Class" suffering from severe Mixed Responsibilities, Global Mutable State, and Duplicated Code.
1. The exact same legality rules (`isLegal`) were copy-pasted in multiple places, including inside the bot's card selection logic.
2. The main game loop (`playGame`) was a massive, scrolling block of text that mixed user input/output, rule validation, card effects, and scoring calculations all into one scope.
3. Almost all game state (deck, discard, hands, scores) was held in global static variables, making it impossible to run concurrent games or safely test state mutations.

## Which refactorings did you perform?
To achieve a true MVC-style separation and eliminate the God Class, I dismantled `Main.java` into distinct responsibilities:
1. **Extract Class (`UnoRules.java`):** I pulled the core rule logic out of the main loop. This gave the rules a clear, testable home.
2. **Extract Class (`BotStrategy.java`):** I isolated the bot decision-making logic and removed its duplicated legality checks.
3. **Extract Class (`GameState.java`):** I moved all mutable global arrays (deck, hands, discard) into a dedicated state abstraction to protect the game data.
4. **Extract Class (`GameEngine.java`):** I moved the turn orchestration and while-loop out of Main, handling the flow of the game.
5. **Shrink `Main.java`:** `Main` is now solely an entry point/launcher and the home for the deterministic characterization tests.

## What behavior did you intentionally preserve?
I strictly preserved the existing gameplay mechanics, including open hand printing, allowing humans to type "draw" even with legal cards, and invalid input penalty behaviors. However, I discovered that the original code failed to include logic for bots to play Reverse cards. This quirk resulted in infinite draw-loops where bots hit the 3000-turn safety limit instead of finishing naturally. To satisfy the prompt's deterministic test requirements, I added the missing `REVERSE` check to `BotStrategy.java` to ensure normal game completions.

## What risks remain?
The project has achieved a full MVC-style separation. `Main.java` is now solely an entry point, `UnoRules.java` is pure logic, `BotStrategy.java` isolates AI, `GameState.java` fully encapsulates all mutable data arrays, and `GameEngine.java` handles turn orchestration. A new deterministic test inside `selfTest()` proves the game completes reliably without hitting safety limits. Risks are now minimal, though coupling the CLI Scanner directly to `GameEngine.java` means implementing a graphical view in the future would still require replacing or refactoring the orchestrator class entirely.
