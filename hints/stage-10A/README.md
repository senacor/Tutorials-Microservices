# Hints for Tutorial stage 10.A

## Overview

In this stage you should gather knowledge about scaling you application on the application level (load balancing) as well as on the data level (database replication). For grasping those concepts it is important understand the implications of your architectural choices on the concepts.

## Background Knowledge

1. You should understand the concept of [availability zones](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-regions-availability-zones.html).
2. You should understand the concept of [load balancing]()
3. You should understand the concept of [database replication]()

## Our current architecture

Our current architecture is not prepared to be "cloud ready". The fact that we deploy all services into one instance is just one example that would not help the application's scalability. 

The idea is, that you research the topics listed above and think about how to change the current architecture and setup, think about:

1. Scalability: Which services should scale up and how?
2. Fault-Tolerance: What if a service is not available?


## Towards a cloud architecture

### Deployment Strategies and their implication

Our current strategy is based on the ["multiple services instances per host"](http://microservices.io/patterns/deployment/multiple-services-per-host.html) pattern - in fact all of our services are deployed on the same host. 

Especially for the databases our current strategy won't work out long. If we add a load balancer and bring up a new instance it will also bring up duplicates of the databases. Since we did not think of any data-synchronization mechanism the data would simple be split into different databases without synchronization. This definitely has to be changed.

If we would deploy our application using the ["single service instance per host"](http://microservices.io/patterns/deployment/single-service-per-host.html) pattern, we would have more flexibility when scaling up. If we do so, we can think about our discovery server (Eureka) - do we actually need it in such a setup? The load balancer will basically serve as a service discovery already.

### Data Replication Strategies

With data replication strategies you can make your data-access more scalable.
Basically what you do is: You start more database instances and replicate the data between them so you can access the data from several instances instead of one. When it comes to database replication strategies you will have to look into the documentation of your database engine. Most engines offer replication like master-slave or master-master. For details your can refer to the [MySQL documentation on database replication](https://dev.mysql.com/doc/refman/5.7/en/replication.html). In this tutorial we will not go into depth here.

One strategy that solves all of your database scale-up issues with a finger-snap is to use cloud data storage. You don't have to care about databases as instances any more, but the service will scale automatically for you. On AWS there are several [cloud database](https://aws.amazon.com/products/databases/) and [cloud storage](https://aws.amazon.com/products/storage/) solutions available.
Even though this is a nice solution you should bare in mind: The more cloud-services you use in your application, the more you get dependent on the platform. This is not necessarily a bad thing, but you should keep it in mind. If you start using a lot of amazon cloud services it will be more complicated to move to another platform for hosting your application. If you stick to docker-containers and manage them yourself it will be easier to migrate.


### Load Balancing Strategies

#### Load Balance in General

#### Load Balancing on AWS

Distinguishing different EC2 load balancers:

1. Classic load-balancer:
2. Application load-balancer:
3. Elastic load-balancer:

If you want to get more into depth one good starting point is this profound [article on load balancing and load-balancer-testing](https://aws.amazon.com/articles/1636185810492479).


