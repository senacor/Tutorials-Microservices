# Hints for Tutorial stage 02

## Overview

In this stage you learn how to use the config-server. Through the config-server you can configure your application without rebuilding and redeploying your project. 

Your Spring project may contain two kinds of configurations (note that we refer to the ```.yml``` files, you can also use ```.properties``` files):

1. ```bootstrap.yml```: Is loaded before the application properties. If a config-server is defined there it will load the configuration from the contif-server and merge it with the configuration specified in ```application.yml```.
2. ```application.yml```: The application properties that define how your application should start up.

In the tutorial you can first setup the config-server using a local git repository, and then using a remote git repository. The repository contains the configuration files for your applications

You have two sides that you need to configure:

1. The config-server. (will be added as a new project)
2. The microservice (customer project) that wants to retrieve its configuration from the config-server.

On the config-server you have to specify the repository to be used (typically the URL of your git repository).
Once the project ask the config-server for the project's configuration, the config-server has to identify the correct configuration within the repository. To add flexibility you can configure four parts to identify the configuration within the customer-project:

1. **uri** (```spring.cloud.config.uri```): defines the config-server URL
2. **label** (```spring.cloud.config.label```): defines the branch within the repository (specified in the config-server)
3. **application name** (```spring.application.name```): defines the (first part of the) configuration file-name for a specific service
4. **profile** (```spring.profiles.active```): defines the second part of the configuration file for a specific service

## Configure the config server with a local git config-repository

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

    ```YAML
    spring:
      cloud:
        config:
          server:
            git:
              uri: file:///${user.home}/Desktop/customer
    server:
      port: 8888
    ```

**Config Repository**

5. Create a folder that matches the pattern of the uri you provided above (```file:///${user.home}/Desktop/customer``` >> just create a folder ```customer``` on your Desktop)
6. run ```git init``` in the folder you created
7. Add the file ```customer-dev.yml``` to the folder. This will be the configuration file that the client application (the customer application) pulls upon startup.
8. Add the configuration for the new port to the ```customer-dev.yml``` file (instead of running the customer application on default port 8080 we run it on port 8081):

    ```YAML
    server:
      port: 8081
    ```

9. Commit the file ```customer-dev.yml``` using ```git commit```
10. Test the configuration by starting the config-server and navigating to ```http://localhost:8888/customer/dev```

**Client Configuration**

11. Add bootstrap dependencies to the client project (the customer project)

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

    ```YAML
    spring:
      application:
        name: customer
      profiles:
        active: dev
      cloud:
        config:
          uri: http://localhost:8888
    ```

14. Test the setup by starting the customer-service. If everything works correctly it will start up on port ```8081``` now.

## Configure the config-server to use a remote config-repository

Once you are done with the local setup you can reconfigure the config server to use a remote config-repository instead of your local repository. To do so you will have to adapt the config server's ```application.yml```:

```YAML
spring:
  cloud:
    config:
      server:
        git:
          uri: [PATH_TO_REMOTE_REPOSITORY]
          searchPaths: [FOLDER_IN_REMOTE_REPOSITORY]
server:
  port: 8888
```

Example configuration with the tutorial repository:

**application.yml** (config project)

```YAML
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/senacor/Tutorials-Microservices
          searchPaths: config-repo
server:
  port: 8888
```

Since the ```config-repo``` folder is on a specific branch within the tutorial repository you have to specify the branch as ```label``` in the ```bootstrap.yml``` of the customer application to make the setup work:

**bootstrap.yml** (customer project)

```YAML
spring:
  application:
    name: customer
  profiles:
    active: dev
  cloud:
    config:
      uri: http://localhost:8888
      label: Stage-01-SpringCloudConfig
```
