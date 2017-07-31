# Hints for Tutorial stage 03

## Overview

To make things clear from the beginning we first take a look at the spring-project structure at this point. We basically stick to the [folder structure as described in the spring documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-structuring-your-code.html) but adapt the names a little.

Folder structure of the demo project for stage 03:

```
com.senacor.bitc
             +- demo
                 +- DemoApplication.java
                 |
                 +- domain
                 |   +- Customer.java
                 |   +- CustomerRepository.java
                 |   +- IpAddress.java
                 |
                 +- rest
                 |   +- CustomerController.java
                 |   +- IpAddressController.java
                 |
                 +- service
                     +- CustomerService.java
```

## Spring data dependencies

You have to add dependencies for:

* Lombok (also add the plugin to IntelliJ)
* Spring Data JPA

The complete ```build.gradle``` file for stage 03:
```
buildscript {
	ext {
		springBootVersion = '1.5.4.RELEASE'
	}
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
		classpath('io.spring.gradle:dependency-management-plugin:0.5.4.RELEASE')
		classpath('mysql:mysql-connector-java:5.1.13')
	}
}

plugins {
	id "org.flywaydb.flyway" version "4.2.0"
}

flyway {
	url = 'jdbc:mysql://localhost:3306'
	user = 'root'
	password = 'mysql'
	schemas = ['demodb']
}

apply plugin: 'java'
apply plugin: 'idea'
apply plugin: 'org.springframework.boot'

version = '0.0.1-SNAPSHOT'
sourceCompatibility = 1.8

repositories {
	mavenCentral()
}

dependencies {
	compile('org.springframework.boot:spring-boot-starter-web')
	compile('org.springframework.cloud:spring-cloud-starter-config')
	compile('org.springframework.boot:spring-boot-starter-actuator')
	compile('org.yaml:snakeyaml')
	testCompile('org.springframework.boot:spring-boot-starter-test')
	compileOnly('org.projectlombok:lombok:1.16.18')
}


dependencyManagement {
	imports {
		mavenBom "org.springframework.cloud:spring-cloud-dependencies:Camden.SR5"
	}
}

jar {
	baseName = 'gs-accessing-data-jpa'
	version =  '0.1.0'
}

repositories {
	mavenCentral()
	maven { url "https://repository.jboss.org/nexus/content/repositories/releases" }
}

sourceCompatibility = 1.8
targetCompatibility = 1.8

dependencies {
	compile("org.springframework.boot:spring-boot-starter-data-jpa")
	compile("mysql:mysql-connector-java:5.1.13")
	testCompile("junit:junit")
}
```

## Configure the database

At some point the application has to know where to find the database and how to connect to it. This is done in the ```application.yml``` file. 

```YAML
spring:
  datasource:
    url: 'jdbc:mysql://localhost:3306/demodb'
    username: 'root'
    password: 'mysql'
    driver-class-name: 'com.mysql.jdbc.Driver'
```

Note that you could also put this configuration into the service's ```demo-dev.yml``` configuration file on the config server. For this tutorial we put it in the project for now, but it is of course valid to let the config server hold this information.

## Add customer Entity and Repository

The entity class represents the structure of the customer database table as a domain object, it is a typical POJO. Since POJOs usually include a lot of boilerplate code there are plugins that can help you be more efficient. One famous plugin out there is Lombok.

Your customer entity class will look something like this:

```Java
@Data // Lombok: auto generate getters and setters
@NoArgsConstructor // Lombok: add no-argument-constructor, needed for JPA
@Entity // JPA
@Table(name = "customer") // JPA
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "first_name", nullable = false)
    public String firstName;
    @Column(name = "last_name", nullable = false)
    public String lastName;
    @Column(name = "birth_date", nullable = false)
    public Date birthDate;
    public String comment;

}
```

Additionally we define a *repository* for the customer entity. Repository in Spring Data help you to avoid writing a lot of boilerplate code again - the boilerplate code to write CRUD (create, read, update, delete) services and select features. Instead implementing all the functions to access the database table by hand, the framework will generate the code for you. 
The repository is configured by marking the repository interface with another interface that defines the underlying entity. The customer repository might look something like this:

```Java
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);

}
```

## Add the customer service

Services abstract logic - logic should not be placed directly into the endpoint. Usually it is good practice to keep endpoints as simple as possible and just forward requests to services that are injected in the endpoint. Of course one can share services among endpoints. Usually one provides an interface for each service, to the implementation can change while the interface is still valid.

The customer service can look something like this:

```Java
@Component
public class MySQLCustomerService implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public MySQLCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    public Customer loadCustomerById(Long customerId) {
        return customerRepository.findOne(customerId);
    }

    @Override
    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public List<Customer> findCustomersByLastName(String lastName) {
        return customerRepository.findByLastName(lastName);
    }
}

```

Note that it is important to mark the Service with ```@Component``` so Spring can find it (for injection with ```@Autowired```).

## Add customer endpoint

Similarly to stage 00 we have to provide a REST endpoint (controller) to be able to communicate with the customer service.

The customer endpoint can look something like this:
```
@RestController
@RequestMapping(value = "/customer", produces = "application/json")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @RequestMapping(value = "/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public Customer getCustomerById(@PathVariable Long customerId) {
        return customerService.loadCustomerById(customerId);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Customer> getCustomersByName(
            @RequestParam(value = "lastName", defaultValue = "", required = false) String lastName) {
        return customerService.findCustomersByLastName(lastName);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createCustomer(@RequestBody Customer customer) {
        customerService.saveCustomer(customer);
    }

    // Note: It is arguable if the search for customers should be in the customer endpoint
    //      If there are several search implementations (lastName, firstName, ...) it would
    //      be better to add a customer-search endpoint ;)

}
```

Notes: 

* The service is injected by constructor-injection. This is the best practice because it offers more flexibility when writing tests.
* ```getCustomerById``` was implemented to accept variables as part of the url-path. To receive the 1st customer you will have to send a GET request like this: ```[host:port]/customer/1```
* ```getCustomerByName``` was implemented to accept the last name of a customer as url-parameter. To retrieve the customer with last name "Hill" you will have to send a GET request like this: ```[host:port]/customer?lastName=Hill```


## Write Tests :)

SpringBoot offers some powerful testing features - powerful but you have to know what you are doing. It saves you a lot of typing work, but it makes it difficult to understand what actually happens in the background (a lot of magic...).

We use two features in our tests:

1. MockMVC: Can be used to mock REST endpoints
2. MockBean: Can be used to mock services directly (through mockito)

Try to write your own tests. You can refer to the reference solution for details.