FROM openjdk:13-alpine3.9

ARG profile=prod

COPY bin/reserv-service.jar /app/app.jar

WORKDIR /app

ENTRYPOINT java -jar -Dspring.profiles.active=$profile app.jar
