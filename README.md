# BankingInTheCloud-Tutorials
This repo provides tutorials for the BankingInTheCloud workshop. 

## Stage 00 - Basic Setup

After finishing stage 00 you should have one Spring Boot Project called ```demo``` that contains one Rest-Controller that offers a GET service method to receive the IP addresee of the server.

## Stage 01 - Spring Cloud Config

After finishing stage 01 you have two projects:

1. **demo**: the demo project created in stage 00
2. **config**: the spring cloud config server created in stage 01

Additionally you have the folder **config-repo** which contains the remote-configuration for the demo application.

The config server is configured through ```application.yml``` in the config project. The config server starts at ```localhost:8888``` and serves the files in the ```config-repo``` folder. 

The demo application is configured through ```bootstrap.yml``` in the demo project. It loads the configuration according to application-name, profile and label. The application-name (```demo```) and profile (```dev```) define which configuration file to fetch from the config server (```demo-dev.yml```). The label specifies the branch (```Stage-01-SpringCloudConfig```) within the config server repository.
The configuration (```demo-dev.yml```) defines the startup port of the demo application. It is set to 8081, so the demo service is now available at port 8081 instead of 8080 (stage 00).

## Stage 02 - Flyway

After finishing stage 02 you have a MySQL database that contains a ```customer``` table and dummy data.

The demo project is configured to use this database. It uses Flyway migration to create the ```customer``` table and the dummy data.

## Stage 03 - Spring Data

After finishing stage 03 you have a service that accesses the customer data created in stage 02 through spring data. You can quey for customers by id and last name and you can create customers through post requests in postman.

## Stage 04 - A second service

After finishing stage 04 you have a second service called "accounting"; you now have three projects:


1. **demo**: the demo project created in stage 00
2. **config**: the spring cloud config server created in stage 01
3. **accounting**: the accounting project created in stage 04

The accounting project contains a REST endpoint for managing accounts for customers. 

A configuration file (```accounting-dev.yml```) was added that defines port ```8082``` for the accounting application. 

The verification of the customer ID through the customer service upon creation of an account is not implemented in this stage yet. There is a TODO defined in the ```AccountService```. The communication with the customer REST endpoint (demo project) is to be added in the next stage.

## Stage 05 - Eureka

After finishing stage 05 you have have Eureka as service registry so services can find each other. You now have four projects:

1. **demo**: the demo project created in stage 00
2. **config**: the spring cloud config server created in stage 01
3. **accounting**: the accounting project created in stage 04
4. **registry**: the Eureka service registry server

Both the demo and the accounting project are configured as Eureka clients, meaning they register at the Eureka service upon startup. 

Additionally the accounting project includes a feign client, so the account service (accounting project) can communicate with the customer endpoint (demo project) to verify if a customer exists upon account creation.

## Stage 06 - Docker

After finishing stage 06 all the components of your application can run in docker containers. The databases (accountingdb, demodb), the config server, the registry as well as the functional services (demo, accounting) are containerized. 

In this stage the linkage of the containers through the configuration files is done by fixed IP addresses. In the next stage docker-compose will be used to apply names instead of IP addresses to achieve a more robust setup.

## Stage 07 - Docker Compose

After finishing stage 07 you have the container management setup with docker-compose. You new control the build, start and stop of the containers through docker compose. 

The linkabe between the containers is not managed through names by docker-compose, no fixed IP addresses are required.

## Stage 08 - First steps with amazon ECS

After finishing stage 08 the two mysql database containers are running an amazon AWS, while the rest of the setup still runs locally. The containers are configured via an ECS (EC2 container service) task definition that automatically starts an instance when configuring a cluster and service for the task definition within the ECS console in AWS. The container image used for the databases is the standard mysql image from Docker-Hub (wich is available in ECS).

The link to the database containers in the cloud is configured via the config-server - the databases are not part of docker-compose in this stage.
