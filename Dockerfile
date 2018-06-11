FROM gradle:jdk8

USER root
RUN wget -O /opt/codacy.jar https://github.com/codacy/codacy-coverage-reporter/releases/download/4.0.1/codacy-coverage-reporter-4.0.1-assembly.jar
