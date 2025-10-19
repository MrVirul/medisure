import { motion } from 'framer-motion';

const Card = ({ 
  children, 
  title, 
  subtitle,
  className = '',
  headerAction,
  noPadding = false,
  hover = false,
  icon = null
}) => {
  return (
    <motion.div 
      className={`bg-white rounded-2xl shadow-md hover:shadow-lg transition-all duration-300 overflow-hidden border border-gray-100 ${hover ? 'hover:scale-[1.02]' : ''} ${className}`}
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
    >
      {(title || headerAction || icon) && (
        <div className="px-6 py-5 border-b border-gray-100 flex justify-between items-center bg-gradient-to-r from-white to-gray-50">
          <div className="flex items-center gap-3">
            {icon && <span className="text-2xl text-primary-600">{icon}</span>}
            <div>
              {title && <h3 className="text-xl font-semibold text-gray-900">{title}</h3>}
              {subtitle && <p className="text-sm text-gray-600 mt-1">{subtitle}</p>}
            </div>
          </div>
          {headerAction && <div>{headerAction}</div>}
        </div>
      )}
      <div className={noPadding ? '' : 'p-6'}>
        {children}
      </div>
    </motion.div>
  );
};

export default Card;

