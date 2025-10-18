import { useState, useEffect } from 'react';
import { claimAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import { useNavigate } from 'react-router-dom';

const MyClaims = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchMyClaims();
  }, []);

  const fetchMyClaims = async () => {
    try {
      const response = await claimAPI.getMyClaims();
      setClaims(response.data.data || []);
    } catch (error) {
      console.error('Error fetching claims:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'APPROVED_BY_FINANCE':
        return 'bg-green-100 text-green-800';
      case 'APPROVED_BY_CLAIMS_MANAGER':
        return 'bg-blue-100 text-blue-800';
      case 'REJECTED':
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
          <h1 className="text-3xl font-bold text-gray-900">My Claims</h1>
          <p className="text-gray-600 mt-1">Track and manage your insurance claims</p>
        </div>
        <Button onClick={() => navigate('/claims/submit')}>
          Submit New Claim
        </Button>
      </div>

      {claims.length > 0 ? (
        <div className="grid gap-6">
          {claims.map((claim) => (
            <Card key={claim.id}>
              <div className="space-y-4">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="text-xl font-bold text-gray-900">Claim #{claim.id}</h3>
                    <p className="text-gray-600 mt-1">{claim.description}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(claim.status)}`}>
                    {claim.status}
                  </span>
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                  <div>
                    <p className="text-gray-600">Amount Claimed</p>
                    <p className="font-semibold text-lg">₹{claim.amountClaimed}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Claim Date</p>
                    <p className="font-semibold">{new Date(claim.claimDate).toLocaleDateString()}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Hospital</p>
                    <p className="font-semibold">{claim.hospitalName}</p>
                  </div>
                  <div>
                    <p className="text-gray-600">Doctor</p>
                    <p className="font-semibold">{claim.doctorName}</p>
                  </div>
                </div>

                {claim.treatmentType && (
                  <div>
                    <p className="text-sm text-gray-600">Treatment Type</p>
                    <p className="font-semibold">{claim.treatmentType}</p>
                  </div>
                )}

                {claim.claimsManagerComments && (
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-sm font-semibold text-gray-700">Claims Manager Comments:</p>
                    <p className="text-sm text-gray-600 mt-1">{claim.claimsManagerComments}</p>
                  </div>
                )}

                {claim.financeManagerComments && (
                  <div className="bg-gray-50 p-3 rounded-lg">
                    <p className="text-sm font-semibold text-gray-700">Finance Manager Comments:</p>
                    <p className="text-sm text-gray-600 mt-1">{claim.financeManagerComments}</p>
                  </div>
                )}

                {claim.amountApproved && (
                  <div className="bg-green-50 p-3 rounded-lg">
                    <p className="text-sm font-semibold text-green-700">Amount Approved:</p>
                    <p className="text-lg font-bold text-green-600">₹{claim.amountApproved}</p>
                  </div>
                )}
              </div>
            </Card>
          ))}
        </div>
      ) : (
        <Card>
          <div className="text-center py-12">
            <p className="text-gray-500 mb-4">You haven't submitted any claims yet</p>
            <Button onClick={() => navigate('/claims/submit')}>
              Submit Your First Claim
            </Button>
          </div>
        </Card>
      )}
    </div>
  );
};

export default MyClaims;

