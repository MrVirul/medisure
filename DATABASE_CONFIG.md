# Medisur - Database Configuration

## ✅ Updated to Neon PostgreSQL Database

Your Medisur application is now configured to use your **Neon PostgreSQL** cloud database instead of H2.

### 🔗 Database Connection Details

**Connection String:**
```
postgresql://neondb_owner:npg_CIsAUnit7GQ9@ep-dark-snow-a14ifffc-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
```

**Configuration Files Updated:**

1. **`src/main/resources/env.properties`**
```properties
DATABASE_URL_PROD=jdbc:postgresql://ep-dark-snow-a14ifffc-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require
DATABASE_USERNAME_PROD=neondb_owner
DATABASE_PASSWORD_PROD=npg_CIsAUnit7GQ9
DATABASE_DRIVER_PROD=org.postgresql.Driver
```

2. **`src/main/resources/application.properties`**
```properties
spring.config.import=classpath:env.properties
spring.datasource.url=${DATABASE_URL_PROD}
spring.datasource.username=${DATABASE_USERNAME_PROD}
spring.datasource.password=${DATABASE_PASSWORD_PROD}
spring.datasource.driver-class-name=${DATABASE_DRIVER_PROD}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

### ✅ Fixed Issues

1. **Circular Dependency**: Fixed the circular reference between JwtAuthenticationFilter, UserService, and SecurityConfig using `@Lazy` injection
2. **Database Switch**: Changed from H2 to PostgreSQL Neon database
3. **Dependencies**: Added PostgreSQL driver to pom.xml

### 📊 Database Features

- **Auto-create tables**: `spring.jpa.hibernate.ddl-auto=update` will automatically create all tables
- **Persistent data**: Data is stored in Neon cloud database (not in-memory)
- **Connection pooling**: HikariCP configured with max 5 connections
- **SSL enabled**: Secure connection with `sslmode=require`

### 🚀 Application Status

✅ **Backend Compiled Successfully**
✅ **Connected to Neon PostgreSQL**  
✅ **Application Running on Port 8080**

### 🔐 Initial Data

On first run, the application will create:
- Admin user: `admin@medisur.com` / `admin123`
- 4 sample insurance policies (Basic, Premium, Family, Senior)

### 📝 Database Tables Created

The application will automatically create these tables in your Neon database:
1. `users`
2. `policies`
3. `policy_holders`
4. `doctors`
5. `appointments`
6. `claims`
7. `claim_documents`
8. `finance_records`
9. `audit_logs`

### 🌐 Access

- **Backend API**: http://localhost:8080
- **Neon Dashboard**: https://console.neon.tech

### ⚠️ Important Notes

1. Your database credentials are stored in `env.properties` - **DO NOT commit this file to public repositories**
2. Add `env.properties` to `.gitignore` if not already added
3. The database is persistent - data will remain between application restarts
4. Use Neon dashboard to view/manage your database directly

### 🎯 Next Steps

Your backend is now fully configured and running with your Neon PostgreSQL database. You can:
1. Test the APIs using Postman/curl
2. View data in Neon dashboard
3. Build the React frontend to interact with these APIs
4. Deploy the application to production

---
**Status**: ✅ Backend fully operational with Neon PostgreSQL database

