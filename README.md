# Project – Java Spring Boot REST API

## Overview
This project is a Java + Spring Boot REST application built with Maven and backed by a PostgreSQL database.
Docker Compose is the recommended way to run the application locally.

---

## Technology Stack
- Java 17+
- Spring Boot
- Maven
- PostgreSQL
- Docker & Docker Compose

---

## Project Structure
```
.
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── src
│   ├── main
│   │   ├── java        # Application source code
│   │   └── resources   # Configuration files (application.yml / properties)
│   └── test
│       └── java        # Unit and integration and endtoend tests
│       └── resources   # End-to-end test and configurations

```

---

## Prerequisites
- Docker & Docker Compose
- Java 17+ (as defined in pom.xml)
- Maven (required only for running without Docker)

---

## Configuration

### Environment Variables
Environment variables are defined in `docker-compose.yml`.

- `SPRING_DATASOURCE_URL` – JDBC URL  
  `jdbc:postgresql://localhost:5433/studentdb`
- `SPRING_DATASOURCE_USERNAME` – Database username  
  `postgres`
- `SPRING_DATASOURCE_PASSWORD` – Database password  
   `password`

Additional JPA / Hibernate settings are also configured via environment variables.

---
## Running the Application

### Using Docker (Recommended)

From the project root directory:

```bash
docker compose up --build
```

This will:
- Build the application Docker image
- Start the PostgreSQL (`db`) container
- Start the Spring Boot (`app`) container

Application URL:
```
http://localhost:8080
```

PostgreSQL (from host machine):
```
localhost:5433  (container port: 5432)
```

#### Run in Detached Mode
```bash
docker compose up --build -d
```

---

### Stopping the Application

Stop containers:
```bash
docker compose down
```

Stop containers and remove volumes (deletes database data):
```bash
docker compose down -v
```

---

## Running Without Docker

1. Start a local or remote PostgreSQL instance.
2. Configure database connection via `application.properties` or environment variables.
3. Build and run the application:

```bash
mvn clean install
mvn spring-boot:run
```

---

## Testing

Run unit tests:
```bash
mvn test
```

---

## Database Access (Docker Defaults)

- **Database:** studentdb
- **Username:** postgres
- **Password:** password
- **Host (from host machine):** localhost:5433

---

## Important Notes

- `depends_on` in `docker-compose.yml` ensures container start order but does **not** guarantee that the database is ready to accept connections.
  Consider adding retry logic, a healthcheck, or a wait-for mechanism if required.
- Log levels can be controlled via environment variables in `docker-compose.yml`, for example:
  - `LOGGING_LEVEL_ORG_SPRINGFRAMEWORK`
  - `LOGGING_LEVEL_COM_EXAMPLE`

---
