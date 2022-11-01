FROM gradle:6.9.3-jdk11-alpine as builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle bootJar

FROM openjdk:11-jre-slim-buster
EXPOSE 8081
VOLUME /tmp
ARG targethost=localhost:8080
ENV API_HOST=$targethost
ARG LIBS=app/build/libs
COPY --from=builder ${LIBS}/ /app/lib
ENTRYPOINT ["java","-jar","./app/lib/auth-0.0.1-SNAPSHOT.jar"]
