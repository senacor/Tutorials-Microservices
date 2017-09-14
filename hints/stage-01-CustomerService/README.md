# Hints for Tutorial stage 01

## Overview

In this stage you create a first REST-controller that is supposed to return the IP address of your server. 

## REST-Controller

Use this [String Boot Guide on RestControllers](https://spring.io/guides/gs/rest-service/) for reference. You can just implement and run the service in IntelliJ IDEA after importing the customer project (generated with the Spring Initializr).

### Retrieve IP-address

You can use this code snippet to retrieve the IP address of the server as String:

```java

import java.net.InetAddress;

// (...)

    private String retrieveServerIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

// (...)
```

## Configure the entry point to your microservice

You will notice that you have a file ```application.properties``` in your ```resources``` folder in the main code folder.
Replace that file with a ```application.yml``` file - we will use YAML configuration files. If you want to you can also use the properties notation, but all further stages will use YAML notation.

Add yaml dependencies to the ```build.gradle```
```
compile('org.yaml:snakeyaml')
```

We recommend that you set the context path, so all the endpoints of your microservice is identified by the same start-pattern. To do so you specify the ```contextPath``` setting for your server in your ```application.yml``` file:

```YAML
server:
  contextPath: /customer
```

## Add simple test cases for your service

Take a look at the [documentation on testing Spring applications](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html). At least create a ```WebMvcTest``` for your endpoint.

## Run your service

The ```CustomerApplication``` class containers the main method.

You should be able to access the endpoint at ```http://localhost:8080/customer/address```


