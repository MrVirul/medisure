# 🔧 Dashboard Loading Issue - Troubleshooting Guide

## Issue Observed
Dashboard shows loading spinner indefinitely with large cursor icon.

---

## ✅ Quick Fix Steps

### **Step 1: Check Browser Console**
1. Open browser DevTools (Press F12 or right-click → Inspect)
2. Go to **Console** tab
3. Look for errors (red text)
4. Look for these log messages:
   - "Fetching users..."
   - "Users response: ..."
   - Any error messages

### **Step 2: Check Network Tab**
1. In DevTools, go to **Network** tab
2. Refresh the page (Ctrl/Cmd + R)
3. Look for request to `/api/admin/users`
4. Check its status:
   - ✅ **200 OK** = Working
   - ❌ **401 Unauthorized** = Not logged in
   - ❌ **403 Forbidden** = No permission
   - ❌ **500 Error** = Backend issue
   - ❌ **CORS Error** = CORS misconfiguration

---

## 🎯 Most Common Issues & Solutions

### **Issue 1: Not Logged In**
**Symptom**: 401 Unauthorized in Network tab

**Solution**:
```bash
# Make sure you're logged in
1. Go to http://localhost:5173/login
2. Login with: admin@medisur.com / admin123
3. Should redirect to /dashboard
```

### **Issue 2: Backend Not Running**
**Symptom**: Failed to fetch, ERR_CONNECTION_REFUSED

**Solution**:
```bash
# Terminal 1: Start Backend
cd "/Users/virul/My Projects/medisure"
mvn spring-boot:run

# Wait for: "Started MedisureApplication"
```

### **Issue 3: CORS Error**
**Symptom**: "CORS policy" error in console

**Solution**: Backend should already be configured for CORS. Check:
```properties
# In application.properties
spring.web.cors.allowed-origins=http://localhost:5173
```

### **Issue 4: Old Build Cache**
**Symptom**: Code changes not reflecting

**Solution**:
```bash
# Clear cache and restart
cd medisur-frontend
rm -rf node_modules/.vite
npm run dev
```

---

## 🚀 Complete Restart Procedure

If still having issues, do a complete restart:

```bash
# Step 1: Stop everything
# Press Ctrl+C in all terminal windows

# Step 2: Start Backend
cd "/Users/virul/My Projects/medisure"
mvn spring-boot:run

# Step 3: Wait for backend to start
# Look for: "Started MedisureApplication in X seconds"

# Step 4: Start Frontend (in new terminal)
cd "/Users/virul/My Projects/medisure/medisur-frontend"
npm run dev

# Step 5: Open browser
# Go to: http://localhost:5173
# Login: admin@medisur.com / admin123
```

---

## 🔍 Manual Test

Test the API directly:

```bash
# Login and get token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@medisur.com","password":"admin123"}'

# Copy the token from response, then:
curl http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# Should return list of users
```

---

## 💡 What I Fixed

Updated `AdminDashboard.jsx` to:
1. ✅ Always set `loading = false` even on error
2. ✅ Add console.log for debugging
3. ✅ Set empty array on error (prevents crash)
4. ✅ Better error logging

---

## 📊 Expected Console Output

When working correctly, you should see:
```
Fetching users...
Users response: {data: {…}, status: 200, …}
```

When there's an error, you should see:
```
Error fetching users: AxiosError {...}
Error details: {...}
```

---

## 🎨 What You Should See

After fixing, the dashboard should display:

1. **Gradient background** (blue → white → purple)
2. **Header card** with "Admin Dashboard" title
3. **4 statistics cards** (Blue, Green, Purple, Orange)
4. **Create Employee button**
5. **Users table** with search bar

---

## ⚡ Quick Debug Commands

```bash
# Check if backend is running
curl http://localhost:8080/api/policies/all

# Check if frontend is running
curl http://localhost:5173

# Check backend logs
tail -f app.log

# Check frontend in browser console
# Look for: "Fetching users..." message
```

---

## 🔥 Nuclear Option (Complete Reset)

If nothing works:

```bash
# 1. Kill all Java processes
pkill -f "spring-boot:run"
pkill -f "java"

# 2. Kill all Node processes
pkill -f "vite"
pkill -f "node"

# 3. Clear all caches
cd medisur-frontend
rm -rf node_modules/.vite
rm -rf dist

# 4. Restart backend
cd "/Users/virul/My Projects/medisure"
mvn clean spring-boot:run

# 5. Restart frontend (new terminal)
cd medisur-frontend
npm run dev

# 6. Clear browser cache
# In Chrome: Ctrl+Shift+Del → Clear cached images and files
```

---

## 📞 Debug Checklist

- [ ] Backend is running on port 8080
- [ ] Frontend is running on port 5173
- [ ] Browser console shows no errors
- [ ] Network tab shows 200 OK for API calls
- [ ] LocalStorage has 'token' and 'user'
- [ ] User is logged in
- [ ] Console shows "Fetching users..." message

---

## 🎯 Next Steps

1. Open browser DevTools (F12)
2. Refresh the page
3. Check Console tab for errors
4. Share any error messages you see
5. Check Network tab for failed requests

---

**If you see errors in the console, that will tell us exactly what's wrong!**

The updated code will now:
- ✅ Always stop loading (even on error)
- ✅ Show helpful console messages
- ✅ Display empty table if API fails
- ✅ Not get stuck in loading state

**Try refreshing the page now and check the browser console!**

