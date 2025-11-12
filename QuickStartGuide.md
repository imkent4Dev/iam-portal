# 🚀 Quick Start Guide - MySQL Version

## Prerequisites ✅

- ☑️ Java 17+ installed
- ☑️ Maven 3.6+ installed
- ☑️ MySQL Server running (MySQL 8.0 recommended)

## Step 1: Setup MySQL Database (2 minutes)

### Option A: Run the SQL script
```bash
mysql -u root -p < init-mysql.sql
```

### Option B: Manual setup
```sql
# Connect to MySQL
mysql -u root -p

# Run these commands
CREATE DATABASE db_user_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'server_01'@'%' IDENTIFIED BY 'asd@123!';
GRANT ALL PRIVILEGES ON db_user_service.* TO 'server_01'@'%';
FLUSH PRIVILEGES;
EXIT;
```

## Step 2: Configure Application (Already Done!)

Your `application.yml` is already configured:
```yaml
spring:
  datasource:
    url: jdbc:mysql://server_01:3306/db_user_service
    username: server_01
    password: asd@123!
server:
  port: 8081
```

## Step 3: Build and Run (1 minute)

```bash
# Navigate to project directory
cd user-service

# Build project
./mvnw clean install

# Run application
./mvnw spring-boot:run
```

**On Windows:**
```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

## Step 4: Wait for Startup (30 seconds)

Look for these messages in console:
```
✅ Permissions initialized
✅ Roles initialized
✅ Users initialized
✅ Data initialization completed!
✅ Started UserServiceApplication in X seconds
```

## Step 5: Test the API (2 minutes)

### Quick Test - Login as Admin

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["ROLE_ADMIN"],
  "permissions": ["USER_READ", "USER_CREATE", ...]
}
```

### Use the Token

```bash
# Save token from response
TOKEN="your-token-here"

# Get all users
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/users
```

### Run Complete Test Suite

```bash
chmod +x test-api.sh
./test-api.sh
```

## Step 6: Verify Database (Optional)

```bash
mysql -u server_01 -p db_user_service
```

Then run:
```sql
-- See all tables
SHOW TABLES;

-- Check users
SELECT id, username, email FROM users;

-- Check roles
SELECT * FROM roles;

-- Expected output: 4 users (admin, manager, user, guest)
```

## 🎯 Test Credentials

| Username | Password    | Role         | Access Level |
|----------|-------------|--------------|--------------|
| admin    | admin123    | ROLE_ADMIN   | Full Access  |
| manager  | manager123  | ROLE_MANAGER | Limited      |
| user     | user123     | ROLE_USER    | Basic        |
| guest    | guest123    | ROLE_GUEST   | Read-only    |

## 📋 Common Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/auth/register` | POST | Register new user | No |
| `/api/auth/login` | POST | Login | No |
| `/api/users` | GET | Get all users | Yes |
| `/api/users/{id}` | GET | Get user by ID | Yes |
| `/api/admin/dashboard` | GET | Admin dashboard | Admin only |
| `/api/manager/dashboard` | GET | Manager dashboard | Admin/Manager |

## 🔧 Troubleshooting

### MySQL Connection Error?

```bash
# Check MySQL is running
sudo systemctl status mysql

# Check MySQL port
netstat -an | grep 3306

# Test connection
mysql -u server_01 -p -h server_01
```

### Port 8081 Already in Use?

Change port in `application.yml`:
```yaml
server:
  port: 8082  # Use different port
```

### Tables Not Created?

Check application logs for errors. Verify:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # Should be 'update'
```

### Can't Login?

Verify test data was loaded:
```sql
SELECT COUNT(*) FROM users;
-- Should return 4
```

If no users, check console for "Data initialization completed!" message.

## 🎉 Success Checklist

- ✅ MySQL database created
- ✅ Application started successfully
- ✅ Can login with test users
- ✅ Can retrieve user list with token
- ✅ 4 users in database
- ✅ 4 roles in database
- ✅ Multiple permissions configured

## 📚 What's Next?

1. **Import Postman Collection** - Use `RBAC-User-Service.postman_collection.json`
2. **Test Role-Based Access** - Try accessing admin endpoints with different users
3. **Add Your Own Users** - Use the register endpoint
4. **Explore Permissions** - Check what each role can do
5. **Read Full Documentation** - See `README.md` for complete API docs

## 🆘 Need Help?

**Application URL:** http://localhost:8081/api

**Test Login:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**View Logs:**
Check console output or application logs for detailed error messages.

**Database Issues:**
```bash
# Connect and verify
mysql -u server_01 -p db_user_service

# Check tables
SHOW TABLES;

# Check data
SELECT * FROM users;
```

---

**Total Setup Time: ~5 minutes**

Happy Coding! 🚀