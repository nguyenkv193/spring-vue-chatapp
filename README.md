# Chat App Base Structure

This directory contains a fresh monorepo skeleton for a realtime chat application using Spring Boot, Vue 3, STOMP over WebSocket, PostgreSQL, Flyway, and Docker Compose.

## 1. Technical assumptions

- Monorepo layout: `chat-app/backend`, `chat-app/frontend`, `chat-app/docker`, `chat-app/docker-compose.yml`
- Backend: Java 21, Gradle, Spring Boot `4.1.0`
- Frontend: Vue `3.5.39`, Vue Router `5.1.0`, Pinia `3.0.4`, Vite `8.1.0`, TypeScript `6.0.x`
- Realtime transport: STOMP over native WebSocket at `/ws`
- Database: PostgreSQL with Flyway migration `V1__init_schema.sql`
- Auth phase: placeholder JWT login flow for local development only

## 2. Architecture overview

### Why monorepo

- Keeps backend, frontend, and dev infrastructure versioned together.
- Makes onboarding easier because local setup lives in one place.
- Fits the current phase where the app is still a single deployable product.

### Backend module layout

- `config`: CORS, WebSocket/STOMP, Spring Security
- `common`: constants, base entity, response envelope, exception handling
- `auth`: login DTOs, JWT service/filter, placeholder login service/controller
- `user`: current-user endpoint and user entity/repository placeholder
- `conversation`: entities, repositories, and read endpoint for conversation list
- `message`: entities, repositories, and read endpoint for message history
- `websocket`: STOMP controller, DTOs, event listener, publish service

### Frontend folder layout

- `app`: router and Pinia bootstrap
- `shared`: fetch client, generic state panel, shared types, local storage helper
- `features/auth`: login page, auth store, auth API contracts
- `features/chat`: chat page, stores, REST API calls, STOMP client, UI components
- `layouts`: authenticated layout and auth layout
- `styles`: global visual system

### Realtime flow

1. User signs in through the placeholder `POST /api/v1/auth/login`.
2. Frontend stores the JWT in `localStorage` for development only.
3. `ChatPage` loads conversations and messages through REST.
4. Frontend opens a STOMP connection to `ws://localhost:8080/ws`.
5. Client publishes messages to `/app/chat.send`.
6. Backend rebroadcasts to `/topic/conversations/{conversationId}`.

### What is intentionally still placeholder

- Login currently issues a dev JWT after basic request validation only.
- No registration, refresh token, password reset, or real credential verification yet.
- STOMP inbound messages are broadcast but not persisted yet.
- No conversation creation flow, presence, read receipt, or notification logic yet.

## 3. Folder structure

```text
chat-app/
  backend/
    src/main/java/com/example/chatapp/
      auth/
      common/
      config/
      conversation/
      message/
      user/
      websocket/
    src/main/resources/
      application.yml
      db/migration/V1__init_schema.sql
  frontend/
    src/
      app/
      features/
      layouts/
      shared/
      styles/
  docker/
  docker-compose.yml
  README.md
```

## 4. Database schema summary

| Table | Purpose | Key columns |
|---|---|---|
| `users` | App user profile/auth seed | `id`, `email`, `password_hash`, `display_name`, `active` |
| `conversations` | Chat thread metadata | `id`, `title`, `type`, `created_by` |
| `conversation_members` | Membership bridge | `conversation_id`, `user_id`, `role`, `joined_at` |
| `messages` | Conversation messages | `conversation_id`, `sender_id`, `content`, `message_type`, `sent_at` |

### Constraints and indexes

- `users.email` is unique.
- `conversation_members` has a unique pair on `(conversation_id, user_id)`.
- `messages` is indexed by `(conversation_id, sent_at)` for message history lookups.
- Foreign keys are included on all relational columns.

## 5. File plan

### Root and Docker

| Type | File | Purpose |
|---|---|---|
| Create | `.gitignore` | Ignore build output, env files, and editor noise |
| Create | `docker-compose.yml` | Provision local PostgreSQL for development |
| Create | `docker/README.md` | Reserve Docker asset folder for later phases |
| Create | `README.md` | Architecture, schema, run, and test guide |

### Backend

| Type | File | Purpose |
|---|---|---|
| Create | `backend/build.gradle` | Spring Boot backend dependencies and Gradle build config |
| Create | `backend/settings.gradle` | Declares the backend Gradle root project name |
| Create | `backend/gradlew*` | Gradle wrapper scripts for consistent local builds |
| Create | `backend/src/main/resources/application.yml` | Spring config from env |
| Create | `backend/src/main/resources/db/migration/V1__init_schema.sql` | Base PostgreSQL schema |
| Create | `backend/src/main/java/com/example/chatapp/**` | Backend modules, config, REST, STOMP, and JWT placeholder |
| Create | `backend/src/test/java/com/example/chatapp/ChatAppApplicationTests.java` | Base context-load test |

### Frontend

| Type | File | Purpose |
|---|---|---|
| Create | `frontend/package.json` | Vue/Vite dependency manifest |
| Create | `frontend/.env.example` | API and WebSocket endpoint template |
| Create | `frontend/src/app/**` | Router and Pinia bootstrap |
| Create | `frontend/src/shared/**` | Fetch wrapper, shared types, generic state UI, storage helper |
| Create | `frontend/src/features/auth/**` | Login page, auth store, auth types and API calls |
| Create | `frontend/src/features/chat/**` | Chat page, components, chat store, REST/STOMP services |
| Create | `frontend/src/layouts/**` | Authenticated and auth layouts |
| Create | `frontend/src/styles/main.css` | Global visual language and responsive layout |

## 6. Run locally

### Start PostgreSQL

Windows PowerShell:

```powershell
cd .\chat-app
docker compose up -d postgres
```

macOS/Linux:

```bash
cd ./chat-app
docker compose up -d postgres
```

### Run backend

Set a JWT secret first. The placeholder login stays disabled until `APP_JWT_SECRET` is at least 32 characters.

Windows PowerShell:

```powershell
cd .\chat-app\backend
$env:APP_JWT_SECRET="replace-with-a-long-random-secret-at-least-32-chars"
.\gradlew bootRun
```

macOS/Linux:

```bash
cd ./chat-app/backend
export APP_JWT_SECRET="replace-with-a-long-random-secret-at-least-32-chars"
./gradlew bootRun
```

### Run frontend

Windows PowerShell:

```powershell
cd .\chat-app\frontend
copy .env.example .env
npm install
npm run dev
```

macOS/Linux:

```bash
cd ./chat-app/frontend
cp .env.example .env
npm install
npm run dev
```

## 7. API and WebSocket checks

### Health endpoint

Windows PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

macOS/Linux:

```bash
curl http://localhost:8080/actuator/health
```

### Placeholder login

Windows PowerShell:

```powershell
Invoke-RestMethod -Method Post http://localhost:8080/api/v1/auth/login `
  -ContentType "application/json" `
  -Body '{"email":"dev@example.com","password":"password123"}'
```

macOS/Linux:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"dev@example.com","password":"password123"}'
```

### WebSocket handshake

- Open the frontend and sign in.
- Open browser devtools and confirm the connection to `ws://localhost:8080/ws` returns HTTP `101 Switching Protocols`.
- A successful STOMP subscription should appear when `ChatPage` boots and a conversation is selected.

### Demo message flow

- Sign in through `http://localhost:5173/login`.
- Seed or create a conversation record later, or insert one manually for testing.
- Open `ChatPage`, select a conversation, and send a demo message from the input box.
- The backend broadcasts it to `/topic/conversations/{conversationId}` and the current tab appends it to the list.

## 8. Suggested next development phases

1. Replace placeholder login with real user lookup, password hashing, and refresh tokens.
2. Add conversation creation endpoints and member management.
3. Persist STOMP inbound messages inside a transaction and enrich them with sender data.
4. Add presence, unread counters, seen receipts, and notification delivery.

## 9. Completion checklist

- [x] Backend skeleton added with Spring Boot, WebSocket/STOMP, JWT placeholder, JPA, Flyway
- [x] Frontend skeleton added with Vue 3, Vite, TypeScript, Router, Pinia, STOMP client
- [x] PostgreSQL schema migration added for `users`, `conversations`, `conversation_members`, `messages`
- [x] Docker Compose added for local PostgreSQL
- [x] Backend reads settings from environment-backed `application.yml`
- [x] Frontend uses env-driven API and WebSocket URLs
- [x] Loading, error, and empty states exist on `ChatPage`
- [x] No JWT secret or production endpoint is hard-coded
- [ ] Backend build verified locally
- [ ] Frontend build verified locally
- [ ] Docker Compose run verified locally

The last three boxes should be checked after dependency installation and local verification.

