# Hints for Tutorial stage 11

## Overview

In this stage you don't manage the MySQL databases of your services by yourself (in docker containers), but you let amazon manage the databases for you using the RDS (Relational Database Service). Furthermore you hide the database using a VPC, so it is not publicly available.

## Understanding VPCs

Now you reached a point where you should start to think about the security of your application and the different security layers offered by the AWS platform. The following steps are based on the tutorial on ["Working with Amazon RDS DB Instances in a VPC"](http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.WorkingWithRDSInstanceinaVPC.html). You can stick closely to the steps in that tutorial and dive deeper when wanted.

### Creating your own VPC (optional)

**Important:** This step is optional! If your account has a default VPC we recommend that you use that default VPC which includes the necessary amount of subnets and the DB subnet group linked to it. If you want to learn more about VPC by creating your own VPC you can go on in this section.

Setup a [VPC (Virtual Private Cloud)](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-vpc.html) for your application, that contains a private subnet (for the database access) and a public subnet for accessing the ECS cluster. From the outside, the database created using the RDS (Relational Database Service) should not be accessible.

1. Navigate to the VPC console by selecting "VPC" in the "Networking & Content Delivery" section.
2. Use the "Start VPC Wizard" button to create a VPC.
3. Chose "VPC with Public and Private Subnets"
4. You will have to [allocate an Elastic IP](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/elastic-ip-addresses-eip.html#using-instance-addressing-eips-allocating) for using the NAT gateway or launch a NAT instance.
5. [Add subnets in multiple availability zones](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/VPC_Subnets.html) to your VPC that meet the availability zone requirements for creating the database instances using RDS. You can do this in the VPC Management Console under "Subnets".
6. Create a DB subnet group that is associated with your VPC in the RDS console before you can create the RDS instance.
7. [Create security groups for the VPC](http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_Tutorials.WebServerDB.CreateVPC.html#CHAP_Tutorials.WebServerDB.CreateVPC.SecurityGroupDB) before you create the RDS instance.

For details on how to create and configure the VPC please refer to step 1 - 3 of the ["Working with Amazon RDS DB Instances in a VPC"](http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.WorkingWithRDSInstanceinaVPC.html) tutorial.

## Creating MySQL databases managed by RDS and VCS

1. Navigate to the RDS console by selecting "RDS" in the "Database" section of your AWS account.
2. Launch a "MySQL" instance; select the "Community Edition" and run it as "Dev/Test".
3. Choose the smallest instance, and leave the default values.
4. Assign the instance to the default VPC of your account or the VPC you created.
5. Don't allow public access to the instance.
6. Configure the database settings according to the service (customer or accounting).

Note: You will have to do this for both the customer and the accounting service. Don't forget to fill in the database name (customerdb and accountingdb). 

Note: You will have to use a password with at least eight characters - don't forget to change that in the configuration later if you have a different one set there.

Note: Instead of a MySQL instance you could also create an Aurora instance, but that will cost you more. 

## Configure docker-compose and generate new task definition

1. Remove the database services from your ```docker-compose.yml```.
2. Alter the configuration file for accounting and customer on the config-server (config-server repo) so they point to the MySQL databases managed by RDS. You will have to put the endpoint of the RDS database in the connection string and configure the correct credentials to sign into the database server.
3. Generate a new task definition:

```
ecs-cli compose --project-name demo-app --file docker-compose.yml create
```

Note: If the path (branch) to your config-repo changes you will have to alter the path configuration in the bootstrap configuration, rebuild the project, rebuild the customer and accounting container and provide the containers with the right bootstrap configuration on docker-hub. 

## Configure and run a cluster

Like before you run a cluster and create a service within that cluster that uses the new task definition. However, your cluster setup must now take into consideration that your databases run on other EC2 instances!

**Important:** The cluster's [VPC (Virtual Private Cloud)](https://aws.amazon.com/vpc/) settings have to match the VPC of the RDS instance! Furthermore, since we don't want to allow public access to our database, we have to link the security group of the ECS container instance to the security group of the RDS instance. To achieve this:

1. Navigate to the RDS instance and go to details. You will see the VPC settings there.
2. Create the cluster and select the VPC that matches the RDS instance's VPC.
3. Add all of the subnets of the VPC to the cluster.
4. Configure the rest of the cluster (open port 8081 and 8082).
5. Launch the cluster.

This might be necessary if you used the default VPC (if you created your own VPC including security groups this should not be necessary, because the security groups are already linked):

6. Retrieve the security group ID of the ECS cluster instance.
7. Fill in the security group ID of the ECS cluster instance into the RDB instances' security groups.

Note: More information and screenshots can be found in the [Bitnami tutorial on ECS and RDS](https://docs.bitnami.com/aws/how-to/ecs-rds-tutorial/#step-23-set-up-amazon-ecs); this tutorial uses the default VPC. 

### Troubleshooting

If you do not configure the ECS cluster in the same VPC the container will not be able to resolve the connection string for the database correctly. One way to check if it is working is to access the container logs on your instance.

#### Access the container logs

You have to adapt the security group of the cluster's instance to allow SSH (port 22). The cluster has to be configured to allow access through an SSH key. You can then SSH into the cluster and check the container logs as described in stage 10.

#### Access the database

If you have problems accessing the customer service's data (or accounting service) make sure the tables were created in the database.
You can change the database to be accessed publicly and then access the MySQL databases like this to check if the tables were configured correctly correctly:
```
mysql -u[USER_NAME] -p[PASSWORD] -h [CONNECTION_STRING_RDS_INSTANCE] -P 3306
```

If the tables were not created upon startup this most likely means that the connection string to the database was not resolved correctly. You can check the log output of the containers upon startup by allowing the SSH into the containers (make sure to open port 22 in the security group of your cluster's EC2 instance). If the customer/accounting service hangs at the Flyway initialization step that means the database cannot be resolved. Check your clusters VPC settings then and make sure everything is configured correctly.

Note: Of course you can also allow all traffic (0.0.0.0/0) to the DB instances. Then you don't have to care about VPC and security group linking but your setup is not secure!

## Cleanup

1. Delete the database instances
2. Unlink the security group of the cluster instance from the security group of the RDS instances (the security group of the RDS database instance is not removed automatically), otherwise you might not be able to delete the cluster properly, because of security-group dependencies.
3. Delete the cluster
4. Optional: Delete the VPC you created.
