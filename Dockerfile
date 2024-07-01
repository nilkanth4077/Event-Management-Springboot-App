FROM openjdk:17-jdk-alpine
WORKDIR /event-management-app
COPY target/event-management-0.0.1-SNAPSHOT.jar event-management-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "event-management-app.jar"]