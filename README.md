# BankingInTheCloud-Tutorials
This repo provides tutorials for the BankingInTheCloud workshop. The workshop is divided in stages that build on each other. Depending on the time constraints certain stages can be skipped. 

Each stages has a two-digit number. There are optional stages which are not integrated into the build-on-each-other pattern, but are side branches of a certain stage. Optional stages are marked with the stage's number they build on and a letter (e.g. 06.A).

Detailed hints for each stage can be found in the [hints folder](https://github.com/senacor/BankingInTheCloud-Tutorials/tree/master/hints). 

## Project Setup

For the BankingInTheCloud-Tutorials you need the following tools:
* Java 1.8.x
* IntelliJ IDEA (community or ultimate edition)
* MySQL Server
* Chrome
* Postman (Chrome extension)
* docker

For alternative stages you additionally need
* docker-compose (for alternative stage 06.A)

Note: The setup for the complete BankingInTheCloud workshop is described in the [BankingInTheCloud-WorkshopSetup repo](https://github.com/senacor/BankingInTheCloud-WorkshopSetup), a list of tools that we use in the workshop can be found [here](https://github.com/senacor/BankingInTheCloud-WorkshopSetup/tree/master/alternative-setup).

## Tutorial stages

The tutorials are done in steps that are based on each other. 
Participants are supposed to solve each tutorial stage by themselfes. A recerence solution can be found in branches.

### Stage 00 - Basic Setup

#### Goal
You have your first spring boot application up and running.

#### Project Setup
The basic project setup is based on the demo-project one can generate using the [SpringBoot Initializr](https://start.spring.io/). Use the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.x``` (latest stable version)
* Group: ```com.senacor.bitc```
* Artifact: ```demo```
* Dependencies: ```Web```

Open the ```demo``` project using IntelliJ IDEA: ```File``` >> ``` Open...``` >> `select the ```demo``` folder

#### Tasks

1. Implement a REST controller that offers one GET method that returns the IP address of the server. 

### Stage 01 - Cloud Config Server

#### Goal
Your spring boot application can be configured via a configuration server. 

#### Project Setup 

The cloud config requires another spring-boot project that represents the cloud config server. Use [SpringBoot Initializr](https://start.spring.io/) to generate the project using the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.x``` (latest stable version)
* Group: ```com.senacor.bitc```
* Artifact: ```config```
* Dependencies: ```Web``` ```Config Server```

Add the ```config``` project as module in IntelliJ IDEA: ```File``` >> ``` New``` >> ``` Module from Existing Sources...``` >> select the extracted ```config``` folder

#### Tasks

1. Configure the **config** project as [cloud-config-server](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html)
2. Configure the spring cloud **config** server to use a git-repsoitory where you put the configuration for your demo service.
2. Configure the **demo** service so it uses the cloud config server for configuration.
3. Configure the port where the **demo** application is served through the **config** server configuration file. The port should not be hard-wired in the **demo** application any more, but is defined by the configuration file served by the **config** server.

We recommend that you use yaml (```.yml```) instead of properties files because the Mifos I/O application we will use later is based on a yaml configuration as well. 


### Stage 02 - Flyway (database migration)

#### Goal
Create a database with a customer table that contains dummy-data for your service using flyway migration scripts.

#### Project Setup

1. [Install MySQL](https://dev.mysql.com/downloads/mysql/) (you can also [install it through apt-get](https://www.digitalocean.com/community/tutorials/how-to-install-mysql-on-ubuntu-16-04)).
2. Create a MySQL database through the mysql command line.

#### Tasks

1. Configure the demo project for mysql and flyway (dependencies).
2. Write flyway migration scripts to create a table ```customer``` with fields ```id```, ```first_name```, ```last_name``` and ```birth_date```.
3. Fill some data into your ```customer``` table by writing and executing more flyway migration script(s).

### Stage 03 - Spring Data

#### Goal
Be able to access customer data from the database through a new REST endpoint (without writing boilerplate code).

#### Setup

Since we are working with entities now and we want to avoid unnecessary boiler-code-writing we recommend to add *Lombok* to the demo project.
You can follow the instructions on how to [add Lombok to IntelliJ IDEA as plugin](https://projectlombok.org/setup/intellij) and how to [integrate Lombok into your project with gradle](https://projectlombok.org/setup/gradle). Note that you will also have to [turn on annotation processing in IntelliJ IDEA](https://stackoverflow.com/questions/41161076/adding-lombok-plugin-to-intellij-project) for Lombok to work.

#### Tasks

1. Use Spring Data to retrieve customer data from the database (entity and repository).
2. Offer a new REST endpoint that provides customer data (at least: customer by id and customer by last name).
3. Test the new REST endpoint (with MockMVC and MockBean).

### Stage 04 - Create a second service

#### Goal
You recap step 00 till 03 again. You create a second service (accounting-service) that will communicate with the first service (demo-service) in the next step. The service should offer an "account" endpoint that that depends on the customer endpoint of the first service.

#### Tasks

1. Do stages 00 till stages 03 again by yourself, but create an accounting service instead of a customer service. When creating an account one has to provide a customer ID. 
2. *Don't* implement the part where the accounting service validates if a customer exists through the customer service! We will do this in the next stage when we add the service discovery.


### Stage 05 - Eureka (service discovery)

#### Goal
You add a service discovery so the services can find each other through the discovery server. The account endpoint retrieves information from the customer endpoint through a Feign client.

#### Project Setup

The discovery server requires another spring-boot project. Use [SpringBoot Initializr](https://start.spring.io/) to generate the project using the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.x``` (latest stable version)
* Group: ```com.senacor.bitc```
* Artifact: ```registry```
* Dependencies: ```Web``` ```Eureka Server```

Add the ```registry``` project as module in IntelliJ IDEA: ```File``` >> ``` New``` >> ``` Module from Existing Sources...``` >> select the extracted ```registry``` folder

#### Tasks

* Configure the registry project as Eureka server.
* Configure the demo project as Eureka client so it register with the Eureka server.
* Configure the accounting project as Eureka client so it registers with the Eureka server.
* Configure a feign client for the customer endpoint in the accounting project and verify the customer ID upon account creation.


### Stage 06 - Docker (containerize)

#### Goal
You pack your services into containers for deployment.

#### Tasks


### Stage 06.A (optional) - Docker Compose

#### Goal
You configure the docker containers of stage 06 through 

#### Tasks


### Stage 06.B (optional) - Messaging and Event Sourcing

#### Goal
You add endpoints that emit events, so your two services don't directly communicate with each other but one service emits an event that the other service consumes.

#### Tasks

### Stage 07 - Running the project on amazon AWS

#### Goal
Deploy the docker containers on amazon AWS

#### Tasks


### Stage 08 - Adding a load balancer on amazon AWS

#### Goal
Add a load balancer to the project setup on AWS

#### Tasks


### Stage 09 - Utilizing Cloud storage instead of a database on amazon AWS

#### Goal
Instead of running a database in a docker container you should utilize the simple storage service (S3) of amazon AWS.

#### Tasks