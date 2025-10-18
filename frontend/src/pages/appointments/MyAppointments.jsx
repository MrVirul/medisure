import { useState, useEffect } from 'react';
import { appointmentAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import { useNavigate } from 'react-router-dom';

const MyAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      const response = await appointmentAPI.getMyAppointments();
      setAppointments(response.data.data || []);
    } catch (error) {
      console.error('Error fetching appointments:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-green-100 text-green-800';
      case 'COMPLETED':
        return 'bg-blue-100 text-blue-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-yellow-100 text-yellow-800';
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">My Appointments</h1>
          <p className="text-gray-600 mt-1">View and manage your medical appointments</p>
        </div>
        <Button onClick={() => navigate('/appointments/book')}>
          Book New Appointment
        </Button>
      </div>

      {appointments.length > 0 ? (
        <div className="grid gap-6">
          {appointments.map((appointment) => (
            <Card key={appointment.id}>
              <div className="space-y-4">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-xl font-bold text-gray-900">
                      Dr. {appointment.doctor.user.fullName}
                    </h3>
                    <p className="text-primary-600 font-semibold">{appointment.doctor.specialization}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(appointment.status)}`}>
                    {appointment.status}
                  </span>
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                  <div>
                    <p className="text-gray-600">Date</p>
                    <p className="font-semibold">{appointment.appointmentDate}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Time</p>
                    <p className="font-semibold">{appointment.appointmentTime}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Qualifications</p>
                    <p className="font-semibold">{appointment.doctor.qualifications}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Experience</p>
                    <p className="font-semibold">{appointment.doctor.experience} years</p>
                  </div>
                </div>

                {appointment.notes && (
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-sm font-semibold text-gray-700">Notes:</p>
                    <p className="text-sm text-gray-600 mt-1">{appointment.notes}</p>
                  </div>
                )}

                <div className="flex items-center space-x-4 text-sm text-gray-600">
                  <div>
                    📧 {appointment.doctor.user.email}
                  </div>
                  {appointment.doctor.user.phone && (
                    <div>
                      📞 {appointment.doctor.user.phone}
                    </div>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <div className="text-center py-12">
            <p className="text-gray-500 mb-4">You haven't booked any appointments yet</p>
            <Button onClick={() => navigate('/appointments/book')}>
              Book Your First Appointment
            </Button>
          </div>
        </Card>
      )}
    </div>
  );
};

export default MyAppointments;

