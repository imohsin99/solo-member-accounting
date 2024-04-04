
# Build stage
#
FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR /app
RUN mvn clean install



#
# Package stage
#
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app/gateway/app.jar
COPY gateway/src/main/resources/application.properties app/gateway/src/main/resources/application.properties
COPY gateway/src/main/resources/application-docker.properties app/gateway/src/main/resources/application-docker.properties
COPY gateway/src/main/resources/application-stage.properties app/gateway/src/main/resources/application-stage.properties
COPY gateway/src/main/resources/application-prod.properties app/gateway/src/main/resources/application-prod.properties
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app/gateway/app.jar"]