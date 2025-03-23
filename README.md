# Task Management System

## Overview
The Task Management System is a web application designed to manage tasks, users, and projects efficiently. It provides functionalities to assign users to tasks and projects, update user information, and handle various exceptions gracefully.

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

## API Endpoints

### User Management
- **Retrieve all users**
  ```http
  GET /users
  ```
  Response:
  ```json
  [
    {
      "id": "UUID",
      "username": "string",
      "email": "string",
      "roleType": "string"
    },
    ...
  ]
  ```

- **Retrieve a user by ID**
  ```http
  GET /users/{id}
  ```
  Response:
  ```json
  {
    "id": "UUID",
    "username": "string",
    "email": "string",
    "roleType": "string"
  }
  ```

- **Create a new user**
  ```http
  POST /users
  ```
  Request Body:
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string",
    "roleType": "string"
  }
  ```
  Response:
  ```json
  {
    "id": "UUID",
    "username": "string",
    "email": "string",
    "roleType": "string"
  }
  ```

- **Update an existing user**
  ```http
  PUT /users/{id}
  ```
  Request Body:
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string",
    "roleType": "string"
  }
  ```
  Response:
  ```json
  {
    "id": "UUID",
    "username": "string",
    "email": "string",
    "roleType": "string"
  }
  ```

- **Delete a user by ID**
  ```http
  DELETE /users/{id}
  ```
  Response:
  ```json
  {
    "message": "string"
  }
  ```

### Task Management
- **Assign a user to a task**
  ```http
  POST /users/assign-to-task
  ```
  Request Body:
  ```json
  {
    "userId": "UUID",
    "taskId": "UUID"
  }
  ```
  Response:
  ```json
  {
    "message": "string"
  }
  ```

- **Retrieve users assigned to a task**
  ```http
  GET /tasks/{taskId}/users
  ```
  Response:
  ```json
  [
    {
      "id": "UUID",
      "username": "string",
      "email": "string",
      "roleType": "string"
    },
    ...
  ]
  ```

### Project Management
- **Assign a user to a project**
  ```http
  POST /users/assign-to-project
  ```
  Request Body:
  ```json
  {
    "userId": "UUID",
    "projectId": "UUID"
  }
  ```
  Response:
  ```json
  {
    "message": "string"
  }
  ```

- **Retrieve users assigned to a project**
  ```http
  GET /projects/{projectId}/users
  ```
  Response:
  ```json
  [
    {
      "id": "UUID",
      "username": "string",
      "email": "string",
      "roleType": "string"
    },
    ...
  ]
  ```

- **Retrieve tasks assigned to a project**
  ```http
  GET /projects/{projectId}/tasks
  ```
  Response:
  ```json
  [
    {
      "id": "UUID",
      "title": "string",
      "description": "string",
      "status": "string",
      "priority": "string",
      "createdAt": "date-time",
      "updatedAt": "date-time"
    },
    ...
  ]
  ```

- **Retrieve projects by department**
  ```http
  GET /projects/department/{departmentName}
  ```
  Response:
  ```json
  [
    {
      "id": "UUID",
      "name": "string",
      "description": "string",
      "department": "string",
      "status": "string",
      "createdAt": "date-time",
      "updatedAt": "date-time"
    },
    ...
  ]
  ```

- **Retrieve project team**
  ```http
  GET /projects/{projectId}/team
  ```
  Response:
  ```json
  [
    {
      "id": "UUID",
      "username": "string",
      "email": "string",
      "roleType": "string"
    },
    ...
  ]
  ```

- **Retrieve project status**
  ```http
  GET /projects/{projectId}/status
  ```
  Response:
  ```json
  {
    "id": "UUID",
    "status": "string",
    "updatedAt": "date-time"
  }
  ```

## Exception Handling
- **UserNotFoundException**: Thrown when a user with the specified ID is not found.
- **DuplicateRecordException**: Thrown when trying to create a duplicate record.
- **GlobalExceptionHandler**: Centralized exception handling for the application.

## Configuration
### Application Properties
The application properties can be configured in the `src/main/resources/application.properties` file.
