import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Shield, DollarSign, FileText, Calendar, Plus, Eye } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { policyHolderAPI, claimAPI, appointmentAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Loading from '../../components/common/Loading';
import { useNavigate } from 'react-router-dom';

const PolicyHolderDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [policyData, setPolicyData] = useState(null);
  const [claims, setClaims] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const [policyRes, claimsRes, appointmentsRes] = await Promise.all([
        policyHolderAPI.getMyPolicy(),
        claimAPI.getMyClaims(),
        appointmentAPI.getMyAppointments(),
      ]);

      setPolicyData(policyRes.data.data);
      setClaims(claimsRes.data.data || []);
      setAppointments(appointmentsRes.data.data || []);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Loading message="Loading your dashboard..." />;
  }

  return (
    <div className="space-y-8">
      {/* Welcome Header */}
      <motion.div 
        className="text-center py-8"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
      >
        <h1 className="text-4xl font-bold text-gray-900 mb-2">
          Welcome back, {user.fullName}! 👋
        </h1>
        <p className="text-xl text-gray-600">
          Manage your health insurance policy and track your claims
        </p>
      </motion.div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <Card 
            className="bg-gradient-to-br from-blue-500 to-blue-600 text-white border-0 shadow-xl hover:shadow-2xl transition-all duration-300"
            hover={true}
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-blue-100 text-sm font-medium">Policy Status</p>
                <p className="text-3xl font-bold mt-2">{policyData?.status || 'N/A'}</p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl">
                <Shield className="w-8 h-8" />
              </div>
            </div>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <Card 
            className="bg-gradient-to-br from-emerald-500 to-emerald-600 text-white border-0 shadow-xl hover:shadow-2xl transition-all duration-300"
            hover={true}
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-emerald-100 text-sm font-medium">Coverage Amount</p>
                <p className="text-2xl font-bold mt-2">
                  ₹{policyData?.policy.coverageAmount?.toLocaleString() || '0'}
                </p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl">
                <DollarSign className="w-8 h-8" />
              </div>
            </div>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          <Card 
            className="bg-gradient-to-br from-purple-500 to-purple-600 text-white border-0 shadow-xl hover:shadow-2xl transition-all duration-300"
            hover={true}
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-purple-100 text-sm font-medium">Total Claims</p>
                <p className="text-3xl font-bold mt-2">{claims.length}</p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl">
                <FileText className="w-8 h-8" />
              </div>
            </div>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <Card 
            className="bg-gradient-to-br from-orange-500 to-orange-600 text-white border-0 shadow-xl hover:shadow-2xl transition-all duration-300"
            hover={true}
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-orange-100 text-sm font-medium">Appointments</p>
                <p className="text-3xl font-bold mt-2">{appointments.length}</p>
              </div>
              <div className="p-3 bg-white/20 rounded-xl">
                <Calendar className="w-8 h-8" />
              </div>
            </div>
          </Card>
        </motion.div>
      </div>

      {/* Policy Details */}
      {policyData && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
        >
          <Card 
            title="My Policy Details" 
            icon={<Shield className="w-6 h-6" />}
            className="shadow-xl"
          >
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <div className="p-4 bg-blue-50 rounded-xl">
                <p className="text-sm text-blue-600 font-medium">Policy Name</p>
                <p className="text-lg font-bold text-gray-900 mt-1">{policyData.policy.name}</p>
              </div>
              <div className="p-4 bg-green-50 rounded-xl">
                <p className="text-sm text-green-600 font-medium">Policy Type</p>
                <p className="text-lg font-bold text-gray-900 mt-1">{policyData.policy.type}</p>
              </div>
              <div className="p-4 bg-purple-50 rounded-xl">
                <p className="text-sm text-purple-600 font-medium">Premium Amount</p>
                <p className="text-lg font-bold text-gray-900 mt-1">₹{policyData.policy.premiumAmount}</p>
              </div>
              <div className="p-4 bg-orange-50 rounded-xl">
                <p className="text-sm text-orange-600 font-medium">Start Date</p>
                <p className="text-lg font-bold text-gray-900 mt-1">{policyData.startDate}</p>
              </div>
              <div className="p-4 bg-red-50 rounded-xl">
                <p className="text-sm text-red-600 font-medium">End Date</p>
                <p className="text-lg font-bold text-gray-900 mt-1">{policyData.endDate}</p>
              </div>
              <div className="p-4 bg-emerald-50 rounded-xl">
                <p className="text-sm text-emerald-600 font-medium">Status</p>
                <span className={`inline-block px-3 py-1 rounded-full text-sm font-semibold mt-1 ${
                  policyData.status === 'ACTIVE' 
                    ? 'bg-emerald-100 text-emerald-800' 
                    : 'bg-gray-100 text-gray-800'
                }`}>
                  {policyData.status}
                </span>
              </div>
            </div>
          </Card>
        </motion.div>
      )}

      {/* Quick Actions */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.6 }}
      >
        <Card 
          title="Quick Actions" 
          icon={<Plus className="w-6 h-6" />}
          className="shadow-xl"
        >
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Button 
              variant="primary" 
              onClick={() => navigate('/claims/submit')}
              icon={<FileText className="w-5 h-5" />}
              className="h-16"
            >
              Submit Claim
            </Button>
            <Button 
              variant="outline" 
              onClick={() => navigate('/claims/my')}
              icon={<Eye className="w-5 h-5" />}
              className="h-16"
            >
              View My Claims
            </Button>
            <Button 
              variant="primary" 
              onClick={() => navigate('/appointments/book')}
              icon={<Calendar className="w-5 h-5" />}
              className="h-16"
            >
              Book Appointment
            </Button>
            <Button 
              variant="outline" 
              onClick={() => navigate('/appointments/my')}
              icon={<Calendar className="w-5 h-5" />}
              className="h-16"
            >
              My Appointments
            </Button>
          </div>
        </Card>
      </motion.div>

      {/* Recent Claims */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.7 }}
      >
        <Card 
          title="Recent Claims" 
          icon={<FileText className="w-6 h-6" />}
          headerAction={
            <Button 
              size="sm" 
              variant="outline"
              onClick={() => navigate('/claims/my')}
              icon={<Eye className="w-4 h-4" />}
            >
              View All
            </Button>
          }
          className="shadow-xl"
        >
          {claims.length > 0 ? (
            <div className="space-y-4">
              {claims.slice(0, 3).map((claim, index) => (
                <motion.div 
                  key={claim.id} 
                  className="border border-gray-200 rounded-xl p-4 hover:bg-gray-50 transition-all duration-200 hover:shadow-md"
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 0.8 + index * 0.1 }}
                >
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <p className="font-bold text-lg text-gray-900">Claim #{claim.id}</p>
                      <p className="text-gray-600 mt-1">{claim.description}</p>
                      <p className="text-sm text-gray-500 mt-2">
                        📅 {new Date(claim.claimDate).toLocaleDateString()}
                      </p>
                    </div>
                    <div className="text-right ml-4">
                      <p className="font-bold text-xl text-gray-900">₹{claim.amountClaimed}</p>
                      <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold mt-2 ${
                        claim.status === 'APPROVED_BY_FINANCE' ? 'bg-emerald-100 text-emerald-800' :
                        claim.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                        'bg-amber-100 text-amber-800'
                      }`}>
                        {claim.status.replace(/_/g, ' ')}
                      </span>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <div className="p-4 bg-gray-100 rounded-full w-16 h-16 mx-auto mb-4 flex items-center justify-center">
                <FileText className="w-8 h-8 text-gray-400" />
              </div>
              <p className="text-gray-500 text-lg font-medium">No claims yet</p>
              <p className="text-gray-400 text-sm mt-1">Submit your first claim to get started</p>
            </div>
          )}
        </Card>
      </motion.div>

      {/* Upcoming Appointments */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.8 }}
      >
        <Card 
          title="Upcoming Appointments" 
          icon={<Calendar className="w-6 h-6" />}
          className="shadow-xl"
        >
          {appointments.length > 0 ? (
            <div className="space-y-4">
              {appointments.slice(0, 3).map((appointment, index) => (
                <motion.div 
                  key={appointment.id} 
                  className="border border-gray-200 rounded-xl p-4 hover:bg-gray-50 transition-all duration-200 hover:shadow-md"
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 0.9 + index * 0.1 }}
                >
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <p className="font-bold text-lg text-gray-900">Dr. {appointment.doctor.user.fullName}</p>
                      <p className="text-gray-600 mt-1">{appointment.doctor.specialization}</p>
                      <p className="text-sm text-gray-500 mt-2 flex items-center gap-2">
                        <Calendar className="w-4 h-4" />
                        {appointment.appointmentDate} at {appointment.appointmentTime}
                      </p>
                    </div>
                    <span className={`inline-block px-3 py-1 rounded-full text-xs font-semibold ${
                      appointment.status === 'CONFIRMED' ? 'bg-emerald-100 text-emerald-800' :
                      appointment.status === 'SCHEDULED' ? 'bg-blue-100 text-blue-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {appointment.status}
                    </span>
                  </div>
                </motion.div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <div className="p-4 bg-gray-100 rounded-full w-16 h-16 mx-auto mb-4 flex items-center justify-center">
                <Calendar className="w-8 h-8 text-gray-400" />
              </div>
              <p className="text-gray-500 text-lg font-medium">No upcoming appointments</p>
              <p className="text-gray-400 text-sm mt-1">Book an appointment to get started</p>
            </div>
          )}
        </Card>
      </motion.div>
    </div>
  );
};

export default PolicyHolderDashboard;

