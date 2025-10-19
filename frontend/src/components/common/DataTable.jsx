import { motion } from 'framer-motion';
import { FileX } from 'lucide-react';

const DataTable = ({ columns, data, onRowClick, loading = false }) => {
  return (
    <div className="overflow-x-auto rounded-2xl border border-gray-200 shadow-sm">
      <table className="min-w-full divide-y divide-gray-100">
        <thead className="bg-gradient-to-r from-gray-50 to-gray-100">
          <tr>
            {columns.map((column, index) => (
              <th
                key={index}
                className="px-6 py-4 text-left text-xs font-semibold text-gray-700 uppercase tracking-wider"
              >
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="bg-white divide-y divide-gray-100">
          {loading ? (
            <tr>
              <td
                colSpan={columns.length}
                className="px-6 py-12 text-center"
              >
                <div className="flex flex-col items-center gap-3">
                  <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
                  <p className="text-gray-500 font-medium">Loading data...</p>
                </div>
              </td>
            </tr>
          ) : data.length > 0 ? (
            data.map((row, rowIndex) => (
              <motion.tr
                key={rowIndex}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: rowIndex * 0.05 }}
                onClick={() => onRowClick && onRowClick(row)}
                className={`transition-all duration-200 ${
                  onRowClick 
                    ? 'hover:bg-primary-50 hover:shadow-sm cursor-pointer' 
                    : 'hover:bg-gray-50'
                }`}
              >
                {columns.map((column, colIndex) => (
                  <td key={colIndex} className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {column.render ? column.render(row) : row[column.accessor]}
                  </td>
                ))}
              </motion.tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={columns.length}
                className="px-6 py-12 text-center"
              >
                <div className="flex flex-col items-center gap-3">
                  <div className="p-3 bg-gray-100 rounded-full">
                    <FileX className="w-6 h-6 text-gray-400" />
                  </div>
                  <p className="text-gray-500 font-medium">No data available</p>
                  <p className="text-gray-400 text-sm">There are no records to display at this time</p>
                </div>
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default DataTable;

