# Hints for Tutorial stage 06

## Overview

In stage 06 we create/use docker containers for all the projects we have instead of running the projects locally. Note that the images are not linked together yet, but we just use the IP addresses of the containers to reference them (e.g. reference the config server in the demo and accounting service configuration).

In stage 07 we will use docker-compose to make the setup more robust and easy to use.

## Configures for the MySQL databases as docker containers

### Startup the containers

MySQL command to run the demo service's database:

```sh
docker run --detach --name demodb -e MYSQL_ROOT_PASSWORD=mysql -e MYSQL_DATABASE=demodb -d mysql
```

MySQL command to run the demo service's database:

```sh
docker run --detach --name accountingdb -e MYSQL_ROOT_PASSWORD=mysql -e MYSQL_DATABASE=accountingdb -d mysql
```

What the command does:

```sh
docker run 
  --detach # detached mode (means the process does not block the terminal upon startup but starts in the background)
  --name accountingdb # defines the name the container will have
  -e MYSQL_ROOT_PASSWORD=mysql # defines the root password to be set
  -e MYSQL_DATABASE=accountingdb # defines the database to be created upon startup
  -d mysql # defines the docker image to be used (from docker-hub)
```


### Configure the demo and accounting project

In order to be able to connect to the database the IP has to be defined correctly. To retrieve the IP address of the container you can run:

```sh
docker inspect demodb | grep IPAddress
```

and

```sh
docker inspect accountingdb | grep IPAddress
```

Note: To get the MySQL console within one of the MySQL docker containers you can use this command (```172.17.0.2``` being the IP address of the container):
```sh
mysql -uroot -pmysql -h 172.17.0.2 -P 3306
```

Configure the database connection string in the ```application.yml``` of the demo and the accounting project; instead of ```localhost``` you should put the IP of the container:

```YAML
spring:
  datasource:
    url: 'jdbc:mysql://172.17.0.3:3306/accountingdb'
    username: 'root'
    password: 'mysql'
    driver-class-name: 'com.mysql.jdbc.Driver'
```


## Container for the discovery

### Use an existing docker image from Docker-Hub

You can just run the springcloud/eureka container:
```sh
docker run --detach --name registry -d springcloud/eureka
```

Note: You might get the error ```DiscoveryClient_ACCOUNTING/10.0.2.15:accounting:8082 - was unable to send heartbeat!``` followed by an exception. This is most likely because of different dependency versions on eureka client and server. If you encounter this error you can create your own image from the registry project (see next optional section).

### Create your own eureka image (optional)

Alternatively to using an already existing docker image (like ```springcloud/eureka```) you can also add a Dockerfile to your registry project and use a plain java container to create the registry container.

The Dockerfile you place into the registry project folder can look something like this:
```
FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD build/libs/registry-0.0.1-SNAPSHOT.jar app.jar
RUN /bin/sh -c 'touch /app.jar\'
ENV JAVA_OPTS=""
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app.jar"]
```

Navigate to the registry project folder in your terminal and build the registry image like this:
```sh
docker build -t registry .
```

Then you can run the container like this: 
```sh
docker run --detach --name registry registry
```

Note: You will have to build the registry project in order to create the JAR file that is added to the container. Run ```./gradlew build``` in the registry project directory.

### Configure the IP address of eureka in the demo and the accounting project

Use ```docker inspect registry | grep IPAddress``` to retrieve the IP address of the eureka server container. Then configure the IP address in the demo and the accounting service:
```YAML
server:
  port: 0
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URI:http://172.17.0.4:8761/eureka}
```

## Container for the config server

Similarly to the registry container you can also use an existing docker image or create your own image for the configuration service. We will create our own container here; feel free to use and configure one of the existing ones on Docker-Hub.

Add the Dockerfile for the configuration server to the config project directory:
```
FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD build/libs/config-0.0.1-SNAPSHOT.jar app.jar
RUN /bin/sh -c 'touch /app.jar\'
ENV JAVA_OPTS=""
EXPOSE 8888
ENTRYPOINT ["java","-jar","/app.jar"]
```

Build the container:
```sh
docker build -t config .
```

Run the container:
```sh
docker run --detach --name config config
```

Use ```docker inspect config | grep IPAddress``` to retrieve the IP address of the config server container. Then configure the IP address in the demo and the accounting service:
```YAML
(...)
  cloud:
    config:
      uri: http://172.17.0.5:8888
(...)
```


## Containers for the demo and accounting service

Same procedure as for the config server: 

1. Add a Dockerfile to the both the demo and the accounting project directory.
2. Build the docker container by executing ```docker build -t [NAME] .```
3. Run the container by executing ```docker run --detach --name [NAME] [NAME]```

Once you all the containers are running your can retrieve the IP addresses of the demo and the accounting service through ```docker inspect``` - then you should be able to access the endpoints like this:
```
http://172.17.0.6:8081/customer/1
```

And retrieve:
```JSON
{"id":1,"firstName":"Bud","lastName":"Spencer","birthDate":"1929-10-31","comment":"cool guy"}
```

If you want to test the POST request against the accounting endpoint you will have to adapt the URI to use the IP address of the accounting service. It might look something like this:
```
http://172.17.0.7:8082/account
```

Note: If you encounter a problem with the Feign client in the accounting project (Error 500 when POSTing a new account) you can extend the Eureka configuration in the application.yml file of both the demo and the accounting project:

```YAML
(...)
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URI:http://172.17.0.4:8761/eureka}
  instance:
    preferIpAddress: true

(...)
```

It should be enough to specify this on the server but somehow the client seems to need it to in some cases...

### Cleanup and build cycle

If you want to build everything from scratch it is best to remove both image and container first: 

1. stop container: ```docker stop [ContainerName]```
2. remove container: ```docker rm [ContainerName]```
3. remove image: ```docker rmi [ImageName]```

Then build the project, image and container new:

4. build project: ```./gradlew build``` (in project directory)
5. build docker image: ```docker build -t [ImageName] .``` (in project directory where Dockerfile is stored)
6. run the container: ```docker run --detach --name [ContainerName] [ImageName]```

Note: You can remove the ```--detach```option to see the command line output of the container upon startup or retrieve the log output by running ```docker logs [ContainerName]```.

After a while your machine might be full of docker images that require quite a lot of space. If you run into space problems you can cleanup all images:
```
docker rmi $(docker images -aq)
```

More advanced options for remove commands [can be found on stackoverflow](https://stackoverflow.com/questions/17665283/how-does-one-remove-an-image-in-docker) ;)

### Remote debugging

You can enable debugging for a container by adding debug parameters to the ENTRYPOINT options in the Dockerfile:
```
FROM openjdk:8-jre-alpine
VOLUME /tmp
ADD build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
RUN /bin/sh -c 'touch /app.jar\'
ENV JAVA_OPTS=""
EXPOSE 8081
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Xdebug", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7081","-jar","/app.jar"]
```

In IntelliJ IDEA:

1. open the "Run/Debug Configurations"
2. create a new "Remote" configuration 
3. fill in the IP address of the container you want to debug (e.g. 172.17.0.6 for the demo application in our setup)
4. fill in the debug port as specified in the Dockerfile of the application you want to debug (e.g. 7081 as defined above for the demo application)

Run the Debug configuration; you should see:
```
Connected to the target VM, address: '172.17.0.6:7081', transport: 'socket'
```