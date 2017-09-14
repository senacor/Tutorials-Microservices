# Hints for Tutorial stage 08

## Overview

In this stage you will use the amazon ECS (EC2 Container Service) to launch mysql databases within amazon AWS instead of your local PC.

Basically you want to create a setup like this:

```
Amazon EC2 VM instance
---------------------------
|                         |
|  ---------------------  |
|  | mysql container 1 |  | >> instance port X maps to container 1 port 3306 (mysql)
|  ---------------------  |
|                         |
|  ---------------------  |
|  | mysql container 2 |  | >> instance port Y maps to container 2 port 3306 (mysql)
|  ---------------------  |
|                         |
---------------------------
```

You launch a virtual machine instance within the EC2 environment, e.g. a T2-micro instance that is part of the free tier (you don't have costs).
Within this instance you want to run two docker containers that use ```mysql``` as docker image (from Docker-Hub).

The most important considerations in this setup are the ports. You will have to open 2 separate ports (X and Y, e.g. 3306 and 3305) on your instance (since you want to provide 2 different database containers). The instance ports will point to the same port on different containers (the mysql port 3306).

The mysql containers have to be configured like in your local setup - the mysql password and database have to be set.

Basically this stage is based on the [amazon ECS tutorial on how to deploy docker containers](https://aws.amazon.com/getting-started/tutorials/deploy-docker-containers/?nc1=h_ls). Note that this tutorial is sometimes very high level - you might have to dive in at certain steps to get the result you want.

## Create a Task definition

The task definition defines your container instances and how they should be launched. You can see it as a JSON specification of a docker-file (in combination with some features from docker-compose).

Your task definition for the two mysql databases might look something like this:

```JSON
{
    "family": "demoapp",
    "containerDefinitions": [
        {
             "name": "customerdb",
             "image": "mysql",
             "memory": 250,
             "cpu": 10,
             "portMappings": [{
                "containerPort": 3306,
                "hostPort": 3306
             }],
             "environment": [
                {
                    "name": "MYSQL_ROOT_PASSWORD",
                    "value": "mysql"
                },
                {
                    "name": "MYSQL_DATABASE",
                    "value": "customerdb"
                }
            ]
        },
        {
             "name": "accountingdb",
             "image": "mysql",
             "memory": 250,
             "cpu": 10,
             "portMappings": [{
                "containerPort": 3306,
                "hostPort": 3305
             }],
             "environment": [
                {
                    "name": "MYSQL_ROOT_PASSWORD",
                    "value": "mysql"
                },
                {
                    "name": "MYSQL_DATABASE",
                    "value": "accountingdb"
                }
            ]
        }
    ],
    "volumes": [],
    "networkMode": "bridge",
    "placementConstraints": []
}
```

Note that when you paste in the JSON taks-definition dialog on amazon ECS, ECS will add several other parameters that will be set to default values automatically.

## Create a Cluster and Service

Once your task definition is defined you have to create a cluster and a service. The service includes the task definition. The cluster runs the service upon startup. The cluster represents the virtual machine instance you will start, while the service is responsible for starting the containers within the instance (cluster) according to the task definition. 

Important: Make sure to open the right ports on your instance (cluster). As said above you will have to open 2 separate ports (X and Y, e.g. 3306 and 3305) within your instance's security group (since you want to provide 2 different database containers). The task definition defines how the instance ports make to the container ports (the mysql port 3306). 

## Configure your local setup to use the cloud databases

Once you have the cluster up and running (check if your task is running correctly in the ECS console) you can retrieve the IP address of your instance by navigating to the cluster's tab "ECS instances". 

Once you retrieved th IP address of your instance you can configure the database-connection string within your configuration files (on the config-server or in the application.yml) of both the customer and the accounting project. Add the IP of the instance and the respective port for the database to the projects.

Note that you will have to remove the databases' service-entries from docker compose (customerdb and accountingdb). Docker compose cannot manage the database containers in this setup, as they are managed by the ECS task definition.

Run and Test the environment:

1. Make sure your ECS instance with the two mysql containers is running. 
2. Start the registry and config services through docker-compose and wait until they are up. 
3. Start the customer and accounting services through docker-compose.
4. You can access your mysql databases in the cloud like this to check if the initial migrations were applied: ```mysql -uroot -pmysql -h [INSTANCE_IP_ADDR] -P [INSTANCE_PORT_THAT_MAPS_TO_DB_CONTAINER]```
5. You should be able to access and create customer/account data like in stage 07.

## Cleanup

After you finished this stage make sure to delete the cluster on amazon ASW again, so you don't run into payments!


