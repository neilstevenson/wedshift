#FROM --platform=linux/s390x quay.io/devfile/maven:3.8.1-openjdk-17-slim
#FROM quay.io/devfile/maven:3.8.1-openjdk-17-slim
#FROM openjdk:17
FROM quay.io/devfile/maven:3.3.3-jdk-9

WORKDIR /build

RUN echo Version 1

RUN echo arch
RUN arch
RUN echo java -version
RUN java -version

COPY pom.xml .
COPY src src
RUN mvn package

ENV MY_HAZELCAST_LICENSE=${MY_HAZELCAST_LICENSE}    
ENV JAVA_ARGS ""
ENV JAVA_OPTS "--add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED \
    --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED \
    --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED \
    --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED"

CMD ["bash", "-c", "set -euo pipefail \
      && echo @@@@@@@@@@ \
      && echo arch \
      && arch \
      && echo @@@@@@@@@@ \
      && echo java --version \
      && java --version \
      && echo @@@@@@@@@@ \
      && echo java $JAVA_ARGS $JAVA_OPTS \
 -Dhazelcast.enterprise.license.key=${MY_HAZELCAST_LICENSE} \
 -Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener \
 -jar application.jar \
      && echo sleep 60 \
      && sleep 60 \
      && java $JAVA_ARGS $JAVA_OPTS \
 -Dhazelcast.enterprise.license.key=${MY_HAZELCAST_LICENSE} \
 -Dlogback.statusListenerClass=ch.qos.logback.core.status.NopStatusListener \
 -jar /build/target/wedshift.jar \
     "] 
