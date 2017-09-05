# BankingInTheCloud-Tutorials
This repo provides tutorials for the BankingInTheCloud workshop. 

## Stage 00 - Basic Setup

After finishing stage 00 you should have one Spring Boot Project called ```customer``` that currently contains one Rest-Controller that offers a GET service method to receive the IP addresee of the server.

## Stage 01 - Spring Cloud Config

After finishing stage 01 you have two projects:

1. **customer**: the customer project created in stage 00
2. **config**: the spring cloud config server created in stage 01

Additionally you have the folder **config-repo** which contains the remote-configuration for the customer application.

The config server is configured through ```application.yml``` in the config project. The config server starts at ```localhost:8888``` and serves the files in the ```config-repo``` folder. 

The customer application is configured through ```bootstrap.yml``` in the customer project. It loads the configuration according to application-name, profile and label. The application-name (```customer```) and profile (```dev```) define which configuration file to fetch from the config server (```customer-dev.yml```). The label specifies the branch (```Stage-01-SpringCloudConfig```) within the config server repository.
The configuration (```customer-dev.yml```) defines the startup port of the customer application. It is set to 8081, so the customer service is now available at port 8081 instead of 8080 (stage 00).

## Stage 02 - Flyway

After finishing stage 02 you have a MySQL database that contains a ```customer``` table and dummy data.

The customer project is configured to use this database. It uses Flyway migration to create the ```customer``` table and the dummy data.

## Stage 03 - Spring Data

After finishing stage 03 you have a service that accesses the customer data created in stage 02 through spring data. You can quey for customers by id and last name and you can create customers through post requests in postman.

## Stage 04 - A second service

After finishing stage 04 you have a second service called "accounting"; you now have three projects:


1. **customer**: the customer project created in stage 00
2. **config**: the spring cloud config server created in stage 01
3. **accounting**: the accounting project created in stage 04

The accounting project contains a REST endpoint for managing accounts for customers. 

A configuration file (```accounting-dev.yml```) was added that defines port ```8082``` for the accounting application. 

The verification of the customer ID through the customer service upon creation of an account is not implemented in this stage yet. There is a TODO defined in the ```AccountService```. The communication with the customer REST endpoint (customer project) is to be added in the next stage.

## Stage 05 - Eureka

After finishing stage 05 you have have Eureka as service registry so services can find each other. You now have four projects:

1. **customer**: the customer project created in stage 00
2. **config**: the spring cloud config server created in stage 01
3. **accounting**: the accounting project created in stage 04
4. **registry**: the Eureka service registry server

Both the customer and the accounting project are configured as Eureka clients, meaning they register at the Eureka service upon startup. 

Additionally the accounting project includes a feign client, so the account service (accounting project) can communicate with the customer endpoint (customer project) to verify if a customer exists upon account creation.

## Stage 06 - Docker

After finishing stage 06 all the components of your application can run in docker containers. The databases (accountingdb, customerdb), the config server, the registry as well as the functional services (customer, accounting) are containerized. 

In this stage the linkage of the containers through the configuration files is done by fixed IP addresses. In the next stage docker-compose will be used to apply names instead of IP addresses to achieve a more robust setup.

## Stage 07 - Docker Compose

After finishing stage 07 you have the container management setup with docker-compose. You new control the build, start and stop of the containers through docker compose. 

The linkabe between the containers is not managed through names by docker-compose, no fixed IP addresses are required.
