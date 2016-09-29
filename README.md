# ERICODE lookup forwarding and caching micro service

### Software Tools and Dependencies:

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

The idea is letting the micro service leverage Spring's dependency injection by selecting a profile tied to an implementation. For the initial use case the calls can be forwarded to the Allies Computing API with: 

```
mvn spring-boot:run -Dspring.profiles.active=alliescomputing
```

You can also run the service on MOCK mode with:

```
mvn spring-boot:run -Dspring.profiles.active=mock
```

Bear in mind that you need to update the configuration files on the **src/main/resources/application-{spring.profiles.active}.properties** or provide them on the command line (like using **--Dspring.config.location** and providing an externalized source).

### Swagger API testing

http://localhost:8080/swagger-ui.html

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
