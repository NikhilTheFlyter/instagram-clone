# Instagram Clone

A full-stack Instagram clone built with **microservice architecture** using Spring Boot, MongoDB, React, and Spring Cloud.

## Architecture

```
                    ┌─────────────┐
                    │   React UI  │ :3000
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │ :8080
                    │  (JWT Auth) │
                    └──────┬──────┘
              ┌────────────┼────────────┐────────────┐
              │            │            │            │
       ┌──────▼──┐  ┌──────▼──┐  ┌─────▼───┐  ┌────▼─────┐
       │  Auth   │  │  Post   │  │ Follow  │  │ Trending │
       │ Service │  │ Service │  │ Service │  │ Service  │
       │  :8081  │  │  :8082  │  │  :8083  │  │  :8084   │
       └────┬────┘  └────┬────┘  └────┬────┘  └────┬─────┘
            │            │            │            │
            └────────────┴─────┬──────┴────────────┘
                               │
                    ┌──────────▼──────────┐
                    │      MongoDB        │ :27017
                    │  (4 databases)      │
                    └─────────────────────┘
                    ┌─────────────────────┐
                    │   Consul (Discovery)│ :8500
                    └─────────────────────┘
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | React 18, Vite, Tailwind CSS, React Query, Zustand, React Router v6 |
| Backend | Java 17, Spring Boot 3.2.5, Spring Cloud 2023.0.1 |
| Database | MongoDB (local) |
| Gateway | Spring Cloud Gateway |
| Discovery | Spring Cloud Consul |
| Security | JWT (jjwt 0.12.5), BCrypt |
| Resilience | Resilience4j Circuit Breaker |
| Docs | Swagger / OpenAPI (springdoc) |
| Testing | JUnit 5, Mockito (93 tests) |

## Microservices

| Service | Port | Database | Responsibility |
|---------|------|----------|----------------|
| `api-gateway` | 8080 | — | JWT auth filter, routing, CORS, load balancing |
| `auth-service` | 8081 | instagram_auth | Registration, login, JWT, profile, password reset, circuit breaker |
| `post-service` | 8082 | instagram_posts | Create/view/delete posts, like/unlike, feed, search |
| `follow-service` | 8083 | instagram_follow | Follow/unfollow, follower lists, stats |
| `trending-service` | 8084 | instagram_trending | Trending posts, hashtags, scheduled refresh |

## Prerequisites

You need: **Java 17**, **Maven**, **MongoDB**, **Consul**, **Node.js**

---

### macOS Setup (Homebrew)

```bash
# Java 17
brew install openjdk@17
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"' >> ~/.zshrc
source ~/.zshrc

# Maven
brew install maven

# Consul
brew install consul

# MongoDB
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb-community

# Node.js
brew install node
```

---

### Windows Setup

#### 1. Java 17

- Download **OpenJDK 17** from https://adoptium.net/temurin/releases/?version=17 (pick Windows x64 `.msi` installer)
- Run the installer — check **"Set JAVA_HOME variable"** and **"Add to PATH"** during install
- Verify in a new Command Prompt / PowerShell:
  ```powershell
  java --version    # Should show 17.x
  ```

#### 2. Maven

- Download from https://maven.apache.org/download.cgi (Binary zip archive)
- Extract to `C:\Program Files\Maven`
- Add to system PATH:
  ```
  System Properties → Environment Variables → Path → Edit → Add:
  C:\Program Files\Maven\apache-maven-3.9.x\bin
  ```
- Or use **Chocolatey**:
  ```powershell
  choco install maven
  ```
- Verify:
  ```powershell
  mvn --version    # Should show Java 17
  ```

#### 3. MongoDB

- Download **MongoDB Community Server** from https://www.mongodb.com/try/download/community (Windows x64 `.msi`)
- Run installer — choose **"Complete"** install, check **"Install MongoDB as a Service"**
- MongoDB Shell (mongosh): https://www.mongodb.com/try/download/shell
- Verify:
  ```powershell
  mongosh --eval "db.runCommand({ping:1})"
  ```

#### 4. Consul

- Download from https://developer.hashicorp.com/consul/install (Windows AMD64 `.zip`)
- Extract `consul.exe` to `C:\Program Files\Consul`
- Add to PATH:
  ```
  System Properties → Environment Variables → Path → Edit → Add:
  C:\Program Files\Consul
  ```
- Or use **Chocolatey**:
  ```powershell
  choco install consul
  ```
- Verify:
  ```powershell
  consul --version
  ```

#### 5. Node.js

- Download from https://nodejs.org (LTS version, Windows Installer)
- Run installer (includes npm)
- Verify:
  ```powershell
  node --version
  npm --version
  ```

#### Windows Quick Start

```powershell
# Terminal 1 — Consul
consul agent -dev

# Terminal 2 — Auth Service
cd auth-service
mvn spring-boot:run -D"maven.test.skip=true"

# Terminal 3 — Post Service
cd post-service
mvn spring-boot:run -D"maven.test.skip=true"

# Terminal 4 — Follow Service
cd follow-service
mvn spring-boot:run -D"maven.test.skip=true"

# Terminal 5 — Trending Service
cd trending-service
mvn spring-boot:run -D"maven.test.skip=true"

# Terminal 6 — API Gateway
cd api-gateway
mvn spring-boot:run -D"maven.test.skip=true"

# Terminal 7 — Frontend
cd frontend
npm install
npm run dev
```

> **Note for Windows**: Use `mvn spring-boot:run -D"maven.test.skip=true"` (with quotes around the property) in PowerShell. In Command Prompt, use `mvn spring-boot:run -Dmaven.test.skip=true` without quotes.

#### Windows One-Liner (PowerShell)

```powershell
# Start all services in background
Start-Process -NoNewWindow consul -ArgumentList "agent","-dev"
Start-Process -NoNewWindow -WorkingDirectory "auth-service" mvn -ArgumentList "spring-boot:run","-Dmaven.test.skip=true"
Start-Process -NoNewWindow -WorkingDirectory "post-service" mvn -ArgumentList "spring-boot:run","-Dmaven.test.skip=true"
Start-Process -NoNewWindow -WorkingDirectory "follow-service" mvn -ArgumentList "spring-boot:run","-Dmaven.test.skip=true"
Start-Process -NoNewWindow -WorkingDirectory "trending-service" mvn -ArgumentList "spring-boot:run","-Dmaven.test.skip=true"
Start-Process -NoNewWindow -WorkingDirectory "api-gateway" mvn -ArgumentList "spring-boot:run","-Dmaven.test.skip=true"

# Wait for boot
Start-Sleep -Seconds 30

# Start frontend
cd frontend
npm install
npm run dev
```

**Stop all services (PowerShell):**
```powershell
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process
Get-Process -Name node -ErrorAction SilentlyContinue | Stop-Process
Get-Process -Name consul -ErrorAction SilentlyContinue | Stop-Process
```

---

### Linux Setup (Ubuntu/Debian)

```bash
# Java 17
sudo apt update
sudo apt install openjdk-17-jdk
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc

# Maven
sudo apt install maven

# Consul
wget https://releases.hashicorp.com/consul/1.22.7/consul_1.22.7_linux_amd64.zip
unzip consul_1.22.7_linux_amd64.zip
sudo mv consul /usr/local/bin/

# MongoDB
# Follow: https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-ubuntu/
sudo systemctl start mongod

# Node.js
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install nodejs
```

---

### Verify installations (all platforms)

```bash
java --version    # 17.x
mvn --version     # 3.9.x (should show Java 17)
consul --version  # 1.x
mongosh --version # 2.x
node --version    # 18+ or 20+
npm --version     # 9+
```

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/NikhilTheFlyter/instagram-clone.git
cd instagram-clone
```

### 2. Start infrastructure

```bash
# Terminal 1 — MongoDB (skip if already running as a service)
mongod

# Terminal 2 — Consul
consul agent -dev
```

### 3. Start backend services (5 terminals)

Each terminal needs Java on PATH:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
```

```bash
# Terminal 3
cd auth-service && mvn spring-boot:run -Dmaven.test.skip=true

# Terminal 4
cd post-service && mvn spring-boot:run -Dmaven.test.skip=true

# Terminal 5
cd follow-service && mvn spring-boot:run -Dmaven.test.skip=true

# Terminal 6
cd trending-service && mvn spring-boot:run -Dmaven.test.skip=true

# Terminal 7
cd api-gateway && mvn spring-boot:run -Dmaven.test.skip=true
```

### 4. Start frontend

```bash
# Terminal 8
cd frontend
npm install
npm run dev
```

### 5. Open the app

- **App**: http://localhost:3000
- **Consul Dashboard**: http://localhost:8500

## One-Liner Start (Background)

Start everything in one terminal (services run in background):

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17" && export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"

# Start Consul
consul agent -dev > /tmp/consul.log 2>&1 &

# Start backend
(cd auth-service && mvn spring-boot:run -Dmaven.test.skip=true > /tmp/auth-service.log 2>&1) &
(cd post-service && mvn spring-boot:run -Dmaven.test.skip=true > /tmp/post-service.log 2>&1) &
(cd follow-service && mvn spring-boot:run -Dmaven.test.skip=true > /tmp/follow-service.log 2>&1) &
(cd trending-service && mvn spring-boot:run -Dmaven.test.skip=true > /tmp/trending-service.log 2>&1) &
(cd api-gateway && mvn spring-boot:run -Dmaven.test.skip=true > /tmp/api-gateway.log 2>&1) &

# Wait for boot
sleep 25

# Start frontend
(cd frontend && npm run dev > /tmp/frontend.log 2>&1) &

echo "All services starting! App at http://localhost:3000"
```

**Stop all services:**

```bash
pkill -f "spring-boot:run"
pkill -f "vite"
```

## API Endpoints

### Auth Service (`/api/auth`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/register` | Register new user | No |
| POST | `/login` | Login, returns JWT | No |
| GET | `/login/status` | Circuit breaker state | No |
| POST | `/forgot-password` | Request password reset token | No |
| POST | `/reset-password` | Reset password with token | No |
| GET | `/profile/{userId}` | Get user profile | Yes |
| PUT | `/profile/{userId}` | Update profile | Yes |
| GET | `/search/users?q=&page=&size=` | Search users | Yes |

### Post Service (`/api/posts`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Create post | Yes |
| GET | `/{postId}` | Get post by ID | Yes |
| GET | `/user/{userId}?page=&size=` | Get user's posts | Yes |
| GET | `/feed?page=&size=` | Feed (followed users' posts) | Yes |
| DELETE | `/{postId}` | Delete own post | Yes |
| POST | `/{postId}/like` | Like a post | Yes |
| DELETE | `/{postId}/like` | Unlike a post | Yes |
| GET | `/{postId}/likes` | Get like status + count | Yes |
| GET | `/search?q=&sort=&page=&size=` | Search posts | Yes |
| GET | `/user/{userId}/count` | Get user's post count | Yes |

### Follow Service (`/api/follow`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/{targetUserId}` | Follow user | Yes |
| DELETE | `/{targetUserId}` | Unfollow user | Yes |
| GET | `/{userId}/followers` | Get followers list | Yes |
| GET | `/{userId}/following` | Get following list | Yes |
| GET | `/{userId}/stats` | Get follower/following counts | Yes |
| GET | `/check/{targetUserId}` | Check if following | Yes |
| GET | `/{userId}/following/ids` | Get following user IDs | Yes |

### Trending Service (`/api/trending`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/posts?filter=&page=&size=` | Get trending posts | Yes |
| GET | `/hashtags?limit=` | Get trending hashtags | Yes |
| POST | `/posts` | Add post to trending | Yes |
| DELETE | `/posts/{postId}` | Remove from trending | Yes |

## Swagger Documentation

When services are running, Swagger UI is available at:

- Auth: http://localhost:8081/swagger-ui.html
- Post: http://localhost:8082/swagger-ui.html
- Follow: http://localhost:8083/swagger-ui.html
- Trending: http://localhost:8084/swagger-ui.html

## Testing

### Run all tests

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"

# All services
cd auth-service && mvn test && \
cd ../post-service && mvn test && \
cd ../follow-service && mvn test && \
cd ../trending-service && mvn test
```

### Test summary (93 tests)

| Service | Tests | Coverage |
|---------|-------|----------|
| auth-service | 63 | Registration, login, JWT, validators, password reset, controller |
| post-service | 12 | CRUD, likes, delete ownership |
| follow-service | 10 | Follow/unfollow, lists, stats |
| trending-service | 8 | Trending posts, hashtags, add/remove |

### Manual API testing with curl

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"John Doe","email":"john@gmail.com","username":"johndoe","password":"Pass@123","confirmPassword":"Pass@123"}'

# Login (save the token)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"johndoe","password":"Pass@123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")

# Create post (use token)
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"caption":"My first post!","mediaUrls":["https://picsum.photos/500"],"mediaType":"IMAGE","hashtags":["hello"],"privacy":"PUBLIC"}'

# Like a post
curl -X POST http://localhost:8080/api/posts/{postId}/like \
  -H "Authorization: Bearer $TOKEN"
```

## Frontend Pages

| Route | Page | Description |
|-------|------|-------------|
| `/login` | Login | Username/password with circuit breaker timer |
| `/register` | Register | Full SRS validations + password strength |
| `/forgot-password` | Forgot Password | Email-based reset token |
| `/reset-password` | Reset Password | Token + new password |
| `/` | Feed | Posts from followed users |
| `/explore` | Explore | Trending posts + hashtags |
| `/search?q=` | Search | Users + posts with sort filters |
| `/profile/:userId` | Profile | Avatar, stats, post grid, follow button |
| `/profile/edit` | Edit Profile | Update name, bio, picture |
| `/post/create` | Create Post | Media, caption, hashtags, privacy |
| `/post/:postId` | Post Detail | Full post view with like/delete |

All interactive elements have `data-cy` attributes for E2E testing.

## Project Structure

```
instagram-clone/
├── pom.xml                          # Maven parent POM
├── auth-service/                    # Authentication microservice
│   ├── src/main/java/com/instagram/auth/
│   │   ├── controller/              # REST controllers
│   │   ├── service/                 # Business logic
│   │   ├── repository/              # MongoDB repositories
│   │   ├── entity/                  # MongoDB documents
│   │   ├── dto/                     # Request/response DTOs
│   │   ├── validation/              # Custom validators
│   │   ├── exception/               # Custom exceptions + global handler
│   │   ├── client/                  # Inter-service REST clients
│   │   ├── util/                    # JWT utility
│   │   ├── config/                  # App config, OpenAPI
│   │   └── aspect/                  # Logging AOP
│   └── src/test/                    # 63 unit tests
├── post-service/                    # Post management microservice
├── follow-service/                  # Follower management microservice
├── trending-service/                # Trending content microservice
├── api-gateway/                     # Spring Cloud Gateway
│   └── src/main/java/com/instagram/gateway/
│       ├── filter/                  # JWT authentication filter
│       ├── util/                    # JWT validation utility
│       └── config/                  # CORS config
├── frontend/                        # React application
│   └── src/
│       ├── api/                     # Axios API clients
│       ├── store/                   # Zustand auth store
│       ├── hooks/                   # Custom hooks
│       ├── components/              # Reusable components
│       │   ├── layout/              # Navbar, MainLayout, AuthLayout
│       │   ├── post/                # PostCard, DeleteConfirmModal
│       │   ├── follow/              # FollowButton, FollowListModal
│       │   └── common/              # LoadingSpinner, ProtectedRoute
│       └── pages/                   # Page components
│           └── auth/                # Login, Register, Forgot/Reset Password
└── SRS_COMPLIANCE_REPORT.md         # SRS vs implementation gap analysis
```

## Registration Validation Rules

| Field | Rules |
|-------|-------|
| Full Name | English letters only, first letter of each word capitalized |
| Email | Valid format, domain must be `com`, `org`, or `in` |
| Username | Lowercase letters, digits, `.` and `_` only |
| Password | 8-16 chars, at least one lowercase, uppercase, digit, special char |
| Confirm Password | Must match password |

## Circuit Breaker (Login)

- **Window**: TIME_BASED, 30 seconds
- **Threshold**: 3 failed login attempts within the window
- **Open duration**: 60 seconds
- **Behavior**: Returns fallback message with timer when open
- **Frontend**: Displays countdown timer during open state

## Inter-Service Communication

```
auth-service  ──→  post-service     (get post count for profile)
auth-service  ──→  follow-service   (get follower/following counts for profile)
post-service  ──→  follow-service   (get following IDs for feed)
post-service  ──→  trending-service (push posts on create/like/delete)
follow-service ──→ auth-service     (enrich follower lists with user data)
```

All inter-service calls use `@LoadBalanced RestTemplate` via Consul discovery and are protected with Resilience4j circuit breakers with graceful fallbacks.

## Troubleshooting

### Services fail to start
```bash
# Check if ports are in use
lsof -i :8080,:8081,:8082,:8083,:8084

# Kill stuck processes
pkill -f "spring-boot:run"
```

### MongoDB connection refused
```bash
# Check if MongoDB is running
mongosh --eval "db.runCommand({ping:1})"

# Start MongoDB
brew services start mongodb-community
# or
mongod
```

### Consul not found
```bash
# Check Consul
curl http://localhost:8500/v1/status/leader

# Start Consul
consul agent -dev
```

### Java version mismatch
```bash
# Ensure Java 17 is active
java --version  # Should show 17.x

# If Maven uses wrong Java
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
mvn --version   # Should show Java 17
```

### Frontend proxy errors
The Vite dev server proxies `/api` to `http://localhost:8080` (gateway). Make sure the gateway is running before starting the frontend.

## Logs

Service logs are at:
```
/tmp/auth-service.log
/tmp/post-service.log
/tmp/follow-service.log
/tmp/trending-service.log
/tmp/api-gateway.log
/tmp/frontend.log
```

View logs:
```bash
tail -f /tmp/auth-service.log
```
