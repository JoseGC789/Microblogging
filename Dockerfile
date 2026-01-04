FROM gradle:9.2.1-jdk25 AS build
WORKDIR /home/app
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src
COPY lombok.config ./lombok.config
RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /home/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
