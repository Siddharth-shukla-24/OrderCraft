# OrderCraft Backend

Spring Boot 3.5.x, Java 21, Maven, Spring Data JPA, Oracle Database.
Stage 1 provides the foundation only: `GET /api/health` and CORS for `http://localhost:4200`.

## Prerequisites
- JDK 21 (`java -version`)
- Maven 3.9+ (`mvn -v`)
- A reachable Oracle Database and a schema user (required to START the app; not required to BUILD it)

## Configuration
Set these environment variables before running the app. Do not commit real values.

```powershell
$env:ORDERCRAFT_DB_URL      = "jdbc:oracle:thin:@//<host>:<port>/<service_name>"
$env:ORDERCRAFT_DB_USERNAME = "<db_user>"
$env:ORDERCRAFT_DB_PASSWORD = "<db_password>"
```

## Build
```powershell
cd C:\ordercraft-project\backend
mvn clean package
```

## Run
```powershell
mvn spring-boot:run
# or
java -jar target\ordercraft-backend-0.1.0-SNAPSHOT.jar
```

## Check
```powershell
Invoke-RestMethod http://localhost:8080/api/health
```