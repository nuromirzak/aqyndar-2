FROM openjdk:21-bullseye

ARG JAR_FILE=build/libs/aqyndar-0.0.1-SNAPSHOT.jar

WORKDIR /app

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]
