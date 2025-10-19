import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { motion } from 'framer-motion';
import { 
  BarChart3, Users, FileText, ClipboardList, Ticket, 
  TrendingUp, FileBarChart, Handshake, Headphones, 
  UserCheck, Clock, DollarSign, Stethoscope, Plus, 
  Calendar, CalendarDays, Search, Shield
} from 'lucide-react';

const Sidebar = () => {
  const { user } = useAuth();
  const location = useLocation();

  const menuItems = {
    ADMIN: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/users', label: 'User Management', icon: Users },
      { path: '/policies', label: 'Policies', icon: FileText },
      { path: '/claims', label: 'Claims', icon: ClipboardList },
      { path: '/tickets', label: 'Support Tickets', icon: Ticket },
    ],
    OPERATION_MANAGER: [
      { path: '/dashboard', label: 'Analytics Dashboard', icon: BarChart3 },
      { path: '/policies', label: 'Policy Management', icon: FileText },
      { path: '/analytics', label: 'System Analytics', icon: TrendingUp },
      { path: '/reports', label: 'Reports', icon: FileBarChart },
    ],
    SALES_OFFICER: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/customers', label: 'Customer Engagement', icon: Handshake },
      { path: '/policies/browse', label: 'Available Policies', icon: FileText },
      { path: '/sales', label: 'Sales Reports', icon: DollarSign },
    ],
    CUSTOMER_SUPPORT_OFFICER: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/tickets', label: 'Support Tickets', icon: Ticket },
      { path: '/customers', label: 'Customer Support', icon: Headphones },
    ],
    POLICY_MANAGER: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/policies', label: 'Manage Policies', icon: FileText },
      { path: '/policy-holders', label: 'Policy Holders', icon: Users },
    ],
    CLAIMS_MANAGER: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/claims', label: 'Claims Queue', icon: ClipboardList },
      { path: '/claims/reviewed', label: 'Reviewed Claims', icon: UserCheck },
    ],
    FINANCE_MANAGER: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/finance/pending', label: 'Pending Claims', icon: Clock },
      { path: '/finance/records', label: 'Finance Records', icon: DollarSign },
    ],
    MEDICAL_COORDINATOR: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/doctors', label: 'Manage Doctors', icon: Stethoscope },
      { path: '/doctors/register', label: 'Register Doctor', icon: Plus },
    ],
    DOCTOR: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/appointments/today', label: "Today's Appointments", icon: Calendar },
      { path: '/appointments/all', label: 'All Appointments', icon: CalendarDays },
    ],
    POLICY_HOLDER: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/my-policy', label: 'My Policy', icon: Shield },
      { path: '/claims/submit', label: 'Submit Claim', icon: ClipboardList },
      { path: '/claims/my', label: 'My Claims', icon: FileText },
      { path: '/appointments/book', label: 'Book Appointment', icon: Calendar },
      { path: '/appointments/my', label: 'My Appointments', icon: CalendarDays },
    ],
    USER: [
      { path: '/dashboard', label: 'Dashboard', icon: BarChart3 },
      { path: '/policies/browse', label: 'Browse Policies', icon: Search },
    ],
  };

  const items = user ? menuItems[user.role] || [] : [];

  return (
    <motion.div 
      className="w-72 bg-gradient-to-b from-slate-900 to-slate-800 min-h-screen text-white border-r border-slate-700"
      initial={{ x: -300 }}
      animate={{ x: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="p-6">
        <motion.h2 
          className="text-2xl font-bold mb-8 text-center"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
        >
          Navigation
        </motion.h2>
        <nav>
          <ul className="space-y-3">
            {items.map((item, index) => {
              const isActive = location.pathname === item.path;
              const IconComponent = item.icon;
              
              return (
                <motion.li 
                  key={item.path}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 0.1 * index }}
                >
                  <Link
                    to={item.path}
                    className={`flex items-center gap-4 px-4 py-4 rounded-xl transition-all duration-300 group ${
                      isActive
                        ? 'bg-primary-600 text-white shadow-lg shadow-primary-600/25'
                        : 'text-slate-300 hover:bg-slate-700 hover:text-white hover:translate-x-1'
                    }`}
                  >
                    <div className={`p-2 rounded-lg transition-colors ${
                      isActive 
                        ? 'bg-primary-500' 
                        : 'bg-slate-600 group-hover:bg-slate-500'
                    }`}>
                      <IconComponent className="w-5 h-5" />
                    </div>
                    <span className="font-medium">{item.label}</span>
                  </Link>
                </motion.li>
              );
            })}
          </ul>
        </nav>
      </div>
    </motion.div>
  );
};

export default Sidebar;

