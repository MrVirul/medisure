import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import DashboardLayout from './layouts/DashboardLayout';

// Dashboard imports
import PolicyHolderDashboard from './pages/dashboards/PolicyHolderDashboard';
import AdminDashboard from './pages/dashboards/AdminDashboard';
import ClaimsManagerDashboard from './pages/dashboards/ClaimsManagerDashboard';
import DoctorDashboard from './pages/dashboards/DoctorDashboard';
import PolicyManagerDashboard from './pages/dashboards/PolicyManagerDashboard';
import FinanceManagerDashboard from './pages/dashboards/FinanceManagerDashboard';
import MedicalCoordinatorDashboard from './pages/dashboards/MedicalCoordinatorDashboard';
import OperationManagerDashboard from './pages/dashboards/OperationManagerDashboard';
import SalesOfficerDashboard from './pages/dashboards/SalesOfficerDashboard';
import CustomerSupportDashboard from './pages/dashboards/CustomerSupportDashboard';

// Other page imports
import BrowsePolicies from './pages/policies/BrowsePolicies';
import SubmitClaim from './pages/claims/SubmitClaim';
import MyClaims from './pages/claims/MyClaims';
import BookAppointment from './pages/appointments/BookAppointment';
import MyAppointments from './pages/appointments/MyAppointments';

// Dynamic Dashboard Router
const DashboardRouter = () => {
  const { user } = useAuth();
  
  if (!user) return <Navigate to="/login" replace />;
  
  switch (user.role) {
    case 'ADMIN':
      return <AdminDashboard />;
    case 'OPERATION_MANAGER':
      return <OperationManagerDashboard />;
    case 'POLICY_MANAGER':
      return <PolicyManagerDashboard />;
    case 'CLAIMS_MANAGER':
      return <ClaimsManagerDashboard />;
    case 'FINANCE_MANAGER':
      return <FinanceManagerDashboard />;
    case 'SALES_OFFICER':
      return <SalesOfficerDashboard />;
    case 'CUSTOMER_SUPPORT_OFFICER':
      return <CustomerSupportDashboard />;
    case 'MEDICAL_COORDINATOR':
      return <MedicalCoordinatorDashboard />;
    case 'DOCTOR':
      return <DoctorDashboard />;
    case 'POLICY_HOLDER':
      return <PolicyHolderDashboard />;
    default:
      return <PolicyHolderDashboard />;
  }
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          {/* Protected Dashboard */}
          <Route path="/dashboard" element={
            <ProtectedRoute>
              <DashboardLayout>
                <DashboardRouter />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Admin Routes */}
          <Route path="/users" element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <DashboardLayout>
                <AdminDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />

          {/* Operation Manager Routes */}
          <Route path="/analytics" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OPERATION_MANAGER']}>
              <DashboardLayout>
                <OperationManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />

          <Route path="/reports" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'OPERATION_MANAGER']}>
              <DashboardLayout>
                <OperationManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />

          {/* Sales Officer Routes */}
          <Route path="/customers" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'SALES_OFFICER']}>
              <DashboardLayout>
                <SalesOfficerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />

          <Route path="/sales" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'SALES_OFFICER']}>
              <DashboardLayout>
                <SalesOfficerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />

          {/* Customer Support Routes */}
          <Route path="/tickets" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'CUSTOMER_SUPPORT_OFFICER']}>
              <DashboardLayout>
                <CustomerSupportDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Policy Routes */}
          <Route path="/policies" element={
            <ProtectedRoute>
              <DashboardLayout>
                <PolicyManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/policies/browse" element={
            <ProtectedRoute>
              <DashboardLayout>
                <BrowsePolicies />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Claim Routes */}
          <Route path="/claims" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'CLAIMS_MANAGER']}>
              <DashboardLayout>
                <ClaimsManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/claims/submit" element={
            <ProtectedRoute allowedRoles={['POLICY_HOLDER']}>
              <DashboardLayout>
                <SubmitClaim />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/claims/my" element={
            <ProtectedRoute allowedRoles={['POLICY_HOLDER']}>
              <DashboardLayout>
                <MyClaims />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/claims/reviewed" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'CLAIMS_MANAGER']}>
              <DashboardLayout>
                <ClaimsManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Policy Holder Routes */}
          <Route path="/policy-holders" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'POLICY_MANAGER']}>
              <DashboardLayout>
                <PolicyManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/my-policy" element={
            <ProtectedRoute allowedRoles={['POLICY_HOLDER']}>
              <DashboardLayout>
                <PolicyHolderDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Finance Routes */}
          <Route path="/finance/pending" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'FINANCE_MANAGER']}>
              <DashboardLayout>
                <FinanceManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/finance/records" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'FINANCE_MANAGER']}>
              <DashboardLayout>
                <FinanceManagerDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Doctor Routes */}
          <Route path="/doctors" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'MEDICAL_COORDINATOR']}>
              <DashboardLayout>
                <MedicalCoordinatorDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/doctors/register" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'MEDICAL_COORDINATOR']}>
              <DashboardLayout>
                <MedicalCoordinatorDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/appointments/today" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'DOCTOR']}>
              <DashboardLayout>
                <DoctorDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/appointments/all" element={
            <ProtectedRoute allowedRoles={['ADMIN', 'DOCTOR']}>
              <DashboardLayout>
                <DoctorDashboard />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Appointment Routes */}
          <Route path="/appointments/book" element={
            <ProtectedRoute allowedRoles={['POLICY_HOLDER']}>
              <DashboardLayout>
                <BookAppointment />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          <Route path="/appointments/my" element={
            <ProtectedRoute allowedRoles={['POLICY_HOLDER']}>
              <DashboardLayout>
                <MyAppointments />
              </DashboardLayout>
            </ProtectedRoute>
          } />
          
          {/* Default Route */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
