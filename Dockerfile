FROM openjdk:17-alpine
COPY target/casinApp_v3-0.0.1-SNAPSHOT.jar casinApp_v3-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/casinApp_v3-0.0.1-SNAPSHOT.jar"]

