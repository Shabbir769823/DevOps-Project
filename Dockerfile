# Stage 1: Build stage using Maven and Eclipse Temurin JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
# Cache maven dependencies
RUN mvn dependency:go-offline -B
COPY src ./src
# Compile and package jar
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage using lightweight JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/employee-app-0.0.1-SNAPSHOT.jar app.jar

# Expose ports
EXPOSE 8080

# Env default vars
ENV DB_HOST=localhost
ENV DB_PORT=3306
ENV DB_NAME=devops_db
ENV DB_USER=root
ENV DB_PASSWORD=root

ENTRYPOINT ["java", "-jar", "app.jar"]
