#!/bin/bash
cd travel-planner-backend
git pull
cd containerization/tp_test/
docker-compose build
docker-compose down
docker-compose up -d
docker ps
docker rmi $(docker images -f "dangling=true" -q)