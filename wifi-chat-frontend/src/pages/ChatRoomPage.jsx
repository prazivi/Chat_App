import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useEffect, useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import api from '../api/axios'
import MessageBubble from '../components/MessageBubble'
import { useAuth } from '../context/useAuth.js'

function ChatRoomPage() {
  const { roomId } = useParams()
  const { token, username } = useAuth()
  const navigate = useNavigate()
  const [messages, setMessages] = useState([])
  const [newMessage, setNewMessage] = useState('')
  const [connected, setConnected] = useState(false)
  const clientRef = useRef(null)
  const endRef = useRef(null)

  useEffect(() => {
    let cancelled = false
    const load = async () => {
      try {
        const response = await api.get(`/messages/room/${roomId}?page=0&size=100`)
        if (!cancelled) setMessages([...response.data].reverse())
      } catch (error) { console.error('Failed to load message history', error) }
    }
    load()
    return () => { cancelled = true }
  }, [roomId])

  useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS(import.meta.env.VITE_WS_URL || '/ws/chat'),
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000,
      onConnect: () => {
        setConnected(true)
        client.subscribe(`/topic/chatroom.${roomId}`, (frame) => setMessages((current) => [...current, JSON.parse(frame.body)]))
        client.publish({ destination: '/app/chat.online' })
      },
      onDisconnect: () => setConnected(false),
      onWebSocketClose: () => setConnected(false),
      onStompError: (frame) => console.error('STOMP error', frame.headers.message),
    })
    client.activate()
    clientRef.current = client
    return () => { client.deactivate(); clientRef.current = null; setConnected(false) }
  }, [roomId, token])

  useEffect(() => { endRef.current?.scrollIntoView({ behavior: 'smooth' }) }, [messages])

  const sendMessage = () => {
    if (!newMessage.trim() || !clientRef.current?.connected) return
    clientRef.current.publish({ destination: '/app/chat.send', body: JSON.stringify({ chatRoomId: Number(roomId), content: newMessage.trim(), messageType: 'TEXT' }) })
    setNewMessage('')
  }

  return <main className="room-shell"><header className="room-header"><button className="back-button" onClick={() => navigate('/chat')} aria-label="Back to rooms">←</button><div><span className="eyebrow">Conversation</span><h1>Room #{roomId}</h1></div><span className={`connection ${connected ? 'is-connected' : ''}`}><i></i>{connected ? 'Connected' : 'Connecting...'}</span></header><section className="message-list">{messages.length === 0 && <div className="empty-chat"><span>✣</span><h2>The room is quiet.</h2><p>Send the first message and start something.</p></div>}{messages.map((message, index) => <MessageBubble key={message.id || `${message.timestamp}-${index}`} message={message} isOwn={message.senderUsername === username} />)}<div ref={endRef} /></section><form className="message-composer" onSubmit={(event) => { event.preventDefault(); sendMessage() }}><input value={newMessage} onChange={(event) => setNewMessage(event.target.value)} placeholder={connected ? 'Write a message...' : 'Connecting to room...'} disabled={!connected} aria-label="Message" /><button type="submit" disabled={!connected || !newMessage.trim()} aria-label="Send message" title="Send message">↗</button></form></main>
}

export default ChatRoomPage