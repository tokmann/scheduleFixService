FROM openjdk:23-jdk-oracle
ARG JAR_FILE=target/*.jar
COPY ./target/ScheduleFixService-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]