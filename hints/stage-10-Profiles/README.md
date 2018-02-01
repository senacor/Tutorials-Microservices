# Hints for Tutorial stage 10

## Overview

Wouldn't it be nice to be able to start your application from the IDE (Intellij IDEA) without caring about the docker containers, but at the same time be able to start the application through docker-compose? - Of course without changing anything, just like that.

The goal of this stage is to configure two profiles for your project properly. Usually you would already think about this when you create the project, but the effect is more visible now.

## What do we have at the moment

After stage-09 (Resilient Setup) our configuration for the customer and the accounting services are basically in two files:

1. The ```bootstrap.yml``` contains the information on how to reach the config server.
2. The ```[SERVICE-NAME]-dev.yml``` in the config-repo folder that is served by the config server for the respective service.

We don't have a separate ```application.yml``` file in the service itself, all the configuration is in the config-repo at the moment.

Our only configuration profile (```dev```) in the config-repo is currently setup to work with docker-compose. Since we are usding the docker-compose ```network``` parameter the configuration contains links to other services that do not contain IP-addresses but service names like ```http://config:8888``` or ```http://customerdb:3306```. 
Without docker-compose the application will however not be able to find other services by name - we would need a  ```localhost``` configuration for our local setup in order to run it without docker-compose.


## Where we want to go

The idea is to create two profiles:

1. ```dev``` profile: will contain the local setup for the developement environment and will be used when starting the services through the IDE.
2. ```prod``` profile: will contain the docker-compose configuration.

In order to reach this goal we have to:

1. Copy the current configuration from ```dev``` to a new ```prod``` profile.
2. Configure the ```dev``` profile for running locally (without containers).
3. Configure the environments to pull the correct profile upon startup (IDEA should pull ```dev```, docker should pull ```prod```).

## Details

### Change bootstrap

In order to connect to the right config-server address you will have to adapt the bootstrap configuration of the customer and accounting project.
The bootstrap configuration file (```bootstrap.yml```) allows you to [add several profiles](https://stackoverflow.com/questions/40981861/how-do-you-properly-set-different-spring-profiles-in-bootstrap-file-for-spring) separated by ```---```. Note that this only applies for YAML configuration files, if you are using ```.properties``` configuration files you will have to create a separate configuration file for each profile.

The ```bootstrap.yml``` file for your customer service should look something like this in the end (adapt accountin bootstrap accoringly):

```YAML
spring:
  profiles: prod
  application:
    name: customer
  cloud:
    config:
      uri: http://config:8888
      label: Stage-10-Profiles
      failFast: true
      retry:
        initialInterval: 10000
        maxInterval: 10000
        maxAttempts: 12

---
spring:
  profiles: dev
  application:
      name: customer
  cloud:
    config:
      uri: http://localhost:8888
      label: Stage-10-Profiles
```

Note that not all properties were included into both profiles. 

### Change configuration in config-repo

This is pretty straight forward, you create a second pair of configurations first, that contain the current configuration (for docker-compose) but name this configuration ```[SERVICE-NAME]-prod.yml```.

Once you copied the old ```dev``` configuration to the new ```prod``` configuration you can adapt the ```dev``` configuration to run on localhost without docker-compose. Just replace all the service names in the URLs with ```localhost```. 

Don't forget to commit and push the new configuration files to the config-repo so they are available to the config server.

### Change database to in memory database for local setup

Since it kindoff annoying to always start, stop and reset the mysql database by hand, you can change your database settings for the local ```dev``` configuration. One possibility is to use an in memory H2 database for the local setup instead of the MySQL container we used to far. 

You will have to change the database settings in your ```[SERVICE-NAME]-dev.yml``` configuration files in the config-repo. As an example see the ```customer-dev.yml``` database configuration:

```YAML
...
spring:
  datasource:
    driver-class-name: 'org.h2.Driver'
    url: 'jdbc:h2:mem:customerdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'
    username: 'root'
    password: 'mysql'
...
```

In order for the project to be able to handle the H2 database, the H2 gradle compile dependency has to be added to the project. Add this to your ```build.gradle``` files in both the customer and the accounting project:

```
dependencies {
    ...
	compile('com.h2database:h2')
	...
}

``` 

There are also drawbacks when you use this approach. Since you have two different kinds of databases now, you might run into the problem that MySQL specific statements will not be executed in the local H2 setup properly. 
You can, however, see this as an advantage as well, because you can now find database statements that are very database specific. If you would want to migrate to a different database provider such kind of statements would make problems anyway. As long as you can avoid it, you should not use provider specific statements, but should stick to standard SQL syntax. Of course, if provider specific statements are needed you will have to think of a different solution.
In order to make the local H2 setup accept the flyway migration scripts you will have to:

* Remove the database selector from the MySQL statement (e.g. ```customerdb.customer``` will be changed to ```customer```). The selector is actually not needed since we specifcy the specific mysql schema in the connection string anyway.
* Remove specific functions like ```STR_TO_DATE(...)``` (you can just provide a string date like ```31-10-1929'```).
* Replace all double quotes (```"```) with single quotes (```'```) when defining strings in SQL statements. Double quotes should not be used in this context within SQL statements anyway (besides H2 e.g. Oracle would also have problems with this).

### Configure IDEA to set the dev profile when running from IDEA

You have to provide the profile to be active when starting the application. The easiest way is to define a parameter in the ```VM options``` section of your launch configuration in IDEA. 

Since you want the ```dev``` profile to be active in IDEA, the VM options parameter to be provided is:
```
-Dspring.profiles.active=dev
```

Set this in the launch configuration of both the customer and the accounting project.

### Configure docker to set the prod profile when running through docker-compose

Similarly the ```Dockerfile```s also needs to set this parameter in the ```ENTRYPOINT```parameter list. Add the parameter (with the ```prod``` profile) to both the ```Dockerfile``` of the customer and the accounting project like this:

```
ENTRYPOINT ["java", ... ,"-Dspring.profiles.active=prod", "-jar","/app.jar"]
```

All the other parameters stay the same.

### Result

Now you should be able to: 

* Start your services (config, registry, customer, accounting) locally in the IDEA and communicate with them.
* Start your services through docker-compose and communicate with them.

You don't have to change anything when switching to a different startup. Communication with the services through postman is the same for both startups. Of course you cannot run both startups at the same time because the services are configured to use the same ports which once allocated are bound and cannot be used.

Note: For running docker-compose you still have to create a build of your project through gradle. This is currently not automated yet. 



