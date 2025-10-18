import { useState, useEffect } from 'react';
import { doctorAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';

const MedicalCoordinatorDashboard = () => {
  const [doctors, setDoctors] = useState([]);
  const [showRegisterForm, setShowRegisterForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    phone: '',
    specialization: '',
    qualifications: '',
    experience: ''
  });

  useEffect(() => {
    fetchDoctors();
  }, []);

  const fetchDoctors = async () => {
    try {
      const response = await doctorAPI.getAllDoctors();
      setDoctors(response.data.data || []);
    } catch (error) {
      console.error('Error fetching doctors:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRegisterDoctor = async (e) => {
    e.preventDefault();
    try {
      await doctorAPI.registerDoctor(formData);
      alert('Doctor registered successfully!');
      setShowRegisterForm(false);
      setFormData({
        fullName: '',
        email: '',
        password: '',
        phone: '',
        specialization: '',
        qualifications: '',
        experience: ''
      });
      fetchDoctors();
    } catch (error) {
      alert('Error registering doctor: ' + error.response?.data?.message);
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Medical Coordinator Dashboard</h1>
          <p className="text-gray-600 mt-1">Manage doctors and medical staff</p>
        </div>
        <Button onClick={() => setShowRegisterForm(!showRegisterForm)}>
          {showRegisterForm ? 'Cancel' : 'Register Doctor'}
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Total Doctors</p>
            <p className="text-3xl font-bold mt-2">{doctors.length}</p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Specialists</p>
            <p className="text-3xl font-bold mt-2">
              {new Set(doctors.map(d => d.specialization)).size}
            </p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-purple-500 to-purple-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Avg. Experience</p>
            <p className="text-3xl font-bold mt-2">
              {doctors.length > 0 
                ? Math.round(doctors.reduce((sum, d) => sum + d.experience, 0) / doctors.length)
                : 0} yrs
            </p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-orange-500 to-orange-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Available Today</p>
            <p className="text-3xl font-bold mt-2">{doctors.length}</p>
          </div>
        </Card>
      </div>

      {/* Register Doctor Form */}
      {showRegisterForm && (
        <Card title="Register New Doctor">
          <form onSubmit={handleRegisterDoctor} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Full Name"
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                required
              />
              <Input
                label="Email"
                type="email"
                name="email"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                required
              />
              <Input
                label="Password"
                type="password"
                name="password"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                required
              />
              <Input
                label="Phone"
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={(e) => setFormData({...formData, phone: e.target.value})}
              />
              <Input
                label="Specialization"
                type="text"
                name="specialization"
                value={formData.specialization}
                onChange={(e) => setFormData({...formData, specialization: e.target.value})}
                placeholder="e.g., Cardiologist, Neurologist"
                required
              />
              <Input
                label="Qualifications"
                type="text"
                name="qualifications"
                value={formData.qualifications}
                onChange={(e) => setFormData({...formData, qualifications: e.target.value})}
                placeholder="e.g., MBBS, MD"
                required
              />
              <Input
                label="Experience (years)"
                type="number"
                name="experience"
                value={formData.experience}
                onChange={(e) => setFormData({...formData, experience: e.target.value})}
                required
              />
            </div>
            <Button type="submit">Register Doctor</Button>
          </form>
        </Card>
      )}

      {/* Doctors List */}
      <Card title="Registered Doctors">
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          {doctors.map((doctor) => (
            <div key={doctor.id} className="border rounded-lg p-4 hover:shadow-lg transition-shadow">
              <div className="mb-3">
                <h3 className="font-bold text-lg">Dr. {doctor.user.fullName}</h3>
                <p className="text-primary-600 font-semibold">{doctor.specialization}</p>
              </div>
              <div className="space-y-2 text-sm">
                <div>
                  <span className="text-gray-600">Qualifications:</span>
                  <p className="font-medium">{doctor.qualifications}</p>
                </div>
                <div>
                  <span className="text-gray-600">Experience:</span>
                  <p className="font-medium">{doctor.experience} years</p>
                </div>
                <div>
                  <span className="text-gray-600">Email:</span>
                  <p className="font-medium">{doctor.user.email}</p>
                </div>
                {doctor.user.phone && (
                  <div>
                    <span className="text-gray-600">Phone:</span>
                    <p className="font-medium">{doctor.user.phone}</p>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
};

export default MedicalCoordinatorDashboard;

