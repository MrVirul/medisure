import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { doctorAPI, appointmentAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';

const BookAppointment = () => {
  const navigate = useNavigate();
  const [doctors, setDoctors] = useState([]);
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [formData, setFormData] = useState({
    appointmentDate: '',
    appointmentTime: '',
    notes: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    try {
      const response = await doctorAPI.getAllDoctors();
      setDoctors(response.data.data || []);
    } catch (error) {
      console.error('Error fetching doctors:', error);
    }
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!selectedDoctor) {
      setError('Please select a doctor');
      return;
    }

    setError('');
    setLoading(true);

    try {
      await appointmentAPI.bookAppointment({
        doctorId: selectedDoctor.id,
        ...formData
      });
      alert('Appointment booked successfully!');
      navigate('/appointments/my');
    } catch (error) {
      setError(error.response?.data?.message || 'Error booking appointment');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <div className="max-w-6xl mx-auto">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Book Appointment</h1>
          <p className="text-gray-600 mt-1">Schedule a consultation with our doctors</p>
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          {/* Doctor Selection */}
          <div>
            <Card title="Select a Doctor">
              <div className="space-y-3 max-h-96 overflow-y-auto">
                {doctors.map((doctor) => (
                  <div
                    key={doctor.id}
                    onClick={() => setSelectedDoctor(doctor)}
                    className={`border rounded-lg p-4 cursor-pointer transition-all ${
                      selectedDoctor?.id === doctor.id
                        ? 'border-primary-500 bg-primary-50'
                        : 'border-gray-200 hover:border-primary-300'
                    }`}
                  >
                    <h3 className="font-semibold text-lg">Dr. {doctor.user.fullName}</h3>
                    <p className="text-sm text-gray-600">{doctor.specialization}</p>
                    <p className="text-xs text-gray-500 mt-1">
                      {doctor.qualifications} • {doctor.experience} years experience
                    </p>
                    <p className="text-xs text-gray-500 mt-1">
                      📧 {doctor.user.email}
                    </p>
                    {doctor.user.phone && (
                      <p className="text-xs text-gray-500">
                        📞 {doctor.user.phone}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </Card>
          </div>

          {/* Appointment Form */}
          <div>
            <Card title="Appointment Details">
              {error && (
                <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded-lg">
                  {error}
                </div>
              )}

              {selectedDoctor ? (
                <form onSubmit={handleSubmit}>
                  <div className="mb-4 p-3 bg-primary-50 rounded-lg">
                    <p className="text-sm font-medium">Selected Doctor:</p>
                    <p className="font-semibold">Dr. {selectedDoctor.user.fullName}</p>
                    <p className="text-sm text-gray-600">{selectedDoctor.specialization}</p>
                  </div>

                  <Input
                    label="Appointment Date"
                    type="date"
                    name="appointmentDate"
                    value={formData.appointmentDate}
                    onChange={handleChange}
                    min={new Date().toISOString().split('T')[0]}
                    required
                  />

                  <Input
                    label="Appointment Time"
                    type="time"
                    name="appointmentTime"
                    value={formData.appointmentTime}
                    onChange={handleChange}
                    required
                  />

                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                      Notes (Optional)
                    </label>
                    <textarea
                      name="notes"
                      value={formData.notes}
                      onChange={handleChange}
                      rows="4"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                      placeholder="Any specific concerns or symptoms..."
                    />
                  </div>

                  <div className="flex space-x-4">
                    <Button
                      type="submit"
                      variant="primary"
                      disabled={loading}
                      fullWidth
                    >
                      {loading ? 'Booking...' : 'Book Appointment'}
                    </Button>
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={() => navigate('/dashboard')}
                      fullWidth
                    >
                      Cancel
                    </Button>
                  </div>
                </form>
              ) : (
                <div className="text-center py-12">
                  <p className="text-gray-500">Please select a doctor to continue</p>
                </div>
              )}
            </Card>
          </div>
        </div>

        <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
          <h3 className="font-semibold text-blue-900 mb-2">📅 Appointment Guidelines</h3>
          <ul className="list-disc list-inside text-sm text-blue-800 space-y-1">
            <li>Select your preferred doctor based on specialization</li>
            <li>Choose a suitable date and time for your consultation</li>
            <li>Appointments are subject to doctor availability</li>
            <li>You will receive a confirmation email once approved</li>
            <li>Please arrive 10 minutes before your scheduled time</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default BookAppointment;

