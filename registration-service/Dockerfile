FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:latest
WORKDIR /app
COPY target/registration-service-0.0.1-SNAPSHOT.jar registration-service-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","registration-service-0.0.1-SNAPSHOT.jar"]
