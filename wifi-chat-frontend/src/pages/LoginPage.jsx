import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/useAuth.js'

function LoginPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await api.post('/auth/login', { username, password })
      login(data.token, data.username || username)
      navigate('/chat')
    } catch (err) {
      setError(err.response?.data?.error || 'Login failed. Check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-aside"><span className="brand-mark">⌁</span><p>Private conversations.<br />Close to home.</p><small>OFFLINE-FIRST CHAT</small></div>
      <section className="auth-panel">
        <div className="auth-heading"><span className="eyebrow">Welcome back</span><h1>Pick up the<br /><em>conversation.</em></h1><p>Sign in to your local WiFi network.</p></div>
        {error && <div className="form-error" role="alert">{error}</div>}
        <form onSubmit={handleSubmit} className="auth-form">
          <label>Username<input value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required /></label>
          <label>Password<input type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" required /></label>
          <button className="primary-button" type="submit" disabled={loading}>{loading ? 'Signing in...' : 'Enter chat'} <span>↗</span></button>
        </form>
        <p className="auth-switch">New here? <Link to="/register">Create an account</Link></p>
      </section>
    </main>
  )
}

export default LoginPage