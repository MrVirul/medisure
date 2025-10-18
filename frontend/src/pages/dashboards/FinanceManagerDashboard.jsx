import { useState, useEffect } from 'react';
import { financeAPI, claimAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';

const FinanceManagerDashboard = () => {
  const [claims, setClaims] = useState([]);
  const [selectedClaim, setSelectedClaim] = useState(null);
  const [loading, setLoading] = useState(true);
  const [processData, setProcessData] = useState({
    action: 'APPROVE',
    amountApproved: '',
    comments: ''
  });

  useEffect(() => {
    fetchClaims();
  }, []);

  const fetchClaims = async () => {
    try {
      const response = await claimAPI.getAllClaims();
      const approvedClaims = (response.data.data || []).filter(
        c => c.status === 'APPROVED_BY_CLAIMS_MANAGER'
      );
      setClaims(approvedClaims);
    } catch (error) {
      console.error('Error fetching claims:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleProcessClaim = async (claimId) => {
    try {
      await financeAPI.processClaim(claimId, processData);
      alert('Claim processed successfully!');
      setSelectedClaim(null);
      setProcessData({ action: 'APPROVE', amountApproved: '', comments: '' });
      fetchClaims();
    } catch (error) {
      alert('Error processing claim: ' + error.response?.data?.message);
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  const pendingFinanceClaims = claims.filter(c => c.status === 'APPROVED_BY_CLAIMS_MANAGER');
  const processedClaims = claims.filter(c => ['APPROVED_BY_FINANCE', 'REJECTED'].includes(c.status));

  return (
    <div className="p-6 space-y-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Finance Management</h1>
        <p className="text-gray-600 mt-1">Process approved claims and manage payments</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-yellow-500 to-yellow-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Pending Review</p>
            <p className="text-3xl font-bold mt-2">{pendingFinanceClaims.length}</p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Approved</p>
            <p className="text-3xl font-bold mt-2">
              {claims.filter(c => c.status === 'APPROVED_BY_FINANCE').length}
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
            <p className="text-sm opacity-90">Total Amount</p>
            <p className="text-2xl font-bold mt-2">
              ₹{claims.reduce((sum, c) => sum + (c.amountApproved || 0), 0).toLocaleString()}
            </p>
          </div>
        </Card>
      </div>

      {/* Pending Claims */}
      <Card title="Claims Pending Finance Approval">
        {pendingFinanceClaims.length > 0 ? (
          <div className="space-y-4">
            {pendingFinanceClaims.map((claim) => (
              <div key={claim.id} className="border rounded-lg p-4 hover:bg-gray-50">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <div className="flex items-center space-x-2">
                      <h3 className="font-semibold text-lg">Claim #{claim.id}</h3>
                      <span className="px-2 py-1 text-xs rounded bg-blue-100 text-blue-800">
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
                        <span className="text-gray-600">Amount Claimed:</span>
                        <span className="ml-2 font-medium">₹{claim.amountClaimed}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Hospital:</span>
                        <span className="ml-2 font-medium">{claim.hospitalName}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Treatment:</span>
                        <span className="ml-2 font-medium">{claim.treatmentType}</span>
                      </div>
                    </div>
                    {claim.claimsManagerComments && (
                      <div className="mt-3 p-2 bg-blue-50 rounded">
                        <p className="text-xs font-semibold">Claims Manager Comments:</p>
                        <p className="text-sm">{claim.claimsManagerComments}</p>
                      </div>
                    )}
                  </div>
                  <Button
                    size="sm"
                    onClick={() => {
                      setSelectedClaim(claim);
                      setProcessData({
                        ...processData,
                        amountApproved: claim.amountClaimed.toString()
                      });
                    }}
                  >
                    Process
                  </Button>
                </div>

                {/* Process Form */}
                {selectedClaim?.id === claim.id && (
                  <div className="mt-4 pt-4 border-t">
                    <h4 className="font-semibold mb-3">Process Claim Payment</h4>
                    <div className="space-y-3">
                      <div>
                        <label className="block text-sm font-medium mb-1">Action</label>
                        <select
                          value={processData.action}
                          onChange={(e) => setProcessData({...processData, action: e.target.value})}
                          className="w-full px-4 py-2 border rounded-lg"
                        >
                          <option value="APPROVE">Approve Payment</option>
                          <option value="REJECT">Reject Payment</option>
                        </select>
                      </div>
                      {processData.action === 'APPROVE' && (
                        <div>
                          <label className="block text-sm font-medium mb-1">Approved Amount</label>
                          <input
                            type="number"
                            value={processData.amountApproved}
                            onChange={(e) => setProcessData({...processData, amountApproved: e.target.value})}
                            className="w-full px-4 py-2 border rounded-lg"
                            placeholder="Enter approved amount"
                          />
                        </div>
                      )}
                      <div>
                        <label className="block text-sm font-medium mb-1">Comments</label>
                        <textarea
                          value={processData.comments}
                          onChange={(e) => setProcessData({...processData, comments: e.target.value})}
                          className="w-full px-4 py-2 border rounded-lg"
                          rows="3"
                          placeholder="Enter comments..."
                        />
                      </div>
                      <div className="flex space-x-2">
                        <Button
                          variant="primary"
                          onClick={() => handleProcessClaim(claim.id)}
                        >
                          Submit
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
          <p className="text-gray-500 text-center py-8">No claims pending finance approval</p>
        )}
      </Card>
    </div>
  );
};

export default FinanceManagerDashboard;

