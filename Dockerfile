FROM openjdk:17.0.2-jdk-slim
EXPOSE 8888
WORKDIR /app
ARG JAR_FILE=target/love-backend-1.0.0.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]