import { useState, useEffect } from 'react';
import { claimsManagerAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';

const ClaimsManagerDashboard = () => {
  const [claims, setClaims] = useState([]);
  const [selectedClaim, setSelectedClaim] = useState(null);
  const [loading, setLoading] = useState(true);
  const [reviewData, setReviewData] = useState({
    action: 'APPROVE',
    comments: ''
  });

  useEffect(() => {
    fetchClaims();
  }, []);

  const fetchClaims = async () => {
    try {
      const response = await claimsManagerAPI.getAllClaims();
      setClaims(response.data.data || []);
    } catch (error) {
      console.error('Error fetching claims:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleReviewClaim = async (claimId) => {
    try {
      await claimsManagerAPI.reviewClaim(claimId, reviewData);
      alert('Claim reviewed successfully!');
      setSelectedClaim(null);
      setReviewData({ action: 'APPROVE', comments: '' });
      fetchClaims();
    } catch (error) {
      alert('Error reviewing claim: ' + error.response?.data?.message);
    }
  };

  const pendingClaims = claims.filter(c => c.status === 'PENDING');
  const reviewedClaims = claims.filter(c => c.status !== 'PENDING');

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6 space-y-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Claims Management</h1>
        <p className="text-gray-600 mt-1">Review and manage insurance claims</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-yellow-500 to-yellow-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Pending Claims</p>
            <p className="text-3xl font-bold mt-2">{pendingClaims.length}</p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Approved</p>
            <p className="text-3xl font-bold mt-2">
              {claims.filter(c => c.status === 'APPROVED_BY_CLAIMS_MANAGER').length}
            </p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-red-500 to-red-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Rejected</p>
            <p className="text-3xl font-bold mt-2">
              {claims.filter(c => c.status === 'REJECTED').length}
            </p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Total Claims</p>
            <p className="text-3xl font-bold mt-2">{claims.length}</p>
          </div>
        </Card>
      </div>

      {/* Pending Claims Queue */}
      <Card title="Pending Claims Queue">
        {pendingClaims.length > 0 ? (
          <div className="space-y-4">
            {pendingClaims.map((claim) => (
              <div key={claim.id} className="border rounded-lg p-4 hover:bg-gray-50">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <div className="flex items-center space-x-2">
                      <h3 className="font-semibold text-lg">Claim #{claim.id}</h3>
                      <span className="px-2 py-1 text-xs rounded bg-yellow-100 text-yellow-800">
                        {claim.status}
                      </span>
                    </div>
                    <p className="text-gray-600 mt-1">{claim.description}</p>
                    <div className="grid grid-cols-2 gap-4 mt-3 text-sm">
                      <div>
                        <span className="text-gray-600">Patient:</span>
                        <span className="ml-2 font-medium">{claim.policyHolder?.user.fullName}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Claim Date:</span>
                        <span className="ml-2 font-medium">{new Date(claim.claimDate).toLocaleDateString()}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Amount:</span>
                        <span className="ml-2 font-medium">₹{claim.amountClaimed}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Hospital:</span>
                        <span className="ml-2 font-medium">{claim.hospitalName}</span>
                      </div>
                    </div>
                  </div>
                  <Button
                    size="sm"
                    onClick={() => setSelectedClaim(claim)}
                  >
                    Review
                  </Button>
                </div>

                {/* Review Form */}
                {selectedClaim?.id === claim.id && (
                  <div className="mt-4 pt-4 border-t">
                    <h4 className="font-semibold mb-3">Review Claim</h4>
                    <div className="space-y-3">
                      <div>
                        <label className="block text-sm font-medium mb-1">Action</label>
                        <select
                          value={reviewData.action}
                          onChange={(e) => setReviewData({...reviewData, action: e.target.value})}
                          className="w-full px-4 py-2 border rounded-lg"
                        >
                          <option value="APPROVE">Approve</option>
                          <option value="REJECT">Reject</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-sm font-medium mb-1">Comments</label>
                        <textarea
                          value={reviewData.comments}
                          onChange={(e) => setReviewData({...reviewData, comments: e.target.value})}
                          className="w-full px-4 py-2 border rounded-lg"
                          rows="3"
                          placeholder="Enter review comments..."
                        />
                      </div>
                      <div className="flex space-x-2">
                        <Button
                          variant="primary"
                          onClick={() => handleReviewClaim(claim.id)}
                        >
                          Submit Review
                        </Button>
                        <Button
                          variant="secondary"
                          onClick={() => setSelectedClaim(null)}
                        >
                          Cancel
                        </Button>
                      </div>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-500 text-center py-8">No pending claims</p>
        )}
      </Card>

      {/* Reviewed Claims */}
      <Card title="Recently Reviewed Claims">
        {reviewedClaims.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Claim ID</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Patient</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {reviewedClaims.slice(0, 10).map((claim) => (
                  <tr key={claim.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">#{claim.id}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{claim.policyHolder?.user.fullName}</td>
                    <td className="px-6 py-4 whitespace-nowrap">₹{claim.amountClaimed}</td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 py-1 text-xs rounded ${
                        claim.status.includes('APPROVED') ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {claim.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">{new Date(claim.claimDate).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <p className="text-gray-500 text-center py-8">No reviewed claims</p>
        )}
      </Card>
    </div>
  );
};

export default ClaimsManagerDashboard;

