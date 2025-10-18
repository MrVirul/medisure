import Button from './common/Button';
import Card from './common/Card';

const PolicyCard = ({ policy, onSelect }) => {
  return (
    <Card className="hover:shadow-lg transition-shadow">
      <div className="space-y-4">
        <div className="flex justify-between items-start">
          <div>
            <h3 className="text-xl font-bold text-gray-900">{policy.name}</h3>
            <span className="inline-block mt-1 px-3 py-1 bg-primary-100 text-primary-800 text-xs rounded-full font-semibold">
              {policy.type}
            </span>
          </div>
          <div className="text-right">
            <p className="text-2xl font-bold text-primary-600">₹{policy.premiumAmount}</p>
            <p className="text-xs text-gray-500">per month</p>
          </div>
        </div>

        <p className="text-gray-600 text-sm line-clamp-3">{policy.description}</p>

        <div className="border-t pt-4">
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div>
              <p className="text-gray-600">Coverage Amount</p>
              <p className="font-semibold text-green-600">₹{policy.coverageAmount.toLocaleString()}</p>
            </div>
            <div>
              <p className="text-gray-600">Duration</p>
              <p className="font-semibold">{policy.policyDurationMonths} months</p>
            </div>
          </div>
        </div>

        {onSelect && (
          <Button variant="primary" fullWidth onClick={() => onSelect(policy)}>
            Select Policy
          </Button>
        )}
      </div>
    </Card>
  );
};

export default PolicyCard;

