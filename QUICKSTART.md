# 🚀 Medisur Quick Start Guide

Get the Medisur Health Insurance Management System up and running in 5 minutes!

---

## 📋 Prerequisites

Before you begin, ensure you have:
- ✅ **Java 17+** installed (`java -version`)
- ✅ **Maven 3.6+** installed (`mvn -version`)
- ✅ **Node.js 18+** installed (`node -version`)
- ✅ **npm 9+** installed (`npm -version`)

---

## 🎯 Step-by-Step Setup

### Step 1: Start the Backend (Spring Boot)

```bash
# Navigate to project root
cd "/Users/virul/My Projects/medisure"

# Run the Spring Boot application
mvn spring-boot:run
```

**Backend will start on:** `http://localhost:8080`

**Expected output:**
```
Started MedisureApplication in X.XXX seconds
```

### Step 2: Start the Frontend (React)

Open a **NEW terminal window** and run:

```bash
# Navigate to frontend directory
cd "/Users/virul/My Projects/medisure/medisur-frontend"

# Install dependencies (first time only)
npm install

# Start the development server
npm run dev
```

**Frontend will start on:** `http://localhost:5173`

**Expected output:**
```
VITE v7.1.10 ready in XXX ms
➜ Local: http://localhost:5173/
```

### Step 3: Access the Application

Open your browser and navigate to:
```
http://localhost:5173
```

---

## 🔐 Test Credentials

### Admin Account
```
Email: admin@medisur.com
Password: admin123
Role: ADMIN
```

### Sample Policies
The system automatically creates sample policies on startup:
- Basic Health Plan (₹5,000)
- Family Health Plan (₹15,000)
- Senior Citizen Plan (₹20,000)
- Critical Illness Plan (₹25,000)

---

## 🎨 Quick Tour

### 1. Login
- Open `http://localhost:5173`
- Enter credentials: `admin@medisur.com` / `admin123`
- Click "Login"

### 2. Admin Dashboard
After login, you'll see:
- Total users count
- Policy holders count
- Employees count
- Doctors count
- User management table

### 3. Create an Employee
- Click "Create Employee" button
- Fill in the form:
  - Full Name: Test Manager
  - Email: test@example.com
  - Password: test123
  - Phone: 1234567890
  - Role: Policy Manager
- Click "Create Employee"

### 4. Browse Policies (As Regular User)
- Logout from admin
- Click "Register here"
- Create a new user account
- After registration, browse available policies
- Click "Select Policy" to purchase

### 5. Submit a Claim
- After purchasing a policy
- Navigate to "Submit Claim" from sidebar
- Fill in claim details
- Upload supporting documents
- Submit claim

### 6. Review Claims (As Claims Manager)
- Login as claims manager
- View pending claims
- Click "Review" on any claim
- Approve or Reject with comments

---

## 🛠️ Troubleshooting

### Backend Issues

**Problem:** Port 8080 already in use  
**Solution:**
```bash
# Find and kill the process
lsof -ti:8080 | xargs kill -9
```

**Problem:** Database connection error  
**Solution:** Check `src/main/resources/env.properties` for correct database credentials

**Problem:** Maven build fails  
**Solution:**
```bash
mvn clean install -DskipTests
```

### Frontend Issues

**Problem:** Port 5173 already in use  
**Solution:**
```bash
# Kill the process
lsof -ti:5173 | xargs kill -9
```

**Problem:** npm install fails  
**Solution:**
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

**Problem:** CORS errors  
**Solution:** Ensure backend is running on port 8080 and frontend on port 5173

### Common Errors

**Error:** 401 Unauthorized  
**Fix:** Token expired, logout and login again

**Error:** Cannot read properties of undefined  
**Fix:** Refresh the page or clear browser cache

**Error:** Failed to fetch  
**Fix:** Verify backend is running on http://localhost:8080

---

## 📁 Project Structure Quick Reference

```
medisure/
├── src/main/java/...          # Backend Java code
├── src/main/resources/        # Application configuration
│   ├── application.properties # Main config
│   └── env.properties        # Database credentials
├── medisur-frontend/          # Frontend React app
│   ├── src/
│   │   ├── components/       # UI components
│   │   ├── pages/           # Page components
│   │   ├── services/        # API services
│   │   └── context/         # State management
│   └── package.json
└── pom.xml                   # Maven dependencies
```

---

## 🎯 Testing Different Roles

### As Admin
1. Login as admin@medisur.com
2. Create employees with different roles
3. View all system users
4. Manage policies

### As Policy Holder
1. Register new user
2. Browse policies
3. Purchase a policy
4. Submit claims
5. Book appointments

### As Claims Manager
1. Login with claims manager account
2. Review pending claims
3. Approve or reject with comments
4. View claim statistics

### As Doctor
1. Login with doctor account
2. View today's appointments
3. Update appointment status
4. Complete appointments

---

## 📊 API Testing (Optional)

Test APIs using curl or Postman:

### Login API
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@medisur.com","password":"admin123"}'
```

### Get All Policies (Public)
```bash
curl http://localhost:8080/api/policies/all
```

### Get My Profile (Authenticated)
```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🚀 Production Build

### Backend Production Build
```bash
cd "/Users/virul/My Projects/medisure"
mvn clean package -DskipTests
java -jar target/medisure-0.0.1-SNAPSHOT.jar
```

### Frontend Production Build
```bash
cd medisur-frontend
npm run build
# Output will be in dist/ folder
```

---

## 📞 Need Help?

1. Check `PROJECT_COMPLETE.md` for comprehensive documentation
2. Check `BACKEND_README.md` for backend details
3. Check `medisur-frontend/README.md` for frontend details
4. Review error messages in:
   - Backend: Console output
   - Frontend: Browser console (F12)

---

## ✅ Quick Checklist

Before starting:
- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] Node.js 18+ installed
- [ ] Ports 8080 and 5173 are free

Running:
- [ ] Backend running on port 8080
- [ ] Frontend running on port 5173
- [ ] Browser opened to http://localhost:5173
- [ ] Logged in successfully

---

## 🎉 You're All Set!

Your Medisur Health Insurance Management System is now running!

**Next Steps:**
1. Explore different user roles
2. Test policy purchase flow
3. Submit and review claims
4. Book appointments
5. Check out all dashboards

**Happy Testing! 🚀**

