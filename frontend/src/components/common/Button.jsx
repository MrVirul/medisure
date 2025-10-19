import { motion } from 'framer-motion';

const Button = ({ 
  children, 
  onClick, 
  type = 'button', 
  variant = 'primary', 
  size = 'md',
  disabled = false,
  fullWidth = false,
  className = '',
  icon = null,
  loading = false
}) => {
  const baseStyles = 'font-medium rounded-xl transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-offset-2 shadow-md hover:shadow-lg disabled:shadow-none';
  
  const variants = {
    primary: 'bg-primary-600 hover:bg-primary-700 text-white focus:ring-primary-500 disabled:bg-primary-300 disabled:text-primary-100',
    secondary: 'bg-slate-600 hover:bg-slate-700 text-white focus:ring-slate-500 disabled:bg-slate-300 disabled:text-slate-100',
    success: 'bg-emerald-600 hover:bg-emerald-700 text-white focus:ring-emerald-500 disabled:bg-emerald-300 disabled:text-emerald-100',
    danger: 'bg-red-600 hover:bg-red-700 text-white focus:ring-red-500 disabled:bg-red-300 disabled:text-red-100',
    warning: 'bg-amber-600 hover:bg-amber-700 text-white focus:ring-amber-500 disabled:bg-amber-300 disabled:text-amber-100',
    outline: 'bg-transparent border-2 border-primary-600 text-primary-600 hover:bg-primary-50 hover:border-primary-700 focus:ring-primary-500 disabled:border-primary-300 disabled:text-primary-300',
    ghost: 'bg-transparent text-primary-600 hover:bg-primary-50 focus:ring-primary-500 disabled:text-primary-300',
  };
  
  const sizes = {
    sm: 'px-4 py-2 text-sm',
    md: 'px-6 py-3 text-base',
    lg: 'px-8 py-4 text-lg',
  };
  
  const widthClass = fullWidth ? 'w-full' : '';
  
  return (
    <motion.button
      type={type}
      onClick={onClick}
      disabled={disabled || loading}
      whileHover={{ scale: disabled || loading ? 1 : 1.02 }}
      whileTap={{ scale: disabled || loading ? 1 : 0.98 }}
      className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${widthClass} ${className} ${disabled || loading ? 'cursor-not-allowed' : 'cursor-pointer'} flex items-center justify-center gap-2`}
    >
      {loading ? (
        <motion.div
          className="w-4 h-4 border-2 border-current border-t-transparent rounded-full"
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
        />
      ) : icon ? (
        <span className="text-lg">{icon}</span>
      ) : null}
      {children}
    </motion.button>
  );
};

export default Button;

