JAVA SPRING MICROSERVICES CLOUD

This project consists of 3 microservices (product-composite, product-recommendation and product-service) 
and general directory (api) for interfaces and models. 

For the storage of the product, a MongoDB has been added to microservice product-service and 
a MySQL database to microservice product-recommendation.

Reactive communication in the form of event streaming have been added between the microservices for the create/add product. For the 
get/read Product action a synchronous none-blocking communication has been applied.

Cloud services like Spring Eureka Discovery, Edge Server Spring Gateway and Spring LoadBalancer have been added.

Summary:
- Java 17
- Spring Boot
- none-blocking REST
- Reactor
- RabbitMQ
- MongoDB and MySQL
- mapstruct mapper
- Spring Eureka Discovery
- Edge Server Spring Gateway
- Spring LoadBalancer
- Docker
- Gradle

Testing:
- start the project with ". docker_up.sh"
- create a product by means of curl on the command line:
  curl -X 'POST'   'http://localhost:8080/composite/addProduct'   -H 'Content-Type: application/json'   -d '{
   "prodID": 40, "prodDesc": "Vierkante erwten", "prodWeight": 15, "trackingID": "<nothingToSeeHere>",
   "recommendations": [{"prodID": 40, "recomID": 1, "recomDescription": "Very good", "trackingID": "noTracking"},
 {"prodID": 40, "recomID": 2, "recomDescription": "Mooi mooi", "trackingID": "weerNoTracking"}
 ]}'

- retrieve the product by typing in "http://localhost:8080/composite/40" at your browser and
  the response will be a JSON aggregated product