import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  getCurrentUser: () => api.get('/auth/me'),
};

// Policy APIs
export const policyAPI = {
  getAllPolicies: () => api.get('/policies/all'),
  getPolicies: () => api.get('/policies'),
  getPolicyById: (id) => api.get(`/policies/${id}`),
  createPolicy: (data) => api.post('/policies', data),
  updatePolicy: (id, data) => api.put(`/policies/${id}`, data),
  deletePolicy: (id) => api.delete(`/policies/${id}`),
};

// Policy Holder APIs
export const policyHolderAPI = {
  purchasePolicy: (policyId) => api.post(`/policy-holder/purchase/${policyId}`),
  getMyPolicy: () => api.get('/policy-holder/my-policy'),
  getAllPolicyHolders: () => api.get('/policy-holder/all'),
};

// Doctor APIs
export const doctorAPI = {
  getAllDoctors: () => api.get('/doctor/all'),
  registerDoctor: (data) => api.post('/doctor/register', data),
  getMyAppointments: () => api.get('/doctor/my-appointments'),
  getTodayAppointments: () => api.get('/doctor/today-appointments'),
  updateAppointmentStatus: (id, status) => api.put(`/doctor/appointment/${id}/status`, { status }),
};

// Appointment APIs
export const appointmentAPI = {
  bookAppointment: (data) => api.post('/appointments', data),
  getMyAppointments: () => api.get('/appointments/my-appointments'),
  getAppointmentById: (id) => api.get(`/appointments/${id}`),
};

// Claim APIs
export const claimAPI = {
  submitClaim: (data) => {
    return api.post('/claims', data, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  uploadDocument: (claimId, formData) => {
    return api.post(`/claims/${claimId}/upload-document`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  getMyClaims: () => api.get('/claims/my-claims'),
  getAllClaims: () => api.get('/claims'),
  getClaimsByStatus: (status) => api.get(`/claims/status/${status}`),
  reviewClaim: (id, data) => api.put(`/claims/${id}/review`, data),
  forwardToFinance: (id, remarks) => api.put(`/claims/${id}/forward-to-finance`, { remarks }),
};

// Claims Manager APIs
export const claimsManagerAPI = {
  getAllClaims: () => api.get('/claims-manager/claims'),
  reviewClaim: (id, data) => api.put(`/claims-manager/claims/${id}/review`, data),
  getPendingClaims: () => api.get('/claims-manager/claims/pending'),
};

// Finance APIs
export const financeAPI = {
  processClaim: (claimId, data) => api.post(`/finance/process-claim/${claimId}`, data),
  getAllFinanceRecords: () => api.get('/finance/records'),
};

// Admin APIs
export const adminAPI = {
  getAllUsers: () => api.get('/admin/users'),
  createEmployee: (data) => api.post('/admin/create-employee', data),
  updateUser: (id, data) => api.put(`/admin/users/${id}`, data),
  changeUserRole: (id, role) => api.put(`/admin/users/${id}/role`, { role }),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
};

export default api;

