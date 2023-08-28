# Filename: docker_up_per_service.sh
# This file should be sourced

#! usr/bin/bash
echo "Docker up per service"

chmod u+x docker_up_per_service.sh

#cd idea-IU-231.8109.175/bin

#./gradlew build --info
./gradlew :microservices:product-service:build -x test
cd microservices/product-service
docker build -t product-service .
#docker run --rm -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" product-service
cd ..
cd ..
docker compose build
docker compose up -d
docker ps -a


pwd
echo $$

#cd


