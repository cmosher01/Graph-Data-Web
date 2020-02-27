FROM amazoncorretto:11 AS build

MAINTAINER Christopher A. Mosher <cmosher01@gmail.com>

USER root
ENV HOME /root
WORKDIR $HOME

RUN echo "org.gradle.daemon=false" >gradle.properties

COPY gradle/ gradle/
COPY gradlew ./
RUN ./gradlew --version

COPY settings.gradle ./
COPY build.gradle ./
COPY src/ ./src/

RUN ./gradlew build



FROM jetty:9-jre11

COPY --from=build /root/build/libs/*.war /var/lib/jetty/webapps/ROOT.war
