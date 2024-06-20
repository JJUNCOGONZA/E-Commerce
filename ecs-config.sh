#!/bin/bash

set -e

# Establecer la URL del endpoint de LocalStack
ENDPOINT_URL="http://localhost:4566"

# Crear el cluster ECS
aws --endpoint-url=$ENDPOINT_URL ecs create-cluster --cluster-name e-commerce-cluster

# Registrar la tarea para product-catalog
aws --endpoint-url=$ENDPOINT_URL ecs register-task-definition --cli-input-json '{
  "family": "product-catalog-task",
  "networkMode": "awsvpc",
  "containerDefinitions": [
    {
      "name": "product-catalog",
      "image": "ja5on96/product-catalog:latest",
      "essential": true,
      "memory": 512,
      "cpu": 256,
      "portMappings": [
        {
          "containerPort": 8089,
          "hostPort": 8089
        }
      ]
    }
  ]
}'

# Registrar la tarea para order-service
aws --endpoint-url=$ENDPOINT_URL ecs register-task-definition --cli-input-json '{
  "family": "order-service-task",
  "networkMode": "awsvpc",
  "containerDefinitions": [
    {
      "name": "order-service",
      "image": "ja5on96/order-service:latest",
      "essential": true,
      "memory": 512,
      "cpu": 256,
      "portMappings": [
        {
          "containerPort": 8090,
          "hostPort": 8090
        }
      ]
    }
  ]
}'

# Crear el servicio para product-catalog
aws --endpoint-url=$ENDPOINT_URL ecs create-service --cluster e-commerce-cluster --service-name product-catalog-service --task-definition product-catalog-task --desired-count 1 --network-configuration "awsvpcConfiguration={subnets=['subnet-12345678'],securityGroups=['sg-12345678']}"

# Crear el servicio para order-service
aws --endpoint-url=$ENDPOINT_URL ecs create-service --cluster e-commerce-cluster --service-name order-service --task-definition order-service-task --desired-count 1 --network-configuration "awsvpcConfiguration={subnets=['subnet-12345678'],

