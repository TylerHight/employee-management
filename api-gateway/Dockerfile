FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:latest
VOLUME /tmp 
COPY target/api-gateway-0.0.1-SNAPSHOT.jar api-gateway-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","api-gateway-0.0.1-SNAPSHOT.jar"]

# Build the Spring Boot application
# ./mvnw clean package
# Build docker image
# podman build -t api-gateway:latest .
# Run container
# podman run --name api-gateway -p 8080:8080 api-gateway:latest
