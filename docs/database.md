# UNO ORM & Persistence Documentation

This document outlines the database architecture, ORM mapping, and query reporting features implemented for Assignment 5.

## 1. Selected Architecture
* **Database:** H2 Embedded In-Memory Database (`jdbc:h2:mem:unodb`). Selected because it provides a self-contained local development runtime requiring zero external server configuration or developer machine state dependencies.
* **ORM Framework:** Hibernate ORM / JPA 3.1 (`jakarta.persistence`).
* **Schema Generation:** Automated via `drop-and-create` lifecycle action defined inside `src/main/resources/META-INF/persistence.xml`.

## 2. Schema & Entity Mappings
The database schema strictly separates core game logic from storage concerns, capturing all required game data across two relational tables:
1. `games` (`GameRecordEntity`): Stores `id`, `winner`, `roundsPlayed`, and `completedTimestamp`.
2. `scores` (`PlayerScoreEntity`): Stores `id`, `playerName`, `score`, and a foreign key `game_id` linking back to the parent game.

Relationships are fully managed via JPA `@OneToMany` and `@ManyToOne` cascading mappings.

## 3. Query & Report Features
The persistence layer supports three primary statistical queries accessed via `UnoGameRepository`:
* **Recent Games:** Retrieves completed matches ordered chronologically by descending completion timestamp.
* **Player Win Count:** Aggregates total career victories for a specific player username.
* **Highest Scores:** Ranks individual player single-game point totals in descending order.

## 4. Running Persistence Tests
Persistence verification is completely isolated from developer machine state. To execute the standalone test suite:
```bash
mvn test -Dtest=UnoGameRepositoryTest