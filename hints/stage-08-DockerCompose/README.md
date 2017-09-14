# Hints for Tutorial stage 08

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
  customerdb: 
    image: mysql
    container_name: customerdb
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=customerdb
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

  customer:
    build:
      context: ./customer
    image: customer
    container_name: customer
    working_dir: /
    ports:
      - "8081:8081"
      - "7081:7081"
    depends_on:
        - customerdb
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
        - customer
    networks:
        - demonet

networks:
  demonet:
```

Note: The port configuration within the docker compose configuration file depicts the ports that will be allocated on the machine it runs on! Thus you can access the customer and accounting service through ```localhost:[PORT]``` again. For the container linkage (using names) this port configuration does not matter, because the containers communicate with each other on a docker-IP+port level.
This is relevant for consideration once you for example deploy your containers on amazon ECS (EC2 Container Service). Similar to docker compose you have "task definitions" there that define the containers and how they are linked together.

## Adapt the configuration within the functional services

Instead of using IP addresses to detect other containers we now use the names of those containers as defined in the docker-compose configuration.

For the customer and the accounting project the ```application.yml``` and ```bootstrap.yml``` file will be adapted. Example blow shows for customer project, the accounting project is to be configured accordingly.

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

Note that the customer and the accounting service depend on:

1. Their respective databases.
2. The config server (otherwise they will not be configured correctly upon startup).
3. The registry (they have to register with Eureka upon startup).

Docker compose does not "wait" until the other services are available by itself - even when listing the other services correctly in the ```depends_on``` section. The ```depends_on``` section just defines that if the service listed as dependency was not started yet, it will be started - but docker-compose will not wait until the service is available.

Thus it is recommended to start the containers like this:

1. Open a terminal and run: ```docker-compose up customerdb accountingdb```
2. Wait until customerdb and accountingdb started successfully.
3. Open another terminal and run: ```docker-compose up config registry```
4. Wait until config and registry started successfully.
5. Open another terminal and run: ```docker-compose up customer accounting```

By running these tasks in separate windows its also easier to read the log information. If you start all containers at once in one terminal there will be six containers logging to the same terminal which is quite tough to read.
