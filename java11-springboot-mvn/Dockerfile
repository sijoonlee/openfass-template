FROM maven:3.6.3-openjdk-11-slim as builder

RUN mkdir -p /home/app/libs

ENV MAVEN_OPTS=""

WORKDIR /home/app

COPY . /home/app/

WORKDIR /home/app

RUN  mvn clean package -e

FROM openfaas/of-watchdog:0.7.6 as watchdog

FROM openjdk:11-jre-slim as ship
RUN apt-get update -qqy \
 && apt-get install -qqy \
  --no-install-recommends \
  unzip
RUN addgroup --system app \
    && adduser --system --ingroup app app

COPY --from=watchdog /fwatchdog /usr/bin/fwatchdog
RUN chmod +x /usr/bin/fwatchdog

WORKDIR /home/app

COPY --from=builder /home/app/function/target/function-1.0.0.jar ./function.jar

RUN unzip ./function.jar -d function

RUN chown -R app /home/app
WORKDIR /home/app/

ENV upstream_url="http://127.0.0.1:8082"
ENV mode="http"
ENV CLASSPATH="/home/app/function.jar:/home/app/function/*"
# ENV fprocess="java -XX:+UseContainerSupport com.openfaas.entrypoint.App"
ENV fprocess="java -XX:+UseContainerSupport org.springframework.boot.loader.JarLauncher"

EXPOSE 8080

HEALTHCHECK --interval=5s CMD [ -e /tmp/.lock ] || exit 1

CMD ["fwatchdog"]
