import { useState, useEffect } from 'react';
import { policyAPI, policyHolderAPI } from '../../services/api';
import Card from '../../components/common/Card';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';

const PolicyManagerDashboard = () => {
  const [policies, setPolicies] = useState([]);
  const [policyHolders, setPolicyHolders] = useState([]);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    type: 'INDIVIDUAL',
    coverageAmount: '',
    premiumAmount: '',
    policyDurationMonths: ''
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [policiesRes, holdersRes] = await Promise.all([
        policyAPI.getAllPolicies(),
        policyHolderAPI.getAllPolicyHolders()
      ]);
      setPolicies(policiesRes.data.data || []);
      setPolicyHolders(holdersRes.data.data || []);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreatePolicy = async (e) => {
    e.preventDefault();
    try {
      await policyAPI.createPolicy(formData);
      alert('Policy created successfully!');
      setShowCreateForm(false);
      setFormData({
        name: '',
        description: '',
        type: 'INDIVIDUAL',
        coverageAmount: '',
        premiumAmount: '',
        policyDurationMonths: ''
      });
      fetchData();
    } catch (error) {
      alert('Error creating policy: ' + error.response?.data?.message);
    }
  };

  const handleDeletePolicy = async (id) => {
    if (window.confirm('Are you sure you want to delete this policy?')) {
      try {
        await policyAPI.deletePolicy(id);
        alert('Policy deleted successfully!');
        fetchData();
      } catch (error) {
        alert('Error deleting policy: ' + error.response?.data?.message);
      }
    }
  };

  if (loading) return <div className="p-6">Loading...</div>;

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Policy Management</h1>
          <p className="text-gray-600 mt-1">Manage insurance policies and policy holders</p>
        </div>
        <Button onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? 'Cancel' : 'Create Policy'}
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="bg-gradient-to-br from-blue-500 to-blue-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Total Policies</p>
            <p className="text-3xl font-bold mt-2">{policies.length}</p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-green-500 to-green-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Active Policy Holders</p>
            <p className="text-3xl font-bold mt-2">
              {policyHolders.filter(p => p.status === 'ACTIVE').length}
            </p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-purple-500 to-purple-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Individual Policies</p>
            <p className="text-3xl font-bold mt-2">
              {policies.filter(p => p.type === 'INDIVIDUAL').length}
            </p>
          </div>
        </Card>
        <Card className="bg-gradient-to-br from-orange-500 to-orange-600 text-white">
          <div className="text-center">
            <p className="text-sm opacity-90">Family Policies</p>
            <p className="text-3xl font-bold mt-2">
              {policies.filter(p => p.type === 'FAMILY').length}
            </p>
          </div>
        </Card>
      </div>

      {/* Create Policy Form */}
      {showCreateForm && (
        <Card title="Create New Policy">
          <form onSubmit={handleCreatePolicy} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Policy Name"
                type="text"
                name="name"
                value={formData.name}
                onChange={(e) => setFormData({...formData, name: e.target.value})}
                required
              />
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Policy Type <span className="text-red-500">*</span>
                </label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({...formData, type: e.target.value})}
                  className="w-full px-4 py-2 border rounded-lg"
                  required
                >
                  <option value="INDIVIDUAL">Individual</option>
                  <option value="FAMILY">Family</option>
                  <option value="SENIOR_CITIZEN">Senior Citizen</option>
                  <option value="CRITICAL_ILLNESS">Critical Illness</option>
                </select>
              </div>
              <Input
                label="Coverage Amount"
                type="number"
                name="coverageAmount"
                value={formData.coverageAmount}
                onChange={(e) => setFormData({...formData, coverageAmount: e.target.value})}
                required
              />
              <Input
                label="Premium Amount"
                type="number"
                name="premiumAmount"
                value={formData.premiumAmount}
                onChange={(e) => setFormData({...formData, premiumAmount: e.target.value})}
                required
              />
              <Input
                label="Duration (Months)"
                type="number"
                name="policyDurationMonths"
                value={formData.policyDurationMonths}
                onChange={(e) => setFormData({...formData, policyDurationMonths: e.target.value})}
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Description <span className="text-red-500">*</span>
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({...formData, description: e.target.value})}
                className="w-full px-4 py-2 border rounded-lg"
                rows="3"
                required
              />
            </div>
            <Button type="submit">Create Policy</Button>
          </form>
        </Card>
      )}

      {/* Policies List */}
      <Card title="All Policies">
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
          {policies.map((policy) => (
            <div key={policy.id} className="border rounded-lg p-4 hover:shadow-lg transition-shadow">
              <div className="flex justify-between items-start mb-2">
                <h3 className="font-bold text-lg">{policy.name}</h3>
                <span className="px-2 py-1 text-xs rounded bg-primary-100 text-primary-800">
                  {policy.type}
                </span>
              </div>
              <p className="text-sm text-gray-600 mb-3 line-clamp-2">{policy.description}</p>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Coverage:</span>
                  <span className="font-semibold">₹{policy.coverageAmount.toLocaleString()}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Premium:</span>
                  <span className="font-semibold">₹{policy.premiumAmount}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Duration:</span>
                  <span className="font-semibold">{policy.policyDurationMonths} months</span>
                </div>
              </div>
              <Button
                variant="danger"
                size="sm"
                fullWidth
                className="mt-4"
                onClick={() => handleDeletePolicy(policy.id)}
              >
                Delete
              </Button>
            </div>
          ))}
        </div>
      </Card>

      {/* Policy Holders */}
      <Card title="Policy Holders">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Policy</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Start Date</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {policyHolders.map((holder) => (
                <tr key={holder.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">{holder.user.fullName}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{holder.user.email}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{holder.policy.name}</td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`px-2 py-1 text-xs rounded ${
                      holder.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                    }`}>
                      {holder.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">{holder.startDate}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};

export default PolicyManagerDashboard;

