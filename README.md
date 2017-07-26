# BankingInTheCloud-Tutorials
This repo provides tutorials for the BankingInTheCloud workshop. 

## Project Setup

The master-branch contains the zip files needed for the stages. 
Note: If you cannot 

## Tutorial stages

The tutorials are done in steps that are based on each other. 
Participants are supposed to solve each tutorial stage by themselfes. A recerence solution can be found in branches.

### Stage 00 - Basic Setup

#### Goal
You have your first spring boot application up and running.

#### Project Setup
The basic project setup is based on the demo-project one can generate using the [SpringBoot Initializr](https://start.spring.io/). Use the following settings:

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.4```
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

* Generate a ```Gradle Project``` with ```Java``` and Spring Boot ```1.5.4```
* Group: ```com.senacor.bitc```
* Artifact: ```config```
* Dependencies: ```Web``` ```Config Server```

Add the ```config``` project as module in IntelliJ IDEA: ```File``` >> ``` New``` >> ``` Module from Existing Sources...``` >> select the extracted ```config``` folder

#### Tasks

1. Configure the **config** project as [cloud-config-server](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html)
2. Configure the spring cloud **config** server to use a git-repsoitory where you put the configuration for your demo service.
2. Configure the **demo** service so it uses the cloud config server for configuration.
3. Configure the port where the **demo** application is served through the **config** server configuration file. The port should not be hard-wired in the **demo** application any more, but is defined by the configuration file served by the **config** server.

We recommend that you use yaml instead of properties files. 

#### Hints

##### Setup with a local git config-repository

Try to configure the config server to use a local config-repository first. You can use the official [Spring Cloud Config documentation](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) and/or the [Baeldung Spring Cloud Config Tutorial](http://www.baeldung.com/spring-cloud-configuration) for reference.

Steps:

**Config Server**

1. Add yaml dependencies to the config-server ```build.gradle```
    ```
    compile('org.yaml:snakeyaml')
    ```
2. Enable the config server by adding annotation ```@EnableConfigServer``` on the class marked with ```@SpringBootApplication``` (```ConfigApplication```)
3. Create file ```application.yml``` in the resources folder (you can remove existing ```.properties``` files)
4. Configure the config-server in the config server's application.yml you just created:

    ```
    spring:
      cloud:
        config:
          server:
            git:
              uri: file:///${user.home}/Desktop/demo
    server:
      port: 8888
    ```

**Config Repository**

5. Create a folder that matches the pattern of the uri you provided above (```file:///${user.home}/Desktop/demo``` >> just create a folder ```demo``` on your Desktop)
6. run ```git init``` in the folder you created
7. Add the file ```demo-dev.yml``` to the folder. This will be the configuration file that the client application (the demo application) pulls upon startup.
8. Add the configuration for the new port to the ```demo-dev.yml``` file (instead of running the demo application on default port 8080 we run it on port 8081):

    ```
    server:
      port: 8081
    ```

9. Commit the file ```demo-dev.yml``` using ```git commit```
10. Test the configuration by starting the config-server and navigating to ```http://localhost:8888/demo/dev```

**Client Configuration**

11. Add yaml and bootstrap dependencies to the client project (the demo project)

    ```
    dependencies {
    	compile('org.springframework.boot:spring-boot-starter-web')
    	compile('org.springframework.cloud:spring-cloud-starter-config')
        compile('org.springframework.boot:spring-boot-starter-actuator')
    	compile('org.yaml:snakeyaml')
        testCompile('org.springframework.boot:spring-boot-starter-test')
    }
    dependencyManagement {
	    imports {
		    mavenBom "org.springframework.cloud:spring-cloud-dependencies:Camden.SR5"
	    }
    }
    ```

12. Create file ```bootstrap.yml``` in the resources folder (you can remove existing ```.properties``` files)
13. Configure the client to find the config-server by in the ```bootstrap.yml```:

    ```
    spring:
      application:
        name: demo
      profiles:
        active: dev
      cloud:
        config:
          uri: http://localhost:8888
    ```

14. Test the setup by starting the demo-service. If everything works correct it will start up on port ```8081``` now.

### Stage 02 - Flyway (database migration)

#### Goal
Create a database with dummy-data for your service using flyway migration scripts.

#### Project Setup

1. Create a XXX database 
2. (...)

#### Tasks



### Stage 03 - Spring Data

#### Goal
Create domain objects and fill them with data from the previously created database using Spring Data.

Note: include Lombok

#### Tasks


### Stage 04 - Create a second service

#### Goal
You create a second service that can communicate with the first service (demo-service).


#### Tasks



### Stage 05 - Eureka (service discovery)

#### Goal
You add a service discovery so the services can find each other through the discovery server.

#### Tasks



### Stage 06 - Docker (containerize)

#### Goal
You pack your services into containers for deployment.

#### Tasks


### Stage 07 - Messaging and Event Sourcing

#### Goal
You add endpoints that emit events, so your two services don't directly communicate with each other but one service emits an event that the other service consumes.

#### Tasks

