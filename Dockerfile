# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cache layer)
COPY pom.xml .
# Add this before dependency go-offline
RUN rm -rf /root/.m2/repository/org/springframework/security

# Or force update
RUN mvn dependency:resolve -U
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the JAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime (smaller image)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (Spring Boot default)
EXPOSE 8081

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]