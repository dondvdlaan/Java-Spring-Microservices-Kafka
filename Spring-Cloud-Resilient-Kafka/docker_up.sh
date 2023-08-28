# Filename: docker_up.sh
# This file should be sourced

#! usr/bin/bash
echo "Docker up"

chmod u+x docker_up.sh

#cd idea-IU-231.8109.175/bin

#./gradlew build --info
./gradlew build -x test
# choose between rabbitmq or kafka
export COMPOSE_FILE=docker-compose-kafka.yml
docker compose build
docker compose up -d
#docker compose up -d --scale recommendation=3
docker ps -a


pwd
echo $$

#cd


