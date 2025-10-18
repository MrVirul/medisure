# Medisur Health Insurance Management System - Backend

## 🩺 Overview
Complete Spring Boot backend for the Medisur Health Insurance Management System with H2 database, JWT authentication, and role-based access control.

## ✅ Features Implemented

### 1. **Authentication & Authorization**
- JWT-based authentication
- Role-based access control (RBAC)
- Password encryption with BCrypt
- 8 User Roles: ADMIN, POLICY_MANAGER, CLAIMS_MANAGER, FINANCE_MANAGER, MEDICAL_COORDINATOR, DOCTOR, POLICY_HOLDER, USER

### 2. **Entities**
- **User** - All system users with roles
- **Policy** - Insurance policies (BASIC, PREMIUM, FAMILY, SENIOR)
- **PolicyHolder** - Users who purchased policies
- **Doctor** - Registered doctors
- **Appointment** - Doctor appointments (Premium policy holders only)
- **Claim** - Insurance claims
- **ClaimDocument** - Claim supporting documents
- **FinanceRecord** - Finance approval records
- **AuditLog** - System audit trail

### 3. **API Endpoints**

#### Authentication (`/api/auth`)
- `POST /register` - User registration
- `POST /login` - User login
- `GET /me` - Get current user

#### Policies (`/api/policies`)
- `GET /all` - Get all active policies (public)
- `GET /` - Get all policies (admin/policy manager)
- `POST /` - Create policy (admin/policy manager)
- `PUT /{id}` - Update policy
- `DELETE /{id}` - Deactivate policy

#### Policy Holders (`/api/policy-holder`)
- `POST /purchase/{policyId}` - Purchase policy
- `GET /my-policy` - Get my policy details
- `GET /all` - Get all policy holders (admin)
- `PUT /{id}/status` - Update policy status

#### Doctors (`/api/doctor`)
- `GET /all` - Get all available doctors
- `POST /register` - Register doctor (medical coordinator)
- `GET /my-appointments` - Doctor's appointments
- `GET /today-appointments` - Today's appointments
- `PUT /appointment/{id}/status` - Update appointment status

#### Appointments (`/api/appointments`)
- `POST /` - Book appointment (premium policy holders)
- `GET /my-appointments` - Get my appointments
- `GET /{id}` - Get appointment by ID

#### Claims (`/api/claims`)
- `POST /` - Submit claim (policy holders)
- `POST /{id}/upload-document` - Upload claim document
- `GET /my-claims` - Get my claims
- `GET /` - Get all claims (managers)
- `GET /status/{status}` - Get claims by status
- `PUT /{id}/review` - Review claim (claims manager)
- `PUT /{id}/forward-to-finance` - Forward to finance

#### Finance (`/api/finance`)
- `POST /process-claim/{claimId}` - Process claim (finance manager)
- `GET /records` - Get all finance records
- `GET /records/{id}` - Get finance record by ID
- `GET /records/claim/{claimId}` - Get records by claim

#### Admin (`/api/admin`)
- `GET /users` - Get all users
- `POST /create-employee` - Create employee account
- `PUT /users/{id}/role` - Change user role
- `DELETE /users/{id}` - Delete user

#### Payments (`/api/payments`)
- `POST /dummy` - Dummy payment endpoint

### 4. **File Storage**
- Local file storage for claim documents
- PDF generation for policy certificates
- Organized folder structure

### 5. **Database**
- **H2 In-Memory Database**
- **URL**: `jdbc:h2:mem:medisur_db`
- **Console**: http://localhost:8080/h2-console
- **Username**: sa
- **Password**: password

### 6. **Initial Data**
Automatically created on startup:
- **Admin User**
  - Email: admin@medisur.com
  - Password: admin123

- **4 Sample Policies**
  - Basic Health Insurance (₹100,000 coverage, ₹5,000 premium)
  - Premium Health Insurance (₹500,000 coverage, ₹15,000 premium)
  - Family Health Insurance (₹750,000 coverage, ₹25,000 premium)
  - Senior Citizen Health Insurance (₹300,000 coverage, ₹20,000 premium)

## 🚀 Running the Backend

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Start the Application
```bash
mvn spring-boot:run
```

The backend will start on **http://localhost:8080**

## 📁 Project Structure
```
src/main/java/com/virul/medisure/
├── config/
│   ├── DataInitializer.java
│   └── SecurityConfig.java
├── controller/
│   ├── AdminController.java
│   ├── AppointmentController.java
│   ├── AuthController.java
│   ├── ClaimController.java
│   ├── DoctorController.java
│   ├── FinanceController.java
│   ├── PaymentController.java
│   ├── PolicyController.java
│   └── PolicyHolderController.java
├── dto/
│   ├── ApiResponse.java
│   ├── AppointmentRequest.java
│   ├── AuthResponse.java
│   ├── ClaimRequest.java
│   ├── DoctorRequest.java
│   ├── LoginRequest.java
│   ├── PolicyRequest.java
│   └── RegisterRequest.java
├── model/
│   ├── Appointment.java
│   ├── AuditLog.java
│   ├── Claim.java
│   ├── ClaimDocument.java
│   ├── Doctor.java
│   ├── FinanceRecord.java
│   ├── Policy.java
│   ├── PolicyHolder.java
│   └── User.java
├── repository/
│   ├── AppointmentRepository.java
│   ├── AuditLogRepository.java
│   ├── ClaimDocumentRepository.java
│   ├── ClaimRepository.java
│   ├── DoctorRepository.java
│   ├── FinanceRecordRepository.java
│   ├── PolicyHolderRepository.java
│   ├── PolicyRepository.java
│   └── UserRepository.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
├── service/
│   ├── AppointmentService.java
│   ├── AuditLogService.java
│   ├── AuthService.java
│   ├── ClaimService.java
│   ├── DoctorService.java
│   ├── FileStorageService.java
│   ├── FinanceService.java
│   ├── PdfService.java
│   ├── PolicyHolderService.java
│   ├── PolicyService.java
│   └── UserService.java
└── MedisureApplication.java
```

## 🔐 API Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## 📊 Workflow Example

1. **User Registration**: User registers → becomes USER role
2. **Policy Purchase**: User purchases policy → role changes to POLICY_HOLDER
3. **Claim Submission**: Policy holder submits claim with documents
4. **Claim Review**: Claims Manager reviews → approves/rejects/forwards
5. **Finance Approval**: Finance Manager processes → approves/rejects
6. **Appointment Booking**: Premium policy holders book doctor appointments
7. **Appointment Management**: Doctors mark appointments as completed/cancelled

## ✅ Status
✅ All entities created
✅ All repositories implemented
✅ JWT authentication configured
✅ All services implemented
✅ All controllers with endpoints
✅ File upload/storage
✅ PDF generation for policies
✅ Audit logging
✅ Initial data seeding
✅ Compilation successful
✅ Application running on port 8080

## 🎯 Next Steps
Now building the React frontend with Vite + TailwindCSS to consume these APIs.

