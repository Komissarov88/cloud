FROM openjdk:8
WORKDIR /app
COPY build/libs .
EXPOSE 8189
ENTRYPOINT ["java", "-jar", "cloud-server-1.0-SNAPSHOT.jar"]