FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/capital_gain-1.0-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "server"]
