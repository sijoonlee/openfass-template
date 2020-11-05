# sudo docker run -it --rm --entrypoint=/bin/bash b90a2719124f // working

FROM maven:3.6.3-openjdk-11-slim as builder
# FROM openjdk:11-jdk-slim as builder

RUN mkdir -p /home/app/libs
# RUN mkdir -p /root/.m2/repository/com/openfaas/template/java

ENV MAVEN_OPTS=""

WORKDIR /home/app

COPY . /home/app/

WORKDIR /home/app

RUN ls /home/app/function

RUN  mvn clean package -e

RUN ls /home/app/function

FROM openfaas/of-watchdog:0.7.6 as watchdog

FROM openjdk:11-jre-slim as ship
RUN apt-get update -qqy \
 && apt-get install -qqy \
  --no-install-recommends \
  unzip
RUN addgroup --system app \
    && adduser --system --ingroup app app

# RUN addgroup -S app && adduser -S -g app app

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

WORKDIR /home/app
ARG artifactId=function
RUN ls /home/app/

COPY --from=builder /home/app/function/target/function-1.0.0-jar-with-dependencies.jar ./function.jar

#COPY --from=builder /home/app/function/build/distributions/function-1.0.zip ./function-1.0.zip
# user app
RUN unzip ./function.jar -d function
RUN ls /home/app/
RUN ls /home/app/function
RUN chown -R app /home/app
WORKDIR /home/app/

ENV upstream_url="http://127.0.0.1:8082"
ENV mode="http"
ENV CLASSPATH="/home/app/function.jar:/home/app/function/*"

ENV fprocess="java -XX:+UseContainerSupport com.openfaas.entrypoint.App"
EXPOSE 8080

HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

CMD ["fwatchdog"]
