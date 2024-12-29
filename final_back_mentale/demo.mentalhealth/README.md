# Mental Health Application - Backend

## Description
Backend application for mental health management system built with Spring Boot and Docker.

## Technologies Used
- Java 17
- Spring Boot
- Docker
- Maven
- SonarQube for code quality
- Eclipse Temurin JDK

## Prerequisites
- Java 17 or higher
- Docker
- Maven
- Docker Compose

## Project Structure
```
mentalhealth/
├── src/
├── target/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Installation Steps

1. Clone the repository
```bash
git clone [your-repository-url]
cd mentalhealth
```

2. Build the application
```bash
mvn clean package -DskipTests
```

3. Build Docker image
```bash
docker build -t monapp-spring:1.0 .
```

4. Run the application
```bash
docker-compose up
```

## Docker Configuration

### Dockerfile
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Docker Build Process
![Docker Build Process](project_screenshots/docker_build.png)
*Docker building the JDK Alpine image and downloading necessary components*

## Docker Compose Execution
![Docker Compose](project_screenshots/docker_compose.png)
*Docker Compose pulling and running the database container*

## Code Quality Analysis (SonarQube)

### Overview Dashboard
![SonarQube Dashboard](project_screenshots/sonarqube_dashboard.png)
*SonarQube analysis showing code quality metrics*

### Detailed Metrics
![SonarQube Details](project_screenshots/sonarqube_details.png)
*Detailed breakdown of code quality parameters*

Current metrics:
- Lines of Code: 1.8k
- Maintainability: A (62 points)
- Reliability: C (17 points)
- Security: E (1 issue)
- Code Coverage: 0%
- Duplications: 0%

## Performance Monitoring

### Memory Usage
![Memory Usage](project_screenshots/memory_usage.png)
*Container memory usage monitoring (706.1MB / 15.16GB)*

### CPU Usage
![CPU Usage](project_screenshots/cpu_usage.png)
*Container CPU usage monitoring (0.80% / 1200%)*

## System Requirements
- Memory: Minimum 1GB recommended (Currently using ~706MB)
- CPU: Multi-core supported (12 CPUs available)

## Database
- Embedded database for evaluation purposes
- Size: ~65MB

## Development Notes
- The embedded database should only be used for evaluation purposes
- Ensure proper security measures are implemented before production deployment
- Increase test coverage recommended
- Address reliability issues identified by SonarQube

## Contributing
[Add your contribution guidelines here]

## License
[Add your license information here]

## Contact
[Add your contact information here]
