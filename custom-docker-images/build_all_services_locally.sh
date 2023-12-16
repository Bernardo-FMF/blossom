#!/bin/bash

display_usage() {
  echo "Usage: $0 [-b]"
  echo "Options:"
  echo "  -b  Perform 'mvn clean install' before building docker images"
}

if [ "$1" == "-h" ] || [ "$1" == "--help" ]; then
  display_usage
  exit 1
fi

perform_maven=false

if [ "$1" == "-b" ]; then
  perform_maven=true
fi

cd ..

if [ "$perform_maven" = true ]; then
  echo "Performing 'mvn clean install'..."
  mvn clean install
else
  echo "Building Docker images without 'mvn clean install'..."
fi

for service in activity-service api-gateway auth-service discovery-server feed-service image-service message-service notification-service post-service social-graph-service; do
  echo "building docker image for $service"
  docker build -t bernardofmf/blossom-$service:latest -f ./$service/Dockerfile ./$service/
done