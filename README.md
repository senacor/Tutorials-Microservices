# BankingInTheCloud-Tutorials
This repo provides tutorials for the BankingInTheCloud workshop. 

## Stage 01 - Basic Setup

After finishing stage 00 you should have one Spring Boot Project called ```customer``` that currently contains one Rest-Controller that offers a GET service method to receive the IP addresee of the server.

## Stage 02 - Spring Cloud Config

After finishing stage 01 you have two projects:

1. **customer**: the customer project created in stage 00
2. **config**: the spring cloud config server created in stage 01

Additionally you have the folder **config-repo** which contains the remote-configuration for the customer application.

The config server is configured through ```application.yml``` in the config project. The config server starts at ```localhost:8888``` and serves the files in the ```config-repo``` folder. 

The customer application is configured through ```bootstrap.yml``` in the customer project. It loads the configuration according to application-name, profile and label. The application-name (```customer```) and profile (```dev```) define which configuration file to fetch from the config server (```customer-dev.yml```). The label specifies the branch (```Stage-01-SpringCloudConfig```) within the config server repository.
The configuration (```customer-dev.yml```) defines the startup port of the customer application. It is set to 8081, so the customer service is now available at port 8081 instead of 8080 (stage 00).

## Stage 03 - Flyway

After finishing stage 02 you have a MySQL database that contains a ```customer``` table and dummy data.

The customer project is configured to use this database. It uses Flyway migration to create the ```customer``` table and the dummy data.
