# 🏗️ Medisur System Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      MEDISUR HEALTH INSURANCE                    │
│                    MANAGEMENT SYSTEM ARCHITECTURE                │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                         FRONTEND LAYER                            │
│                      (React 18 + Vite)                           │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌───────────────┐  ┌───────────────┐  ┌────────────────┐      │
│  │  Components   │  │     Pages     │  │   Context API  │      │
│  │               │  │               │  │                │      │
│  │ • Button      │  │ • Login       │  │ • AuthContext  │      │
│  │ • Card        │  │ • Register    │  │ • UserState    │      │
│  │ • Input       │  │ • Dashboards  │  │                │      │
│  │ • Navbar      │  │ • Claims      │  └────────────────┘      │
│  │ • Sidebar     │  │ • Policies    │                          │
│  └───────────────┘  └───────────────┘                          │
│                                                                   │
│  ┌────────────────────────────────────────────────────┐        │
│  │              API Service Layer (Axios)              │        │
│  │  • Authentication • Policies • Claims • Doctors     │        │
│  └────────────────────────────────────────────────────┘        │
│                           ▼                                       │
│                  JWT Token (LocalStorage)                        │
│                           ▼                                       │
│              HTTP Requests with Authorization                    │
└──────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTPS/REST API
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│                        BACKEND LAYER                              │
│                   (Spring Boot 3 + Maven)                        │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌───────────────────────────────────────────────────────┐      │
│  │              SECURITY LAYER                            │      │
│  │  ┌──────────────┐  ┌──────────────┐  ┌────────────┐  │      │
│  │  │ JWT Filter   │→ │ SecurityConfig│→ │ Password   │  │      │
│  │  │              │  │               │  │ Encoder    │  │      │
│  │  └──────────────┘  └──────────────┘  └────────────┘  │      │
│  └───────────────────────────────────────────────────────┘      │
│                                                                   │
│  ┌───────────────────────────────────────────────────────┐      │
│  │              CONTROLLER LAYER (REST APIs)              │      │
│  │                                                         │      │
│  │  • AuthController        • ClaimController            │      │
│  │  • PolicyController      • AppointmentController      │      │
│  │  • AdminController       • DoctorController           │      │
│  │  • PolicyHolderController • FinanceController         │      │
│  └───────────────────────────────────────────────────────┘      │
│                              ▼                                    │
│  ┌───────────────────────────────────────────────────────┐      │
│  │                  SERVICE LAYER                         │      │
│  │  (Business Logic + Validation)                         │      │
│  │                                                         │      │
│  │  • UserService          • ClaimService                │      │
│  │  • PolicyService        • AppointmentService          │      │
│  │  • AuthService          • FileStorageService          │      │
│  │  • DoctorService        • FinanceService              │      │
│  │  • AuditLogService      • PdfService                  │      │
│  └───────────────────────────────────────────────────────┘      │
│                              ▼                                    │
│  ┌───────────────────────────────────────────────────────┐      │
│  │              REPOSITORY LAYER (JPA)                    │      │
│  │                                                         │      │
│  │  • UserRepository       • ClaimRepository             │      │
│  │  • PolicyRepository     • AppointmentRepository       │      │
│  │  • PolicyHolderRepo     • DoctorRepository            │      │
│  │  • FinanceRecordRepo    • AuditLogRepository          │      │
│  └───────────────────────────────────────────────────────┘      │
│                              ▼                                    │
│  ┌───────────────────────────────────────────────────────┐      │
│  │                    ENTITY LAYER                        │      │
│  │  (Database Models with Relationships)                  │      │
│  │                                                         │      │
│  │  User ──┬── PolicyHolder ──┬── Claim ─── ClaimDocument│      │
│  │         │                   └── Appointment            │      │
│  │         └── Doctor ───────────┘                        │      │
│  │                                                         │      │
│  │  Policy ─────── PolicyHolder                          │      │
│  │  FinanceRecord ─── Claim                              │      │
│  │  AuditLog (System-wide)                               │      │
│  └───────────────────────────────────────────────────────┘      │
└──────────────────────────────────────────────────────────────────┘
                              │
                              │ JDBC
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│                       DATABASE LAYER                              │
│                    PostgreSQL (NeonDB Cloud)                     │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Tables:                                                          │
│  • users                • claims                                 │
│  • policy_holders       • claim_documents                        │
│  • policies             • appointments                           │
│  • doctors              • finance_records                        │
│  • audit_logs                                                    │
│                                                                   │
│  Backup DB: H2 Database (Embedded for testing)                  │
└──────────────────────────────────────────────────────────────────┘
                              │
                              │ File System
                              ▼
┌──────────────────────────────────────────────────────────────────┐
│                       FILE STORAGE                                │
│                    (Local Filesystem)                            │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  /uploads/                                                        │
│    └── policy-documents/                                         │
│         └── [claim-id]/                                          │
│              ├── document1.pdf                                   │
│              ├── document2.jpg                                   │
│              └── document3.png                                   │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## 🔐 Authentication Flow

```
┌──────────┐                  ┌──────────┐                 ┌──────────┐
│ Frontend │                  │  Backend │                 │ Database │
└─────┬────┘                  └────┬─────┘                 └────┬─────┘
      │                            │                            │
      │ 1. POST /api/auth/login   │                            │
      │ {email, password}          │                            │
      ├───────────────────────────>│                            │
      │                            │ 2. Check credentials       │
      │                            ├───────────────────────────>│
      │                            │<───────────────────────────┤
      │                            │ 3. Generate JWT token      │
      │                            │    (with user details)     │
      │ 4. Return JWT + User Data  │                            │
      │<───────────────────────────┤                            │
      │                            │                            │
      │ 5. Store in localStorage   │                            │
      │    • token                 │                            │
      │    • user details          │                            │
      │                            │                            │
      │ 6. All future requests     │                            │
      │    include Authorization   │                            │
      │    header with JWT         │                            │
      ├───────────────────────────>│                            │
      │                            │ 7. Validate token          │
      │                            │    Extract user info       │
      │                            │    Check role permissions  │
      │                            │                            │
      │ 8. Return requested data   │                            │
      │<───────────────────────────┤                            │
      │                            │                            │
```

---

## 👥 User Role Hierarchy

```
                        ┌─────────────┐
                        │    ADMIN    │
                        │  (Root)     │
                        └──────┬──────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
  ┌─────▼─────┐         ┌─────▼─────┐        ┌──────▼──────┐
  │  POLICY   │         │  CLAIMS   │        │  FINANCE    │
  │  MANAGER  │         │  MANAGER  │        │  MANAGER    │
  └───────────┘         └───────────┘        └─────────────┘
        │                      │                      │
        │ Manages              │ Reviews              │ Approves
        │ Policies             │ Claims               │ Payments
        │                      │                      │
        ▼                      ▼                      ▼
  ┌──────────────────────────────────────────────────────┐
  │              POLICY HOLDER                           │
  │  (Purchases Policies, Submits Claims, Books Appts)   │
  └──────────────────────────────────────────────────────┘
                              │
                              │ Books
                              ▼
  ┌──────────────────────────────────────────────────────┐
  │                   DOCTOR                              │
  │  (Manages Appointments, Medical Consultations)        │
  └───────────────────────────▲──────────────────────────┘
                              │
                              │ Registered by
                              │
                    ┌─────────┴─────────┐
                    │     MEDICAL       │
                    │   COORDINATOR     │
                    └───────────────────┘
```

---

## 📊 Data Flow: Claim Submission

```
1. POLICY HOLDER
   │
   │ Submits Claim + Documents
   ▼
┌─────────────────────────────────────────┐
│  Frontend: SubmitClaim.jsx              │
│  • Fill form                            │
│  • Upload files (PDF/JPG/PNG)           │
│  • Submit                               │
└─────────────────┬───────────────────────┘
                  │
                  │ POST /api/claims
                  │ FormData with files
                  ▼
┌─────────────────────────────────────────┐
│  Backend: ClaimController               │
│  • Validate request                     │
│  • Save claim entity                    │
│  • Store files in filesystem            │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│  Database: claims table                 │
│  Status: PENDING                        │
└─────────────────┬───────────────────────┘
                  │
                  │
2. CLAIMS MANAGER │
                  │
┌─────────────────▼───────────────────────┐
│  Frontend: ClaimsManagerDashboard.jsx   │
│  • View pending claims                  │
│  • Review details                       │
│  • Approve/Reject with comments         │
└─────────────────┬───────────────────────┘
                  │
                  │ PUT /api/claims-manager/claims/{id}/review
                  │ {action: "APPROVE", comments: "..."}
                  ▼
┌─────────────────────────────────────────┐
│  Backend: ClaimsManagerController       │
│  • Update claim status                  │
│  • Add comments                         │
│  • Status: APPROVED_BY_CLAIMS_MANAGER   │
└─────────────────┬───────────────────────┘
                  │
                  │
3. FINANCE MANAGER│
                  │
┌─────────────────▼───────────────────────┐
│  Frontend: FinanceManagerDashboard.jsx  │
│  • View approved claims                 │
│  • Set approved amount                  │
│  • Process payment                      │
└─────────────────┬───────────────────────┘
                  │
                  │ POST /api/finance/process-claim/{id}
                  │ {action: "APPROVE", amountApproved: 10000}
                  ▼
┌─────────────────────────────────────────┐
│  Backend: FinanceController             │
│  • Update claim                         │
│  • Create finance record                │
│  • Status: APPROVED_BY_FINANCE          │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│  Notification to Policy Holder          │
│  Claim Approved ✓                       │
└─────────────────────────────────────────┘
```

---

## 🔒 Security Architecture

```
┌────────────────────────────────────────────────────────────┐
│                    SECURITY LAYERS                          │
└────────────────────────────────────────────────────────────┘

Layer 1: Transport Security
├── HTTPS (Production)
└── Secure Headers

Layer 2: Authentication
├── JWT Token Generation
├── Token Validation
├── Token Expiry (24 hours)
└── Refresh Token (7 days)

Layer 3: Password Security
├── BCrypt Hashing (Strength 10)
├── Salt per password
└── Never store plain text

Layer 4: Authorization
├── Role-Based Access Control (RBAC)
├── Method-Level Security (@PreAuthorize)
├── URL-Level Security (SecurityFilterChain)
└── Frontend Route Guards

Layer 5: Input Validation
├── Backend: @Valid annotations
├── Frontend: Form validation
├── SQL Injection Prevention (JPA)
└── XSS Prevention (React)

Layer 6: CORS
├── Allowed Origins: localhost:5173
├── Allowed Methods: GET, POST, PUT, DELETE
├── Credentials: true
└── Configurable per environment

Layer 7: File Upload Security
├── File type validation
├── File size limits (10MB)
├── Virus scanning (ready to add)
└── Secure storage path
```

---

## 📱 Frontend Component Tree

```
App.jsx
│
├── AuthProvider (Context)
│   └── Router
│       │
│       ├── Public Routes
│       │   ├── Login
│       │   └── Register
│       │
│       └── Protected Routes
│           └── DashboardLayout
│               ├── Navbar
│               │   ├── Logo
│               │   └── User Menu
│               │
│               ├── Sidebar (Role-based)
│               │   └── Navigation Items
│               │
│               └── Main Content
│                   │
│                   ├── Dashboards (8)
│                   │   ├── AdminDashboard
│                   │   ├── PolicyHolderDashboard
│                   │   ├── PolicyManagerDashboard
│                   │   ├── ClaimsManagerDashboard
│                   │   ├── FinanceManagerDashboard
│                   │   ├── MedicalCoordinatorDashboard
│                   │   └── DoctorDashboard
│                   │
│                   ├── Policy Pages
│                   │   └── BrowsePolicies
│                   │       └── PolicyCard (multiple)
│                   │
│                   ├── Claim Pages
│                   │   ├── SubmitClaim
│                   │   │   ├── Input fields
│                   │   │   └── File upload
│                   │   └── MyClaims
│                   │       └── Card (multiple)
│                   │
│                   └── Appointment Pages
│                       ├── BookAppointment
│                       │   ├── Doctor selection
│                       │   └── Date/time picker
│                       └── MyAppointments
│                           └── Card (multiple)
```

---

## 🗄️ Database Relationships

```
                      users
                        │
                        │ id (PK)
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
  policy_holders    doctors       (other users)
        │               │
        │               │
        ├───────────┐   │
        │           │   │
        ▼           ▼   ▼
    policies    claims  appointments
        │           │       │
        │           ▼       │
        │    claim_documents│
        │           │       │
        │           ▼       │
        │    finance_records│
        │                   │
        └───────────────────┘

Relationships:
• User ──1:1── PolicyHolder
• User ──1:1── Doctor
• Policy ──1:Many── PolicyHolder
• PolicyHolder ──1:Many── Claim
• Claim ──1:Many── ClaimDocument
• PolicyHolder ──Many:Many── Doctor (through Appointment)
• Claim ──1:1── FinanceRecord
```

---

## 🔄 CI/CD Pipeline (Recommended)

```
┌─────────────────────────────────────────────────────────┐
│                    SOURCE CODE                          │
│                  (GitHub Repository)                    │
└───────────────────────┬─────────────────────────────────┘
                        │
                        │ git push
                        ▼
┌─────────────────────────────────────────────────────────┐
│                 CI/CD SERVER                            │
│               (GitHub Actions)                          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Backend Pipeline:                                       │
│  1. Checkout code                                        │
│  2. Setup Java 17                                        │
│  3. Run mvn clean test                                   │
│  4. Run mvn package                                      │
│  5. Build Docker image                                   │
│  6. Push to registry                                     │
│                                                          │
│  Frontend Pipeline:                                      │
│  1. Checkout code                                        │
│  2. Setup Node.js 18                                     │
│  3. Run npm install                                      │
│  4. Run npm test (if tests exist)                        │
│  5. Run npm run build                                    │
│  6. Deploy to Vercel/Netlify                            │
│                                                          │
└───────────────────────┬─────────────────────────────────┘
                        │
                        │ deploy
                        ▼
┌─────────────────────────────────────────────────────────┐
│                  PRODUCTION                             │
│                                                          │
│  Backend: AWS/Heroku/GCP                                │
│  Frontend: Vercel/Netlify                               │
│  Database: NeonDB/AWS RDS                               │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

---

## 📊 Technology Stack Summary

```
┌────────────────────────────────────────────────────┐
│                  FRONTEND                          │
├────────────────────────────────────────────────────┤
│ Core:        React 18, JavaScript                  │
│ Build:       Vite 7                                │
│ Routing:     React Router 6                        │
│ Styling:     TailwindCSS 4                         │
│ HTTP:        Axios                                 │
│ State:       Context API                           │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│                   BACKEND                          │
├────────────────────────────────────────────────────┤
│ Framework:   Spring Boot 3.2.1                     │
│ Language:    Java 17                               │
│ Build:       Maven                                 │
│ Security:    Spring Security + JWT                 │
│ ORM:         Spring Data JPA + Hibernate           │
│ Validation:  Hibernate Validator                   │
│ PDF:         iText                                 │
│ Utils:       Lombok, Commons IO                    │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│                  DATABASE                          │
├────────────────────────────────────────────────────┤
│ Production:  PostgreSQL (Neon Cloud)               │
│ Testing:     H2 Database (Embedded)                │
│ ORM:         JPA/Hibernate                         │
└────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────┐
│                   TOOLS                            │
├────────────────────────────────────────────────────┤
│ Version Control: Git + GitHub                      │
│ API Testing:     Postman                           │
│ IDE:             IntelliJ IDEA / VS Code           │
│ DB Tool:         pgAdmin / DBeaver                 │
└────────────────────────────────────────────────────┘
```

---

## 🎯 System Capabilities

- ✅ Multi-role user management (8 roles)
- ✅ JWT-based authentication & authorization
- ✅ Policy creation and purchase workflow
- ✅ Multi-level claim approval process
- ✅ File upload and storage
- ✅ Appointment scheduling
- ✅ Doctor management
- ✅ Financial processing
- ✅ Audit logging
- ✅ Responsive UI for all devices
- ✅ RESTful API architecture
- ✅ Secure password storage
- ✅ Role-based dashboards
- ✅ Real-time data updates

---

**Architecture Status:** ✅ PRODUCTION-READY

