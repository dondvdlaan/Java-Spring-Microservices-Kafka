JAVA SPRING MICROSERVICES CLOUD

This project consists of 3 microservices (product-composite, product-recommendation and product-service) 
and general directory (api) for interfaces and models. 

For the storage of the product, a MongoDB has been added to microservice product-service and 
a MySQL database to microservice product-recommendation.

Summary:
- Java 17
- Spring Boot
- MongoDB and MySQL
- mapstruct mapper
- Docker
- Gradle

Testing:
- start the project with ". docker_up.sh"
- create a product by means of curl on the command line:
 curl -X 'POST'   'http://localhost:8080/composite/addProduct'   -H 'Content-Type: application/json'   -d '{
  "prodID": 40, "prodDesc": "Vierkante erwten", "prodWeight": 15, "trackingID": "<nothingToSeeHere>",
  "recommendations": [{"prodID": 40, "recommID": 1, "recommendation": "Very good", "trackingID": "noTracking"},
{"prodID": 40, "recommID": 2, "recommendation": "Mooi mooi", "trackingID": "weerNoTracking"}
]}'

- retrieve the product by typing in "http://localhost:8080/composite/40" at your browser and
  the response will be a JSON aggregated product