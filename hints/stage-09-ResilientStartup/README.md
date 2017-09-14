# Hints for Tutorial stage 09

## Overview

The goal of this stage is, that you can run ```docker-compose up``` for all the services together, without the need to start several services first and start the others once the first ones are available.

## Move all the configuration to the config-server

That is a rather simple task: take the configuration from your ```application.yml``` files and put it into the configuration files in the config-repo. Make sure you commit and push your changes ;)

Note: If you put your configuration changes on the config-repo on a different branch than before, don't forget to update your customer and accounting project's ```bootstrap.yml```.

## Resilient Startup

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

### Database configurations (optional)

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