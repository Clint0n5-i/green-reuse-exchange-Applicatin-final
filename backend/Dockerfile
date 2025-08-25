# Multi-stage build for smaller image
FROM eclipse-temurin:17-jdk-alpine as builder

# Install necessary tools including git (needed for some Maven dependencies)
RUN apk add --no-cache bash git

WORKDIR /app

# Copy only the necessary files for Maven to work first
COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

# Make mvnw executable and download dependencies first (improves layer caching)
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B

# Copy the rest of the source code
COPY src/ src/

# Run the build
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Use a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

ENTRYPOINT ["java", "-jar", "/app.jar"]