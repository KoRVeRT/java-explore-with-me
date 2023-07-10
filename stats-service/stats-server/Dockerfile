FROM amazoncorretto:11-alpine-jdk
COPY target/stats-server-1.0.0.jar ewm-stats-service.jar
ENTRYPOINT ["java","-jar","/ewm-stats-service.jar"]