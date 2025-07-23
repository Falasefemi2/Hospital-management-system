# Stage 1: Build the app with Maven
FROM maven:3.9.6-eclipse-temurin-21 as builder

WORKDIR /app

# Copy all files
COPY . .

# Package the app
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the app
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy only the built JAR from the first stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
