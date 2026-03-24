FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy gradle wrapper and build files first for layer caching
COPY gradlew gradlew.bat ./
COPY gradle/ ./gradle/
COPY build.gradle settings.gradle ./

# Copy source code
COPY src/ ./src/

# Build the JAR
RUN chmod +x gradlew && \
    ./gradlew bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre
LABEL authors="vasyl"

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]