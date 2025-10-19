# Medisure Health Insurance Management System - Frontend

A modern, responsive React frontend for the Medisure Health Insurance Management System built with Vite, React Router, TailwindCSS, and Axios.

## 🚀 Features

### Authentication & Authorization
- ✅ JWT-based authentication
- ✅ Role-based access control (8 user roles)
- ✅ Protected routes
- ✅ Persistent sessions with localStorage
- ✅ Auto-redirect on unauthorized access

### User Roles & Dashboards
1. **Admin** - Complete system management
2. **Policy Manager** - Policy and policyholder management
3. **Claims Manager** - Claims review and approval
4. **Finance Manager** - Financial processing and approval
5. **Medical Coordinator** - Doctor management
6. **Doctor** - Appointment management
7. **Policy Holder** - Policy purchase, claims, appointments
8. **User** - Browse policies (unregistered)

### Core Functionality

#### Policy Management
- ✅ Browse available policies
- ✅ View policy details (coverage, premium, duration)
- ✅ Purchase policies
- ✅ Create and manage policies (Admin/Policy Manager)
- ✅ View policy holders

#### Claims Management
- ✅ Submit insurance claims with document uploads
- ✅ Track claim status
- ✅ Review and approve/reject claims (Claims Manager)
- ✅ Financial approval workflow (Finance Manager)
- ✅ View claim history with comments

#### Appointments
- ✅ Browse available doctors by specialization
- ✅ Book appointments with preferred doctors
- ✅ View appointment history
- ✅ Doctor appointment management
- ✅ Update appointment status

#### User Management
- ✅ User registration
- ✅ Employee creation (Admin)
- ✅ Doctor registration (Medical Coordinator)
- ✅ Role management

## 🛠️ Tech Stack

- **React 18** - UI library
- **Vite** - Build tool and dev server
- **React Router 6** - Client-side routing
- **TailwindCSS** - Utility-first CSS framework
- **Axios** - HTTP client
- **Context API** - State management

## 📦 Installation

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

## 🔧 Configuration

### Environment Setup
The frontend connects to the backend at `http://localhost:8080/api` by default.

To change the API URL, update `src/services/api.js`:

```javascript
const API_BASE_URL = 'http://your-backend-url/api';
```

### Vite Configuration
Vite is configured to run on port **5173** (default). The backend CORS is configured to accept requests from this origin.

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── common/          # Common components (Button, Card, Input, etc.)
│   │   └── PolicyCard.jsx   # Policy display component
│   ├── context/             # React Context for state management
│   │   └── AuthContext.jsx  # Authentication context
│   ├── layouts/             # Layout components
│   │   └── DashboardLayout.jsx
│   ├── pages/               # Page components
│   │   ├── auth/            # Login, Register
│   │   ├── dashboards/      # Role-based dashboards
│   │   ├── claims/          # Claim-related pages
│   │   ├── appointments/    # Appointment pages
│   │   └── policies/        # Policy browsing
│   ├── services/            # API service layer
│   │   └── api.js           # Axios configuration and API calls
│   ├── App.jsx              # Main app component with routing
│   ├── main.jsx             # Application entry point
│   └── index.css            # Global styles with Tailwind
├── public/                  # Static assets
├── index.html               # HTML template
├── vite.config.js           # Vite configuration
├── tailwind.config.js       # Tailwind configuration
└── package.json             # Dependencies and scripts
```

## 🎨 UI Components

### Common Components
- **Button** - Customizable button with variants (primary, secondary, success, danger, outline)
- **Card** - Content container with optional title and actions
- **Input** - Form input with label, validation, and error display
- **Select** - Dropdown select with options
- **Loading** - Loading spinner
- **Navbar** - Top navigation bar with user info
- **Sidebar** - Role-based navigation menu
- **Modal** - Reusable modal dialog
- **DataTable** - Dynamic data table with sorting
- **ProtectedRoute** - Route wrapper for authentication

### Specialized Components
- **PolicyCard** - Display policy information with purchase option

## 🔐 Authentication Flow

1. User logs in with email and password
2. Backend validates credentials and returns JWT token
3. Token is stored in localStorage
4. All API requests include the token in Authorization header
5. On 401 response, user is redirected to login
6. User data is stored in AuthContext for easy access

## 🚦 Routing

### Public Routes
- `/login` - User login
- `/register` - User registration

### Protected Routes
- `/dashboard` - Dynamic dashboard based on user role
- `/policies/browse` - Browse available policies
- `/claims/submit` - Submit a new claim
- `/claims/my` - View my claims
- `/appointments/book` - Book an appointment

## 📱 Responsive Design

The application is fully responsive and works on:
- Desktop (1920px+)
- Laptop (1024px - 1920px)
- Tablet (768px - 1024px)
- Mobile (320px - 768px)

## 🎨 Color Scheme

Primary colors are defined in `tailwind.config.js`:
- Primary Blue: `#0284c7` (blue-600)
- Shades from 50 to 900 for variations

## 🔄 API Integration

All API calls are centralized in `src/services/api.js`:

```javascript
// Example API calls
authAPI.login({ email, password })
policyAPI.getAllPolicies()
claimAPI.submitClaim(formData)
appointmentAPI.bookAppointment(data)
```

## 🧪 Testing

Currently, the project doesn't include automated tests. To add testing:

```bash
# Install testing libraries
npm install -D vitest @testing-library/react @testing-library/jest-dom

# Run tests
npm run test
```

## 🚀 Deployment

### Production Build

```bash
npm run build
```

This creates an optimized production build in the `dist/` directory.

### Deployment Options

1. **Vercel** (Recommended for Vite)
   ```bash
   npm install -g vercel
   vercel
   ```

2. **Netlify**
   ```bash
   npm install -g netlify-cli
   netlify deploy --prod
   ```

3. **Static Hosting** (Apache, Nginx)
   - Upload `dist/` contents to web server
   - Configure server for SPA routing

## 📝 Demo Credentials

```
Email: admin@medisur.com
Password: admin123
Role: ADMIN
```

Additional test users are created by the backend DataInitializer.

## 🔧 Development Tips

1. **Hot Module Replacement** - Changes reflect instantly during development
2. **React DevTools** - Use browser extension for debugging
3. **Network Tab** - Monitor API calls in browser DevTools
4. **Console Logging** - Check browser console for errors

## 🐛 Common Issues

### CORS Errors
Ensure the backend is running and CORS is configured to allow `http://localhost:5173`

### 401 Unauthorized
Token may be expired or invalid. Clear localStorage and login again.

### Build Errors
Clear node_modules and reinstall:
```bash
rm -rf node_modules package-lock.json
npm install
```

## 📄 License

This project is part of a university project for educational purposes.

## 👥 Contributors

- Virul (Project Lead)

## 📞 Support

For issues or questions, please contact the development team.
