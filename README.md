# ManageTask Application

This is a Spring Boot application for managing tasks. The application includes user authentication and task management features.

## Prerequisites

- Java 17
- Docker
- Gradle

## Building the Project

To build the project in local, run the following command:

```sh
./gradlew build
```
Running the Application Locally
To run the application locally, use the following command:
```sh
./gradlew bootRun
```
The application will be available at http://localhost:8080.

Dockerizing the Application
To build and run the Docker container, follow these steps:

Step 1: Build the Project
Before building the Docker image, you need to build your Spring Boot project to generate the JAR file. Run the following command:
```sh
./gradlew build
```
Step 2: Build the Docker Image
Build the Docker image using the Dockerfile:
```sh
docker build -t managetask-app .
```
Step 3: Run the Docker Container
Run the Docker container:
```sh
docker run -p 8080:8080 managetask-app
```
The application will be available at http://localhost:8080.

API Documentation
The API documentation is available via Swagger UI at http://localhost:8080/swagger-ui.html.

Running Tests
To run the tests, use the following command:
```sh
./gradlew test
```
