FROM eclipse-temurin:24-jdk-alpine AS build

WORKDIR /workspace

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

RUN addgroup -S cultural && adduser -S cultural -G cultural

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

USER cultural

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]