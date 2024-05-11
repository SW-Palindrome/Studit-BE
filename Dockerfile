FROM gradle:7.6.4-jdk17 AS builder
WORKDIR /build
COPY build.gradle settings.gradle /build
COPY src src

RUN gradle build -x test --parallel

FROM openjdk:17
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
