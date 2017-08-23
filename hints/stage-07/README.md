# Hints for Tutorial stage 07

## Overview

Docker-compose is basically a configuration ontop of docker. You can define how containers should be linked together within a network that consists of several containers (that might depend on each other).



## docker-compose configuration file

Docker-compose expects a configuration file named ```docker-compose-yml``` which contains the complete configuration for all containers. This file is to be placed in the root folder of the repository (above all the project directories).

You have to define:

1. Service configuration entry for both databases.
2. Service configuration entry for the config-server.
3. Service configuration entry for the registry.
4. Service configuration entry for both functional services.
5. Define a network and add all the services defined above to that network.


### The complete configuration file

```YAML
version: '3.2'

services:
  demodb: 
    image: mysql
    container_name: demodb
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=demodb
    ports:
      - "3306:3306"
    networks:
        - demonet

  accountingdb: 
    image: mysql
    container_name: accountingdb
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=accountingdb
    ports:
      - "3305:3306"
    networks:
        - demonet

  registry:
    build:
      context: ./registry
    image: registry
    container_name: registry
    working_dir: /
    ports:
      - "8761:8761"
    networks:
        - demonet

  config:
    build:
      context: ./config
    image: config
    container_name: config
    working_dir: /
    ports:
      - "8888:8888"
    networks:
        - demonet

  demo:
    build:
      context: ./demo
    image: demo
    container_name: demo
    working_dir: /
    ports:
      - "8081:8081"
      - "7081:7081"
    depends_on:
        - demodb
        - registry
        - config
    networks:
        - demonet
    
  accounting:
    build:
      context: ./accounting
    image: accounting
    container_name: accounting
    working_dir: /
    ports:
      - "8082:8082"
      - "7082:7082"
    depends_on:
        - accountingdb
        - registry
        - config
        - demo
    networks:
        - demonet

networks:
  demonet:
```

Note: The port configuration within the docker compose configuration file depicts the ports that will be allocated on the machine it runs on! Thus you can access the demo and accounting service through ```localhost:[PORT]``` again. For the container linkage (using names) this port configuration does not matter, because the containers communicate with each other on a docker-IP+port level.
This is relevant for consideration once we use the amazon ECS (EC2 Container Service) in stage 08. Similar to docker compose we have to define task definitions that define the containers and how they are linked together.

## Adapt the configuration within the functional services

Instead of using IP addresses to detect other containers we now use the names of those containers as defined in the docker-compose configuration.

For the demo and the accounting project the ```application.yml``` and ```bootstrap.yml``` file will be adapted. Example blow shows for demo project, the accounting project is to be configured accordingly.

application.yml:

```YAML
(...)
spring:
  datasource:
    url: 'jdbc:mysql://accountingdb:3306/accountingdb'

(...)

eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URI:http://registry:8761/eureka}
(...)
```

bootstrap.yml:
```YAML
spring:
(...)
    config:
      uri: http://config:8888
(...)
```

## Starting the application

Note that the demo and the accounting service depend on:

1. Their respective databases.
2. The config server (otherwise they will not be configured correctly upon startup).
3. The registry (they have to register with Eureka upon startup).

Docker compose does not "wait" until the other services are available by itself - even when listing the other services correctly in the ```depends_on``` section. The ```depends_on``` section just defines that if the service listed as dependency was not started yet, it will be started - but docker-compose will not wait until the service is available.

Thus it is recommended to start the containers like this:

1. Open a terminal and run: ```docker-compose up demodb accountingdb```
2. Wait until demodb and accountingdb started successfully.
3. Open another terminal and run: ```docker-compose up config registry```
4. Wait until config and registry started successfully.
5. Open another terminal and run: ```docker-compose up demo accounting```

By running these tasks in separate windows its also easier to read the log information. If you start all containers at once in one terminal there will be six containers logging to the same terminal which is quite tough to read.
