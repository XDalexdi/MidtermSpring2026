# Final Project Report

## Architecture and Rule Separation
The primary goal of this final project was to strictly separate the game rules from the CLI interactions. This was achieved by removing hardcoded logic from the `GameEngine` and delegating it to specialized, state-independent classes:
* `UnoRuleValidator`: Handles pure rule checking (color, number, wild).
* `TurnManager`: Tracks the current player index and handles directional shifts (Skip, Reverse).
* `CardEffectHandler`: Applies drawing penalties to player hands.
* `UnoScorer`: Calculates final hand values mathematically.
* `UnoCallManager`: Detects the one-card state and handles penalties.

## CLI Playability
The game is fully playable from the command line. `GameEngine` acts as the controller, managing the `while` loop, outputting the current top card, prompting human players for input, and executing basic bot logic to select legal cards via the `UnoRuleValidator`.

## Testing Evidence
Extensive JUnit testing was added to prove the underlying rule logic without requiring console interaction. Tests specifically verify:
* Deck size and Wild card counts.
* Legal and illegal card matching.
* Clockwise/Counter-clockwise turn navigation and skip mechanics.
* Draw Two and Wild Draw Four hand manipulation.
* Correct card value scoring and missed-UNO penalties.

## Database Integration
The architecture remains compatible with the previous persistence requirements. Once a round concludes, a `GameRecordEntity` is instantiated and saved to the H2 database via the `UnoGameRepository`.