FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:latest
VOLUME /tmp 
COPY target/discovery-service-0.0.1-SNAPSHOT.jar discovery-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","discovery-service-0.0.1-SNAPSHOT.jar"]

# mvn clean package
# podman build -t discovery-service .
# podman run -p 8761:8761 discovery-service