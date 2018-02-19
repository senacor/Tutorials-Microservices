# Hints for Tutorial stage 12

## Overview

In stage 11 we took a look at HATEOAS (Hypermedia as the Engine of Application State). In stage 12 we want to extend our HATEOAS implementation by making it compatible to HAL (Hypertext Application Language) standard. 

Note that the HAL standard is currently still in a proposal phase and not an accepted standard yet; check out this [overview](http://stateless.co/hal_specification.html) and the [latest version of the standard's proposal](https://tools.ietf.org/html/draft-kelly-json-hal-08) for details.

Through the HAL standard links are defined according to a specified structure. This helps to create standardized RESTful APIs over the borders of applications. However, since the standard is not complete yet, some questions remain open or are not specified completely yet (like nesting entities on multiple levels).

## Implementation 

In order to be HAL compliant we first have to remove the parameter that surpressed HAL-compliant link creation in stage 11 from the configuration (remove both in main and test config):

```YAML
spring:
  hateoas:
    use-hal-as-default-json-media-type: false

```

After removing your parameter you will already see, that the link-style changed - when retrieving a customer by id it now looks like this:

```JSON
{
    "key": 1,
    "firstName": "Bud",
    "lastName": "Spencer",
    "birthDate": "1929-10-31",
    "comment": "cool guy",
    "_links": {
        "self": {
            "href": "http://localhost:8081/customers/1"
        }
    }
}
```

### Dealing with collections

When retrieving the list of resources from the "customer search" endpoint, the links are not HAL compliant, but are still depicted as in stage 11:

```
GET http://localhost:8081/customers?lastName=Spencer
```

```JSON
[
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
]
```

This is because we return a ```java.util.List``` there that cannot be interpreted by Spring HATEOAS correclty. Instead of using the List we have to wrap our entities with the ```Resources<>``` class provided by Spring HATEOAS:

```JAVA
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Resources<CustomerResponse> getCustomersByName(
            @RequestParam(value = "lastName", defaultValue = "", required = false) String lastName) {

        return new Resources<>(
                customerService.findCustomersByLastName(lastName)
                        .stream()
                        .map(customer -> customerMapper.fromCustomerToCustomerResponse(customer))
                        .collect(Collectors.toList()));
    }
```

This will result in a HAL compliant representation:

```JSON
{
    "_embedded": {
        "customerResponseList": [
            {
                "key": 1,
                "firstName": "Bud",
                "lastName": "Spencer",
                "birthDate": "1929-10-31",
                "comment": "cool guy",
                "_links": {
                    "self": {
                        "href": "http://localhost:8081/customers/1"
                    }
                }
            }
        ]
    }
}
```

If you don't like the representation as ```customerResponseList``` you can easlily adapt this by adding a ```@Relation``` mapping to your response objects. Here is an example for the customer response:

```JAVA
@AllArgsConstructor
@Getter
@Setter
@Builder
@Relation(value = "customer", collectionRelation = "customers")
public class CustomerResponse extends ResourceSupport {

    ...

}
```

The result looks like this:

```JSON
{
    "_embedded": {
        "customers": [
            {
                "key": 1,
                "firstName": "Bud",
                "lastName": "Spencer",
                "birthDate": "1929-10-31",
                "comment": "cool guy",
                "_links": {
                    "self": {
                        "href": "http://localhost:8081/customers/1"
                    }
                }
            }
        ]
    }
}
```

Note that collections are treated as ```_embedded``` resources in HAL. The ```_embedded```  object contains the actual data of the collection (including links to the respective resource). The collection itself also specifies links. In the simplest cases this is a link to the collection itself, but more complex examples (e.g. specifying ```previous``` and ```next```) are possible, as described in [this tutorial](https://knpuniversity.com/screencast/rest-ep2/hal-collection).  


### Adding a self-reference-link to the resource collection

Now we can add a "search link" to the collection response. This might causes problems tough.

In the customer service we specified the search for a customer by ```lastName``` directly on the base-path (context path) URI:

```
/customers?lastName={someLastName}
```

This, however, is problematic for Spring-HATEOS' ```ControllerLinkBuilder``` as described in [this issue](https://github.com/spring-projects/spring-hateoas/issues/434). You might run into this problem in the ```CustomerController``` in method ```getCustomerByName```:

```JAVA
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Resources<CustomerResponse> getCustomersByName(
            @RequestParam(value = "lastName", defaultValue = "", required = false) String lastName) {

        ...
    }
```

This search endpoint specifies a request parameter directly under the base-path (context path) ```/customer```.

When trying to build the link like this:

```JAVA
...
resources.add(linkTo(methodOn(CustomerController.class).getCustomersByName(lastName)).withSelfRel());
...
```

an Excelption will be thrown upon GET requests:

```JSON
{
    "timestamp": "2018-02-19T14:26:54.817+0000",
    "status": 500,
    "error": "Internal Server Error",
    "exception": "java.lang.IllegalArgumentException",
    "message": "'uriTemplate' must not be null",
    "path": "/customers/"
}
```

The problem is, that the base path is not handled correctly, as Spring-HATEOAS adds a slash to the base-path (context path). 

For the reference solution we provide a simple workaround for this problem by extending the path for he method explicitly like this:

```
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public Resources<CustomerResponse> getCustomersByName(
            @RequestParam(value = "lastName", defaultValue = "", required = false) String lastName) {

        Resources<CustomerResponse> resources = new Resources<>(
                customerService.findCustomersByLastName(lastName)
                        .stream()
                        .map(customer -> customerMapper.fromCustomerToCustomerResponse(customer))
                        .collect(Collectors.toList()));

        resources.add(linkTo(methodOn(CustomerController.class).getCustomersByName(lastName)).withSelfRel());

        return resources;
    }

```

The correct result is then returned for a GET request for ```customers/search?lastName=Spencer```:

```JSON
{
    "_embedded": {
        "customers": [
            {
                "key": 1,
                "firstName": "Bud",
                "lastName": "Spencer",
                "birthDate": "1929-10-31",
                "comment": "cool guy",
                "_links": {
                    "self": {
                        "href": "http://localhost:8081/customers/1"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8081/customers/search?lastName=Spencer"
        }
    }
}
```

### Adapting the test cases

Since the JSON representation of the response resources changed due to introducing the HAL standard the test cases have to be adapted accordingly. 



