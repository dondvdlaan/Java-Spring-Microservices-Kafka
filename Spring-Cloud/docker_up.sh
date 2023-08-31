# Filename: docker_up.sh
# This file should be sourced

#! usr/bin/bash
echo "Docker up"

chmod u+x docker_up.sh

#cd idea-IU-231.8109.175/bin

#./gradlew build --info
./gradlew build -x test
docker compose build
# Spinning up 3 recommendation instances
docker compose up -d --scale recommendation=3
docker ps -a


pwd
echo $$

#cd


