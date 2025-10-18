import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { policyHolderAPI, claimAPI, appointmentAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
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
    return <div className="p-6">Loading...</div>;
  }

  return (
    <div className="p-6 space-y-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Welcome, {user.fullName}!</h1>
        <p className="text-gray-600 mt-1">Manage your health insurance policy and claims</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Policy Status</p>
            <p className="text-3xl font-bold mt-2">{policyData?.status || 'N/A'}</p>
          </div>
        </Card>

        <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Coverage Amount</p>
            <p className="text-2xl font-bold mt-2">
              ₹{policyData?.policy.coverageAmount?.toLocaleString() || '0'}
            </p>
          </div>
        </Card>

        <Card className="bg-gradient-to-br from-purple-500 to-purple-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Total Claims</p>
            <p className="text-3xl font-bold mt-2">{claims.length}</p>
          </div>
        </Card>

        <Card className="bg-gradient-to-br from-orange-500 to-orange-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Appointments</p>
            <p className="text-3xl font-bold mt-2">{appointments.length}</p>
          </div>
        </Card>
      </div>

      {/* Policy Details */}
      {policyData && (
        <Card title="My Policy Details">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-gray-600">Policy Name</p>
              <p className="font-semibold">{policyData.policy.name}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Policy Type</p>
              <p className="font-semibold">{policyData.policy.type}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Start Date</p>
              <p className="font-semibold">{policyData.startDate}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">End Date</p>
              <p className="font-semibold">{policyData.endDate}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Premium Amount</p>
              <p className="font-semibold">₹{policyData.policy.premiumAmount}</p>
            </div>
            <div>
              <p className="text-sm text-gray-600">Status</p>
              <span className={`px-3 py-1 rounded-full text-sm font-semibold ${
                policyData.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
              }`}>
                {policyData.status}
              </span>
            </div>
          </div>
        </Card>
      )}

      {/* Quick Actions */}
      <Card title="Quick Actions">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <Button variant="primary" onClick={() => navigate('/claims/submit')}>
            Submit Claim
          </Button>
          <Button variant="secondary" onClick={() => navigate('/claims/my')}>
            View My Claims
          </Button>
          <Button variant="primary" onClick={() => navigate('/appointments/book')}>
            Book Appointment
          </Button>
          <Button variant="secondary" onClick={() => navigate('/appointments/my')}>
            My Appointments
          </Button>
        </div>
      </Card>

      {/* Recent Claims */}
      <Card title="Recent Claims" headerAction={
        <Button size="sm" onClick={() => navigate('/claims/my')}>View All</Button>
      }>
        {claims.length > 0 ? (
          <div className="space-y-3">
            {claims.slice(0, 3).map((claim) => (
              <div key={claim.id} className="border rounded-lg p-4 hover:bg-gray-50">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-semibold">Claim #{claim.id}</p>
                    <p className="text-sm text-gray-600">{claim.description}</p>
                    <p className="text-sm text-gray-500 mt-1">{new Date(claim.claimDate).toLocaleDateString()}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold">₹{claim.amountClaimed}</p>
                    <span className={`px-2 py-1 rounded text-xs ${
                      claim.status === 'APPROVED_BY_FINANCE' ? 'bg-green-100 text-green-800' :
                      claim.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                      'bg-yellow-100 text-yellow-800'
                    }`}>
                      {claim.status}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-center py-4">No claims yet</p>
        )}
      </Card>

      {/* Upcoming Appointments */}
      <Card title="Upcoming Appointments">
        {appointments.length > 0 ? (
          <div className="space-y-3">
            {appointments.slice(0, 3).map((appointment) => (
              <div key={appointment.id} className="border rounded-lg p-4 hover:bg-gray-50">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-semibold">Dr. {appointment.doctor.user.fullName}</p>
                    <p className="text-sm text-gray-600">{appointment.doctor.specialization}</p>
                    <p className="text-sm text-gray-500 mt-1">
                      📅 {appointment.appointmentDate} at {appointment.appointmentTime}
                    </p>
                  </div>
                  <span className={`px-2 py-1 rounded text-xs ${
                    appointment.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                    appointment.status === 'SCHEDULED' ? 'bg-blue-100 text-blue-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    {appointment.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-center py-4">No upcoming appointments</p>
        )}
      </Card>
    </div>
  );
};

export default PolicyHolderDashboard;

