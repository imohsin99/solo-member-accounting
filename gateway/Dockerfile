FROM openjdk:17-jdk-slim-buster
VOLUME /tmp
ENV AMQP_HOST=tcp://soloactivemq:61616
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
COPY src/main/resources/application.properties src/main/resources/application.properties
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]