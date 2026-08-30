import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'

function RegisterPage() {
  const [form, setForm] = useState({ username: '', email: '', password: '', displayName: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const update = (event) => setForm({ ...form, [event.target.name]: event.target.value })

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      await api.post('/auth/register', form)
      navigate('/login', { state: { registered: true } })
    } catch (err) {
      const data = err.response?.data
      setError(data?.errors ? Object.values(data.errors).join(', ') : data?.error || 'Registration failed.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="auth-page">
      <div className="auth-aside"><span className="brand-mark">⌁</span><p>A small network.<br />A shared space.</p><small>YOUR WIFI, YOUR ROOM</small></div>
      <section className="auth-panel">
        <div className="auth-heading"><span className="eyebrow">Join the network</span><h1>Make room for<br /><em>good company.</em></h1><p>Create your local chat identity.</p></div>
        {error && <div className="form-error" role="alert">{error}</div>}
        <form onSubmit={handleSubmit} className="auth-form">
          <label>Username<input name="username" value={form.username} onChange={update} autoComplete="username" required /></label>
          <label>Email<input name="email" type="email" value={form.email} onChange={update} autoComplete="email" required /></label>
          <label>Password<input name="password" type="password" value={form.password} onChange={update} minLength="6" required /></label>
          <label>Display name <span className="optional">optional</span><input name="displayName" value={form.displayName} onChange={update} /></label>
          <button className="primary-button" type="submit" disabled={loading}>{loading ? 'Creating...' : 'Create account'} <span>↗</span></button>
        </form>
        <p className="auth-switch">Already have an account? <Link to="/login">Sign in</Link></p>
      </section>
    </main>
  )
}

export default RegisterPage