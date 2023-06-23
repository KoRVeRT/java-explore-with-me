FROM amazoncorretto:11-alpine-jdk
COPY target/stats-server-0.0.1-SNAPSHOT.jar ewm-stats-service.jar
ENTRYPOINT ["java","-jar","/ewm-stats-service.jar"]