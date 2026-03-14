# YesAuction 

YesAuction is a full-stack, real-time auction platform built with modern technologies, strong backend architecture, scalability, fault tolerance, real-time features, and containerization — perfect to start auction platform.

Update this file as we proceed for tracking purpose, soon it will launched.

## Features

- Secure user authentication & authorization (JWT + role-based access)
- Create, view, and bid on auctions in real time
- Live bid updates via WebSockets (STOMP over SockJS)
- Asynchronous bid processing & notifications using RabbitMQ
- Caching with Redis for high-performance reads
- Containerized multi-service architecture with Docker Compose
- PostgreSQL for persistent data storage
- Responsive React frontend (TypeScript + Tailwind CSS)

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.5.11
- Spring Security + JWT
- Spring Data JPA + PostgreSQL
- Spring WebSocket + STOMP
- RabbitMQ (async tasks & notifications)
- Redis (caching)
- Maven

### DevOps & Tools
- Docker + Docker Compose
- Git + GitHub
- pgAdmin (DB management)

## Setup & Run (Local Development)

### Prerequisites
- Java 21
- Maven
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL (optional – can use Docker)

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/YesAuction.git
cd YesAuction
```

## Services will be available at:

Backend API: http://localhost:8081
Frontend: http://localhost:3000
RabbitMQ Management: http://localhost:15673 (guest/guest)
PostgreSQL: localhost:5433 (user: postgres, password: your_password)

## API Endpoints (Main ones)

### Authentication

POST /api/auth/register → Register new user
POST /api/auth/login → Login & get JWT

### Auctions

POST /api/auctions → Create auction (authenticated)
GET /api/auctions → List active auctions
GET /api/auctions/{id} → Get single auction
POST /api/auctions/{id}/bid → Place bid (authenticated)
GET /api/auctions/{id}/bids → Get bid history

### Profile

GET /api/users/me → My private profile (authenticated)
GET /api/users/{username} → Public profile
GET /api/users/admin/all → List all users (admin only)

### WebSocket (Real-time)

Connect: ws://localhost:8080/ws
Subscribe to live bids: /topic/auction/{auctionId}

### Security & Roles

ROLE_USER → normal users
ROLE_ADMIN → full access (admin endpoints)

To create an admin user:
(haven't built an api for this yet)
Register normally
In database: INSERT INTO user_roles (user_id, roles) VALUES (<your_user_id>, 'ROLE_ADMIN');

### Future tasks

Email notifications for outbid & auction win
Auction creation form in frontend
User profile page with bid/auction history
Search & filter auctions
Rate limiting & better error handling

## Author
Chakradhar Malage
