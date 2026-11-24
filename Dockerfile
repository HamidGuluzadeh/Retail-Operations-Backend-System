FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/retail-operations-0.0.1-SNAPSHOT.jar /app/retail-operations-0.0.1-SNAPSHOT.jar
EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "retail-operations.jar"]