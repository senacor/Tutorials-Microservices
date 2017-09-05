# Hints for Tutorial stage 09

## Overview

This is basically the same setup as in stage 07 or stage 07.A (you can also use stage 08 and run the databases in the amazon cloud), but instead of running your containers directly after building them on the local machine you upload them to Docker-Hub so they are available in the cloud. 

The idea of this stage is, that you run the complete setup locally on your machine (but with all containers pulled from the cloud); in the next stage you can then port the docker-compose file to the amazon ECS (EC2 containers service) and run the complete setup in the cloud.

## Bringing the containers to Docker-Hub

At this point it is assumed that you already have a Docker-Hub account - if not [signup on Docker-Hub](https://hub.docker.com/).

This tutorial is based on the [Docker documentation on pushing repository-images (container images) to docker-hub](https://docs.docker.com/docker-hub/repos/#pushing-a-repository-image-to-docker-hub).

### Project setup and configuration

Basically you can stick to the the project setup of stage 07 (or stage 07.A). The docker-compose file should be the complete local setup, with the services being linked together by name.

Note that it is also possible to use stage 08 for the setup, but it is not recommended because you have to change more in stage 09 then.

### Bringing your containers to Docker-Hub

#### Build the images and tag them correctly

All the images that you have a context.build configured in docker-compose will have to be built or re-tagged. 

Recommended workflow (exmple for customer-container and image):

1. Remove the container and the container-image (if exists): ```docker rm customer``` and ```docker rmi customer```
2. Navigate to the customer folder and run: ```docker build -t [YOUR_DOCKERHUB_UNAME]/customer:[TAG_YOU_CHOOSE] .```
3. You should get a success message once the container-image was built.

Note: We use ```stage-09``` as tag - basically you can choose any tag. Typically one would use versioning-schemas for tags.

Note: You can also just re-tag your existing containers (given that they contain the right configuration) and push them, see the [Docker documentation on pushing repository-images (container images) to docker-hub](https://docs.docker.com/docker-hub/repos/#pushing-a-repository-image-to-docker-hub) for details.

#### Push images to docker

Login to your Docker-Hub account in your browser and create a repository for every container-image you want to push; for the customer service you can create a ```customer``` repository which will be accessible like this:

```
[YOUR_DOCKERHUB_UNAME]/customer
```

To push your container-images to your Docker-Hub repository you first have to login to docker in your terminal:

```
docker login
```

You will have to enter your Docker-Hub user name and password. You will get a success message once the login worked correctly.


Then you can push the container like this:

```
docker push [YOUR_DOCKERHUB_UNAME]/customer:[TAG_YOU_CHOOSE]
```

The senacor container-images (created from the stage-09 tutorial code) can be found [here](https://hub.docker.com/u/senacortutorials/).

## Running the project locally with cloud container-images

You will have to configure your ```docker-compose.yml``` to use cloud container-images only.

Instead of defining a build context and local image you should define the cloud container you want to use. 

As an example we use the customer service configuration from docker-compose - you have to replace this
```YAML
  customer:
    build:
      context: ./customer
    image: customer
```

by this:
```YAML
    image: [YOUR_DOCKERHUB_UNAME]/customer:[TAG_YOU_CHOOSE]
```

Once you have configured all containers to be pulled from the cloud you can start the application as before:

1. Open a terminal and run: ```docker-compose up customerdb accountingdb```
2. Wait until customerdb and accountingdb started successfully.
3. Open another terminal and run: ```docker-compose up config registry```
4. Wait until config and registry started successfully.
5. Open another terminal and run: ```docker-compose up customer accounting```

The behavior should be exactly the same as for stage 07 since you basically run the same containers, but they are available in the cloud.



