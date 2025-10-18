import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { policyAPI, policyHolderAPI } from '../../services/api';
import Card from '../../components/common/Card';
import PolicyCard from '../../components/PolicyCard';
import Button from '../../components/common/Button';
import { useAuth } from '../../context/AuthContext';

const BrowsePolicies = () => {
  const [policies, setPolicies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedPolicy, setSelectedPolicy] = useState(null);
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    fetchPolicies();
  }, []);

  const fetchPolicies = async () => {
    try {
      const response = await policyAPI.getAllPolicies();
      setPolicies(response.data.data || []);
    } catch (error) {
      console.error('Error fetching policies:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePurchasePolicy = async (policyId) => {
    try {
      await policyHolderAPI.purchasePolicy(policyId);
      alert('Policy purchased successfully!');
      navigate('/dashboard');
    } catch (error) {
      alert('Error purchasing policy: ' + error.response?.data?.message);
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6 space-y-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Browse Insurance Policies</h1>
        <p className="text-gray-600 mt-1">Find the perfect health insurance plan for you and your family</p>
      </div>

      {/* Policy Cards Grid */}
      <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
        {policies.map((policy) => (
          <PolicyCard
            key={policy.id}
            policy={policy}
            onSelect={(policy) => setSelectedPolicy(policy)}
          />
        ))}
      </div>

      {/* Purchase Confirmation Modal */}
      {selectedPolicy && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-2xl w-full p-6">
            <h2 className="text-2xl font-bold mb-4">Confirm Policy Purchase</h2>
            <div className="space-y-4 mb-6">
              <div>
                <h3 className="font-bold text-xl">{selectedPolicy.name}</h3>
                <p className="text-gray-600">{selectedPolicy.description}</p>
              </div>
              <div className="grid grid-cols-2 gap-4 p-4 bg-gray-50 rounded-lg">
                <div>
                  <p className="text-sm text-gray-600">Coverage Amount</p>
                  <p className="font-bold text-lg">₹{selectedPolicy.coverageAmount.toLocaleString()}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Premium Amount</p>
                  <p className="font-bold text-lg text-primary-600">₹{selectedPolicy.premiumAmount}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Policy Type</p>
                  <p className="font-semibold">{selectedPolicy.type}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-600">Duration</p>
                  <p className="font-semibold">{selectedPolicy.policyDurationMonths} months</p>
                </div>
              </div>
              <div className="p-4 bg-blue-50 border border-blue-200 rounded-lg">
                <h4 className="font-semibold text-blue-900 mb-2">📋 Important Notes:</h4>
                <ul className="list-disc list-inside text-sm text-blue-800 space-y-1">
                  <li>Policy will be activated immediately after purchase</li>
                  <li>Premium is payable monthly</li>
                  <li>Coverage starts from the date of purchase</li>
                  <li>You can submit claims after 30 days of policy activation</li>
                </ul>
              </div>
            </div>
            <div className="flex space-x-4">
              <Button
                variant="primary"
                fullWidth
                onClick={() => handlePurchasePolicy(selectedPolicy.id)}
              >
                Confirm Purchase
              </Button>
              <Button
                variant="secondary"
                fullWidth
                onClick={() => setSelectedPolicy(null)}
              >
                Cancel
              </Button>
            </div>
          </div>
        </div>
      )}

      {policies.length === 0 && (
        <Card>
          <div className="text-center py-12">
            <p className="text-gray-500">No policies available at the moment</p>
          </div>
        </Card>
      )}
    </div>
  );
};

export default BrowsePolicies;

