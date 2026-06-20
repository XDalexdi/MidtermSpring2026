# Stage 1: Build the project using Maven
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package

# Stage 2: Run the compiled jar
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/uno-cli-1.0-SNAPSHOT.jar app.jar

# Set the default command to start the game
ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--bots", "2", "--games", "1"]