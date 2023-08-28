# Filename: docker_up.sh
# This file should be sourced

#! usr/bin/bash
echo "Docker up"

chmod u+x docker_up.sh

#cd idea-IU-231.8109.175/bin

#./gradlew build --info
./gradlew build -x test
docker compose build
docker compose up -d
docker ps -a


pwd
echo $$

#cd


