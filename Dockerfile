# syntax=docker/dockerfile:1
FROM ubuntu:20.04
COPY src /app/src
COPY manifest.mf /app
RUN apt-get update
RUN apt-get -y install xauth
RUN apt-get install -y openjdk-8-jdk-headless
RUN export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64/"
RUN export PATH="/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH"
WORKDIR /app
RUN find -name '*.java' > sources.txt
RUN mkdir output
RUN javac -encoding ISO-8859-1 -d output @sources.txt
RUN cp -r src/main/resources/* output
WORKDIR /app/output
RUN jar cmf ../manifest.mf thethetim.jar com data
CMD java -jar thethetim.jar
