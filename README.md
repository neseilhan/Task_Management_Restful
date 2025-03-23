# Task Management System

## Overview
This project, which includes elements such as project, user, task, implements a job and task management. Users can access processes according to certain roles. Projects and tasks have progress and priority statuses. Access is provided at the security layer with roles such as project manager, team leader, team member.

![task_management_clickup](https://github.com/user-attachments/assets/7c3a3a23-2c58-4aca-97d8-157ee32f8649)


## Features
- **User Management**: Create, update, delete, and retrieve user information.
- **Task Management**: Assign users to tasks, retrieve tasks by various criteria.
- **Project Management**: Assign users to projects, retrieve projects by various criteria.
- **Comment Management**: Allows management of comments made to tasks.
- **Attachment Management**: Provides management of files added to tasks (text or image).
- **Exception Handling**: Graceful handling of common exceptions like user not found, duplicate records, etc.
- **Security**: Role-based access control for different functionalities.

## Technologies Used
- **Java 21**
- **Spring Boot 3**
- **Spring Security**
- **Spring Data JPA**
- **PostgreSql**
- **Jacoco**
- **JUnit 5**
- **Mockito**
- **Lombok**
- **Jackson**

## Getting Started

### Prerequisites
- Java 21 or higher
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

# API Endpoints

## Authentication
- **POST /auth/register**: Creates a new user account.
- **POST /auth/login**: Logs in a user and returns authentication details.
- **POST /auth/logout**: Logs out the current user.

## Attachments
- **POST /attachments/task/{taskId}**: Adds an attachment to the specified task ID and returns the newly added file.
- **GET /attachments/task/{taskId}**: Retrieves all attachments related to the specified task ID.
- **DELETE /attachments/{attachmentId}**: Deletes the specified attachment.

## Comments
- **POST /comments/task/{taskId}/user/{userId}**: Adds a comment to the specified task ID and user ID.
- **GET /comments/task/{taskId}**: Retrieves all comments related to the specified task ID.
- **GET /comments**: Retrieves all comments.
- **GET /comments/{id}**: Retrieves a comment by its ID.
- **DELETE /comments/{id}**: Deletes a comment by its ID.

## Projects
- **POST /projects/create**: Creates a new project.
- **PUT /projects/{projectId}**: Updates the specified project ID.
- **GET /projects/{projectId}**: Retrieves the specified project by its ID.
- **GET /projects**: Retrieves all projects.
- **DELETE /projects/{projectId}**: Deletes the specified project by its ID.
- **GET /projects/department/{departmentName}**: Retrieves all projects related to the specified department.
- **GET /projects/{projectId}/tasks**: Retrieves all tasks related to the specified project ID.
- **GET /projects/{projectId}/team**: Retrieves the team members of the specified project ID.
- **GET /projects/{projectId}/status**: Retrieves the status of the specified project ID.

## Tasks
- **POST /tasks**: Creates a new task.
- **PUT /tasks/{taskId}**: Updates the specified task ID.
- **GET /tasks/{taskId}**: Retrieves the specified task by its ID.
- **GET /tasks**: Retrieves all tasks.
- **PATCH /tasks/{taskId}/status**: Updates the status of the specified task ID.
- **PATCH /tasks/{taskId}/block-or-cancel**: Updates the blocking or cancellation reason of the specified task ID.
- **PATCH /tasks/{taskId}/priority**: Updates the priority of the specified task ID.
- **PATCH /tasks/{taskId}/title-description**: Updates the title and description of the specified task ID.
- **DELETE /tasks/{taskId}**: Deletes the specified task by its ID.

## Users
- **GET /users/{id}**: Retrieves the specified user by ID.
- **GET /users**: Retrieves all users.
- **DELETE /users/{id}**: Deletes the specified user by ID.
- **PUT /users/{id}**: Updates the details of the specified user ID.

## Configuration

### Application Properties
The application properties can be configured in the `src/main/resources/application.properties` file.
