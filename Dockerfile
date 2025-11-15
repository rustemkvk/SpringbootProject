FROM eclipse-temurin:21-jdk
WORKDIR /app
FROM eclipse-temurin:21-jre
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]