FROM eclipse-temurin:21-jdk

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} algoboza.jar


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/algoboza.jar"]
