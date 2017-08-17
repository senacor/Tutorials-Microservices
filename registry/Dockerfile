FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD build/libs/registry-0.0.1-SNAPSHOT.jar app.jar
RUN /bin/sh -c 'touch /app.jar\'
ENV JAVA_OPTS=""
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app.jar"]
