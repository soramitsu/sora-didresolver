FROM openjdk:8-jdk-alpine
MAINTAINER Soramitsu Co Ltd <bogdan@soramitsu.co.jp>
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
