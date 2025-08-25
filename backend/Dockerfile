# Multi-stage build for smaller image
FROM eclipse-temurin:17-jdk-alpine as builder

# Install necessary tools
RUN apk add --no-cache bash

WORKDIR /app
COPY . .

# Make mvnw executable and run build
RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]