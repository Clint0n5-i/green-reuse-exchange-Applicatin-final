# Production Readiness Checklist

## Frontend

- See `.env.example` for required environment variables (API URL, secrets, configs)
- Use `.env.production` for deployment
- Run `npm run lint` to check code quality
- Run `npm run build` to generate optimized files in `dist`
- Use HTTPS for API calls and asset delivery
- Use `ErrorBoundary` for robust client-side error handling
- Log errors to a monitoring service if possible
- All major components/pages have basic tests in `src/__tests__`
- Run tests with `npm test` before deployment
- Serve static files from `dist` using a CDN or static host
- Remove unused code/assets before deployment
- Document any additional config or setup steps

## Backend

- All secrets and DB credentials use environment variables
- SSL/HTTPS enabled (see `application.properties`)
- Logging set to INFO for production
- Actuator endpoints restricted
- JPA `ddl-auto` set to `validate` or use Flyway/Liquibase for schema migrations
- Run `mvn clean package` for production build
- Set up monitoring and alerting (Actuator, external tools)

## Testing & CI/CD

- Ensure all tests pass before deployment
- Integrate with CI/CD pipeline (GitHub Actions, Jenkins, etc.)
- Use code quality tools (ESLint, SonarQube)

## General

- Remove unused code and assets regularly
- Document deployment steps and environment variables

---

This file was auto-generated to help you finalize production readiness.
