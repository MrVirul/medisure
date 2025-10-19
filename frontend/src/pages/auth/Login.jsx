import { useState } from 'react';
import { motion } from 'framer-motion';
import { Mail, Lock, AlertCircle, Eye, EyeOff, ArrowRight, Stethoscope } from 'lucide-react';

const EnhancedLogin = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [focusedField, setFocusedField] = useState(null);
  const [rememberMe, setRememberMe] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!formData.email || !formData.password) {
      setError('Please fill in all fields');
      return;
    }
    
    setError('');
    setLoading(true);
    
    setTimeout(() => {
      setLoading(false);
      // Simulating successful login - replace with actual API call
      console.log('Login successful:', formData);
    }, 2000);
  };

  const containerVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.6, ease: 'easeOut' } }
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 10 },
    visible: (i) => ({
      opacity: 1,
      y: 0,
      transition: { delay: 0.1 * i, duration: 0.5 }
    })
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100 flex items-center justify-center px-4 py-8 relative overflow-hidden">
      {/* Animated Background Blobs */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <motion.div
          className="absolute top-20 left-10 w-72 h-72 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20"
          animate={{ x: [0, 50, 0], y: [0, 30, 0] }}
          transition={{ duration: 8, repeat: Infinity }}
        />
        <motion.div
          className="absolute bottom-20 right-10 w-72 h-72 bg-blue-100 rounded-full mix-blend-multiply filter blur-3xl opacity-20"
          animate={{ x: [0, -50, 0], y: [0, -30, 0] }}
          transition={{ duration: 8, repeat: Infinity, delay: 1 }}
        />
      </div>

      {/* Main Container */}
      <motion.div
        className="w-full max-w-md relative z-10"
        variants={containerVariants}
        initial="hidden"
        animate="visible"
      >
        {/* Header Section */}
        <motion.div
          className="text-center mb-12"
          variants={itemVariants}
          custom={0}
          initial="hidden"
          animate="visible"
        >
          {/* Logo */}
          <motion.div
            className="flex justify-center mb-6"
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.95 }}
          >
            <div className="relative">
              <div className="absolute inset-0 bg-gradient-to-r from-blue-400 to-blue-600 rounded-3xl blur opacity-60"></div>
              <div className="relative p-4 bg-gradient-to-br from-blue-500 to-blue-700 rounded-3xl shadow-xl">
                <Stethoscope className="w-8 h-8 text-white" />
              </div>
            </div>
          </motion.div>

          {/* Title */}
          <motion.h1
            className="text-5xl font-bold bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent mb-3"
            variants={itemVariants}
            custom={1}
          >
            MediSure
          </motion.h1>

          {/* Subtitle */}
          <motion.p
            className="text-gray-600 text-lg font-medium"
            variants={itemVariants}
            custom={2}
          >
            Smart Healthcare Management
          </motion.p>
        </motion.div>

        {/* Login Card */}
        <motion.div
          className="relative"
          variants={itemVariants}
          custom={3}
        >
          {/* Card Background Glow */}
          <div className="absolute inset-0 bg-gradient-to-r from-blue-100 to-blue-50 rounded-3xl blur-lg opacity-30"></div>

          {/* Card Container */}
          <div className="relative bg-white rounded-3xl shadow-2xl overflow-hidden border border-gray-100">
            {/* Card Header */}
            <div className="bg-gradient-to-r from-blue-50 to-blue-100 px-8 py-8 border-b border-gray-200">
              <h2 className="text-2xl font-bold text-gray-900 mb-2">Welcome Back</h2>
              <p className="text-gray-600 text-sm">Sign in to access your healthcare account</p>
            </div>

            {/* Card Content */}
            <div className="p-8">
              {/* Error Alert */}
              {error && (
                <motion.div
                  className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 rounded-xl flex items-start gap-3"
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                >
                  <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
                  <div>
                    <p className="text-red-800 font-semibold text-sm mb-1">Login Failed</p>
                    <p className="text-red-700 text-sm">{error}</p>
                  </div>
                </motion.div>
              )}

              {/* Form */}
              <div className="space-y-5">
                {/* Email Field */}
                <motion.div
                  variants={itemVariants}
                  custom={4}
                  className="space-y-2"
                >
                  <label className="block text-sm font-semibold text-gray-700 ml-1">
                    Email Address
                  </label>
                  <div className="relative group">
                    <Mail className={`absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 transition-colors pointer-events-none ${
                      focusedField === 'email' ? 'text-blue-600' : 'text-gray-400'
                    }`} />
                    <input
                      type="email"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      onFocus={() => setFocusedField('email')}
                      onBlur={() => setFocusedField(null)}
                      placeholder="you@company.com"
                      className="w-full pl-12 pr-4 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:bg-white focus:border-blue-500 focus:outline-none transition-all duration-300 font-medium placeholder:text-gray-400"
                    />
                  </div>
                </motion.div>

                {/* Password Field */}
                <motion.div
                  variants={itemVariants}
                  custom={5}
                  className="space-y-2"
                >
                  <label className="block text-sm font-semibold text-gray-700 ml-1">
                    Password
                  </label>
                  <div className="relative group">
                    <Lock className={`absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 transition-colors pointer-events-none ${
                      focusedField === 'password' ? 'text-blue-600' : 'text-gray-400'
                    }`} />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      onFocus={() => setFocusedField('password')}
                      onBlur={() => setFocusedField(null)}
                      placeholder="Enter your password"
                      className="w-full pl-12 pr-12 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:bg-white focus:border-blue-500 focus:outline-none transition-all duration-300 font-medium placeholder:text-gray-400"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
                      aria-label="Toggle password visibility"
                    >
                      {showPassword ? (
                        <EyeOff className="w-5 h-5" />
                      ) : (
                        <Eye className="w-5 h-5" />
                      )}
                    </button>
                  </div>
                </motion.div>

                {/* Remember & Forgot */}
                <div className="flex items-center justify-between pt-2">
                  <label className="flex items-center gap-2 cursor-pointer group">
                    <input
                      type="checkbox"
                      checked={rememberMe}
                      onChange={(e) => setRememberMe(e.target.checked)}
                      className="w-4 h-4 accent-blue-600 cursor-pointer rounded"
                    />
                    <span className="text-sm text-gray-600 group-hover:text-gray-700">Remember me</span>
                  </label>
                  <a href="#" className="text-sm font-semibold text-blue-600 hover:text-blue-700 transition-colors">
                    Forgot password?
                  </a>
                </div>

                {/* Submit Button */}
                <motion.button
                  type="submit"
                  disabled={loading}
                  onClick={handleSubmit}
                  className="w-full mt-8 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white font-bold rounded-xl hover:from-blue-700 hover:to-blue-800 disabled:from-gray-400 disabled:to-gray-500 transition-all duration-300 disabled:cursor-not-allowed shadow-lg hover:shadow-xl relative overflow-hidden group"
                  variants={itemVariants}
                  custom={6}
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <motion.div
                    className="absolute inset-0 bg-gradient-to-r from-transparent via-white to-transparent opacity-0 group-hover:opacity-20"
                    animate={{ x: ['-100%', '100%'] }}
                    transition={{ duration: 2, repeat: Infinity }}
                  />
                  <div className="relative flex items-center justify-center gap-2">
                    {loading ? (
                      <>
                        <motion.div
                          className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                          animate={{ rotate: 360 }}
                          transition={{ duration: 1, repeat: Infinity }}
                        />
                        <span>Signing in...</span>
                      </>
                    ) : (
                      <>
                        <span>Sign In</span>
                        <motion.div
                          animate={{ x: [0, 4, 0] }}
                          transition={{ duration: 2, repeat: Infinity }}
                        >
                          <ArrowRight className="w-5 h-5" />
                        </motion.div>
                      </>
                    )}
                  </div>
                </motion.button>
              </div>

              {/* Divider */}
              <div className="mt-8 flex items-center gap-3">
                <div className="flex-1 h-px bg-gray-200"></div>
                <span className="text-sm text-gray-500">New user?</span>
                <div className="flex-1 h-px bg-gray-200"></div>
              </div>

              {/* Register Link Button */}
              <motion.a
                href="/register"
                className="block mt-6 py-3 px-4 border-2 border-blue-200 text-blue-600 font-bold rounded-xl hover:bg-blue-50 transition-all duration-300 text-center"
                variants={itemVariants}
                custom={7}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                Create Account
              </motion.a>
            </div>
          </div>
        </motion.div>

        {/* Footer Security Message */}
        <motion.p
          className="text-center text-gray-600 text-xs mt-8"
          variants={itemVariants}
          custom={8}
        >
          🔒 Protected by industry-standard security encryption
        </motion.p>
      </motion.div>
    </div>
  );
};

export default EnhancedLogin;