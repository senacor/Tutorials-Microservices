# Hints for Tutorial stage 05

## Overview

After adding the registry project you have 4 projects now:

1. **demo**: The demo service (including the customer endpoint)
2. **config**: The config server
2. **accounting**: The accounting service (including the account endpoint)
3. **registry**: The Eureka server

## Configuring the Eureka server

You can follow the [official spring-cloud-netflix documentation](http://cloud.spring.io/spring-cloud-netflix/spring-cloud-netflix.html) (refer to section "Declarative REST Client: Feign") and the [spring-cloud-netflix-eureka tutorial from Baeldung](http://www.baeldung.com/spring-cloud-netflix-eureka).

The registry project already includes the necessary dependencies (as configured through the Initializr).

You will have to annotate the application class with ```@EnableEurekaServer```.

The server configuration (```application.yml```) should look like this:
```YAML
server:
  port: 8761
eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
  instance:
    preferIpAddress: true
 ```

## Configuring the Eureka clients

Both the demo and the accounting project have to register at the Eureka server upon startup; thus they have to be configured as Eureka clients.

The following dependencies have to be added to ```build.gradle```:
```
compile('org.springframework.cloud:spring-cloud-starter-eureka')
```

The ```@EnableEurekaClient``` annotation has to be added to the application classes:
```Java
@SpringBootApplication
@EnableEurekaClient
public class [AcclicationName]Application {
	...
}
```

Configure the Eureka server in the clients' ```application.yml```; *add* the following configuration:
```YAML
# additional configuration for Eureka
server:
  port: 0
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_URI:http://localhost:8761/eureka}
```

Startup Eureka and the demo and accounting service. Navigate to the Eureka startpage (http://localhost:8761/). You should see the demo and the accounting project registered in the "Instances currently registered with Eureka" section.

Note: The application name is already configured in the ```bootstrap.yml```, thus it is not specified in the ```application.yml```.

## Configuring the Feign client 

The feign client is defined in the accounting project, to access the customer endpoint (in demo) from the account service (in accounting).

First you have to add the necessary dependencies to the ```build.gradle``` of the accounting project:
```
compile('org.springframework.cloud:spring-cloud-starter-feign')
compile('org.springframework.boot:spring-boot-starter-thymeleaf')
```

The following annotations have to be added to the application class:
```Java
@EnableFeignClients
@Configuration
```

Note that it is important to add the ```@configuration``` class, otherwise the Feign client will not be configured properly.

Basically the feign client is an interface that is marked with the ```@FeignClient("APPLICATION_NAME")``` annotation. The feign client to access the customer endpoint in the demo project looks something like this:

```Java
@FeignClient("demo")
@RequestMapping(value = "/customer", produces = "application/json")
public interface CustomerClient {

    @RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    Customer getCustomerById(@PathVariable(name = "customerId") Integer customerId);

}
```

You can notice that this is pretty much the same definition as in the customer endpoint - sure, because the feign client defines the interface for the customer endpoint.

You can inject the feign client like this in the implementation of the account service:
```Java
private final CustomerClient customerClient;
private final AccountRepository accountRepository;

@Autowired
public MySQLAccountService(
        AccountRepository accountRepository,
        CustomerClient customerClient) {
    this.accountRepository = accountRepository;
    this.customerClient = customerClient;
}
```

Then you can add the part where we check if the customer exists upon account creation:
```Java
    @Override
    public Account saveAccount(Account account) throws CustomerNotFoundException {

        Integer customerId = account.customerId;

        if (customerId == null ||
                customerClient.getCustomerById(customerId) == null)
        {
            throw new CustomerNotFoundException();
        }

        return accountRepository.save(account);
    }
```

Now you are ready to check if your account creation with customer ID check works. you can use the postman POST request against the account endpoint (from stage 04) to test the the account creation with customer ID check. In the reference solution we throw a custom exception if the customer cannot be found.

Note: Don't forget to start the config server as well, otherwise the demo and the accounting application will register with a random free port at Eureka.

If you run into the problem, that *the first request you send via the feign client fails with a timeout* (when you create an account using the account endpoint) you can add this to the ```application.yml``` of the accounting project to avoid the timeout:
```YAML
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 10000
``` 