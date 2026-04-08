const colors = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  IN_PROGRESS: 'bg-blue-100 text-blue-800',
  RESOLVED: 'bg-green-100 text-green-800',
  LOW: 'bg-gray-100 text-gray-700',
  MEDIUM: 'bg-orange-100 text-orange-700',
  HIGH: 'bg-red-100 text-red-700',
}

export default function Badge({ value }) {
  return (
    <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${colors[value] ?? 'bg-gray-100 text-gray-600'}`}>
      {value?.replace('_', ' ')}
    </span>
  )
}
