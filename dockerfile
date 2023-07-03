FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:11
RUN mkdir /app
COPY --from=build /home/gradle/src/docker-entrypoint /app/docker-entrypoint
COPY --from=build /home/gradle/src/build/libs/*.jar /app/sslmo-api.jar

ENTRYPOINT ["/app/docker-entrypoint"]