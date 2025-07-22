# Hospital Management System

This is a Hospital Management System built with Spring Boot.

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Usage](#usage)
- [License](#license)

## Features

- User registration (for patients)
- User login with JWT authentication
- Password reset functionality
- Admin functionality to create users with different roles (ADMIN, DOCTOR, PATIENT)

## Requirements

- Java 21
- Maven
- MySQL

## Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Falasefemi2/Hospital-management-system.git
   cd Hospital-management-system
   ```

2. **Create a MySQL database:**
   - Create a database named `hospital_management`.

3. **Configure the application:**
   - Open `src/main/resources/application.yml` and update the database credentials:
     ```yaml
     spring:
       datasource:
         url: jdbc:mysql://localhost:3306/hospital_management
         username: your-username
         password: your-password
     ```

4. **Build and run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will be available at `http://localhost:8080`.

## API Endpoints

### Authentication

- **POST** `/api/users/register` - Register a new patient.
- **POST** `/api/users/login` - Login for all users.
- **POST** `/api/users/reset-password/request` - Request a password reset link.
- **POST** `/api/users/reset-password` - Reset password with a valid token.

### Admin

- **POST** `/api/admin/users` - Create a new user (ADMIN, DOCTOR, or PATIENT).

## Configuration

- **JWT Secret Key:**
  - The JWT secret key is configured in `application.yml`. It is recommended to use a strong, environment-specific secret key in a production environment.
    ```yaml
    jwt:
      secret: your-jwt-secret
    ```

## Usage

1. **Register a patient:**
   - Send a `POST` request to `/api/users/register` with the patient's details.

2. **Login:**
   - Send a `POST` request to `/api/users/login` with the user's credentials to get a JWT token.

3. **Access protected endpoints:**
   - Include the JWT token in the `Authorization` header as a Bearer token to access protected endpoints.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
