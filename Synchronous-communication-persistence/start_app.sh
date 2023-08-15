# Filename: start_app.sh
# This file should be sourced

#! usr/bin/bash
echo "Starting app"

chmod u+x start_app.sh

#cd idea-IU-231.8109.175/bin

#./gradlew build --info
./gradlew build -x test
java -jar microservices/product-service/build/libs/*.jar &
java -jar microservices/product-composite/build/libs/*.jar &
java -jar microservices/product-recommendation/build/libs/*.jar &


pwd
echo $$

#cd


