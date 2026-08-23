# 🛜 Offline WiFi Chat System

A real-time, full-stack chat application built to operate entirely on a local area network (LAN) **without an active internet connection**. 

One machine acts as the host server, and any device connected to the same WiFi router can join, authenticate, and chat in real-time.



---

## 🛠️ Tech Stack

**Backend:**
- **Java 17 & Spring Boot 3.3**
- **Spring Security & JWT** (Stateless authentication)
- **WebSocket & STOMP** (Real-time bidirectional messaging)
- **Hibernate / Spring Data JPA** (ORM)

**Frontend:**
- **React 18** (SPA)
- **Axios** (REST API client with JWT interceptors)
- **STOMP.js & SockJS** (WebSocket client)

**Database & DevOps:**
- **PostgreSQL 15** (Persistent storage with indexes)
- **Docker & Docker Compose** (Multi-stage builds, container orchestration)
- **Nginx** (Reverse proxy for API & WebSocket)
- **Swagger / OpenAPI 3** (API Documentation)

---

## ✨ Key Features

- **True Offline Capability:** Binds to `0.0.0.0` allowing any LAN device to connect. No cloud servers required.
- **Real-Time Messaging:** Instant message delivery using WebSockets, replacing inefficient HTTP polling.
- **Concurrent Message Handling:** Utilizes per-room `ReentrantLock` to prevent race conditions when multiple users message the same room simultaneously.
- **Stateless Authentication:** Secure JWT-based auth flow with BCrypt password hashing.
- **Persistent Chat History:** Messages are safely stored in PostgreSQL and retrieved via paginated REST endpoints.
- **Live Status Tracking:** Automatically tracks online/offline users when WebSocket connections open and close.

---

## 🚀 Quick Start (One-Command Deploy)

You don't need Java or Node installed to run this. Just Docker.

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/offline-wifi-chat.git
   cd offline-wifi-chat
   ```

2. **Start the system**
   ```bash
   docker-compose up --build
   ```
   *This spins up 3 containers: `postgres`, `backend`, and `frontend` (Nginx).*

3. **Access from the Host Machine**
   - Open browser to: `http://localhost:3000`

---

## 📱 How to Connect Other Devices (Phones, Laptops)

To chat across different devices, they must be connected to the **same WiFi network**.

1. Find the host machine's Local IP address:
   - **Mac/Linux:** Run `ifconfig | grep "inet " | grep -v 127.0.0.1`
   - **Windows:** Run `ipconfig` (Look for IPv4 Address)
   - *Example output: `192.168.1.42`*

2. On any other device (phone, tablet, friend's laptop), open the browser and go to:
   ```
   http://192.168.1.42:3000
   ```
3. Register a user and start chatting in real time!

---

## 🧠 System Design & Engineering Decisions

I built this project to practice system design, concurrency, and architecture. Here are some of the key technical decisions:

| Challenge | Solution | Why? |
|-----------|----------|------|
| **Race Conditions in Chat** | Per-Room `ReentrantLock` | Instead of locking the whole app (slow), each room processes its messages sequentially, while different rooms process in parallel. |
| **Password Security** | BCrypt Hashing | Intentionally slow hashing algorithm (~100ms) to make brute-force database attacks mathematically impractical. |
| **Chat History Speed** | Compound DB Index | Added an index on `(chat_room_id, created_at)` so loading historical messages is an $O(\log N)$ B-tree lookup instead of a full table scan. |
| **Image Size / Deploy** | Multi-stage Docker Builds | The Spring Boot backend image drops from ~500MB (Maven/JDK) to ~100MB (JRE only) for production. |
| **API & WS Routing** | Nginx Reverse Proxy | The frontend only needs to talk to port `3000`. Nginx handles routing `/api/*` to REST endpoints and upgrading `/ws/*` for WebSockets. |

*(For a production scale-up, I would migrate in-memory locks to Kafka/Redis, JWTs from localStorage to HttpOnly cookies, and the STOMP broker to RabbitMQ).*

---

## 📚 API Documentation

The REST API is fully documented using Swagger OpenAPI. 

Once the application is running, view the interactive documentation at:
**`http://localhost:8080/swagger-ui.html`**

You can use the "Authorize 🔒" button to pass your JWT and test endpoints directly from the browser.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
