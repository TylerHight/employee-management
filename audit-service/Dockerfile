FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:latest
WORKDIR /app

# Copy the pre-built JAR file
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]