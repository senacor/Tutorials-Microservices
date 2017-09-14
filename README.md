# Microservices Tutorial
This repo provides tutorials for our Microservices training workshop. The workshop is divided in stages that build on each other. Depending on the time constraints certain stages can be skipped. 

Each stages has a two-digit number. There are optional stages which are not integrated into the build-on-each-other pattern, but are side branches of a certain stage. Optional stages are marked with the stage's number they are built on and a letter (e.g. 07.A) if they are independent, or the stage's number they are built on and a number if several stages form a branch (e.g. 03.1 and 03.2).

Detailed hints for each stage can be found in the [hints folder](https://github.com/senacor/MicroservicesAndCloud-Tutorials/tree/master/hints). 

## Setup

For the Microservices Tutorials you need the following tools:
* Java 1.8.x
* IntelliJ IDEA (community or ultimate edition)
* MySQL Server
* Chrome
* Postman (Chrome extension)
* docker

For alternative stages you additionally need
* docker-compose (for alternative stage 06.A)

Note: The VM setup for the Microservices & Cloud workshop is described in the [BankingInTheCloud-WorkshopSetup repo](https://github.com/senacor/BankingInTheCloud-WorkshopSetup), a list of tools that we use in the workshop can be found [here](https://github.com/senacor/BankingInTheCloud-WorkshopSetup/tree/master/alternative-setup). 

## Tutorial stages

The tutorials are done in steps that are based on each other. 
Participants are supposed to solve each tutorial stage by themselves. A reference solution can be found in branches.

### Stage 01 - Your first service

#### Goal
You have your first spring boot application up and running.

#### Setup
The basic project setup is based on the customer project one can generate using the [SpringBoot Initializr](https://start.spring.io/). Use the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.x``` (latest stable version)
* Group: ```com.senacor.bitc```
* Artifact: ```customer```
* Name: ```customer```
* Package Name: ```com.senacor.bitc.demo```
* Dependencies: ```Web```

Open the ```customer``` project using IntelliJ IDEA: ```File``` >> ``` Open...``` >> select the ```customer``` folder

#### Tasks

1. Implement a REST controller that offers one GET method that returns the IP address of the server. 

### Stage 02 - Cloud Config Server

#### Goal
Your spring boot application can be configured via a configuration server. 

#### Setup 

The cloud config requires another spring-boot project that represents the cloud config server. Use [SpringBoot Initializr](https://start.spring.io/) to generate the project using the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.x``` (latest stable version)
* Group: ```com.senacor.bitc```
* Artifact: ```config```
* Dependencies: ```Web``` ```Config Server```

Add the ```config``` project as module in IntelliJ IDEA: ```File``` >> ``` New``` >> ``` Module from Existing Sources...``` >> select the extracted ```config``` folder

#### Tasks

1. Configure the **config** project as [cloud-config-server](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html)
2. Configure the spring cloud **config** server to use a git-repsoitory where you put the configuration for your customer service.
2. Configure the **customer** service so it uses the cloud config server for configuration.
3. Configure the port where the **customer** service is served through the **config** server configuration file. The port should not be hard-wired in the **customer** service any more, but is defined by the configuration file served by the **config** server.

We recommend that you use yaml (```.yml```) instead of properties files because the Mifos I/O application we will use later is based on a yaml configuration as well. 


### Stage 03 - Flyway (database migration)

#### Goal
Create a database with a customer table that contains dummy-data for your service using flyway migration scripts.

#### Setup

1. [Install MySQL](https://dev.mysql.com/downloads/mysql/) (you can also [install it through apt-get](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-16-04)).
2. Create a MySQL database through the mysql command line.

Note: If you are already familiar with docker you can of course use a docker container instead of the local installation of mysql (as in stage 07).

#### Tasks

1. Configure the customer project for mysql and flyway (dependencies).
2. Write flyway migration scripts to create a table ```customer``` with fields ```id```, ```first_name```, ```last_name``` and ```birth_date```.
3. Fill some data into your ```customer``` table by writing and executing more flyway migration script(s).

### Stage 04 - Spring Data

#### Goal
Be able to access customer data from the database through a new REST endpoint (without writing boilerplate code).

#### Setup

Since we are working with entities now and we want to avoid unnecessary boiler-code-writing we recommend to add *Lombok* to the customer project.
You can follow the instructions on how to [add Lombok to IntelliJ IDEA as plugin](https://projectlombok.org/setup/intellij) and how to [integrate Lombok into your project with gradle](https://projectlombok.org/setup/gradle). Note that you will also have to [turn on annotation processing in IntelliJ IDEA](https://stackoverflow.com/questions/41161076/adding-lombok-plugin-to-intellij-project) for Lombok to work.

#### Tasks

1. Use Spring Data to retrieve customer data from the database (entity and repository).
2. Offer a new REST endpoint that provides customer data (at least: customer by id and customer by last name).
3. Test the new REST endpoint (with MockMVC and MockBean).

### Stage 04.1 (optional) - HATEOAS (REST application architecture constraint)

*Disclaimer: This stage is not implemented in code yet (you will not find a branch for this but you can do it on your own).*

#### Goal 
You understand the concept of HATEOAS (Hypermedia as the Engine of Application State) and adapt your service accordingly.

#### Tasks

1. Follow the [spring tutorial on HATEOAS](https://spring.io/guides/gs/rest-hateoas/) to adapt your rest endpoint according to the pattern.
2. Think about the design of your endpoints with respect to the HATEOAS pattern.

### Stage 04.2 (optional) - Hystrix (Fault Tolerance)

*Disclaimer: This stage is not implemented in code yet (you will not find a branch for this but you can do it on your own).*

#### Goal
You understand the concept of Netflix's Hystrix library and what you can do with it. You use the Circuit Breaker implementation of the Hystrix library to add a Circuit Breaker to your service.

#### Tasks

1. Follow the [spring tutorial on Circuit Breakers with Hystrix](https://spring.io/guides/gs/circuit-breaker/) to get an overview. Build in a circuit breaker into your customer service.
2. Think about the design of your endpoints with respect to the Circuit Breaker pattern and other fault tolerance concepts.

### Stage 05 - Create a second service

#### Goal
You recap step 00 till 03 again. You create a second service (accounting-service) that will communicate with the first service (customer 
service) in the next step. The service should offer an "account" endpoint that that depends on the customer endpoint of the first service.

#### Tasks

1. Do stages 00 till stages 03 again by yourself, but create an accounting service instead of a customer service. When creating an account one has to provide a customer ID. 
2. *Don't* implement the part where the accounting service validates if a customer exists through the customer service! We will do this in the next stage when we add the service discovery.


### Stage 06 - Eureka (service discovery)

#### Goal
You add a service discovery so the services can find each other through the discovery server. The account endpoint retrieves information from the customer endpoint through a Feign client.

#### Setup

The discovery server requires another spring-boot project. Use [SpringBoot Initializr](https://start.spring.io/) to generate the project using the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.x``` (latest stable version)
* Group: ```com.senacor.bitc```
* Artifact: ```registry```
* Dependencies: ```Web``` ```Eureka Server```

Add the ```registry``` project as module in IntelliJ IDEA: ```File``` >> ``` New``` >> ``` Module from Existing Sources...``` >> select the extracted ```registry``` folder

#### Tasks

1. Configure the registry project as Eureka server.
2. Configure the customer project as Eureka client so it register with the Eureka server.
3. Configure the accounting project as Eureka client so it registers with the Eureka server.
4. Configure a feign client for the customer endpoint in the accounting project and verify the customer ID upon account creation.


### Stage 07 - Docker (containerize)

#### Goal
The complete application (databases, config server, registry and functional services) works as before but runs in docker containers. 

#### Tasks

1. Configure containers for the two MySQL databases by using MySQL images from Docker-Hub
2. Configure container for the registry (through image from Docker-Hub or by adding a Dockerfile to the registry project)
3. Configure container for the config server (through image from Docker-Hub or by adding a Dockerfile to the registry project)
4. Configure container for the customer service by adding a Dockerfile to the customer project
5. Configure container for the accounting service by adding a Dockerfile to the accounting project

In is recommended that you test the new containers after each task by starting the newly created container as well as the other parts (locally, outside of container).

Note: In this stage it is enough to link the containers on IP address level. In the docker-compose stage we will create a more generic setup.

### Stage 08 - Docker Compose

#### Goal
You configure the docker containers of stage 06 through docker-compose so you don't have to link the containers using the specific IP addresses of the containers, but use a configuration by name through docker-compose.

#### Tasks

1. Add a docker-compose configuration file to the top folder of the repository.
2. Configure all the services in the docker-compose configuration (add entry for all the different components and define the linkage).
3. Startup the "backbone containers" (database, config server, registry) through docker-compose.
4. Startup the "functional containers" (customer, accounting) through docker-compose.
5. Test customer and account retrieval and account creation.

### Stage 09 - Resilient Startup

#### Goal
You understand the purpose of the config-server and the config-repo better in the context of a microservices deployment. You understand how to make the customer and accounting service more resilient, so you can just run ```docker-compose up``` without starting specific services in a dedicated order by hand.

#### Tasks

1. Move all the configuration entries from the customer and accounting projects' ```application.yml``` files to the respective configuration on the config server. 
2. Configure for the config-server connection at startup to customer and accounting service. It should not matter if the config-server is immediately available at startup or later.
3. Configure the database connection, so it does not matter if the database is always available. The application should be able to deal with it without crashing.
4. Commit the configuration files and let the config server configuration (in ```bootstrap.yml```) point to the correct repo/branch.
5. Build the accounting and customer projects and containers new.
6. Run and test the setup by creating a new account through Postman.

### Stage 09.A (optional) - Messaging and Event Sourcing

*Disclaimer: This stage is not implemented in code yet (you will not find a branch for this but you can do it on your own).*

#### Goal
You add endpoints that emit events, so your two services don't directly communicate with each other but one service emits an event that the other service consumes.

#### Tasks

1. Follow the [spring tutorial on the Java Messaging Service using ActiveMQ](https://spring.io/guides/gs/messaging-jms/).
2. Add messaging functionality to your services.