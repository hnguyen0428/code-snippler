FROM ubuntu:16.04

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    add-apt-repository ppa:webupd8team/java -y && \
    apt-get update && \
    echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
    apt-get install -y oracle-java8-installer && \
    apt-get clean

COPY . /codesnippler_backend

WORKDIR /codesnippler_backend

ARG MONGODB_URI
ARG MONGODB_DATABASE
ARG MONGODB_USERNAME
ARG MONGODB_PASSWORD
ARG CLIENT_KEY_ENC
ARG ADMIN_KEY_ENC

RUN ./gradlew build

ENTRYPOINT ["java", "-jar", "build/libs/codesnippler-0.0.1-SNAPSHOT.jar"]
