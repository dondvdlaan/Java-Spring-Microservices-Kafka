# Filename: git_update.sh
# This file should be sourced

#! usr/bin/bash
echo "update to git"

chmod u+x git_update.sh

#cd idea-IU-231.8109.175/bin

cd ..
git add .
git commit -m "Adding Eureka and Gateway"
git push Java-Spring-Microservices-Kafka
cd Spring-Cloud


pwd
echo $$

#cd


