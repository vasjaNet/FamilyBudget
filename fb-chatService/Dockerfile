FROM eclipse-temurin:21-jre
LABEL authors="vasyl"

WORKDIR /app
COPY build/libs/fb-chatService-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
