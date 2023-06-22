FROM amazoncorretto:11-alpine-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ewm-main-service.jar
ENTRYPOINT ["java","-jar","/ewm-main-service.jar"]