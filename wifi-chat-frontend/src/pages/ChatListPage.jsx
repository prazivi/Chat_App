import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/useAuth.js'

function ChatListPage() {
  const [rooms, setRooms] = useState([])
  const [onlineUsers, setOnlineUsers] = useState([])
  const [newRoomName, setNewRoomName] = useState('')
  const [error, setError] = useState('')
  const { username, logout } = useAuth()
  const navigate = useNavigate()

  const loadData = async () => {
    try {
      const [roomResponse, userResponse] = await Promise.all([api.get('/chatrooms/my'), api.get('/users/online')])
      setRooms(roomResponse.data)
      setOnlineUsers(userResponse.data)
    } catch {
      setError('Could not reach the chat server. Is the backend running?')
    }
  }
  useEffect(() => {
    Promise.resolve().then(loadData)
  }, [])

  const createRoom = async (event) => {
    event.preventDefault()
    if (!newRoomName.trim()) return
    try {
      await api.post('/chatrooms', { name: newRoomName.trim(), type: 'GROUP' })
      setNewRoomName('')
      loadData()
    } catch (err) { setError(err.response?.data?.error || 'Could not create room.') }
  }

  return (
    <main className="app-shell">
      <header className="topbar"><div className="wordmark"><span>⌁</span> WIFI CHAT</div><div className="profile"><span className="avatar">{username?.[0]?.toUpperCase()}</span><span>{username}</span><button className="icon-button" onClick={() => { logout(); navigate('/login') }} aria-label="Log out" title="Log out">↪</button></div></header>
      <div className="directory-layout">
        <section className="directory-main"><div className="page-intro"><div><span className="eyebrow">Your local network</span><h1>Rooms</h1></div><span className="room-count">{rooms.length} {rooms.length === 1 ? 'room' : 'rooms'}</span></div>
          {error && <div className="form-error" role="alert">{error}</div>}
          <form className="create-room" onSubmit={createRoom}><input placeholder="Name a new room..." value={newRoomName} onChange={(event) => setNewRoomName(event.target.value)} /><button type="submit" aria-label="Create room" title="Create room">+</button></form>
          <div className="room-list">{rooms.length === 0 ? <div className="empty-state"><span>○</span><h2>No rooms yet</h2><p>Create the first space for your network.</p></div> : rooms.map((room) => <button className="room-item" key={room.id} onClick={() => navigate(`/chat/${room.id}`)}><span className="room-icon">{room.type === 'GROUP' ? '✣' : '◌'}</span><span className="room-copy"><strong>{room.name || `Room #${room.id}`}</strong><small>{room.type === 'GROUP' ? 'Group room' : 'Direct chat'}</small></span><span className="chevron">→</span></button>)}</div>
        </section>
        <aside className="online-panel"><div className="online-heading"><span className="status-dot"></span><h2>Online now</h2><strong>{onlineUsers.length}</strong></div><p className="muted">People on this network</p><div className="user-list">{onlineUsers.length === 0 ? <p className="muted">No one else is online.</p> : onlineUsers.map((user) => <div className="user-row" key={user.id}><span className="avatar avatar-small">{(user.displayName || user.username)?.[0]?.toUpperCase()}</span><span>{user.displayName || user.username}</span><span className="user-dot"></span></div>)}</div><div className="network-note"><span>⌁</span><p>Messages stay<br />on your network.</p></div></aside>
      </div>
    </main>
  )
}

export default ChatListPage