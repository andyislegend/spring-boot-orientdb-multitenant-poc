#!/bin/bash

# Stop docker orientdb container
echo 'Stopping OrientDB docker container'
docker-compose -f infrastructure/docker-compose-orientdb.yml stop

# Stop docker app container
echo 'Stopping REST Multitenant app docker container'
docker-compose stop
