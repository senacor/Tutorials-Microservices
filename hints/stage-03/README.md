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
* Newest version of hibernate (Java 8)
* Jackson (for clean mapping)

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
	}
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
	compile('org.flywaydb:flyway-core')
	compile("mysql:mysql-connector-java:5.1.13")
	compile('org.springframework.boot:spring-boot-starter-data-jpa')
	compile('org.hibernate:hibernate-java8:5.1.0.Final')
	compile('com.fasterxml.jackson.datatype:jackson-datatype-jsr310')
	compileOnly('org.projectlombok:lombok:1.16.18')
	testCompile('org.springframework.boot:spring-boot-starter-test')
}


dependencyManagement {
	imports {
		mavenBom "org.springframework.cloud:spring-cloud-dependencies:Camden.SR5"
	}
}


```

## Configure the database

For stage 03 our ```application.yml``` file looks pretty much the same as in stage 02; we just added some jackson configuration (for the mapping in the tests): 

```YAML
spring:
  datasource:
    url: 'jdbc:mysql://localhost:3306/demodb'
    username: 'root'
    password: 'mysql'
    driver-class-name: 'com.mysql.jdbc.Driver'
  jackson:
    serialization:
      WRITE_DATES_AS_TIMESTAMPS: false
    default-property-inclusion: non_null
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
    public Integer id;
    @Column(name = "first_name", nullable = false)
    public String firstName;
    @Column(name = "last_name", nullable = false)
    public String lastName;
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "birth_date", nullable = false)
    public LocalDate birthDate;
    public String comment;

}
```

Additionally we define a *repository* for the customer entity. Repository in Spring Data help you to avoid writing a lot of boilerplate code again - the boilerplate code to write CRUD (create, read, update, delete) services and select features. Instead implementing all the functions to access the database table by hand, the framework will generate the code for you. 
The repository is configured by marking the repository interface with another interface that defines the underlying entity. The customer repository might look something like this:

```Java
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    List<Customer> findByLastName(String lastName);

}
```

## Add the customer service

Services abstract logic - logic should not be placed directly into the endpoint. Usually it is good practice to keep endpoints as simple as possible and just forward requests to services that are injected in the endpoint. Of course one can share services among endpoints. Usually one provides an interface for each service, to the implementation can change while the interface is still valid.

The customer service can look something like this:

```Java
@Service
public class MySQLCustomerService implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public MySQLCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    public Customer loadCustomerById(Integer customerId) {
        return customerRepository.findOne(customerId);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> findCustomersByLastName(String lastName) {
        return customerRepository.findByLastName(lastName);
    }
}

```

Note that it is important to mark the Service as Spring component so Spring can find it (for injection with ```@Autowired```); you can use ```@Component``` or the more specific stereotype ```@Service```.

## Add customer endpoint

Similarly to stage 00 we have to provide a REST endpoint (controller) to be able to communicate with the customer service.

The customer endpoint can look something like this:
```Java
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
    public Customer getCustomerById(@PathVariable Integer customerId) {
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
    @ResponseBody
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }
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
2. MockBean: Can be used to mock services (through mockito) with the pattern ```given([service].[method_call]).willReturn([some_mocked_result]);```

**Try to write test cases on your own.**
You can refer to the reference solution for details.


### Add a Builder to your entity

Builders help you to create test data quickly. We can extend the customer entity to include a builder with fluent setters (through Lombok) like this:
```Java
@Data
@Builder // Lombok: builder
@NoArgsConstructor // needed for JPA
@AllArgsConstructor // needed for builder (because of NoArgsConstructor)
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    @Column(name = "first_name", nullable = false)
    public String firstName;
    @Column(name = "last_name", nullable = false)
    public String lastName;
    @JsonSerialize(using = LocalDateSerializer.class)
    @Column(name = "birth_date", nullable = false)
    public LocalDate birthDate;
    public String comment;

}
```

Using the builder you can create mock-data like this:
```Java
private Customer getCustomer() {

        return Customer.builder()
                .id(1)
                .firstName("First")
                .lastName("Last")
                .birthDate(LocalDate.of(2000,1,1))
                .comment("comment")
                .build();
}
```

## Send requests to the service

Start the project and retrieve customer data by ID or by last name in your browser:

```
[host:port]/customer/{CustomerId}
[host:port]/customer?lastName={CustomerLastName}
```

In the reference solution the following command has the following output.

http://localhost:8081/customer/1

> {"id":1,"firstName":"Bud","lastName":"Spencer","birthDate":"1929-10-31","comment":"cool guy"}

http://localhost:8081/customer?lastName=Hill

> [{"id":2,"firstName":"Terence","lastName":"Hill","birthDate":"1939-03-29","comment":"cool guy"}]


You can use the postman environment provided in the ```postman``` folder to send a POST requests that creates a customer.
The customer to be created is to be defined in the request-body:
```
{
    "firstName": "Other",
    "lastName": "Guy",
    "birthDate": "2000-01-01",
    "comment": "nothing"
}
```

The response should contain the created customer with an ID:
```
{
    "id": 3,
    "firstName": "Other",
    "lastName": "Guy",
    "birthDate": "2000-01-01",
    "comment": "nothing"
}
```

Note: Don't forget to run the config server as well, otherwise your demo service will be available at port 8080 not port 8081!