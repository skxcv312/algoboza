FROM ubuntu:latest
LABEL authors="skxcv312"

FROM eclipse-temurin:21-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} demo1.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/demo1.jar"]