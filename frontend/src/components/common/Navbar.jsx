import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Heart, LogOut, User } from 'lucide-react';
import Button from './Button';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <motion.nav 
      className="bg-white shadow-lg border-b border-gray-100"
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-20">
          <div className="flex items-center">
            <div className="flex-shrink-0 flex items-center gap-3">
              <div className="p-2 bg-primary-100 rounded-xl">
                <Heart className="w-8 h-8 text-primary-600" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">
                  Medisure
                </h1>
                <p className="text-sm text-gray-600 -mt-1">
                  Health Insurance Management
                </p>
              </div>
            </div>
          </div>
          
          {user && (
            <motion.div 
              className="flex items-center gap-4"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
            >
              <div className="flex items-center gap-3 px-4 py-2 bg-gray-50 rounded-xl">
                <div className="p-2 bg-primary-100 rounded-lg">
                  <User className="w-4 h-4 text-primary-600" />
                </div>
                <div className="text-right">
                  <p className="text-sm font-semibold text-gray-900">{user.fullName}</p>
                  <p className="text-xs text-gray-600 capitalize">{user.role.replace(/_/g, ' ').toLowerCase()}</p>
                </div>
              </div>
              <Button
                variant="danger"
                size="sm"
                onClick={handleLogout}
                icon={<LogOut size={16} />}
              >
                Logout
              </Button>
            </motion.div>
          )}
        </div>
      </div>
    </motion.nav>
  );
};

export default Navbar;

