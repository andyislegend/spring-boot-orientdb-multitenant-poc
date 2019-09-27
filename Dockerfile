FROM gradle:jdk11-slim AS builder
WORKDIR /usr/src
USER root
COPY src/ ./src/
COPY build.gradle .
COPY settings.gradle .
RUN gradle build -x test

FROM openjdk:11.0.4-jre-slim
USER root
WORKDIR /usr/src
COPY docker-startup.sh .
RUN chmod +x *.sh
COPY --from=builder /usr/src/build/libs/*.jar .
EXPOSE 8001
CMD ["/bin/sh", "docker-startup.sh"]
