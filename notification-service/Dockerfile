FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:latest
VOLUME /tmp 
COPY target/notification-service-0.0.1-SNAPSHOT.jar notification-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","notification-service-0.0.1-SNAPSHOT.jar"]