FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:latest
VOLUME /tmp 
COPY target/login-service-0.0.1-SNAPSHOT.jar login-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","login-service-0.0.1-SNAPSHOT.jar"]

# Build the Spring Boot application
# ./mvnw clean package
# Build docker image
# podman build -t login-service:latest .
# Run container
# podman run --name login-service -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3307/login_db -e SPRING_DATASOURCE_USERNAME=user -e SPRING_DATASOURCE_PASSWORD=password login-service:latest


