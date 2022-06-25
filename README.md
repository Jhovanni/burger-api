# Burger API

RESTful microservice aligning (mostly) with https://laboratoria.github.io/burger-queen-api/module-products.html.

## Implementation

### Language and frameworks

Developed using Kotlin and Spring framework.

### Testing

There is no unit tests for the project. It is not my practice to do that, but since the project is simple enough and I
have limited time to work on pet projects, it will need to suffice for now.

Testing has been done via Postman collection.

### Database

The application is configured to use an SQL database to store data. While developing the app, a Postgresql database has
been used.

To change the database, it will require to update `spring.datasource.driver-class-name`
and `spring.jpa.database-platform`
in the properties file.

SQL databases allows to link tables via primary keys, but to simplify the mappings this app is not using that feature,
and it is enforcing data consistency via application code, like it will do for Cassandra, Mongo or another
non-relational
database. This is intentional, to make sure the core of the application works without depending on database validations.

### Save and fetch data

To map business models to database tables, `spring-data` is being used.
It is using separate business models from database entities. This is to prevent database constraints permeating to
business models.

## Deployment

### Secrets

The following secrets are required

- DATABASE_URL: Postgresql connection URL, it follows the pattern `jdbc:postgresql://{domain}:
  {port}/{databaseName}?password={password}&sslmode=require&user={username}` (pattern may change if the database to use
  is changed).
- ADMIN_EMAIL: Email to authenticate initial admin user.
- ADMIN_PASSWORD: Password to authenticate initial admin user.

### Run locally

```./gradlew bootRun```

*The secrets mentioned above will be required as environment variables*

## Documentation

### Swagger

Access swagger documentation using the path /swagger-ui.html. For example: http://localhost:8080/swagger-ui.html (when
running locally).

