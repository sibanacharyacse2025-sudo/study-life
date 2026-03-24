# Stdili Backend (MVP)

Node.js + Express backend for Stdili.

## Features

- Auth APIs (`signup`, `login`, `guest`)
- User profile fetch
- Senior-junior matching + request accept/reject
- Study groups create/list/join
- AI API proxy to local Ollama (`llama3`)

## Run locally

1. Copy `.env.example` to `.env`
2. Set MongoDB and Ollama URLs
3. Install and start:

```bash
npm install
npm run dev
```

Server starts on `http://localhost:3000`.

## Deploy

Deploy the `backend` folder to Render/Railway.
