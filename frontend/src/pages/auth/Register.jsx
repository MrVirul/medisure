import { useState } from 'react';
import { motion } from 'framer-motion';
import { User, Mail, Lock, Phone, AlertCircle, ArrowRight, CheckCircle, Stethoscope, Eye, EyeOff } from 'lucide-react';

const EnhancedRegister = () => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    phone: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [focusedField, setFocusedField] = useState(null);
  const [completedFields, setCompletedFields] = useState({});
  const [agreedToTerms, setAgreedToTerms] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
    
    if (value.trim()) {
      setCompletedFields({
        ...completedFields,
        [name]: true,
      });
    } else {
      setCompletedFields({
        ...completedFields,
        [name]: false,
      });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!formData.fullName || !formData.email || !formData.password) {
      setError('Please fill in all required fields');
      return;
    }

    if (!agreedToTerms) {
      setError('You must agree to the terms and conditions');
      return;
    }
    
    setError('');
    setLoading(true);
    
    setTimeout(() => {
      setLoading(false);
      console.log('Registration successful:', formData);
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

  const progressPercentage = (Object.values(completedFields).filter(Boolean).length / Object.keys(formData).length) * 100;

  const formFields = [
    { name: 'fullName', label: 'Full Name', type: 'text', icon: User, placeholder: 'John Doe', required: true },
    { name: 'email', label: 'Email Address', type: 'email', icon: Mail, placeholder: 'you@company.com', required: true },
    { name: 'phone', label: 'Phone Number', type: 'tel', icon: Phone, placeholder: '+1 (555) 000-0000', required: false },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100 flex items-center justify-center px-4 py-8 relative overflow-hidden">
      {/* Animated Background Blobs */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <motion.div
          className="absolute top-10 right-20 w-80 h-80 bg-blue-200 rounded-full mix-blend-multiply filter blur-3xl opacity-20"
          animate={{ x: [0, 50, 0], y: [0, 30, 0] }}
          transition={{ duration: 8, repeat: Infinity }}
        />
        <motion.div
          className="absolute -bottom-10 left-20 w-80 h-80 bg-blue-100 rounded-full mix-blend-multiply filter blur-3xl opacity-20"
          animate={{ x: [0, -50, 0], y: [0, -30, 0] }}
          transition={{ duration: 8, repeat: Infinity, delay: 1 }}
        />
      </div>

      {/* Main Container */}
      <motion.div
        className="w-full max-w-2xl relative z-10"
        variants={containerVariants}
        initial="hidden"
        animate="visible"
      >
        {/* Header Section */}
        <motion.div
          className="text-center mb-10"
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
            className="text-4xl md:text-5xl font-bold bg-gradient-to-r from-blue-600 to-blue-800 bg-clip-text text-transparent mb-2"
            variants={itemVariants}
            custom={1}
          >
            Join MediSure
          </motion.h1>

          {/* Subtitle */}
          <motion.p
            className="text-gray-600 text-lg"
            variants={itemVariants}
            custom={2}
          >
            Create your account and manage your healthcare seamlessly
          </motion.p>
        </motion.div>

        {/* Register Card */}
        <motion.div
          className="relative"
          variants={itemVariants}
          custom={3}
        >
          {/* Card Background Glow */}
          <div className="absolute inset-0 bg-gradient-to-r from-blue-100 to-blue-50 rounded-3xl blur-lg opacity-30"></div>

          {/* Card Container */}
          <div className="relative bg-white rounded-3xl shadow-2xl overflow-hidden border border-gray-100">
            {/* Card Header with Progress */}
            <div className="bg-gradient-to-r from-blue-50 to-blue-100 px-8 py-8 border-b border-gray-200">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h2 className="text-2xl font-bold text-gray-900">Create Account</h2>
                  <p className="text-gray-600 text-sm mt-1">Fill in your details to get started</p>
                </div>
                <div className="text-right">
                  <div className="text-3xl font-bold bg-gradient-to-r from-blue-600 to-blue-700 bg-clip-text text-transparent">
                    {/* Divider */}
              <div className="mt-8 flex items-center gap-3">
                <div className="flex-1 h-px bg-gray-200"></div>
                <span className="text-sm text-gray-500">Already registered?</span>
                <div className="flex-1 h-px bg-gray-200"></div>
              </div>

              {/* Login Link Button */}
              <motion.a
                href="/login"
                className="block mt-6 py-3 px-4 border-2 border-blue-200 text-blue-600 font-bold rounded-xl hover:bg-blue-50 transition-all duration-300 text-center"
                variants={itemVariants}
                custom={10}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                Sign In Instead
              </motion.a>
            </div>

            {/* Footer Benefits */}
            <div className="bg-gradient-to-r from-blue-50 to-blue-100 px-8 py-6 border-t border-gray-200">
              <p className="text-xs text-gray-600 font-semibold mb-3">WHAT YOU GET:</p>
              <div className="grid grid-cols-3 gap-4 text-center">
                <div>
                  <p className="text-2xl mb-1">🔒</p>
                  <p className="text-xs text-gray-700 font-medium">Secure</p>
                </div>
                <div>
                  <p className="text-2xl mb-1">⚡</p>
                  <p className="text-xs text-gray-700 font-medium">Fast</p>
                </div>
                <div>
                  <p className="text-2xl mb-1">✨</p>
                  <p className="text-xs text-gray-700 font-medium">Simple</p>
                </div>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Footer Security Message */}
        <motion.p
          className="text-center text-gray-600 text-xs mt-8"
          variants={itemVariants}
          custom={11}
        >
          💡 Your data is encrypted and never shared with third parties
        </motion.p>
      </motion.div>
    </div>
  );
};

export default EnhancedRegister;Math.round(progressPercentage)}%
                  </div>
                  <p className="text-xs text-gray-600">Complete</p>
                </div>
              </div>
              
              {/* Progress Bar */}
              <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                <motion.div
                  className="h-full bg-gradient-to-r from-blue-500 to-blue-600"
                  initial={{ width: 0 }}
                  animate={{ width: `${progressPercentage}%` }}
                  transition={{ duration: 0.5 }}
                />
              </div>
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
                    <p className="text-red-800 font-semibold text-sm mb-1">Registration Error</p>
                    <p className="text-red-700 text-sm">{error}</p>
                  </div>
                </motion.div>
              )}

              {/* Form */}
              <div className="space-y-5">
                {/* Grid for 2 columns on desktop */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                  {formFields.map((field, index) => {
                    const Icon = field.icon;
                    return (
                      <motion.div
                        key={field.name}
                        variants={itemVariants}
                        custom={4 + index}
                        className={field.name === 'email' ? 'md:col-span-2' : ''}
                      >
                        <div className="space-y-2">
                          <div className="flex items-center justify-between">
                            <label className="block text-sm font-semibold text-gray-700">
                              {field.label}
                              {field.required && <span className="text-red-500 ml-1">*</span>}
                            </label>
                            {completedFields[field.name] && (
                              <motion.div
                                initial={{ scale: 0, rotate: -180 }}
                                animate={{ scale: 1, rotate: 0 }}
                                className="text-green-500"
                              >
                                <CheckCircle className="w-4 h-4" />
                              </motion.div>
                            )}
                          </div>

                          <div className="relative group">
                            <Icon className={`absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 transition-colors pointer-events-none ${
                              focusedField === field.name ? 'text-blue-600' : 'text-gray-400'
                            }`} />
                            <input
                              type={field.type}
                              name={field.name}
                              value={formData[field.name]}
                              onChange={handleChange}
                              onFocus={() => setFocusedField(field.name)}
                              onBlur={() => setFocusedField(null)}
                              placeholder={field.placeholder}
                              className="w-full pl-12 pr-4 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:bg-white focus:border-blue-500 focus:outline-none transition-all duration-300 font-medium placeholder:text-gray-400"
                              required={field.required}
                            />
                          </div>
                        </div>
                      </motion.div>
                    );
                  })}
                </div>

                {/* Password Field - Full Width */}
                <motion.div
                  variants={itemVariants}
                  custom={7}
                  className="space-y-2"
                >
                  <div className="flex items-center justify-between">
                    <label className="block text-sm font-semibold text-gray-700">
                      Password <span className="text-red-500">*</span>
                    </label>
                    {completedFields.password && (
                      <motion.div
                        initial={{ scale: 0, rotate: -180 }}
                        animate={{ scale: 1, rotate: 0 }}
                        className="text-green-500"
                      >
                        <CheckCircle className="w-4 h-4" />
                      </motion.div>
                    )}
                  </div>
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
                      placeholder="Create a strong password"
                      className="w-full pl-12 pr-12 py-3 bg-gray-50 border-2 border-gray-200 rounded-xl focus:bg-white focus:border-blue-500 focus:outline-none transition-all duration-300 font-medium placeholder:text-gray-400"
                      required
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
                  <p className="text-xs text-gray-500 mt-2">
                    At least 8 characters with uppercase, lowercase, and numbers
                  </p>
                </motion.div>

                {/* Terms & Conditions */}
                <motion.div
                  variants={itemVariants}
                  custom={8}
                  className="flex items-start gap-3 p-4 bg-blue-50 rounded-xl border border-blue-100"
                >
                  <input
                    type="checkbox"
                    id="terms"
                    checked={agreedToTerms}
                    onChange={(e) => setAgreedToTerms(e.target.checked)}
                    className="w-5 h-5 accent-blue-600 cursor-pointer mt-0.5 flex-shrink-0 rounded"
                  />
                  <label htmlFor="terms" className="text-sm text-gray-700 cursor-pointer">
                    I agree to the{' '}
                    <a href="#" className="text-blue-600 font-semibold hover:underline">
                      Terms of Service
                    </a>
                    {' '}and{' '}
                    <a href="#" className="text-blue-600 font-semibold hover:underline">
                      Privacy Policy
                    </a>
                  </label>
                </motion.div>

                {/* Submit Button */}
                <motion.button
                  type="submit"
                  disabled={loading || !agreedToTerms}
                  onClick={handleSubmit}
                  className="w-full mt-8 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white font-bold rounded-xl hover:from-blue-700 hover:to-blue-800 disabled:from-gray-400 disabled:to-gray-500 transition-all duration-300 disabled:cursor-not-allowed shadow-lg hover:shadow-xl relative overflow-hidden group"
                  variants={itemVariants}
                  custom={9}
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
                        <span>Creating Account...</span>
                      </>
                    ) : (
                      <>
                        <span>Create Account</span>
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

              {