# Car Park Availability Service 🚗🅿️

## Overview
The Car Park Availability Service is a Spring Boot-based application designed to manage and provide real-time availability information for car parks. The application follows a hexagonal architecture, ensuring a clean separation of concerns and adherence to SOLID principles.

# Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Docker Compose](#docker-compose)
- [Local Development](#local-development)
- [API Endpoints](#api-endpoints)
- [Swagger Documentation](#swagger-documentation)
- [Scheduled Tasks](#scheduled-tasks)
- [Testing](#testing)
- [Room for improvement](#room-for-improvement)
- [Performance](#performance)
- [Scalability](#scalability)
- [Maintainability](#maintainability)

## Features
- 📊 Fetch and Store Data: Fetch car park availability data from external sources and store it in the database.
- 📂 CSV Data Reload: Reload car park data from a CSV file.
- 🔍 API Endpoints: Query car park availability and nearest car parks.
- ⏲️ Scheduled Tasks: Periodically fetch and update car park availability data.
- 🛢️ PostgreSQL Integration: Persistent storage using PostgreSQL.
- 📜 Swagger Integration: API documentation.
- 🛠️ Centralized Exception Handling and Logging: Robust error handling and logging.

## Architecture
This application follows the hexagonal architecture, also known as ports and adapters. The main layers are:

- Domain Layer: Contains the business logic and domain models.
- Application Layer: Contains service implementations and orchestrates the application's use cases.
- Infrastructure Layer: Contains implementations for persistence, external services, and other technical details.
  
## Prerequisites
- JDK 17 or higher
- Maven 3.8.1 or higher
- Docker and Docker Compose

## Configuration
- Update the application.properties with your specific configuration:
  - spring.datasource.url=your_url
  - spring.datasource.username=your_username
  - spring.datasource.password=your_password
  - csv.file.path=your_csv_path

## Docker Compose
Build and run the application using Docker Compose:

```
docker-compose up --build
```

## Local Development
- For local development, you can run the application using Maven:
```
mvn spring-boot:run
```

## API Endpoints
- The following endpoints are available:
  - Reload CSV Data: GET /carparks/reload-csv
  - Fetch Car Park Availability: GET /carparks/availability
  - Get Nearest Car Parks: GET /carparks/nearest
## Swagger Documentation
- API documentation is available at /api-docs.

## Scheduled Tasks
The application includes a scheduled task to fetch car park availability data every 10 minutes. This can be configured in the CarParkAvailabilityScheduler class.

## Testing
- Unit and Integration Tests
- Run the tests using Maven:
```
mvn test
```

## Challenges

### Choosing Between Using External API or Library for Coordinate Conversion
#### Options:
  1. Using OneMap API ❌ 
  2. Importing a coordinate conversion library ✅
#### Pros and Cons:  
- Using OneMap API:
  - Pros: No need for library maintenance, potentially up-to-date with latest changes.
  - Cons: Dependency on external service, potential latency issues, requires network access.
- Importing a Library:
   - Pros: Faster performance as conversion happens locally, no dependency on external service.
   - Cons: Need to maintain the library, ensure it's up-to-date.

### Choosing Between Loading Data from CSV into Database or In-Memory
#### Options:
  1. Loading data from CSV into the database ✅ 
  2. Loading data from CSV into in-memory storage ❌ 
#### Pros and Cons:  
- Loading data from CSV into the database:
  - Pros: Persistent storage, data available even after application restart, leveraging database capabilities for queries.
  - Cons: Slower load times, potential for larger storage requirements.
- Loading data from CSV into in-memory storage:
   - Pros: Faster data access, reduced storage requirements, simpler setup for small datasets.
   - Cons: Data loss on application restart, not suitable for large datasets, limited querying capabilities.

### Choosing Between Using Two Tables or One Table to Store Data
#### Options:
  1. Using separate tables for car park information and availability ✅ 
  2. Using a single table to store all data ❌ 
#### Pros and Cons:  
- Using separate tables for car park information and availability:
  - Pros: Clear separation of concerns, easier to manage and update individual aspects of data.
  - Cons: More complex joins and potentially slower queries.
- Using a single table to store all data:
   - Pros: Simpler queries, potentially faster data retrieval.
   - Cons: Harder to manage and update specific parts of the data, risk of data duplication.
 
### Choosing to Calculate Distance in Service or Database
#### Options:
  1. Calculating distance in the database layer ✅ 
  2. Calculating distance in the service layer ❌ 
#### Pros and Cons:  
- Calculating distance in the database layer:
  - Pros: Faster queries as calculation is done during data retrieval, utilizes database indexing.
  - Cons: More complex SQL queries, harder to maintain and debug.
- Calculating distance in the service layer:
   - Pros: Easier to implement complex logic, decouples business logic from database.
   - Cons: Potentially slower as data needs to be fetched first and not fit for large data.
 
## Room for improvement
### Performance
#### 1. Handling Large CSV Files:
- Current Challenge: When the CSV file is large, loading it into the database can be slow and resource-intensive.
- Potential Solutions:
  1. Implement batch processing to load data in chunks.
  2. Use multi-threading to parallelize the data loading process.
  3. Compress the CSV file to reduce the size and improve load times.
#### 2. Optimizing Database Queries:
- Current Challenge: Database don't have index yet cause the dataset is small but as the dataset grows, database queries might become slower.
- Potential Solutions:
  1. Adding appropriate indexes in frequent column queries with high cardinarity
  2. Can using geohash to group nearby lat,long of location 
  3. Geo-location Indexes: Use GiST (Generalized Search Tree) indexes for geohash support by Postgresql.
  4. Consider using store procedure
#### 3. Caching
- Current Challenge: System don't have caching yet due to small dataset and small request call but in future when dataset is larger and request call is frequently we can apply.
- Potential Solutions:
  1. Caching by lat,long or geohash index
  2. Using Read-Through Caching with TTL is less than schedule time to get new data
#### 4. Asynchronous Processing
- Current Challenge: System don't use any async process
- Potential Solution:
  1. Add asynchronous processing when fetching real-time data.

### Scalability
Challenge: As the number of users and car park data increases, the system needs to handle a higher load without performance degradation.
- Potential Solutions:
   1. Database Sharding: Split the database into smaller, more manageable pieces to distribute the load.
   2. Horizontal Scaling: Add more instances of your application to handle increased traffic.
   3. Microservices Architecture: Break down the monolithic application into smaller, independent services that can be scaled individually.
   4. Message Queues: Use message queues like RabbitMQ or Kafka to decouple processing and improve performance.
### Maintainability
Challenge: Making the system easy to maintain, debug, and extend.
- Potential Solutions
   1. Code Quality: Use design patterns like Command Bus, Middleware, and Adapter patterns to keep the codebase clean and modular.
   2. Centralized Logging: Implement centralized logging to aggregate logs from different services, making debugging easier.
   3. Graceful Shutdown: Ensure that the application can shut down gracefully, completing ongoing tasks before termination.
   4. Configuration Management: Use environment variables or configuration management tools to handle sensitive data and configuration.
   5. Continuous Integration/Continuous Deployment (CI/CD): Set up CI/CD pipelines to automate the build, test, and deployment processes, ensuring that new changes are quickly and reliably integrated into the application.
   6. Load Testing: Perform load testing to simulate high traffic and identify potential performance bottlenecks.

