#!/bin/bash

#### Check for dependencies ####
if ! [ -x "$(command -v curl)" ]; then
    echo 'Error: curl is not installed.' >&2
    exit 1
fi

if ! [ -x "$(command -v unzip)" ]; then
    echo 'Error: unzip is not installed.' >&2
    exit 1
fi

if ! [ -x "$(command -v docker)" ]; then
    echo 'Error: docker is not installed.' >&2
    exit 1
fi

if ! [ -x "$(command -v docker-compose)" ]; then
    echo 'Error: docker-compose is not installed.' >&2
    exit 1
fi

if ! [ -d infrastructure/orientdb/databases/demodb ]; then
  # Download Orient DemoDB
  echo 'Downloading Orient DemoDB'
  curl https://orientdb.com/public-databases/demodb.zip -o infrastructure/orientdb/demodb.zip

  # Create neccesary folders
  cd infrastructure && cd orientdb && \
  mkdir databases && cd databases && mkdir demodb && \
  cd ..

  # Extract data from archive to binding docker volume folder
  echo 'Extracting Orient DemoDB archive into target docker volumes'
  unzip demodb.zip -d databases/demodb/

  # Remove archive
  rm demodb.zip && cd ../../
fi

# Init network if not already existing
docker network inspect cv &>/dev/null || \
docker network create cv

# Start up docker orientdb container
if ! docker-compose -f infrastructure/docker-compose-orientdb.yml ps | grep -q -e 'orientdb'>/dev/null; then
  echo 'Starting up OrientDB docker container'
  docker-compose -f infrastructure/docker-compose-orientdb.yml up -d
  sleep 10
fi

# Init demo data multitenant partitioning
# Check firstly if the changes have been already applied
if ! docker-compose -f infrastructure/docker-compose-orientdb.yml exec orientdb /orientdb/bin/console.sh \
'CONNECT remote:localhost/databases/demodb root rootpwd;SELECT IsMultitenant FROM DBInfo;DISCONNECT' \
| grep -q -e 'true' > /dev/null; then
  echo 'Initializing OrientDB demo data multitenant partitioning'
  docker-compose -f infrastructure/docker-compose-orientdb.yml exec orientdb /orientdb/bin/console.sh \
  /orientdb/scripts/init.sql
fi

# Start up docker app container
echo 'Starting up REST Multitenant app docker container'
docker-compose up -d
