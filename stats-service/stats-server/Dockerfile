FROM amazoncorretto:11-alpine-jdk
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ewm-stats-service.jar
ENTRYPOINT ["java","-jar","/ewm-stats-service.jar"]