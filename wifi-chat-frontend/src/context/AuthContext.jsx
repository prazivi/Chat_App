import { useState } from 'react'
import AuthContext from './AuthContext.js'

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [username, setUsername] = useState(() => localStorage.getItem('username'))
  const login = (newToken, newUsername) => { localStorage.setItem('token', newToken); localStorage.setItem('username', newUsername); setToken(newToken); setUsername(newUsername) }
  const logout = () => { localStorage.removeItem('token'); localStorage.removeItem('username'); setToken(null); setUsername(null) }
  return <AuthContext.Provider value={{ token, username, login, logout, isAuthenticated: Boolean(token) }}>{children}</AuthContext.Provider>
}