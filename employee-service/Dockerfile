FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:latest
VOLUME /tmp 
COPY target/employee-service-0.0.1-SNAPSHOT.jar employee-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","employee-service-0.0.1-SNAPSHOT.jar"]

# Build the Spring Boot application
# ./mvnw clean package
# Build docker image
# podman build -t employee-service .
# Run container
# podman run -d --name employee-service -p 8081:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://host.containers.internal:3308/employee_db employee-service



