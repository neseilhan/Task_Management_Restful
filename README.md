# Task Management System

## Overview
The Task Management System is a web application designed to manage tasks, users, and projects efficiently. It provides functionalities to assign users to tasks and projects, update user information, and handle various exceptions gracefully.

![uml](https://github.com/user-attachments/assets/e24ad8b3-5576-4468-9e0e-29634273275c)



## Features
- **User Management**: Create, update, delete, and retrieve user information.
- **Task Management**: Assign users to tasks, retrieve tasks by various criteria.
- **Project Management**: Assign users to projects, retrieve projects by various criteria.
- **Exception Handling**: Graceful handling of common exceptions like user not found, duplicate records, etc.
- **Security**: Role-based access control for different functionalities.

## Technologies Used
- **Java 11**
- **Spring Boot**
- **Spring Security**
- **Spring Data JPA**
- **H2 Database (for development and testing)**
- **JUnit 5**
- **Mockito**
- **Lombok**
- **Jackson**

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven

### Running the Application
1. Clone the repository:
    ```sh
    git clone https://github.com/neseilhan/task-management-system.git
    cd task-management-system
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    mvn spring-boot:run
    ```

4. The application will start at `http://localhost:8080`.

### Running Tests
To run the tests, use the following command:
```sh
mvn test
```

## Exception Handling
- **UserNotFoundException**: Thrown when a user with the specified ID is not found.
- **DuplicateRecordException**: Thrown when trying to create a duplicate record.
- **GlobalExceptionHandler**: Centralized exception handling for the application.

## Configuration
### Application Properties
The application properties can be configured in the `src/main/resources/application.properties` file.
