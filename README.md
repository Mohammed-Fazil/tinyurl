# TinyURL Service

A URL shortening service built with Java and Spring Boot.

The service converts a long URL into a compact short URL and redirects requests from the generated short code to the original URL.

## Current Features

- Create a short URL from a valid HTTP/HTTPS URL
- Generate short codes using Base62 encoding
- Use a database-backed sequence for unique short codes
- Generate short codes with a minimum length of four characters
- Store URL mappings in MongoDB
- Redirect short URLs to their original URLs
- Return `404 Not Found` when a short code does not exist
- Validate URLs before creating a mapping
- Return `400 Bad Request` for invalid URLs
- Centralized exception handling
- Unit and controller tests using JUnit 5 and Mockito

## Technology Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data MongoDB
- MongoDB
- Maven
- JUnit 5
- Mockito
- Lombok

## API

### Create Short URL

**POST**

```text
/api/urls
```

Request:

```json
{
  "url": "https://www.google.com"
}
```

Example response:

```json
{
  "shortUrl": "http://localhost:8080/1000"
}
```

### Redirect

**GET**

```text
/{shortCode}
```

Example:

```text
GET /1000
```

The service looks up the short code and redirects to the original URL.

### Short URL Not Found

A non-existent short code returns:

```text
404 Not Found
```

Example:

```json
{
  "message": "Short URL not found",
  "timestamp": "..."
}
```

### Invalid URL

Only HTTP and HTTPS URLs are accepted.

```text
https://www.google.com     valid
http://example.com         valid

google.com                 invalid
example                    invalid
ftp://example.com          invalid
```

Invalid URLs return:

```text
400 Bad Request
```

## Base62 Short Code Generation

The service uses a database-backed sequence and converts the sequence ID to Base62.

Base62 uses:

```text
0-9
a-z
A-Z
```

The sequence starts in the range required to produce at least four Base62 characters.

For example:

```text
238328 -> 1000
238329 -> 1001
238330 -> 1002
```

The short code is generated deterministically from the unique sequence ID.

## Local Configuration

The application supports a `local` Spring profile.

Activate it with:

```text
-Dspring.profiles.active=local
```

The database connection is supplied through an environment variable instead of being hard-coded.

Example property:

```properties
spring.mongodb.uri=${MONGODB_URI}
```

Environment-specific credentials and secret connection strings should not be committed to the repository.

## Running the Application

### Maven

Windows:

```bash
mvnw.cmd spring-boot:run
```

Run the tests:

```bash
mvnw.cmd clean test
```

### Spring Tool Suite

Run the project as a Spring Boot application with the `local` profile enabled.

## Testing

The project contains tests for:

- Base62 encoding
- Sequence generation
- URL validation
- Short URL creation
- Short URL lookup
- Missing short URLs
- Invalid URL handling
- HTTP short URL creation
- HTTP redirects
- HTTP validation errors
- Exception handling

Run:

```bash
mvnw.cmd clean test
```

Expected result:

```text
BUILD SUCCESS
```

## Application Flow

### Create URL

```text
Client
  |
  v
POST /api/urls
  |
  v
Request Validation
  |
  v
URL Validation
  |
  v
Generate Sequence ID
  |
  v
Base62 Encoding
  |
  v
Save URL Mapping
  |
  v
Return Short URL
```

### Redirect

```text
Client
  |
  v
GET /{shortCode}
  |
  v
Find URL Mapping
  |
  v
Original URL
  |
  v
HTTP Redirect
```

## Current Status

```text
URL creation             [x]
URL validation           [x]
Sequence generation      [x]
Base62 encoding          [x]
MongoDB persistence      [x]
URL redirection          [x]
Exception handling       [x]
Automated tests          [x]
```
