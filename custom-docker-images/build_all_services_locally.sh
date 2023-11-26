#!/bin/bash

cd ..

mvn clean install

for service in activity-service api-gateway auth-service discovery-server feed-service image-service message-service notification-service post-service social-graph-service; do
  echo "building docker image for $service"
  docker build -t bernardofmf/blossom-$service:latest -f ./$service/Dockerfile ./$service/
done