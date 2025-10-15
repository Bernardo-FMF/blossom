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

![auth-architecture.jpg](./docs/assets/auth-architecture.png)

The auth microservice handles all authentication and authorization flows of the application.
#### &#10022;Features&#10022;
- User registration;
- User login;
- User logout (specific session or all sessions);
- Email verification;
- Email change;
- Password change;
- JWT validation and refresh;
- Enable and disable MFA, and also MFA code validation;
- Delete account;
- Profile image change;
- User search;

Notes:
- All requests that sent to the gateway that require the user to be authenticated, will send a request to the auth microservice to validate the JWT.
- If a user has MFA enabled and tries to login, the response will only be the user model. The client needs to check a flag of that model to know if MFA is enabled and so to obtain a JWT, a new request needs to be sent to validate the MFA code using the chosen application for authentication. 
- When a user is created, a kafka message is propagated to the different microservices that require this information, as shown in the above diagram.

### [Image microservice](https://github.com/Bernardo-FMF/blossom/tree/master/image-service)

![image-architecture.jpg](./docs/assets/image-architecture.png)

The image microservice exposes a gRPC server that handles the upload and delete of images.
It's an internal microservice used by the auth and post microservices.

- The image upload is handled using a client streaming RPC, where the client sends blocks of bytes that compose the image instead of the full image in one go. When the client completes the streaming and the server finishes writing the image, the server generates the identifier of that image which is just the URL the client can use to access the image.
  - There are 2 ways that images are stored, depending on the environment configuration:
    - AWS S3 bucket;
    - Disk;
- The image delete is handled using a unary RPC using the previously mentioned identifier to delete the image from the S3 bucket or from disk.

### [Post microservice](https://github.com/Bernardo-FMF/blossom/tree/master/post-service)

![post-architecture.jpg](./docs/assets/post-architecture.png)



### [Activity microservice](https://github.com/Bernardo-FMF/blossom/tree/master/activity-service)

![activity-architecture.jpg](./docs/assets/activity-architecture.png)



### [Social graph microservice](https://github.com/Bernardo-FMF/blossom/tree/master/social-graph-service)

![social-graph-architecture.jpg](./docs/assets/social-graph-architecture.png)



### [Feed microservice](https://github.com/Bernardo-FMF/blossom/tree/master/feed-service)

![feed-architecture.jpg](./docs/assets/feed-architecture.png)



### [Notification microservice](https://github.com/Bernardo-FMF/blossom/tree/master/notification-service)

![notification-architecture.jpg](./docs/assets/notification-architecture.png)



### [Message microservice](https://github.com/Bernardo-FMF/blossom/tree/master/message-service)

![message-architecture.jpg](./docs/assets/message-architecture.png)


