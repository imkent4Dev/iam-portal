# MySQL Setup Guide for RBAC User Service

## Prerequisites

1. **MySQL Server installed and running**
2. **Java 17 or higher**
3. **Maven 3.6+**

## Step 1: Create MySQL Database

Connect to your MySQL server and create the database:

```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE db_user_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (if not exists)
CREATE USER 'server_01'@'%' IDENTIFIED BY 'asd@123!';

-- Grant privileges
GRANT ALL PRIVILEGES ON db_user_service.* TO 'server_01'@'%';

-- Flush privileges
FLUSH PRIVILEGES;

-- Verify
SHOW DATABASES;
USE db_user_service;
```

## Step 2: Update application.yml

Your `application.yml` is already configured:

```yaml
spring:
  datasource:
    url: jdbc:mysql://server_01:3306/db_user_service?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: server_01
    password: asd@123!
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # Will create/update tables automatically
    show-sql: true
```

### Important Configuration Notes:

- **ddl-auto: update** - Hibernate will automatically create/update tables on startup
- **show-sql: true** - See SQL queries in console (useful for debugging)
- **MySQL8Dialect** - Use MySQL 8.x specific features

### DDL-Auto Options:

| Option | Description |
|--------|-------------|
| `update` | Updates schema, never drops data (RECOMMENDED for dev) |
| `create` | Drops and recreates schema on each startup |
| `create-drop` | Creates schema on startup, drops on shutdown |
| `validate` | Only validates schema, doesn't make changes |
| `none` | No automatic schema management |

## Step 3: Update pom.xml

The MySQL dependency is already added:

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Step 4: Start the Application

```bash
# Clean and build
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

## Step 5: Verify Database Tables

The application will automatically create these tables:

```sql
-- Connect to database
USE db_user_service;

-- List all tables
SHOW TABLES;

-- Expected tables:
-- users
-- roles
-- permissions
-- user_roles
-- role_permissions

-- View table structure
DESCRIBE users;
DESCRIBE roles;
DESCRIBE permissions;
DESCRIBE user_roles;
DESCRIBE role_permissions;
```

## Step 6: Verify Test Data

Check if initial data was loaded:

```sql
-- Check users
SELECT id, username, email, first_name, last_name, enabled FROM users;

-- Check roles
SELECT * FROM roles;

-- Check permissions
SELECT * FROM permissions;

-- Check user-role assignments
SELECT 
    u.username,
    r.name as role_name
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id;

-- Check role-permission assignments
SELECT 
    r.name as role_name,
    p.name as permission_name
FROM roles r
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
ORDER BY r.name, p.name;
```

## Troubleshooting

### Issue 1: Connection Refused

**Error:** `Communications link failure`

**Solutions:**
```bash
# Check if MySQL is running
sudo systemctl status mysql
# or
sudo service mysql status

# Start MySQL
sudo systemctl start mysql
# or
sudo service mysql start

# Check MySQL port
netstat -an | grep 3306
```

### Issue 2: Access Denied

**Error:** `Access denied for user 'server_01'@'localhost'`

**Solution:**
```sql
-- Recreate user with proper permissions
DROP USER IF EXISTS 'server_01'@'%';
CREATE USER 'server_01'@'%' IDENTIFIED BY 'asd@123!';
GRANT ALL PRIVILEGES ON db_user_service.* TO 'server_01'@'%';
FLUSH PRIVILEGES;
```

### Issue 3: Host Not Allowed

**Error:** `Host 'xxx' is not allowed to connect`

**Solution:**
```sql
-- Allow connections from specific host
CREATE USER 'server_01'@'your_host_ip' IDENTIFIED BY 'asd@123!';
GRANT ALL PRIVILEGES ON db_user_service.* TO 'server_01'@'your_host_ip';

-- Or allow from anywhere (less secure)
CREATE USER 'server_01'@'%' IDENTIFIED BY 'asd@123!';
GRANT ALL PRIVILEGES ON db_user_service.* TO 'server_01'@'%';

FLUSH PRIVILEGES;
```

### Issue 4: Timezone Issues

**Error:** `The server time zone value 'XXX' is unrecognized`

**Solution:** Already handled in your URL:
```
jdbc:mysql://server_01:3306/db_user_service?serverTimezone=UTC
```

Or set MySQL timezone:
```sql
SET GLOBAL time_zone = '+00:00';
```

### Issue 5: SSL Warnings

If you see SSL warnings, your config already handles this:
```
useSSL=false&allowPublicKeyRetrieval=true
```

For production, enable SSL:
```yaml
spring:
  datasource:
    url: jdbc:mysql://server_01:3306/db_user_service?useSSL=true&requireSSL=true&serverTimezone=UTC
```

## Environment-Specific Configuration

### Development (application-dev.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_user_service_dev?useSSL=false
    username: dev_user
    password: dev_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

logging:
  level:
    com.rbc.demo: DEBUG
    org.hibernate.SQL: DEBUG
```

### Production (application-prod.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/db_user_service?useSSL=true&requireSSL=true
    username: ${DB_USERNAME}  # From environment variable
    password: ${DB_PASSWORD}  # From environment variable
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  jpa:
    hibernate:
      ddl-auto: validate  # Don't auto-create in production!
    show-sql: false

logging:
  level:
    com.rbc.demo: INFO
```

Run with profile:
```bash
# Development
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Database Backup & Restore

### Backup
```bash
# Backup entire database
mysqldump -u server_01 -p db_user_service > backup.sql

# Backup structure only
mysqldump -u server_01 -p --no-data db_user_service > structure.sql

# Backup data only
mysqldump -u server_01 -p --no-create-info db_user_service > data.sql
```

### Restore
```bash
# Restore from backup
mysql -u server_01 -p db_user_service < backup.sql
```

## Performance Tuning

### Add Indexes for Better Performance

```sql
-- Index on username (frequently queried)
CREATE INDEX idx_username ON users(username);

-- Index on email
CREATE INDEX idx_email ON users(email);

-- Index on role name
CREATE INDEX idx_role_name ON roles(name);

-- Index on permission name
CREATE INDEX idx_permission_name ON permissions(name);

-- Composite index for user_roles
CREATE INDEX idx_user_roles ON user_roles(user_id, role_id);

-- Composite index for role_permissions
CREATE INDEX idx_role_permissions ON role_permissions(role_id, permission_id);
```

### Connection Pool Configuration

Add to `application.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Testing with MySQL

Update your test configuration (`application-test.yml`):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/db_user_service_test?useSSL=false
    username: test_user
    password: test_password
  jpa:
    hibernate:
      ddl-auto: create-drop  # Fresh database for each test run
```

## Migration from H2 to MySQL

If you were using H2, the migration is seamless because:

1. ✅ Same JPA entities work with both
2. ✅ Same repositories work with both
3. ✅ Only configuration changes needed
4. ✅ Spring Boot auto-configures based on driver

## Next Steps

1. ✅ MySQL database created
2. ✅ Application running
3. ✅ Tables auto-created
4. ✅ Test data loaded
5. 🔄 Test API endpoints
6. 🔄 Monitor logs for any SQL errors
7. 🔄 Check performance and add indexes if needed

## Quick Reference

| Action | Command |
|--------|---------|
| Start MySQL | `sudo systemctl start mysql` |
| Stop MySQL | `sudo systemctl stop mysql` |
| Connect to DB | `mysql -u server_01 -p db_user_service` |
| Show tables | `SHOW TABLES;` |
| View data | `SELECT * FROM users;` |
| Application URL | `http://localhost:8081/api` |
| Test login | `POST http://localhost:8081/api/auth/login` |