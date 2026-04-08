import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import Badge from '../components/Badge'
import toast from 'react-hot-toast'
import { PlusCircle, Eye, RefreshCw } from 'lucide-react'

export default function StudentDashboard() {
  const { user } = useAuth()
  const [complaints, setComplaints] = useState([])
  const [categories, setCategories] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ title: '', description: '', priority: 'MEDIUM', categoryId: '' })
  const [image, setImage] = useState(null)
  const [loading, setLoading] = useState(false)
  const [catLoading, setCatLoading] = useState(false)

  const fetchCategories = async () => {
    setCatLoading(true)
    try {
      const { data } = await api.get('/categories')
      setCategories(data)
      if (data.length === 0) toast('No categories yet. Ask admin to add some.', { icon: 'ℹ️' })
    } catch (err) {
      toast.error('Failed to load categories: ' + (err.response?.data?.message || err.message))
    } finally {
      setCatLoading(false)
    }
  }

  const fetchComplaints = async () => {
    try {
      const { data } = await api.get(`/complaints/user/${user.id}`)
      setComplaints(data)
    } catch (err) {
      toast.error('Failed to load complaints: ' + (err.response?.data?.message || err.message))
    }
  }

  useEffect(() => {
    fetchComplaints()
    fetchCategories()
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.categoryId) return toast.error('Please select a category')
    setLoading(true)
    try {
      const fd = new FormData()
      fd.append(
        'data',
        new Blob(
          [JSON.stringify({ ...form, categoryId: Number(form.categoryId) })],
          { type: 'application/json' }
        )
      )
      if (image) fd.append('image', image)
      await api.post('/complaints', fd)
      toast.success('Complaint submitted!')
      setShowForm(false)
      setForm({ title: '', description: '', priority: 'MEDIUM', categoryId: '' })
      setImage(null)
      fetchComplaints()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to submit')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-4xl mx-auto p-6">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">My Complaints</h2>
          <p className="text-gray-500 text-sm">Welcome, {user.name}</p>
        </div>
        <button
          onClick={() => { setShowForm(!showForm); if (!showForm) fetchCategories() }}
          className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          <PlusCircle size={16} />
          {showForm ? 'Cancel' : 'New Complaint'}
        </button>
      </div>

      {showForm && (
        <div className="bg-white rounded-xl shadow p-6 mb-6 border border-indigo-100">
          <h3 className="font-semibold text-gray-700 mb-4">Submit a Complaint</h3>
          <form onSubmit={handleSubmit} className="space-y-3">
            <input
              required
              placeholder="Title"
              value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })}
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
            />
            <textarea
              required
              placeholder="Description"
              rows={3}
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
            />
            <div className="grid grid-cols-2 gap-3">
              <select
                value={form.priority}
                onChange={(e) => setForm({ ...form, priority: e.target.value })}
                className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
              >
                {['LOW', 'MEDIUM', 'HIGH'].map((p) => <option key={p}>{p}</option>)}
              </select>

              <div className="flex gap-1">
                <select
                  value={form.categoryId}
                  onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
                  className="flex-1 border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
                >
                  <option value="">
                    {catLoading ? 'Loading...' : categories.length === 0 ? 'No categories available' : 'Select Category'}
                  </option>
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
                <button
                  type="button"
                  onClick={fetchCategories}
                  title="Refresh categories"
                  className="border rounded-lg px-2 py-2 text-gray-500 hover:text-indigo-600 hover:border-indigo-400"
                >
                  <RefreshCw size={14} className={catLoading ? 'animate-spin' : ''} />
                </button>
              </div>
            </div>

            {categories.length === 0 && (
              <p className="text-xs text-red-500">
                No categories found. Please ask the Admin to add categories first, then click the refresh button above.
              </p>
            )}

            <div>
              <label className="block text-xs text-gray-500 mb-1">Attach Image (optional)</label>
              <input
                type="file"
                accept="image/*"
                onChange={(e) => setImage(e.target.files[0])}
                className="text-sm"
              />
            </div>
            <button
              type="submit"
              disabled={loading || categories.length === 0}
              className="bg-indigo-600 hover:bg-indigo-700 text-white px-5 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
            >
              {loading ? 'Submitting…' : 'Submit'}
            </button>
          </form>
        </div>
      )}

      {complaints.length === 0 ? (
        <div className="text-center py-16 text-gray-400">
          <p className="text-lg">No complaints yet.</p>
          <p className="text-sm">Click "New Complaint" to get started.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {complaints.map((c) => (
            <div key={c.id} className="bg-white rounded-xl shadow-sm border p-4 flex items-center justify-between hover:shadow-md transition">
              <div>
                <p className="font-semibold text-gray-800">{c.title}</p>
                <p className="text-xs text-gray-500 mt-0.5">{c.category?.name} &middot; {new Date(c.createdAt).toLocaleDateString()}</p>
                <div className="flex gap-2 mt-1.5">
                  <Badge value={c.status} />
                  <Badge value={c.priority} />
                </div>
              </div>
              <Link
                to={`/complaint/${c.id}`}
                className="flex items-center gap-1 text-indigo-600 hover:text-indigo-800 text-sm font-medium"
              >
                <Eye size={15} /> View
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
