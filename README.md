# Realtime chatroom
## Table of contents
1. Contributors
2. Project structures
3. Tech stacks
4. Previews
## Contributors
## Project structures
## Tech stacks
1. Spring boot
2. Bootstrap
3. SQL Server
## Previews
# Realtime chatroom
A secure realtime chat application built with Spring Boot, SQL Server, and WebSocket. The frontend is implemented using simple HTML, CSS, and JavaScript. Authentication and authorization are handled via Spring Security. Includes CRUD APIs for user and message management.
### Backend (Spring Boot)
- User authentication and authorization using Spring Security
- WebSocket endpoint for realtime messaging
- REST API for:
    - User management (CRUD)
    - Message retrieval (history, by user)
- SQL Server as database

### Frontend (HTML/CSS/JS)
- Simple chat interface
- Connects to WebSocket endpoint
- Authenticated users can send/receive messages in realtime

---

## Basic Business Requirements

1. *User Registration/Login*  
   Users must authenticate to join the chat system.

2. *Realtime Messaging*  
   Authenticated users can chat in real time using WebSocket.

3. *Message History*  
   Users can retrieve chat history from the database.

---
