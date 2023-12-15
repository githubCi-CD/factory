# Build Stage
FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Final Stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /app/deploy/pinpoint /app/pinpoint

EXPOSE 8082

ENTRYPOINT ["java", "-javaagent:/app/pinpoint/pinpoint-bootstrap-2.5.3.jar", "-Dpinpoint.agentId=factory", "-Dpinpoint.applicationName=factory", "-Dpinpoint.config=/app/pinpoint/pinpoint-root.config" ,"-jar", "app.jar"]

