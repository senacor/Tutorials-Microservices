# Microservices Tutorial

## Stage 01 - Your first service

After finishing stage 01 you should have one Spring Boot Project called ```customer``` that currently contains one Rest-Controller that offers a GET service method to receive the IP address of the server.

## Stage 02 - Spring Cloud Config

After finishing stage 02 you have two projects:

1. **customer**: the customer project created in stage 01
2. **config**: the spring cloud config server created in stage 02

Additionally you have the folder **config-repo** which contains the remote-configuration for the customer application.

The config server is configured through ```application.yml``` in the config project. The config server starts at ```localhost:8888``` and serves the files in the ```config-repo``` folder. 

The customer application is configured through ```bootstrap.yml``` in the customer project. It loads the configuration according to application-name, profile and label. The application-name (```customer```) and profile (```dev```) define which configuration file to fetch from the config server (```customer-dev.yml```). The label specifies the branch (```Stage-02-SpringCloudConfig```) within the config server repository.
The configuration (```customer-dev.yml```) defines the startup port of the customer application. It is set to 8081, so the customer service is now available at port 8081 instead of 8080 (stage 01).

## Stage 03 - Flyway

After finishing stage 03 you have a MySQL database that contains a ```customer``` table and dummy data.

The customer project is configured to use this database. It uses Flyway migrations to create the ```customer``` table and the dummy data.

## Stage 04 - Spring Data

After finishing stage 04 you have a service that accesses the customer data created in stage 03 through spring data. You can quey for customers by id and last name and you can create customers through post requests in postman.

## Stage 05 - A second service

After finishing stage 05 you have a second service called "accounting"; you now have three projects:


1. **customer**: the customer project created in stage 01
2. **config**: the spring cloud config server created in stage 02
3. **accounting**: the accounting project created in stage 05

The accounting project contains a REST endpoint for managing accounts for customers. 

A configuration file (```accounting-dev.yml```) was added that defines port ```8082``` for the accounting application. 

The verification of the customer ID through the customer service upon creation of an account is not implemented in this stage yet. There is a TODO defined in the ```AccountService```. The communication with the customer REST endpoint (customer project) is to be added in the next stage.

## Stage 06 - Eureka

After finishing stage 06 you have have Eureka as service registry so services can find each other. You now have four projects:

1. **customer**: the customer project created in stage 01
2. **config**: the spring cloud config server created in stage 02
3. **accounting**: the accounting project created in stage 05
4. **registry**: the Eureka service registry server in stage 06

Both the customer and the accounting project are configured as Eureka clients, meaning they register at the Eureka service upon startup. 

Additionally the accounting project includes a feign client, so the account service (accounting project) can communicate with the customer endpoint (customer project) to verify if a customer exists upon account creation.

## Stage 07 - Docker

After finishing stage 07 all the components of your application can run in docker containers. The databases (accountingdb, customerdb), the config server, the registry as well as the functional services (customer, accounting) are containerized. 

In this stage the linkage of the containers through the configuration files is done by fixed IP addresses. In the next stage docker-compose will be used to apply names instead of IP addresses to achieve a more robust setup.

## Stage 08 - Docker Compose

After finishing stage 08 you have the container management setup with docker-compose. You new control the build, start and stop of the containers through docker compose. 

The linkabe between the containers is not managed through names by docker-compose, no fixed IP addresses are required.

## Stage 09 - Resilient Startup

After finishing stage 09 the configuration for the demo and the accounting service are completely managed by the config server. The ```bootstrap.yml``` of the services only contains the reference to the config server, the ```application.yml``` was completely moved to the config server and removed from the services.

Additionally resilient features were included into the configuration so:

1. the application retries to contact the config-server when it starts.
2. the application retries to connect to the database if it is not avilable (the application does not just crash).

## Stage 10 - Profiles

After finishing stage 10 you have a ```dev``` and a ```prod``` profile. The ```dev``` profile is used to start the application locally in IDEA (without docker and docker-compose). The ```dev``` profile includes the configuration for an in memory H2 database, so you don't have to care about starting/stopping databases during development.
The ```prod``` profile pretty much contains the configuration from the previous stages. The Dockerfiles of accounting and customer service were extended to set the right profile (```prod```) when starting the application through docker-compose.
