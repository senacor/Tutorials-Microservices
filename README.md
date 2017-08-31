# BankingInTheCloud-Tutorials
This repo provides tutorials for the BankingInTheCloud workshop. The workshop is divided in stages that build on each other. Depending on the time constraints certain stages can be skipped. 

Each stages has a two-digit number. There are optional stages which are not integrated into the build-on-each-other pattern, but are side branches of a certain stage. Optional stages are marked with the stage's number they build on and a letter (e.g. 06.A).

Detailed hints for each stage can be found in the [hints folder](https://github.com/senacor/BankingInTheCloud-Tutorials/tree/master/hints). 

## Setup

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

#### Setup
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

#### Setup 

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

#### Setup

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

#### Setup

The discovery server requires another spring-boot project. Use [SpringBoot Initializr](https://start.spring.io/) to generate the project using the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.x``` (latest stable version)
* Group: ```com.senacor.bitc```
* Artifact: ```registry```
* Dependencies: ```Web``` ```Eureka Server```

Add the ```registry``` project as module in IntelliJ IDEA: ```File``` >> ``` New``` >> ``` Module from Existing Sources...``` >> select the extracted ```registry``` folder

#### Tasks

1. Configure the registry project as Eureka server.
2. Configure the demo project as Eureka client so it register with the Eureka server.
3. Configure the accounting project as Eureka client so it registers with the Eureka server.
4. Configure a feign client for the customer endpoint in the accounting project and verify the customer ID upon account creation.


### Stage 06 - Docker (containerize)

#### Goal
The complete application (databases, config server, registry and functional services) works as before but runs in docker containers. 

#### Tasks

1. Configure containers for the two MySQL databases by using MySQL images from Docker-Hub
2. Configure container for the registry (through image from Docker-Hub or by adding a Dockerfile to the registry project)
3. Configure container for the config server (through image from Docker-Hub or by adding a Dockerfile to the registry project)
4. Configure container for the demo service by adding a Dockerfile to the demo project
5. Configure container for the accounting service by adding a Dockerfile to the accounting project

In is recommended that you test the new containers after each task by starting the newly created container as well as the other parts (locally, outside of container).

Note: In this stage it is enough to link the containers on IP address level. In the docker-compose stage we will create a more generic setup.

### Stage 07 - Docker Compose

#### Goal
You configure the docker containers of stage 06 through docker-compose so you don't have to link the containers using the specific IP addresses of the containers, but use a configuration by name through docker-compose.

#### Tasks

1. Add a docker-compose configuration file to the top folder of the repository.
2. Configure all the services in the docker-compose configuration (add entry for all the different components and define the linkage).
3. Startup the "backbone containers" (database, config server, registry) through docker-compose.
4. Startup the "functional containers" (demo, accounting) through docker-compose.
5. Test customer and account retrieval and account creation.

### Stage 07.A (optional) - Move all the configuration to the Config Server

#### Goal
Move all configurations that are applied at runtime (```application.yml```) but not at startup (```bootstrap.yml```) to the config server's configuration so all the configuration is at one place. The startup configuration should only contain the information how to reach the config server. 

#### Tasks

1. Move all the configuration entries from the demo and accounting projects' ```application.yml``` files to the respective configuration on the config server.
2. Commit the configuration files and let the config server configuration (in ```bootstrap.yml```) point to the correct repo/branch. 
3. Build the accounting and demo projects and containers new.
4. Run and test the setup by creating a new account through Postman.

### Stage 07.B (optional) - Messaging and Event Sourcing

*Disclaimer: This stage is not implemented yet. It will be added at a later point once the AWS stages are complete.*

#### Goal
You add endpoints that emit events, so your two services don't directly communicate with each other but one service emits an event that the other service consumes.

#### Tasks

### Stage 08 - First steps with amazon ECS

#### Goal
Instead of running the mysql databases (demodb and accountingdb) in local docker-containers you run docker containers in the AWS cloud using amazon ECS (EC2 Container Service).

#### Setup

1. Amazon AWS account is required.

#### Tasks

1. Create a Task Definition within the amazon ECS environment that defines the mysql containers.
2. Define a cluster and service that use the task definition.
3. Get the IP address of the instance where your containers run.
4. Configure your local setup to use the containerized mysql databases from the cloud instance.

Note: ECS already uses the Docker-Hub registry, so you don't need to upload any containers to the AWS container registry in this step! The "mysql" container you pull locally from Docker-Hub also works within the Task-Definition of ECS.

Note: This setup goes against the idea of docker-compose because the mysql containers are not managed by docker-compose. You will have to configure the databases on the config-server rather then through docker-compose. You can remove the database container entries from docker-compose for this stage because you don't need them.


### Stage 09 - Docker-Hub (container repository)

#### Goal
Instead of creating the containers locally you push them to Docker-Hub so that they are publicly available. For the local setup the containers are then pulled from the Docker-Hub repository.

#### Setup

1. Docker-Hub account is required.

#### Tasks

1. Decide which containers you can just reuse from Docker-Hub because they are standard containers and which containers are "self built" so you have to push to repositories in your Docker-Hub account. 
2. Create a repository for each self-built container that will be pushed.
3. Push the self build containers to their respective repositories on docker-hub.
4. Adapt the docker-compose configuration so you use the docker-hub containers and not the local Dockerfiles.

Note: The self built containers are those containers that have a "context" defined in docker-compose. They are built and run using Dockerfiles.

### Stage 09.A - Using the amazon ECR (EC2 Container Registry)

#### Goal
You understand how the AWS container registry works as an alternative to Docker-Hub. Your are able to upload docker containers to an AWS container repository.  

#### Setup

1. Amazon AWS account is required.
2. The [AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/installing.html) is required on your local machine.
3. Optionally you can [create an IAM user](http://docs.aws.amazon.com/AmazonECR/latest/userguide/get-set-up-for-amazon-ecr.html), if you operate within your own AWS account. If you create an IAM user you will have to [configure the CLI for that IAM user](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html).

#### Tasks

The ECR is an alternative to Docker-Hub. You can upload your container-images there so they are available in the AWS cloud.

1. Follow the instructions from the [ECR documentation on how to upload docker container images](http://docs.aws.amazon.com/AmazonECR/latest/userguide/ECR_GetStarted.html) and upload one of your container-images to the ECR.
2. Alter the task definition and use the container you uploaded to the amazon ECR instead of the Docker-Hub container-image.


### Stage 10 - Amazon ECS-CLI compose

#### Goal
You automatically generate a task-definition on amazon ECS out of your local docker-compose setup using the CLI. You use this task-definition to run containers in a new ECS cluster and service without any further adaption.

#### Setup

1. It is required that all the containers that the application consist of are available in the cloud (Docker-Hub or ECR).
2. The [ECS CLI](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_CLI_installation.html) is required on your local machine.
3. You will have to [configure the ECS CLI](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_CLI_Configuration.html) similar as to configuring the AWS CLI; for that purpose you might want to [create an IAM user](http://docs.aws.amazon.com/general/latest/gr/managing-aws-access-keys.html) if you are operating within your private AWS account.

#### Tasks

1. Adapt the docker-compose file so it is compatible to the [ecl-cli compose features](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/cmd-ecs-cli-compose.html) (only certain options are available).
2. Generate a task definition through [ecl-cli compose](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/cmd-ecs-cli-compose.html).
3. Add the generated task-definition (in your AWS account) to a new service within a new cluster and run the cluster.
4. Hope that the magic works :)

Note: Most likely it will not work just like that: Think about what could be the problem - if you can't solve it you can take a look at the [hints for stage 10](https://github.com/senacor/BankingInTheCloud-Tutorials/tree/master/hints/stage-10).

### Stage 10.A - Thinking about the architecture of your application

#### Goal
You reach a stage where you can think about the architecture of your application running in the cloud. You understand the basic principles of load balancing at an application level, database clusters/replication techniques and deployment strategies for microservices.  

#### Tasks

1. Read the [AWS introduction to load balancing](http://docs.aws.amazon.com/elasticloadbalancing/latest/userguide/what-is-load-balancing.html) and reflect on it.
2. Think about instances and containers - Which services will have to scale-up? Which services don't have to scale-up? What would be a good setup when it comes to scalability and fault-tolerance?
3. Read the [mysql introduction to data replication](https://dev.mysql.com/doc/refman/5.7/en/replication.html) and reflect on it.
4. Think about your data - How can you keep the data persistent between service instances when doing load balancing at application level?
5. Read about [microservices architectures](http://microservices.io/patterns/microservices.html) and dive into deployment patterns like [Single Service Instance per Host](http://microservices.io/patterns/deployment/single-service-per-host.html) and [Multiple Service Instances per Host](http://microservices.io/patterns/deployment/multiple-services-per-host.html) - reflect on them. 
6. Think about our current microservices architecture and out deployment setup - What could be done different and what would be the advantages and disadvantages? How would you apply load balancing?


### Stage 11 - Load Balancing on amazon AWS

#### Goal
You add an elastic load balancer to the project setup on AWS. You understand application level load balancing.

#### Tasks

1. Define a load balancing strategy for the project. You will most likely have to adapt the database setup.
2. Create a new task definition that that defines a container deployment that makes it possible to apply your load balancing strategy. 
3. Create an ECS cluster and Service and add a load balancer.


### Stage 12 - Utilizing Cloud storage instead of a database on amazon AWS

#### Goal
Instead of running a database in a docker container you should utilize the simple storage service (S3) of amazon AWS.

#### Tasks