# Hints for Tutorial stage 10

## Overview

The goal of this stage is to achieve a setup like this:

```
Amazon EC2 VM instance
------------------------------
|                            |
|  ------------------------  |
|  | customer container       |  | >> instance port 8081 maps to customer container port 8081
|  ------------------------  |
|                            |
|  ------------------------  |
|  | accounting container |  | >> instance port 8082 maps to accounting container port 8082
|  ------------------------  |
|                            |
|  ------------------------  |
|  | registry container   |  |
|  ------------------------  |
|                            |
|  ------------------------  |
|  | config container     |  |
|  ------------------------  |
|                            |
|  ------------------------  |
|  | customerdb container     |  | 
|  ------------------------  |
|                            |
|  ------------------------  |
|  | accountingdb cont.   |  | 
|  ------------------------  |
|                            |
------------------------------
```

## Make the docker-compose file ECS-CLI compatible

When this tutorial was created (2017/08) the ecs-cli did only support selected docker-compose version 2 commands.
"Advanced" options like ```networks```, ```depends_on``` and several other options are not supported, hence we have to dumb-down our docker-compose file so we can generate the task definition without problems. 

Instead of ```networks``` we will fall back on the old ```links``` option. Other options can just be removed (like the ```container_name``` which is not supported either). In the end your docker-compose file should look something like this:

```YAML
version: '2'

services:
  customerdb:
    image: mysql
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=customerdb
    ports:
      - "3306:3306"

  accountingdb:
    image: mysql
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=accountingdb
    ports:
      - "3305:3306"

  registry:
    image: senacortutorials/registry:stage-09
    ports:
      - "8761:8761"

  config:
    image: senacortutorials/config:stage-09
    ports:
      - "8888:8888"

  customer:
    image: senacortutorials/customer:stage-09
    ports:
      - "8081:8081"
      - "7081:7081"
    links:
        - customerdb
        - registry
        - config

  accounting:
    image: senacortutorials/accounting:stage-09
    ports:
      - "8082:8082"
      - "7082:7082"
    links:
        - accountingdb
        - registry
        - config
```

## Run the project locally

Before you generate the task definition and run all the services in an AWS cluster, make sure you are able to run the project locally. With the above docker-compose configuration that should work without problems. 

## Make the project resilient (start all together without problems)

So far we operated mostly locally - so it was not really important how the complete application would startup.
Remember, so far we always started certain services at first (databases, config, registry) and then started the application services. 

Now that we want to operate in a cloud-environment it gets overly complicated to do that. Sure, we could still try to start our services in a specified order by adding some [startup-script logic to docker-compose](https://docs.docker.com/compose/startup-order/) - but is that the right way to go? 

As the docker documentation tells us, the better way is to make our application more resilient towards connection problems. If e.g. the config server is not available the application should not just fail, but it should try to connect to the server until it is available (at startup). If the database is not available at some point the application should show us an error, but it should not just die (at runtime).

So, how do we achieve this? Yes, you guessed it: By adding some more (rather difficult to find) configuration entries to our startup bootstrap and application configuration files.

For the spring cloud configuration client you can [define retries](https://cloud.spring.io/spring-cloud-config/multi/multi__spring_cloud_config_client.html#config-client-retry), so the application will try to retry contacting the config server upon startup if it is not available yet. You have to at least activate the ```failFast``` option in your customer's and the accounting's ```bootstrap.yml```; furthermore you can refine the options by specifying specific ```retry``` options:

```YAML
(...)
spring:
  (...)
  cloud:
    config:
      (...)
      failFast: true
      retry:
        initialInterval: 10000
        maxInterval: 10000
        maxAttempts: 10
```

With this configuration the server will try to contact the config server for 12 times in a 10 seconds interval until it can reach the server.

#### Database configurations (optional)

The options for the database are more complicated - and for just starting the application you will actually not need them (because the database will most likely start faster than the spring-boot applications). However, here are [some options](https://stackoverflow.com/questions/23850585/how-to-start-spring-boot-app-without-depending-on-database) you might find helpful:

```YAML
(...)
spring:
  datasource:
    url: 'jdbc:mysql://accountingdb:3306/accountingdb'
    username: 'root'
    password: 'mysql'
    driver-class-name: 'com.mysql.jdbc.Driver'
    continueOnError: true
    initialize: false
    initialSize: 0
    timeBetweenEvictionRunsMillis: 5000
    minEvictableIdleTimeMillis: 5000
    minIdle: 0
  jackson:
    serialization:
      WRITE_DATES_AS_TIMESTAMPS: false
    default-property-inclusion: non_null
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: none
      naming_strategy: org.hibernate.cfg.DefaultNamingStrategy
    properties:
      hibernate:   
        dialect: org.hibernate.dialect.MySQL5Dialect
        hbm2ddl:
          auto: none
        temp:
          use_jdbc_metadata_defaults: false
(...)
```

### Create new container-images and push them to docker-hub 

Make sure that you build your projects newly so they include the new configuration settings. Once the project builds are up to date you should rebuild your container-images and push them to docker-hub so they are available in the cloud.

Note: Since we use the stages for tagging the containers in the tutorial, the tags will change to "stage-10" for the customer and the accounting container in the docker-compose configuration, as those containers were newly built and pushed for stage-10:

```YAML
(...)

  customer:
    image: senacortutorials/customer:stage-10
    (...)

  accounting:
    image: senacortutorials/accounting:stage-10
    (...)
```

## Generate the task definition using the ecs-cli

The ecs-cli (EC2 Container Service Command Line Interface) is a command line tool offered by Amazon. After [installing](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_CLI_installation.html) and [configuring](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_CLI_Configuration.html) the ecs-cli it is fairly simple to use it to generate task definition, create services and clusters and to start, stop or destroy them.

As we don't want to pack too much magic into the creation of our cluster yet, we just generate the task definition as defined in the [ecs-cli compose documentation](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/cmd-ecs-cli-compose-service.html):
```
ecs-cli compose --project-name demo-app --file docker-compose.yml create
```

Note: "demo-app" is just a name that you can choose.

Note: The ecs-cli might give you some warnings like "Skipping unsupported YAML option ..." - if such a warning concerns an option that is actually in use in your docker-compose file then you should either remove it or find a way that is compatible to the ecl-cli compose. If the ecl-cli compose warns you about an option that is not in use in your docker-compose file you can simply ignore the warnings.

## Run a cluster from the task definition

### First try - cluster and service

Once you successfully generated the task definition your can have a look at it in your ECS console (you already know it from stage 08).

Create a cluster with a relatively strong machine - remember the services need quite a lot of resources to startup on your local machine as well. The instance "m4.large" proposed as default for the cluster will definitely do, you can also go for something a little smaller if you want. Make sure you configure the right ports (or port range) so you can access your services. At least port 8081 (customer) and 8082 (accounting) should be open on your instance.

Note: At this point you might run into costs, since instance stronger then t2.micro are not part of the free tier. If you only run them for a short time the costs will be a few cents, but don't forget to delete the cluster in the end...

Once the cluster was create you create a service and attach the generated task definition to it. Take a look at the service and the task that it tries to run. Most likely the task will fail after a while. 
Once you have a closer look at a failed task (in the "Stopped" tab) you will most likely notice that the spring-boot services (usually config and/or registry fail first) don't have enough memory. The default memory-limit set by the task-definition generator (512MB) kicks in here.

### How to solve the "out of memory" issues?

Well, give those services more memory. The quick-and-dirty way to do it is to just alter the generated task definition right in your ECS console (online).

The more sustainable way is to add the memory settings to the docker-compose file (locally) and generate a new task definition using ecs-cli compose. Your ```docker-compose.yml``` file should look something like this now:

```YAML
version: '2'

services:
  customerdb:
    image: mysql
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=customerdb
    ports:
      - "3306:3306"

  accountingdb:
    image: mysql
    environment:
      - MYSQL_ROOT_PASSWORD=mysql
      - MYSQL_DATABASE=accountingdb
    ports:
      - "3305:3306"

  registry:
    image: senacortutorials/registry:stage-09
    ports:
      - "8761:8761"
    mem_limit: 1073741824

  config:
    image: senacortutorials/config:stage-09
    ports:
      - "8888:8888"
    mem_limit: 1073741824


  customer:
    image: senacortutorials/customer:stage-10
    ports:
      - "8081:8081"
      - "7081:7081"
    links:
        - customerdb
        - registry
        - config
    mem_limit: 1073741824

  accounting:
    image: senacortutorials/accounting:stage-10
    ports:
      - "8082:8082"
      - "7082:7082"
    links:
        - accountingdb
        - registry
        - config
    mem_limit: 1073741824
```

### Test the application by retrieving and creating customers/accounts 

You can access your application by retrieving the IP address of EC2 instance that is managed by the cluster. If your application works you will be able to access the data with the IP address as before: 

```
[INSTANCE_IP_ADDRESS]:8081/customer/[CUSTOMER_ID]
[INSTANCE_IP_ADDRESS]:8082/account/[ACCOUNT_ID]
(...)
```

You will be able to POST against the customer/account endpoint using the IP of the instance. 

Note that you can also use the "Public DNS" instead of the IP address.

### Security group settings for more insight

At the end of the day the "cluster" created for you by ECS is just an instance that runs docker containers. There is a lot of configuration around it, but in the end it is just a virtual machine running on an AWS server. You can have a look at this instance in your EC2 console online - and of course you can also inspect and alter the security group that was created for that instance. 

If you navigate to the security groups in your EC2 console and select the right (generated) security group you will be able to inspect and alter its port settings. This can be interesting if you want to e.g. take a look at your Eureka container's front-page offered at port 8761 - if you allow access to that port in the security group you will be able to see the registration of the customer and accounting services at Eureka. Inbound-rule changes will apply instantly.

Of course you can go even further and allow to [SSH into the instance](http://docs.aws.amazon.com/AmazonECS/latest/developerguide/instance-connect.html). Make sure your SSH port (22) is open in the security group of the instance. Once you are connected to the instance you can run commands like [docker ps](https://docs.docker.com/engine/reference/commandline/ps/) to see the status of your containers: 
```
docker ps -a
```

You can then retrieve the log output of a container using [docker logs](https://docs.docker.com/engine/reference/commandline/logs/):
```
docker logs [CONTAINER_ID]
```

