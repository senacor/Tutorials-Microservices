# Hints for Tutorial stage 06

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