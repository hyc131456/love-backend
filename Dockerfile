# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /workspace

COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache tzdata \
    && addgroup -S loveapp \
    && adduser -S loveapp -G loveapp \
    && mkdir -p /app/uploads \
    && chown -R loveapp:loveapp /app

WORKDIR /app
COPY --from=build --chown=loveapp:loveapp /workspace/target/love-backend-*.jar app.jar

USER loveapp
EXPOSE 18888

ENTRYPOINT ["java", "-jar", "app.jar"]
