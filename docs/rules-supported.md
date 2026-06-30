# Supported Rules

This project implements the following rules from the Final Project UNO Rules Reference:

* **Deck Composition:** Classic 108-card deck with correct distributions.
* **Legal Play Validation:** Cards are validated by matching color, number, action type, or being a Wild card.
* **Skip:** Next player loses their turn.
* **Reverse:** Turn direction changes (acts as a Skip in 2-player games).
* **Draw Two:** Next player draws two cards and loses their turn.
* **Wild:** Player chooses the next active color.
* **Wild Draw Four:** Player chooses the next active color, next player draws four cards and loses their turn.
* **Draw/Pass Behavior:** Player draws one card and passes without playing it.
* **UNO Call & Penalty:** The system detects the one-card state, supports UNO calls, and penalizes missed calls with a two-card draw.
* **Round End & Scoring:** The round ends when a hand is empty. Points are calculated based on remaining opponents' hands (Number cards = face value, Actions = 20, Wilds = 50).