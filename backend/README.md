# Green Reuse Exchange Platform - Backend

A Spring Boot application for the Green Reuse Exchange Platform, enabling users to list and exchange reusable items to reduce waste.

## Features

- **User Authentication**: JWT-based authentication with user registration and login
- **Item Management**: CRUD operations for reusable items
- **Search & Filtering**: Search items by category, location, and keywords
- **Item Claims**: Users can claim available items
- **Admin Panel**: Admin users can manage all items
- **Location-based Search**: Find items in specific locations
- **Transaction Tracking**: Track item claims and exchanges

## Technology Stack

- **Spring Boot 3.2.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA** for database operations
- **MySQL** database
- **Lombok** for reducing boilerplate code
- **Validation** for request validation

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup Instructions

### 1. Database Setup

1. Install and start MySQL server
2. Create a database (optional - will be created automatically):
   ```sql
   CREATE DATABASE greenreuse_exchange;
   ```
3. Update database credentials in `src/main/resources/application.properties` if needed

### 2. Application Setup

1. Clone the repository
2. Navigate to the backend directory:
   ```bash
   cd backend
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

- `POST /api/auth/signup` - User registration
- `POST /api/auth/login` - User login

### Items (Public)

- `GET /api/items` - Get all items
- `GET /api/items/available` - Get available items only
- `GET /api/items/category/{category}` - Get items by category
- `GET /api/items/location/{location}` - Get items by location
- `GET /api/items/search?searchTerm={term}&location={location}` - Search items
- `GET /api/items/{id}` - Get item by ID

### Items (Authenticated)

- `POST /api/items` - Create new item (requires USER role)
- `PUT /api/items/{id}/claim` - Claim an item (requires USER role)
- `DELETE /api/items/{id}` - Delete item (owner or ADMIN only)

### Admin

- `GET /api/admin/items` - Get all items (requires ADMIN role)
- `DELETE /api/admin/items/{id}` - Delete any item (requires ADMIN role)

## Sample Data

The application comes with pre-loaded sample data:

### Users

- **Admin**: admin@greenreuse.com / password123
- **Regular Users**:
  - john.doe@email.com / password123
  - jane.smith@email.com / password123
  - mike.johnson@email.com / password123
  - sarah.wilson@email.com / password123
  - david.brown@email.com / password123

### Sample Items

- 10 sample items across different categories (Books, Furniture, Electronics, etc.)
- Items located in Kisii, Nairobi, and Mombasa

## Database Schema

### Users Table

- id (Primary Key)
- email (Unique)
- password (BCrypt encrypted)
- name
- location
- role (USER/ADMIN)

### Items Table

- id (Primary Key)
- title
- description
- category (enum)
- status (AVAILABLE/CLAIMED)
- location
- posted_by (Foreign Key to Users)
- created_at

### Transactions Table

- id (Primary Key)
- item_id (Foreign Key to Items)
- claimed_by (Foreign Key to Users)
- date_claimed

## Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control
- CORS enabled for frontend integration

## Development

### Running Tests

```bash
mvn test
```

### Building JAR

```bash
mvn clean package
```

The JAR file will be created in the `target` directory.

## Configuration

Key configuration properties in `application.properties`:

- Database connection settings
- JWT secret and expiration
- Server port
- Logging levels

## API Documentation

- Swagger UI: After starting the backend, visit [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) for interactive API docs.
- OpenAPI spec: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Health Checks & Monitoring

- Actuator health endpoint: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- More actuator endpoints can be enabled/configured in `application.properties`.

## Environment Variables

- `DB_PASSWORD` - MySQL database password
- `JWT_SECRET` - Secret for JWT signing
- `SSL_KEYSTORE_PASSWORD` - Password for SSL keystore

## Deployment

- Use a CI/CD pipeline to build, test, and deploy the backend.
- Set all secrets and environment variables securely in your deployment environment.
- Restrict CORS in production to your frontend domain (see `@CrossOrigin` in controllers).

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.
