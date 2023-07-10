FROM amazoncorretto:11-alpine-jdk
COPY target/main-service-1.0.0.jar ewm-main-service.jar
ENTRYPOINT ["java","-jar","/ewm-main-service.jar"]