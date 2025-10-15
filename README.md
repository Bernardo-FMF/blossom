# Blossom

![blossom-banner.jpg](./docs/assets/blossom_banner.jpg)

- [Overview](#overview)
- [How to run Blossom](#how-to-run-blossom)
- [Documentation](#documentation)

## Overview

Blossom is a fully functional social media network designed as a hands-on implementation of modern microservices and event-driven architecture principles. The goal was to simulate a scalable, distributed system capable of handling real-world social interactions - such as posting content, managing user profiles, following other users, real-time messaging and notifications.

## How to run Blossom

The easiest way to run Blossom is by deploying it using the docker-compose file.
For that there are a few prerequisites to install:
- [Git](https://git-scm.com/downloads)
- [Docker](https://docs.docker.com/desktop/)

Then you also need to clone this repository:

```git clone https://github.com/Bernardo-FMF/blossom.git```

In the root of the repository, create an .env file, and refer to the following table to define the necessary environment variables:

TODO

Now the .env file should be in the same level as the docker-compose.yml file, all that is left is to run this file:

```docker-compose up ```

This requires docker to already be running.
If everything was set up correctly, all the containers should have been created. If there's a microservices that is down, simply restart it and it should work correctly.

## Documentation

Below is a brief introduction to all the microservices that are part of this project, their functionalities and how they relate to each other.

- [Docker containers](#docker-containers)
- [Configuration profiles](#configuration-profiles)
- [Architecture](#architecture)
- [Common utilities and gRPC contracts](#common-utilities-and-grpc-contracts)
- [Discovery server](#discovery-server)
- [Api gateway](#api-gateway)
- [Auth microservice and security practices](#auth-microservice-and-security-practices)
- [Image microservice](#image-microservice)
- [Post microservice](#post-microservice)
- [Activity microservice](#activity-microservice)
- [Social graph microservice](#social-graph-microservice)
- [Feed microservice](#feed-microservice)
- [Notification microservice](#notification-microservice)
- [Message microservice](#message-microservice)

### Docker containers

Each microservice contains a Dockerfile in its root folder, so they can be containerized locally if desired.

Additionally, this project uses websockets with STOMP enabled, so for that I created a [custom RabbitMQ image](https://github.com/Bernardo-FMF/blossom/tree/master/custom-docker-images) with the STOMP plugin enabled.

### Configuration profiles

Each microservice has 3 profiles:
- **Default**: Contains the necessary variables to run the microservice, ranging from the port it runs on to database configuration, among others.
- **Docker**: Uses all the default profile variables but for the discovery server it points to the container hostname instead of localhost.
- **Kubernetes**: Kubernetes deployment files were momentarily removed from this project - will be added in the future.

### Architecture

![architecture.jpg](./docs/assets/architecture.png)

Here we can see the full workflow of the application, and the different dependencies between each microservice.
In the next sections, I will go more in-depth regarding each microservice.

### [Common utilities](https://github.com/Bernardo-FMF/blossom/tree/master/common-utilities) and [gRPC contracts](https://github.com/Bernardo-FMF/blossom/tree/master/grpc-interface)

These modules contain common utilities and contracts used all throughout the project.
Here I defined the gRPC contracts and models, API facades for example to define gRPC servers and clients, the model utilized for kafka messages, and some security utilities.

### [Discovery server](https://github.com/Bernardo-FMF/blossom/tree/master/discovery-server)

Server used to register all microservices included in this project. In this case, using [Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
each client can simultaneously act as a server, to replicate its status to a connected peer. In other words, a client retrieves a list of all connected
peers of a service registry and makes all further requests to any other services through a load-balancing algorithm (Ribbon by default).

### [Api gateway](https://github.com/Bernardo-FMF/blossom/tree/master/api-gateway)

Using [Spring Gateway](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html), this is the entry point of the application.
Here, we can route the requests to the correct microservice, and can even validate if the request should reach its destination, based on if it's a protected route or not.
It also validates JWT, and creates headers that each microservice uses to identify the logged user.

### [Auth microservice](https://github.com/Bernardo-FMF/blossom/tree/master/auth-service) and security practices

### [Image microservice](https://github.com/Bernardo-FMF/blossom/tree/master/image-service)

### [Post microservice](https://github.com/Bernardo-FMF/blossom/tree/master/post-service)

### [Activity microservice](https://github.com/Bernardo-FMF/blossom/tree/master/activity-service)

### [Social graph microservice](https://github.com/Bernardo-FMF/blossom/tree/master/social-graph-service)

### [Feed microservice](https://github.com/Bernardo-FMF/blossom/tree/master/feed-service)

### [Notification microservice](https://github.com/Bernardo-FMF/blossom/tree/master/notification-service)

### [Message microservice](https://github.com/Bernardo-FMF/blossom/tree/master/message-service)