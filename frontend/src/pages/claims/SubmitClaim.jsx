import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { claimAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';

const SubmitClaim = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    description: '',
    amountClaimed: '',
    claimDate: new Date().toISOString().split('T')[0],
    hospitalName: '',
    doctorName: '',
    treatmentType: ''
  });
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleFileChange = (e) => {
    const files = Array.from(e.target.files);
    setDocuments(files);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = new FormData();
      data.append('description', formData.description);
      data.append('amountClaimed', formData.amountClaimed);
      data.append('claimDate', formData.claimDate);
      data.append('hospitalName', formData.hospitalName);
      data.append('doctorName', formData.doctorName);
      data.append('treatmentType', formData.treatmentType);
      
      documents.forEach(file => {
        data.append('documents', file);
      });

      await claimAPI.submitClaim(data);
      alert('Claim submitted successfully!');
      navigate('/claims/my');
    } catch (error) {
      setError(error.response?.data?.message || 'Error submitting claim');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6">
      <div className="max-w-3xl mx-auto">
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Submit Insurance Claim</h1>
          <p className="text-gray-600 mt-1">Fill in the details to submit your claim</p>
        </div>

        <Card>
          {error && (
            <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded-lg">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="space-y-4">
              <Input
                label="Claim Description"
                type="text"
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Brief description of the claim"
                required
              />

              <Input
                label="Amount Claimed"
                type="number"
                name="amountClaimed"
                value={formData.amountClaimed}
                onChange={handleChange}
                placeholder="0.00"
                required
              />

              <Input
                label="Claim Date"
                type="date"
                name="claimDate"
                value={formData.claimDate}
                onChange={handleChange}
                required
              />

              <Input
                label="Hospital Name"
                type="text"
                name="hospitalName"
                value={formData.hospitalName}
                onChange={handleChange}
                placeholder="Name of the hospital"
                required
              />

              <Input
                label="Doctor Name"
                type="text"
                name="doctorName"
                value={formData.doctorName}
                onChange={handleChange}
                placeholder="Name of the attending doctor"
                required
              />

              <Input
                label="Treatment Type"
                type="text"
                name="treatmentType"
                value={formData.treatmentType}
                onChange={handleChange}
                placeholder="e.g., Surgery, Consultation, Emergency"
                required
              />

              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Supporting Documents <span className="text-red-500">*</span>
                </label>
                <input
                  type="file"
                  onChange={handleFileChange}
                  multiple
                  accept=".pdf,.jpg,.jpeg,.png"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  required
                />
                <p className="mt-1 text-xs text-gray-500">
                  Upload medical bills, reports, and prescriptions (PDF, JPG, PNG)
                </p>
                {documents.length > 0 && (
                  <div className="mt-2">
                    <p className="text-sm font-medium">Selected files:</p>
                    <ul className="list-disc list-inside text-sm text-gray-600">
                      {documents.map((file, index) => (
                        <li key={index}>{file.name}</li>
                      ))}
                    </ul>
                  </div>
                )}
              </div>

              <div className="flex space-x-4 pt-4">
                <Button
                  type="submit"
                  variant="primary"
                  disabled={loading}
                  fullWidth
                >
                  {loading ? 'Submitting...' : 'Submit Claim'}
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
            </div>
          </form>
        </Card>

        <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
          <h3 className="font-semibold text-blue-900 mb-2">📋 Important Information</h3>
          <ul className="list-disc list-inside text-sm text-blue-800 space-y-1">
            <li>Ensure all information is accurate and complete</li>
            <li>Upload clear copies of all medical documents</li>
            <li>Claims are typically processed within 7-10 business days</li>
            <li>You will be notified via email once your claim is reviewed</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default SubmitClaim;

