function MessageBubble({ message, isOwn }) {
  const timestamp = message.timestamp || message.createdAt
  return (
    <div className={`message-row ${isOwn ? 'message-row-own' : ''}`}>
      <article className={`message-bubble ${isOwn ? 'message-bubble-own' : ''}`}>
        {!isOwn && <div className="message-sender">{message.senderUsername || message.sender?.username || 'Unknown'}</div>}
        <div className="message-content">{message.content}</div>
        <time>{timestamp ? new Date(timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : ''}</time>
      </article>
    </div>
  )
}

export default MessageBubble