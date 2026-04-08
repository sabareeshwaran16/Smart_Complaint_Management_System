import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import Badge from '../components/Badge'
import toast from 'react-hot-toast'
import { Eye, PlusCircle, X } from 'lucide-react'

export default function AdminDashboard() {
  const { user } = useAuth()
  const [complaints, setComplaints] = useState([])
  const [categories, setCategories] = useState([])
  const [filterStatus, setFilterStatus] = useState('')
  const [filterCategory, setFilterCategory] = useState('')
  const [showCatForm, setShowCatForm] = useState(false)
  const [catName, setCatName] = useState('')
  const [loading, setLoading] = useState(false)

  const fetchComplaints = async () => {
    try {
      const params = {}
      if (filterStatus) params.status = filterStatus
      if (filterCategory) params.categoryId = filterCategory
      const { data } = await api.get('/complaints', { params })
      setComplaints(data)
    } catch {
      toast.error('Failed to load complaints')
    }
  }

  useEffect(() => {
    fetchComplaints()
    api.get('/categories').then(({ data }) => setCategories(data))
  }, [filterStatus, filterCategory])

  const handleAddCategory = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await api.post('/categories', { name: catName })
      toast.success('Category added!')
      setCatName('')
      setShowCatForm(false)
      const { data } = await api.get('/categories')
      setCategories(data)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed')
    } finally {
      setLoading(false)
    }
  }

  const stats = {
    total: complaints.length,
    pending: complaints.filter((c) => c.status === 'PENDING').length,
    inProgress: complaints.filter((c) => c.status === 'IN_PROGRESS').length,
    resolved: complaints.filter((c) => c.status === 'RESOLVED').length,
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">
            {user.role === 'ADMIN' ? 'Admin' : 'Warden'} Dashboard
          </h2>
          <p className="text-gray-500 text-sm">Welcome, {user.name}</p>
        </div>
        {user.role === 'ADMIN' && (
          <button
            onClick={() => setShowCatForm(!showCatForm)}
            className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm font-medium"
          >
            <PlusCircle size={16} />
            {showCatForm ? 'Cancel' : 'Add Category'}
          </button>
        )}
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        {[
          { label: 'Total', value: stats.total, color: 'bg-indigo-50 text-indigo-700' },
          { label: 'Pending', value: stats.pending, color: 'bg-yellow-50 text-yellow-700' },
          { label: 'In Progress', value: stats.inProgress, color: 'bg-blue-50 text-blue-700' },
          { label: 'Resolved', value: stats.resolved, color: 'bg-green-50 text-green-700' },
        ].map((s) => (
          <div key={s.label} className={`rounded-xl p-4 ${s.color} shadow-sm`}>
            <p className="text-2xl font-bold">{s.value}</p>
            <p className="text-sm font-medium">{s.label}</p>
          </div>
        ))}
      </div>

      {/* Add Category Form */}
      {showCatForm && (
        <div className="bg-white rounded-xl shadow p-4 mb-6 border border-indigo-100 flex gap-3 items-end">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">Category Name</label>
            <input
              value={catName}
              onChange={(e) => setCatName(e.target.value)}
              placeholder="e.g. Plumbing"
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
            />
          </div>
          <button
            onClick={handleAddCategory}
            disabled={loading || !catName.trim()}
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
          >
            {loading ? 'Adding…' : 'Add'}
          </button>
        </div>
      )}

      {/* Filters */}
      <div className="flex gap-3 mb-4 flex-wrap">
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
        >
          <option value="">All Statuses</option>
          {['PENDING', 'IN_PROGRESS', 'RESOLVED'].map((s) => <option key={s}>{s}</option>)}
        </select>
        <select
          value={filterCategory}
          onChange={(e) => setFilterCategory(e.target.value)}
          className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
        >
          <option value="">All Categories</option>
          {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        {(filterStatus || filterCategory) && (
          <button
            onClick={() => { setFilterStatus(''); setFilterCategory('') }}
            className="flex items-center gap-1 text-sm text-gray-500 hover:text-red-500"
          >
            <X size={14} /> Clear
          </button>
        )}
      </div>

      {/* Complaints Table */}
      {complaints.length === 0 ? (
        <div className="text-center py-16 text-gray-400">No complaints found.</div>
      ) : (
        <div className="bg-white rounded-xl shadow overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-600 text-left">
              <tr>
                <th className="px-4 py-3 font-medium">#</th>
                <th className="px-4 py-3 font-medium">Title</th>
                <th className="px-4 py-3 font-medium">Student</th>
                <th className="px-4 py-3 font-medium">Category</th>
                <th className="px-4 py-3 font-medium">Priority</th>
                <th className="px-4 py-3 font-medium">Status</th>
                <th className="px-4 py-3 font-medium">Date</th>
                <th className="px-4 py-3 font-medium"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {complaints.map((c) => (
                <tr key={c.id} className="hover:bg-gray-50 transition">
                  <td className="px-4 py-3 text-gray-400">{c.id}</td>
                  <td className="px-4 py-3 font-medium text-gray-800 max-w-[180px] truncate">{c.title}</td>
                  <td className="px-4 py-3 text-gray-600">{c.user?.name}</td>
                  <td className="px-4 py-3 text-gray-600">{c.category?.name}</td>
                  <td className="px-4 py-3"><Badge value={c.priority} /></td>
                  <td className="px-4 py-3"><Badge value={c.status} /></td>
                  <td className="px-4 py-3 text-gray-400">{new Date(c.createdAt).toLocaleDateString()}</td>
                  <td className="px-4 py-3">
                    <Link
                      to={`/complaint/${c.id}`}
                      className="flex items-center gap-1 text-indigo-600 hover:text-indigo-800 font-medium"
                    >
                      <Eye size={14} /> View
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
