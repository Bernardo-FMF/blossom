#!/bin/bash

# Build the Docker image
docker build -t bernardofmf/blossom-custom-rabbitmq-stomp:latest -f RabbitMq-Dockerfile .

# Deploy the Docker image
docker push bernardofmf/blossom-custom-rabbitmq-stomp:latest