FROM openjdk:17-jdk-slim

WORKDIR /app
COPY ./ ./
RUN ./gradlew build

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "./build/libs/Factory-0.0.1-SNAPSHOT.jar"]