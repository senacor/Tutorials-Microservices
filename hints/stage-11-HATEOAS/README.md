# Hints for Tutorial stage 11

## Overview

> HATEOAS (Hypermedia as the Engine of Application State) is a constraint of the REST application architecture.
>
> -- <cite> [Spring.io on HATEOAS](https://spring.io/understanding/HATEOAS) </cite>

In short this constraint is about adding links to your "resources" (the data-objects returned by your service), so that a consumer of the service does not have to care about the navigation any more, but the server tells the consumer where he can navigate.

The most simple link is always the ```self``` reference of a resource. 
Until stage 10 a customer resource returned by the customer endpoint looked like this:

```JSON
{
    "id": 1,
    "firstName": "Bud",
    "lastName": "Spencer",
    "birthDate": "1929-10-31",
    "comment": "cool guy"
}
```

With a self-link the customer resource could look something like this:

```JSON
{
    "key": 1,
    "firstName": "Bud",
    "lastName": "Spencer",
    "birthDate": "1929-10-31",
    "comment": "cool guy",
    "links": [
        {
            "rel": "self",
            "href": "http://localhost:8081/customers/1"
        }
    ]
}
```

Once our service grows we can have more relations between resources and thus can define routes through links. we could for example define that a customer can have an address which could be depicted like this in the link section: 

```JSON
{
    "key": 1,
    "firstName": "Bud",
    "lastName": "Spencer",
    "birthDate": "1929-10-31",
    "comment": "cool guy",
    "links": [
        {
            "rel": "self",
            "href": "http://localhost:8081/customers/1"
        },
        {
            "rel": "address",
            "href": "http://localhost:8081/customers/1/address"
        }
    ]
}
```

Note that many of these concepts are opinionated - and one can discuss a lot what would be a good "architecture" or "application cut". Similarly one can discuss a lot about "resource relations" and how to depict them in a readable yet proper way (according to the relation to other resources). Another discussion point is the huge amount of boiler-plate code that is added by extensively using DTOs. 

In this stage we want to take a look at the HATEOAS concept in general, in the next stage we will show how to extend this concept with the HAL (Hypertext Application Language) standard. 

## Implementation overview

There is no "perfect" way to apply the HATEOAS concept on a service. We provide slightly different implementations in the ```customer``` and the ```accounting``` service to show different possibilities within Spring-HATOAS. In short, the ```customer``` service's DTOs use inheritance to make them HATEOAS resources (include the links) whereas the ```accounting``` service uses generics to wrap the DTO responses with the ```Resource<>``` class. 

### Some words on REST service design

#### Things to consider and best practice links

Designing a good REST interface is not easy - it requires experience. There are several things to consider, here is a list of some points:

* Correct usage of HTTP verbs, return status codes and multimedia types. Generally a good overview can be found in [this Microsoft REST best practice guide](https://docs.microsoft.com/en-us/azure/architecture/best-practices/api-design).
* When to use URL-path-variables ```some/path/{variable}```, and when to use URL-parameters ```some/path?some-param=some-val```, see e.g. this [discussion on stackoverflow](https://stackoverflow.com/questions/3198492/rest-standard-path-parameters-or-request-parameters).
* Use singular or plural for endpoints, e.g. ```some/path/customer``` vs. ```some/path/customers```. Plural is the widely adopted standard now, see [this stackoverflow discussion](https://stackoverflow.com/questions/6845772/rest-uri-convention-singular-or-plural-name-of-resource-while-creating-it). 
* Relationships between resources. Deciding which endpoint should serve what, e.g. ```some/path/customers/{customer-id}/address``` or ```some/path/address?customerid=X```. Other issues can be if one embeds resources into other resources or keeps them "flat".

Note that many of these points are highly opinionated. The general idea is to get a feeling about what is "a good practice" and what is a "bad practice" - it is recommended read through some tutorials for more details!

Note that in the reference solution for stage 11 the endpoint context path was changed to plural names (i.e. ```customers``` and ```accounts``` in the URLs).


#### Some words on readability: Functional vs. Technical Keys

One goal of REST interfaces is to be human-readable. This principle sometimes goes against the traditional "database-view" we might have in our heads. In traditional, relational database we often use IDs - identifiers that are of technical nature. In the simplest cases this is an Integer value that is automatically incremented by the database-engine every time a new object is added. These IDs usually serve as primary key in the database tables. 

In REST, however, we don't want to have technical IDs, but we want to have functional keys. This is not always achievable. In our example it is, in fact, difficult to achieve. A customer cannot simply be identified by a unique criteria, thus the customer-id will most likely be exposed to the consumer of the service. 

For other use-cases it is, however, possible to go with a function identifier rather than a technical identifier. A simple example for a functional key could be the user-name of a user. If the service is designed in a way, that the user-name is a unique criteria to identify a user, then the endpoint path could be depicted as functional key rather than technical. Nevertheless the user can still have a technical key within the back-end; but it is not exposed to consumers of the service.


Examples:

```
User endpoint with technical identifier (Integer/Long):
some/path/users/1

User endpoint with technical identifier (UUID, encoded):
some/path/users/00112233-4455-6677-8899-aabbccddeeff

User endpoint with technical identifier (MongoDB-Hash):
some/path/users/507f191e810c19729de860ea

User endpoint with functional identifier:
some/path/users/some-user-name
```

An advantage of using functional keys is, that they are always valid, no matter what database or technology is behind it. If the service database is to be migrated the consumers of the service won't even notice, because the functional identifiers will stay the same (while the technical identifiers will most likely change).

Note that in some cases using functional keys can be a security risk! Account-IDs (e.g. the IBAN of a bank account) should not be used in paths as the path might be visible to others that could then see the Account-ID. For public, data-driven REST APIs it is, however, a good practice to use functional keys. Generally you should think about this when you design your service.

### General implementation overview for customer and accounting service

First we have have to add the Spring-HATEOAS gradle dependency to both the customer and the accounting build.gradle file:
```
...
dependencies {
    ...
	compile('org.springframework.boot:spring-boot-starter-hateoas')
	...
}
...
```

Since we don't want to deal with HAL just yet we will switch off the HAL representation of the links through a parameters in the configuration (add this to your local ```application.yml``` or to the configuration in the config-server-repository):
```
spring:
  hateoas:
    use-hal-as-default-json-media-type: false
```

Generally, it is not recommended to pass database entities directly to consumers of a service, as it makes the back-end entity design highly dependent on the endpoints and thus the consumers. If an entity in the back-end changes, but the service-interface (on an endpoint) level is not concerned the service interface should not change!
Since we want to decouple the entities from the service we need another layer of objects that will depict service requests and service responses. We call them data transfer objects (DTOs).

For each data-object (resource) that our server should accept (request) or serve (response) we need a DTO now. Furthermore we need a mapping class that converts from request-objects to entity and from entity to response-object. For example, for our customer object we add the following three classes to on the ```rest``` package level of the project:

* ```rest.[some-sub-package].```*CustomerRequest*: Defines what the server accepts when a customer is POSTed to the customer endpoint.
* ```rest.[some-sub-package].```*CustomerResponse*: Defines how the server returns a customer upon GET to the customer endpoint.
* ```rest.[some-sub-package].```*CustomerMapper*: Maps between the database entity (```domain.Customer```) and the CustomerRequest and CustomerResponse.

In the ```rest.CustomerController``` we then replace all the entity classes with the respective DTO. 

In order to add links to our response DTO we have to turn our response into a Spring-HATEOAS resource. We present two different ways to do this in the subsections below. 
Note that the links are added the support of the Spring-HATOAS Link-Builder.


### DTO Implementation in the customer service

In the customer service the turning the response resources into HATEOAS-resources is implemented by inheriting from the class ```org.springframework.hateoas.ResourceSupport```. This class adds the ```links``` to the response objects. 

Example ```CustomerResponse```:

```Java
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CustomerResponse extends ResourceSupport {

    private Integer key;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    private String comment;

}
```

Example mapping code from ```Customer``` entity to ```CustomerResponse``` (includes adding the links):

```Java
@Component
public class CustomerMapper {

	...

    public CustomerResponse fromCustomerToCustomerResponse(Customer customer) {
        return addCustomerResponseLinks(
                CustomerResponse.builder()
                        .firstName(customer.getFirstName())
                        .lastName(customer.getLastName())
                        .birthDate(customer.getBirthDate())
                        .key(customer.getId())
                        .comment(customer.getComment())
                        .build(),
                customer);
    }

    public static CustomerResponse addCustomerResponseLinks(CustomerResponse customerResponse, Customer customer) {
        customerResponse.add(
                linkTo(methodOn(CustomerController.class)
                        .getCustomerById(customerResponse.getKey()))
                        .withSelfRel());

        if (customer.getCustomerAddress() != null) {
            customerResponse.add(linkTo(methodOn(CustomerAddressController.class)
                    .getCustomerAddress(customerResponse.getKey()))
                    .withRel(LinkRelations.ADDRESS.getName()));
        }

        return customerResponse;
    }

}
```

One downside if the inheritance approach is, that the customer-id cannot be depicted as ```id``` field in the response entity, because the ```id``` member is reserved by the ```org.springframework.hateoas.ResourceSupport``` class. There was [a huge discussion about this](https://github.com/spring-projects/spring-hateoas/issues/66) a while ago, but in the end the implementation was not changed. 

One way to work around this - if the technical identifier has to be part of the response resource for whatever reason - is to rename the ```id``` into ```key``` on DTO level. This was done in the reference solution. Another way to deal with this is to use the Resource generics instead of inheritance; as depicted in the implementation of the accounting service.

#### Customer Service Customer-Address endpoint

To show how links can be used in relations between resources a second endpoint was added to the customer service. This customer-address endpoint is depicted as part of the customer endpoint (details in code of the respective stage branch):

```
POST .../customers/{customerId}/address
GET .../customers/{customerId}/address
```

Feel free to depict this in a different way in your solution. Think about a good way to implement this. What if a customer is allowed to have many addresses? 

### DTO Implementation in the accounting service

In the accounting service the resource-link-support is added through the generic ```org.springframework.hateoas.Resource``` class instead of inheriting from ```org.springframework.hateoas.ResourceSupport```.

Example ```AccountRsponse```: 

```Java
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountResponse {

    private AccountType accountType;
    private Integer customerId;

}

```

Example mapping from ```Account``` to ```AccountResponse```:

```Java
@Component
public class AccountMapper {

	...

    public Resource<AccountResponse> fromAccountToAccountResponse(Account account) {

        AccountResponse response = AccountResponse.builder()
                .accountType(account.getAccountType())
                .customerId(account.getCustomerId())
                .build();

        return addAccountLinks(new Resource<>(response), account.getId());
    }

    public static Resource<AccountResponse> addAccountLinks(Resource<AccountResponse> accountResponseResource, Integer accountId) {

        accountResponseResource.add(
                linkTo(methodOn(AccountController.class).getAccountById(accountId))
                .withSelfRel());

        return accountResponseResource;

    }

}

``` 

## Adapting the controller tests

Since the objects are mapped now the tests have to controller tests adapted accordingly. Take a look at the reference solution branch for details. 

Some more details on testing Spring-HATEOAS controllers can for example be found [in this blog entry](https://lankydanblog.com/2017/09/18/testing-a-hateoas-service/) and in the [Spring-HATEOAS documentation](https://docs.spring.io/spring-hateoas/docs/0.19.0.RELEASE/reference/html/#fundamentals.links) (unfortunately not too many examples there).



