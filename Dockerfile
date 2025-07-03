# ---------- Stage 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build
COPY src ./src
RUN mvn clean install


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/hotel-0.0.1-SNAPSHOT.jar app.jar

# Expose port and run
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
