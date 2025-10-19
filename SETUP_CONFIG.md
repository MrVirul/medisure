# Configuration Setup Guide

## ⚠️ IMPORTANT: Sensitive Configuration Files

The following sensitive configuration files have been removed from git tracking for security reasons:

- `src/main/resources/application.properties`
- `src/main/resources/env.properties`

## Setup Instructions

### 1. Create Configuration Files

Copy the template files and fill in your actual credentials:

```bash
# Copy template files
cp src/main/resources/application.properties.template src/main/resources/application.properties
cp src/main/resources/env.properties.template src/main/resources/env.properties
```

### 2. Configure Database Connection

Edit `src/main/resources/env.properties` and update with your actual database credentials:

```properties
DATABASE_URL_PROD=jdbc:postgresql://your-database-host:5432/your-database-name?sslmode=require&channel_binding=require
DATABASE_USERNAME_PROD=your-database-username
DATABASE_PASSWORD_PROD=your-database-password
DATABASE_DRIVER_PROD=org.postgresql.Driver
```

### 3. Configure JWT Secret

Edit `src/main/resources/application.properties` and update the JWT secret:

```properties
jwt.secret=your-secure-jwt-secret-key-here
```

### 4. Environment Variables (Alternative)

Instead of using properties files, you can also set environment variables:

```bash
export DATABASE_URL_PROD="your-database-url"
export DATABASE_USERNAME_PROD="your-username"
export DATABASE_PASSWORD_PROD="your-password"
export JWT_SECRET="your-jwt-secret"
```

## Security Notes

- Never commit sensitive configuration files to git
- Use strong, unique passwords and secrets
- Consider using environment variables in production
- Regularly rotate your database passwords and JWT secrets
- Use different credentials for development, staging, and production environments

## .gitignore Protection

The following patterns are now protected in `.gitignore`:

- `*.env*` - All environment files
- `src/main/resources/application*.properties` - Application configuration
- `src/main/resources/env.properties` - Environment variables
- `*.log` - Log files
- `secrets.properties` - Secret files
- `credentials.properties` - Credential files
