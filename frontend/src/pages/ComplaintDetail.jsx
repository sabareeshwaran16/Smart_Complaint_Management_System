import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'
import Badge from '../components/Badge'
import toast from 'react-hot-toast'
import { ArrowLeft, Clock, User, Tag, Image } from 'lucide-react'

export default function ComplaintDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [complaint, setComplaint] = useState(null)
  const [history, setHistory] = useState([])
  const [wardens, setWardens] = useState([])
  const [newStatus, setNewStatus] = useState('')
  const [assignId, setAssignId] = useState('')
  const [loading, setLoading] = useState(false)

  const isAdminOrWarden = user.role === 'ADMIN' || user.role === 'WARDEN'

  const fetchData = async () => {
    try {
      const [cRes, hRes] = await Promise.all([
        api.get(`/complaints/${id}`),
        api.get(`/complaints/${id}/history`),
      ])
      setComplaint(cRes.data)
      setHistory(hRes.data)
      setNewStatus(cRes.data.status)
    } catch {
      toast.error('Complaint not found')
      navigate(-1)
    }
  }

  useEffect(() => {
    fetchData()
    if (isAdminOrWarden) {
      // Fetch all users to find wardens — we use a workaround via complaints assignedTo data
      // Since there's no /api/users endpoint, we'll let admin type the warden ID manually
    }
  }, [id])

  const handleStatusUpdate = async () => {
    setLoading(true)
    try {
      await api.put(`/complaints/${id}/status`, { status: newStatus })
      toast.success('Status updated!')
      fetchData()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed')
    } finally {
      setLoading(false)
    }
  }

  const handleAssign = async () => {
    if (!assignId) return toast.error('Enter a user ID')
    setLoading(true)
    try {
      await api.put(`/complaints/${id}/assign`, { assignedToId: Number(assignId) })
      toast.success('Assigned successfully!')
      setAssignId('')
      fetchData()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to assign')
    } finally {
      setLoading(false)
    }
  }

  if (!complaint) return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600" />
    </div>
  )

  return (
    <div className="max-w-3xl mx-auto p-6">
      <button
        onClick={() => navigate(-1)}
        className="flex items-center gap-1 text-sm text-gray-500 hover:text-indigo-600 mb-4"
      >
        <ArrowLeft size={15} /> Back
      </button>

      <div className="bg-white rounded-2xl shadow p-6 mb-4">
        <div className="flex items-start justify-between mb-3">
          <h2 className="text-xl font-bold text-gray-800">{complaint.title}</h2>
          <div className="flex gap-2">
            <Badge value={complaint.status} />
            <Badge value={complaint.priority} />
          </div>
        </div>
        <p className="text-gray-600 text-sm mb-4">{complaint.description}</p>

        <div className="grid grid-cols-2 gap-3 text-sm text-gray-600">
          <div className="flex items-center gap-2">
            <User size={14} className="text-indigo-400" />
            <span>Submitted by: <strong>{complaint.user?.name}</strong></span>
          </div>
          <div className="flex items-center gap-2">
            <Tag size={14} className="text-indigo-400" />
            <span>Category: <strong>{complaint.category?.name}</strong></span>
          </div>
          <div className="flex items-center gap-2">
            <Clock size={14} className="text-indigo-400" />
            <span>Created: <strong>{new Date(complaint.createdAt).toLocaleString()}</strong></span>
          </div>
          {complaint.assignedTo && (
            <div className="flex items-center gap-2">
              <User size={14} className="text-green-400" />
              <span>Assigned to: <strong>{complaint.assignedTo.name}</strong></span>
            </div>
          )}
        </div>

        {complaint.imagePath && (
          <div className="mt-4">
            <p className="text-xs text-gray-400 flex items-center gap-1 mb-1"><Image size={12} /> Attachment</p>
            <img
              src={`/uploads/${complaint.imagePath}`}
              alt="complaint"
              className="rounded-lg max-h-64 object-cover border"
            />
          </div>
        )}
      </div>

      {/* Admin/Warden Actions */}
      {isAdminOrWarden && (
        <div className="bg-white rounded-2xl shadow p-6 mb-4">
          <h3 className="font-semibold text-gray-700 mb-4">Actions</h3>
          <div className="flex gap-3 flex-wrap items-end mb-4">
            <div>
              <label className="block text-xs text-gray-500 mb-1">Update Status</label>
              <select
                value={newStatus}
                onChange={(e) => setNewStatus(e.target.value)}
                className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400"
              >
                {['PENDING', 'IN_PROGRESS', 'RESOLVED'].map((s) => (
                  <option key={s}>{s}</option>
                ))}
              </select>
            </div>
            <button
              onClick={handleStatusUpdate}
              disabled={loading || newStatus === complaint.status}
              className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
            >
              {loading ? 'Saving…' : 'Update Status'}
            </button>
          </div>

          <div className="flex gap-3 flex-wrap items-end">
            <div>
              <label className="block text-xs text-gray-500 mb-1">Assign to Staff (User ID)</label>
              <input
                type="number"
                value={assignId}
                onChange={(e) => setAssignId(e.target.value)}
                placeholder="Enter user ID"
                className="border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-400 w-40"
              />
            </div>
            <button
              onClick={handleAssign}
              disabled={loading || !assignId}
              className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg text-sm font-medium disabled:opacity-60"
            >
              {loading ? 'Assigning…' : 'Assign'}
            </button>
          </div>
        </div>
      )}

      {/* History */}
      <div className="bg-white rounded-2xl shadow p-6">
        <h3 className="font-semibold text-gray-700 mb-4 flex items-center gap-2">
          <Clock size={16} className="text-indigo-400" /> Status History
        </h3>
        {history.length === 0 ? (
          <p className="text-sm text-gray-400">No history yet.</p>
        ) : (
          <ol className="relative border-l border-indigo-100 ml-2 space-y-4">
            {history.map((h) => (
              <li key={h.id} className="ml-4">
                <div className="absolute -left-1.5 mt-1.5 w-3 h-3 rounded-full bg-indigo-400 border-2 border-white" />
                <div className="flex items-center gap-2">
                  <Badge value={h.status} />
                  <span className="text-xs text-gray-400">{new Date(h.updatedAt).toLocaleString()}</span>
                </div>
              </li>
            ))}
          </ol>
        )}
      </div>
    </div>
  )
}
