# ERICODE lookup fowarding and caching microservice

Software Tools and Dependencies:
- Maven
- Spring Boot
- Redis
- JUnit
- Swagger
- Docker

### Building

```
mvn clean install
```

### Running

```
mvn spring-boot:run -Pdev -Dspring.profiles.active=alliescomputing
```

### Packaging

```
mvn package
```

### Building the container

```
docker build -t flexco/eircode .
```

### Starting the container

```
docker run -i -t -p 8080:8080 -v /path/to/your/env/conf:/home/eircode/conf flexco/eircode:latest
```
