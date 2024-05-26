FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/carpark-0.0.1-SNAPSHOT.jar /app/carpark-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "carpark-api.jar"]
