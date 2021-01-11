# Ecom-App

## Problem Statement

A modern ecommerce platform that has many sets of microservices: customer, order, product,
inventory, payment, shipping/delivery etc.
For this exercise, you are going to focus on the order microservice using Spring Boot.
Please read the [Ecom-Order-Processing-Service.pdf](ecommerce-order-processing-service.pdf) for details.

## Requirements

1. Use Java8 (or higher version of Java), or Kotlin for the programming language.
2. Spring Boot, Spring Data JPA, and other Spring modules for the microservices.
3. Design new schema keeping PostgreSQL in the mind.
4. For the RESTful API, use Swagger to document your endpoints. Use appropriate HTTP verbs and status codes. Assume no AuthN or AuthZ is required for the API.
5. Implement unit and/or integration tests.
6. Containerize your services using Docker. Use docker-compose if required.
7. If you are thinking about async communication patterns, use Apache Kafka.
8. See, if you can follow few of the 12 Factor App Principles (https://12factor.net).

## Infrastructure:

The project is deployed using Docker-compose and has the following Services:

### Components:

    - Order-Service:
    	-  Microservice based on Spring Boot.
    - Database:
    	-  PostgreSQL database
    - Kafa:
    	-  Stream-processing software platform
    - Zookeeper:
    	-  Centralized service for maintaining configuration information and Kafka Cluster
    - KafkaDrop:
		-  A web UI for viewing Kafka topics and browsing consumer groups.

## PreReq tools that you need

1. `Docker Desktop`
2. `git`
3. `IDE`

## Assumptions

1. Order service is being called by a valid user having a valid user ID.
2. The User session is maintained and validated using Using OIDC by an agregator service or an API Gateway.
3. We are assuming there are separate services for catalogue (which will handle crud operations relevant to objects such as items dealers, warehouse), Payment service (which will maintain trasanction related intricate details and card information), Shipping Services.
4. In-place of these services we are calling json stubs for fetching neceessary data.

## Steps to run the project Using Docker-compose

To run all services use the following command.
```bash
docker-compose up -d
```

You can run services individually by running the following command with the service name
```bash
docker-compose up -d <service-name>
```

## Steps to run to on IDE

0. Pre-req: To run order-service using IDE, you need to first configure PostgreSQL and Kafka locally or run these services individually using dockeer-compose command mentioned above.
1. Insall IDE (preferrably [Eclipse](https://www.eclipse.org/downloads/packages/release/helios/sr1/eclipse-ide-java-developers))
2. Clone the repository using [http](https://github.com/rishijatania/Ecom-App.git) or [ssh](git@github.com:rishijatania/Ecom-App.git)
3. Open the spring project in the IDE
4. Run project as "Spring Boot App"

## To test the api service locally
 - Use any API testing tool (Example: Postman) or hit url http://localhost:8080/swagger-ui/ then click on an api spec and 'Try it out' button.  


## URLS
1. [Order-service](https://localhost:8080/api/v1/orders)
2. [Swagger](http://localhost:8080/swagger-ui/)
3. [KafkaDrop](http://localhost:9000/)

## Steps to run unit test cases

1. Run the project as JUnit test cases by right click and run as "JUnit test cases"
2. By command line use `mvn test`