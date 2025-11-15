FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /workspace

# copy pom first to leverage Docker cache for dependencies
COPY pom.xml ./
COPY src ./src

# build the application (skip tests in container build to speed up; CI can run tests earlier)
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# copy the fat jar produced by the builder stage
COPY --from=build /workspace/target/demo-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]