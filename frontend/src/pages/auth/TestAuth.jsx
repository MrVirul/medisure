import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { authAPI } from '../../services/api';

const TestAuth = () => {
  const { login, register, user } = useAuth();
  const [testResults, setTestResults] = useState([]);

  const addResult = (test, result, details = '') => {
    setTestResults(prev => [...prev, { test, result, details, timestamp: new Date().toLocaleTimeString() }]);
  };

  const testLogin = async () => {
    try {
      addResult('Login Test', 'Starting...', 'Testing login with test@example.com');
      const result = await login('test@example.com', 'password123');
      if (result.success) {
        addResult('Login Test', 'SUCCESS', `User: ${result.data.fullName}, Role: ${result.data.role}`);
      } else {
        addResult('Login Test', 'FAILED', result.error);
      }
    } catch (error) {
      addResult('Login Test', 'ERROR', error.message);
    }
  };

  const testRegister = async () => {
    try {
      const testEmail = `test${Date.now()}@example.com`;
      addResult('Register Test', 'Starting...', `Testing registration with ${testEmail}`);
      const result = await register({
        fullName: 'Test User',
        email: testEmail,
        phone: '1234567890',
        password: 'password123'
      });
      if (result.success) {
        addResult('Register Test', 'SUCCESS', `User: ${result.data.fullName}, Role: ${result.data.role}`);
      } else {
        addResult('Register Test', 'FAILED', result.error);
      }
    } catch (error) {
      addResult('Register Test', 'ERROR', error.message);
    }
  };

  const testDirectAPI = async () => {
    try {
      addResult('Direct API Test', 'Starting...', 'Testing direct API call');
      const response = await authAPI.login({ email: 'test@example.com', password: 'password123' });
      addResult('Direct API Test', 'SUCCESS', `Response: ${JSON.stringify(response.data)}`);
    } catch (error) {
      addResult('Direct API Test', 'FAILED', error.message);
    }
  };

  const clearResults = () => {
    setTestResults([]);
  };

  return (
    <div className="p-8 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Authentication Test Page</h1>
      
      <div className="mb-6">
        <h2 className="text-xl font-semibold mb-4">Current User State:</h2>
        <pre className="bg-gray-100 p-4 rounded-lg">
          {JSON.stringify(user, null, 2)}
        </pre>
      </div>

      <div className="mb-6">
        <h2 className="text-xl font-semibold mb-4">Test Controls:</h2>
        <div className="flex gap-4">
          <button 
            onClick={testLogin}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Test Login
          </button>
          <button 
            onClick={testRegister}
            className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
          >
            Test Register
          </button>
          <button 
            onClick={testDirectAPI}
            className="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
          >
            Test Direct API
          </button>
          <button 
            onClick={clearResults}
            className="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
          >
            Clear Results
          </button>
        </div>
      </div>

      <div>
        <h2 className="text-xl font-semibold mb-4">Test Results:</h2>
        <div className="space-y-2">
          {testResults.map((result, index) => (
            <div 
              key={index}
              className={`p-3 rounded-lg border ${
                result.result === 'SUCCESS' ? 'bg-green-50 border-green-200' :
                result.result === 'FAILED' ? 'bg-red-50 border-red-200' :
                result.result === 'ERROR' ? 'bg-red-50 border-red-200' :
                'bg-yellow-50 border-yellow-200'
              }`}
            >
              <div className="flex justify-between items-start">
                <div>
                  <span className="font-semibold">{result.test}</span>
                  <span className={`ml-2 px-2 py-1 rounded text-xs ${
                    result.result === 'SUCCESS' ? 'bg-green-200 text-green-800' :
                    result.result === 'FAILED' ? 'bg-red-200 text-red-800' :
                    result.result === 'ERROR' ? 'bg-red-200 text-red-800' :
                    'bg-yellow-200 text-yellow-800'
                  }`}>
                    {result.result}
                  </span>
                </div>
                <span className="text-sm text-gray-500">{result.timestamp}</span>
              </div>
              {result.details && (
                <div className="mt-2 text-sm text-gray-600">
                  {result.details}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TestAuth;
