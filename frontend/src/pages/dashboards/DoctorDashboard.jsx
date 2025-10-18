import { useState, useEffect } from 'react';
import { doctorAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';

const DoctorDashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [todayAppointments, setTodayAppointments] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      const response = await doctorAPI.getMyAppointments();
      const allAppointments = response.data.data || [];
      setAppointments(allAppointments);
      
      // Filter today's appointments
      const today = new Date().toISOString().split('T')[0];
      const todayAppts = allAppointments.filter(apt => apt.appointmentDate === today);
      setTodayAppointments(todayAppts);
    } catch (error) {
      console.error('Error fetching appointments:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateStatus = async (appointmentId, status) => {
    try {
      await doctorAPI.updateAppointmentStatus(appointmentId, { status });
      alert('Appointment status updated successfully!');
      fetchAppointments();
    } catch (error) {
      alert('Error updating status: ' + error.response?.data?.message);
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6 space-y-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Doctor Dashboard</h1>
        <p className="text-gray-600 mt-1">Manage your appointments and consultations</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Today's Appointments</p>
            <p className="text-3xl font-bold mt-2">{todayAppointments.length}</p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Total Appointments</p>
            <p className="text-3xl font-bold mt-2">{appointments.length}</p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-purple-500 to-purple-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Completed</p>
            <p className="text-3xl font-bold mt-2">
              {appointments.filter(a => a.status === 'COMPLETED').length}
            </p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-orange-500 to-orange-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Scheduled</p>
            <p className="text-3xl font-bold mt-2">
              {appointments.filter(a => a.status === 'SCHEDULED').length}
            </p>
          </div>
        </Card>
      </div>

      {/* Today's Appointments */}
      <Card title="Today's Appointments">
        {todayAppointments.length > 0 ? (
          <div className="space-y-4">
            {todayAppointments.map((appointment) => (
              <div key={appointment.id} className="border rounded-lg p-4 hover:bg-gray-50">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <div className="flex items-center space-x-2">
                      <h3 className="font-semibold text-lg">
                        {appointment.policyHolder.user.fullName}
                      </h3>
                      <span className={`px-2 py-1 text-xs rounded ${
                        appointment.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                        appointment.status === 'SCHEDULED' ? 'bg-blue-100 text-blue-800' :
                        'bg-gray-100 text-gray-800'
                      }`}>
                        {appointment.status}
                      </span>
                    </div>
                    <div className="grid grid-cols-2 gap-4 mt-3 text-sm">
                      <div>
                        <span className="text-gray-600">Time:</span>
                        <span className="ml-2 font-medium">{appointment.appointmentTime}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Email:</span>
                        <span className="ml-2 font-medium">{appointment.policyHolder.user.email}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Phone:</span>
                        <span className="ml-2 font-medium">{appointment.policyHolder.user.phone || 'N/A'}</span>
                      </div>
                      {appointment.notes && (
                        <div className="col-span-2">
                          <span className="text-gray-600">Notes:</span>
                          <span className="ml-2">{appointment.notes}</span>
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="flex flex-col space-y-2">
                    {appointment.status === 'SCHEDULED' && (
                      <Button
                        size="sm"
                        variant="success"
                        onClick={() => handleUpdateStatus(appointment.id, 'CONFIRMED')}
                      >
                        Confirm
                      </Button>
                    )}
                    {appointment.status === 'CONFIRMED' && (
                      <Button
                        size="sm"
                        variant="primary"
                        onClick={() => handleUpdateStatus(appointment.id, 'COMPLETED')}
                      >
                        Complete
                      </Button>
                    )}
                    <Button
                      size="sm"
                      variant="danger"
                      onClick={() => handleUpdateStatus(appointment.id, 'CANCELLED')}
                    >
                      Cancel
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-center py-8">No appointments scheduled for today</p>
        )}
      </Card>

      {/* All Appointments */}
      <Card title="All Appointments">
        {appointments.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Patient</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Time</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Contact</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {appointments.map((appointment) => (
                  <tr key={appointment.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">{appointment.policyHolder.user.fullName}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{appointment.appointmentDate}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{appointment.appointmentTime}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs rounded ${
                        appointment.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                        appointment.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-800' :
                        appointment.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                        'bg-yellow-100 text-yellow-800'
                      }`}>
                        {appointment.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">{appointment.policyHolder.user.phone || 'N/A'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-gray-500 text-center py-8">No appointments</p>
        )}
      </Card>
    </div>
  );
};

export default DoctorDashboard;

