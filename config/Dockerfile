FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD build/libs/config-0.0.1-SNAPSHOT.jar app.jar
RUN /bin/sh -c 'touch /app.jar\'
ENV JAVA_OPTS=""
EXPOSE 8888
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Xdebug", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7888","-jar","/app.jar"]
