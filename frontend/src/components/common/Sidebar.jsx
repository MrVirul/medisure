import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Sidebar = () => {
  const { user } = useAuth();
  const location = useLocation();

  const menuItems = {
    ADMIN: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/users', label: 'User Management', icon: '👥' },
      { path: '/policies', label: 'Policies', icon: '📋' },
      { path: '/claims', label: 'Claims', icon: '📝' },
      { path: '/tickets', label: 'Support Tickets', icon: '🎫' },
    ],
    OPERATION_MANAGER: [
      { path: '/dashboard', label: 'Analytics Dashboard', icon: '📊' },
      { path: '/policies', label: 'Policy Management', icon: '📋' },
      { path: '/analytics', label: 'System Analytics', icon: '📈' },
      { path: '/reports', label: 'Reports', icon: '📄' },
    ],
    SALES_OFFICER: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/customers', label: 'Customer Engagement', icon: '🤝' },
      { path: '/policies/browse', label: 'Available Policies', icon: '📋' },
      { path: '/sales', label: 'Sales Reports', icon: '💰' },
    ],
    CUSTOMER_SUPPORT_OFFICER: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/tickets', label: 'Support Tickets', icon: '🎫' },
      { path: '/customers', label: 'Customer Support', icon: '🎧' },
    ],
    POLICY_MANAGER: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/policies', label: 'Manage Policies', icon: '📋' },
      { path: '/policy-holders', label: 'Policy Holders', icon: '👥' },
    ],
    CLAIMS_MANAGER: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/claims', label: 'Claims Queue', icon: '📝' },
      { path: '/claims/reviewed', label: 'Reviewed Claims', icon: '✅' },
    ],
    FINANCE_MANAGER: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/finance/pending', label: 'Pending Claims', icon: '⏳' },
      { path: '/finance/records', label: 'Finance Records', icon: '💰' },
    ],
    MEDICAL_COORDINATOR: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/doctors', label: 'Manage Doctors', icon: '👨‍⚕️' },
      { path: '/doctors/register', label: 'Register Doctor', icon: '➕' },
    ],
    DOCTOR: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/appointments/today', label: "Today's Appointments", icon: '📅' },
      { path: '/appointments/all', label: 'All Appointments', icon: '📆' },
    ],
    POLICY_HOLDER: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/my-policy', label: 'My Policy', icon: '📋' },
      { path: '/claims/submit', label: 'Submit Claim', icon: '📝' },
      { path: '/claims/my', label: 'My Claims', icon: '📄' },
      { path: '/appointments/book', label: 'Book Appointment', icon: '📅' },
      { path: '/appointments/my', label: 'My Appointments', icon: '📆' },
    ],
    USER: [
      { path: '/dashboard', label: 'Dashboard', icon: '📊' },
      { path: '/policies/browse', label: 'Browse Policies', icon: '🔍' },
    ],
  };

  const items = user ? menuItems[user.role] || [] : [];

  return (
    <div className="w-64 bg-gray-900 min-h-screen text-white">
      <div className="p-4">
        <h2 className="text-xl font-bold mb-6">Menu</h2>
        <nav>
          <ul className="space-y-2">
            {items.map((item) => {
              const isActive = location.pathname === item.path;
              return (
                <li key={item.path}>
                  <Link
                    to={item.path}
                    className={`flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${
                      isActive
                        ? 'bg-primary-600 text-white'
                        : 'text-gray-300 hover:bg-gray-800 hover:text-white'
                    }`}
                  >
                    <span className="text-xl">{item.icon}</span>
                    <span>{item.label}</span>
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>
      </div>
    </div>
  );
};

export default Sidebar;

