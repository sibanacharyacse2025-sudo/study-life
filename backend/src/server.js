import bcrypt from "bcryptjs";
import cors from "cors";
import dotenv from "dotenv";
import express from "express";
import http from "http";
import Joi from "joi";
import jwt from "jsonwebtoken";
import mongoose from "mongoose";
import morgan from "morgan";
import { Server as SocketIOServer } from "socket.io";

dotenv.config();

const app = express();
const httpServer = http.createServer(app);
const io = new SocketIOServer(httpServer, {
  cors: { origin: "*" }
});
app.use(cors());
app.use(express.json({ limit: "2mb" }));
app.use(morgan("combined"));

const PORT = Number(process.env.PORT || 3000);
const MONGODB_URI = process.env.MONGODB_URI;
const OLLAMA_URL = process.env.OLLAMA_URL || "http://127.0.0.1:11434";
const OLLAMA_MODEL = process.env.OLLAMA_MODEL || "llama3";
const JWT_SECRET = process.env.JWT_SECRET || "dev-secret";
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || "7d";
const OLLAMA_TIMEOUT_MS = Number(process.env.OLLAMA_TIMEOUT_MS || 20000);

const userSchema = new mongoose.Schema(
  {
    name: { type: String, required: true },
    email: { type: String, required: true, lowercase: true },
    password: { type: String, required: true },
    role: { type: String, enum: ["junior", "senior", "guest"], required: true },
    classGrade: { type: String, default: "" },
    subjects: [{ type: String }],
    goals: { type: String, default: "" },
    preferredLanguage: { type: String, default: "English" },
    studyHours: { type: Number, default: 0 },
    streak: { type: Number, default: 0 },
    points: { type: Number, default: 0 },
    level: { type: Number, default: 1 },
    availability: { type: String, enum: ["online", "offline"], default: "offline" },
    isOnline: { type: Boolean, default: false },
    rating: { type: Number, default: 0, min: 0, max: 5 },
    experienceYears: { type: Number, default: 0, min: 0 },
    studentsHelped: { type: Number, default: 0, min: 0 }
  },
  { timestamps: true }
);
userSchema.index({ email: 1 }, { unique: true });
userSchema.index({ role: 1, classGrade: 1, preferredLanguage: 1, availability: 1 });
userSchema.index({ subjects: 1 });

const mentorRequestSchema = new mongoose.Schema(
  {
    juniorId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    seniorId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    status: { type: String, enum: ["pending", "accepted", "rejected"], default: "pending" },
    subject: { type: String, default: "" },
    note: { type: String, default: "" }
  },
  { timestamps: true }
);
mentorRequestSchema.index({ seniorId: 1, status: 1, createdAt: -1 });
mentorRequestSchema.index({ juniorId: 1, status: 1, createdAt: -1 });

const groupSchema = new mongoose.Schema(
  {
    name: { type: String, required: true },
    description: { type: String, required: true },
    category: { type: String, default: "General" },
    adminId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    members: [{ type: mongoose.Schema.Types.ObjectId, ref: "User" }],
    sharedNotes: [{ title: String, content: String, createdBy: String, createdAt: Date }],
    sessions: [{ title: String, startsAt: Date, createdBy: String }]
  },
  { timestamps: true }
);
groupSchema.index({ createdAt: -1 });
groupSchema.index({ members: 1 });

const groupMessageSchema = new mongoose.Schema(
  {
    groupId: { type: mongoose.Schema.Types.ObjectId, ref: "Group", required: true },
    senderId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    text: { type: String, required: true }
  },
  { timestamps: true }
);
groupMessageSchema.index({ groupId: 1, createdAt: -1 });

const directMessageSchema = new mongoose.Schema(
  {
    roomId: { type: String, required: true },
    senderId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    receiverId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    text: { type: String, required: true }
  },
  { timestamps: true }
);
directMessageSchema.index({ roomId: 1, createdAt: -1 });

const notificationSchema = new mongoose.Schema(
  {
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    type: { type: String, required: true },
    title: { type: String, required: true },
    body: { type: String, required: true },
    read: { type: Boolean, default: false }
  },
  { timestamps: true }
);
notificationSchema.index({ userId: 1, read: 1, createdAt: -1 });

const User = mongoose.model("User", userSchema);
const MentorRequest = mongoose.model("MentorRequest", mentorRequestSchema);
const Group = mongoose.model("Group", groupSchema);
const GroupMessage = mongoose.model("GroupMessage", groupMessageSchema);
const DirectMessage = mongoose.model("DirectMessage", directMessageSchema);
const Notification = mongoose.model("Notification", notificationSchema);

const requiredEnvSchema = Joi.object({
  MONGODB_URI: Joi.string().uri({ scheme: [/mongodb/, /mongodb\+srv/] }).required(),
  JWT_SECRET: Joi.string().min(16).required(),
  JWT_EXPIRES_IN: Joi.string().required(),
  OLLAMA_URL: Joi.string().uri({ scheme: [/http/, /https/] }).required(),
  OLLAMA_MODEL: Joi.string().min(1).required(),
  PORT: Joi.number().integer().min(1).max(65535).required()
});

const asyncHandler = (fn) => (req, res, next) => Promise.resolve(fn(req, res, next)).catch(next);

const validateBody = (schema) =>
  asyncHandler(async (req, res, next) => {
    const { error, value } = schema.validate(req.body, { abortEarly: false, stripUnknown: true });
    if (error) {
      return res.status(422).json({
        error: "Validation failed",
        details: error.details.map((d) => d.message)
      });
    }
    req.body = value;
    next();
  });

const authMiddleware = asyncHandler(async (req, res, next) => {
  const authHeader = req.headers.authorization || "";
  const token = authHeader.startsWith("Bearer ") ? authHeader.slice(7) : null;
  if (!token) return res.status(401).json({ error: "Missing access token" });
  try {
    req.auth = jwt.verify(token, JWT_SECRET);
    next();
  } catch {
    return res.status(401).json({ error: "Invalid or expired token" });
  }
});

const signupSchema = Joi.object({
  name: Joi.string().min(2).max(80).required(),
  email: Joi.string().email().required(),
  password: Joi.string()
    .min(8)
    .pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/)
    .required()
    .messages({ "string.pattern.base": "Password must include upper, lower and number" }),
  role: Joi.string().valid("junior", "senior").required(),
  classGrade: Joi.string().max(40).allow(""),
  subjects: Joi.array().items(Joi.string().max(40)).default([]),
  goals: Joi.string().max(500).allow(""),
  preferredLanguage: Joi.string().max(50).default("English"),
  availability: Joi.string().valid("online", "offline").default("offline")
});

const loginSchema = Joi.object({
  email: Joi.string().email().required(),
  password: Joi.string().required()
});

const createGroupSchema = Joi.object({
  name: Joi.string().min(3).max(100).required(),
  description: Joi.string().min(5).max(500).required(),
  category: Joi.string().max(40).default("General")
});

const mentorRequestSchemaValidation = Joi.object({
  seniorId: Joi.string().required(),
  subject: Joi.string().max(40).allow(""),
  note: Joi.string().max(300).allow("")
});

const aiChatSchema = Joi.object({
  prompt: Joi.string().min(2).max(4000).required(),
  mode: Joi.string().valid("tutor", "notes", "flashcards").default("tutor")
});

const buildToken = (user) =>
  jwt.sign({ userId: user._id.toString(), role: user.role, email: user.email }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });

const publicUser = (userDoc) => {
  const user = userDoc.toObject ? userDoc.toObject() : userDoc;
  delete user.password;
  return user;
};

const parseClassNumeric = (classGrade) => {
  const m = String(classGrade || "").match(/\d+/);
  return m ? Number(m[0]) : 0;
};

const scoreMentor = (junior, mentor) => {
  const juniorSubjects = new Set((junior.subjects || []).map((s) => s.toLowerCase()));
  const mentorSubjects = (mentor.subjects || []).map((s) => s.toLowerCase());
  const overlap = mentorSubjects.filter((s) => juniorSubjects.has(s)).length;

  const juniorClass = parseClassNumeric(junior.classGrade);
  const mentorClass = parseClassNumeric(mentor.classGrade);
  const classGap = Math.max(0, mentorClass - juniorClass);
  const classScore = classGap > 0 ? Math.min(20, classGap * 5) : 0;
  const availabilityScore = mentor.availability === "online" ? 10 : 2;
  const ratingScore = Number(mentor.rating || 0) * 10;
  const experienceScore = Math.min(15, Number(mentor.experienceYears || 0) * 2);
  const helpedScore = Math.min(15, Math.floor(Number(mentor.studentsHelped || 0) / 5));

  const total = overlap * 15 + classScore + availabilityScore + ratingScore + experienceScore + helpedScore;
  return {
    matchScore: total,
    matchBreakdown: {
      subjectOverlap: overlap,
      classGap,
      availability: mentor.availability,
      rating: mentor.rating,
      experienceYears: mentor.experienceYears,
      studentsHelped: mentor.studentsHelped
    }
  };
};

app.get("/health", (_, res) => res.json({ ok: true, service: "stdili-api" }));

app.post("/api/auth/signup", validateBody(signupSchema), asyncHandler(async (req, res) => {
  const { name, email, password, role, classGrade, subjects, goals, preferredLanguage, availability } = req.body;
  const existing = await User.findOne({ email: email.toLowerCase() });
  if (existing) return res.status(409).json({ error: "Email already exists" });
  const hashedPassword = await bcrypt.hash(password, 10);

  const user = await User.create({
    name,
    email,
    password: hashedPassword,
    role,
    classGrade,
    subjects,
    goals,
    preferredLanguage,
    availability,
    points: 100
  });

  const token = buildToken(user);
  res.status(201).json({ user: publicUser(user), token });
}));

app.post("/api/auth/login", validateBody(loginSchema), asyncHandler(async (req, res) => {
  const { email, password } = req.body;
  const user = await User.findOne({ email: String(email).toLowerCase() });
  if (!user) return res.status(401).json({ error: "Invalid credentials" });
  const ok = await bcrypt.compare(password, user.password);
  if (!ok) return res.status(401).json({ error: "Invalid credentials" });

  const token = buildToken(user);
  res.json({ user: publicUser(user), token });
}));

app.post("/api/auth/guest", (_, res) => {
  res.json({
    user: {
      _id: null,
      name: "Guest",
      role: "guest",
      preferredLanguage: "English",
      limited: true
    },
    token: null
  });
});

app.get("/api/users/:id", authMiddleware, asyncHandler(async (req, res) => {
  const user = await User.findById(req.params.id).lean();
  if (!user) return res.status(404).json({ error: "User not found" });
  res.json({ user: publicUser(user) });
}));

app.get("/api/matching/find-mentor", authMiddleware, asyncHandler(async (req, res) => {
  const junior = await User.findById(req.auth.userId);
  if (!junior) return res.status(404).json({ error: "Current user not found" });
  if (junior.role !== "junior") return res.status(403).json({ error: "Only juniors can find mentors" });

  const { subject = "", classGrade = "", language = "", availability = "" } = req.query;
  const mentors = await User.find({
    role: "senior",
    ...(subject ? { subjects: { $elemMatch: { $regex: String(subject), $options: "i" } } } : {}),
    ...(classGrade ? { classGrade: { $regex: String(classGrade), $options: "i" } } : {}),
    ...(language ? { preferredLanguage: { $regex: String(language), $options: "i" } } : {}),
    ...(availability ? { availability: String(availability).toLowerCase() } : {})
  }).lean();

  const filtered = mentors
    .filter((m) => parseClassNumeric(m.classGrade) > parseClassNumeric(junior.classGrade))
    .map((m) => ({ ...m, ...scoreMentor(junior, m) }))
    .sort((a, b) => b.matchScore - a.matchScore);
  res.json({ mentors: filtered });
}));

app.post("/api/matching/request", authMiddleware, validateBody(mentorRequestSchemaValidation), asyncHandler(async (req, res) => {
  const juniorId = req.auth.userId;
  const { seniorId, subject, note } = req.body;

  const junior = await User.findById(juniorId);
  const senior = await User.findById(seniorId);
  if (!junior || !senior || senior.role !== "senior") return res.status(404).json({ error: "Senior not found" });
  if (junior.role !== "junior") return res.status(403).json({ error: "Only juniors can send requests" });

  const exists = await MentorRequest.findOne({ juniorId, seniorId, status: "pending" });
  if (exists) return res.status(409).json({ error: "Request already pending" });

  const request = await MentorRequest.create({ juniorId, seniorId, subject, note });
  await Notification.create({
    userId: seniorId,
    type: "mentor_request",
    title: "New mentor request",
    body: `${junior.name} sent you a mentor request`
  });
  io.to(`user:${seniorId}`).emit("notification:new", { type: "mentor_request", requestId: request._id });
  res.status(201).json({ request });
}));

app.patch("/api/matching/request/:id", authMiddleware, asyncHandler(async (req, res) => {
  const { status } = req.body;
  if (!["accepted", "rejected"].includes(status)) {
    return res.status(422).json({ error: "Status must be accepted or rejected" });
  }
  const request = await MentorRequest.findByIdAndUpdate(req.params.id, { status }, { new: true });
  if (!request) return res.status(404).json({ error: "Request not found" });
  if (String(request.seniorId) !== req.auth.userId) return res.status(403).json({ error: "Forbidden" });

  if (status === "accepted") {
    await User.findByIdAndUpdate(request.seniorId, { $inc: { studentsHelped: 1 } });
    await Notification.create({
      userId: request.juniorId,
      type: "request_accepted",
      title: "Mentor request accepted",
      body: "A mentor accepted your request."
    });
    io.to(`user:${String(request.juniorId)}`).emit("notification:new", { type: "request_accepted", requestId: request._id });
  }
  res.json({ request });
}));

app.get("/api/matching/my-students", authMiddleware, asyncHandler(async (req, res) => {
  const requests = await MentorRequest.find({ seniorId: req.auth.userId, status: "accepted" }).populate("juniorId");
  const students = requests.map((r) => r.juniorId).filter(Boolean);
  res.json({ students });
}));

app.get("/api/matching/my-mentors", authMiddleware, asyncHandler(async (req, res) => {
  const requests = await MentorRequest.find({ juniorId: req.auth.userId, status: "accepted" }).populate("seniorId");
  const mentors = requests.map((r) => r.seniorId).filter(Boolean);
  res.json({ mentors });
}));

app.post("/api/groups", authMiddleware, validateBody(createGroupSchema), asyncHandler(async (req, res) => {
  const creator = await User.findById(req.auth.userId);
  if (!creator || creator.role === "guest") return res.status(403).json({ error: "Guests cannot create groups" });

  const { name, description, category } = req.body;
  const group = await Group.create({ name, description, category, adminId: creator._id, members: [creator._id], sharedNotes: [], sessions: [] });
  res.status(201).json({ group });
}));

app.get("/api/groups", authMiddleware, asyncHandler(async (_, res) => {
  const groups = await Group.find().lean();
  res.json({ groups });
}));

app.post("/api/groups/:id/join", authMiddleware, asyncHandler(async (req, res) => {
  const userId = req.auth.userId;
  const group = await Group.findByIdAndUpdate(req.params.id, { $addToSet: { members: userId } }, { new: true });
  if (!group) return res.status(404).json({ error: "Group not found" });
  res.json({ group });
}));

app.get("/api/groups/:id/messages", authMiddleware, asyncHandler(async (req, res) => {
  const limit = Math.min(100, Math.max(1, Number(req.query.limit || 30)));
  const before = req.query.before ? new Date(String(req.query.before)) : null;
  const query = { groupId: req.params.id };
  if (before && !Number.isNaN(before.getTime())) {
    query.createdAt = { $lt: before };
  }
  const messages = await GroupMessage.find(query).sort({ createdAt: -1 }).limit(limit).lean();
  res.json({
    messages: messages.reverse(),
    page: {
      limit,
      hasMore: messages.length === limit,
      nextBefore: messages.length ? messages[messages.length - 1].createdAt : null
    }
  });
}));

app.post("/api/groups/:id/messages", authMiddleware, asyncHandler(async (req, res) => {
  const text = String(req.body.text || "").trim();
  if (!text) return res.status(422).json({ error: "Message text is required" });
  const group = await Group.findById(req.params.id).lean();
  if (!group) return res.status(404).json({ error: "Group not found" });
  const userId = req.auth.userId;
  const isMember = (group.members || []).map((m) => String(m)).includes(String(userId));
  if (!isMember) return res.status(403).json({ error: "Join group before messaging" });

  const msg = await GroupMessage.create({ groupId: req.params.id, senderId: userId, text });
  io.to(`group:${req.params.id}`).emit("group:message", msg);
  res.status(201).json({ message: msg });
}));

app.get("/api/chat/direct/:otherUserId/messages", authMiddleware, asyncHandler(async (req, res) => {
  const limit = Math.min(100, Math.max(1, Number(req.query.limit || 30)));
  const before = req.query.before ? new Date(String(req.query.before)) : null;
  const myId = String(req.auth.userId);
  const other = String(req.params.otherUserId);
  const roomId = [myId, other].sort().join(":");

  const query = { roomId };
  if (before && !Number.isNaN(before.getTime())) {
    query.createdAt = { $lt: before };
  }
  const messages = await DirectMessage.find(query).sort({ createdAt: -1 }).limit(limit).lean();
  res.json({
    messages: messages.reverse(),
    page: {
      limit,
      hasMore: messages.length === limit,
      nextBefore: messages.length ? messages[messages.length - 1].createdAt : null
    }
  });
}));

app.post("/api/chat/direct/:otherUserId/messages", authMiddleware, asyncHandler(async (req, res) => {
  const text = String(req.body.text || "").trim();
  if (!text) return res.status(422).json({ error: "Message text is required" });
  const senderId = String(req.auth.userId);
  const receiverId = String(req.params.otherUserId);
  const roomId = [senderId, receiverId].sort().join(":");
  const msg = await DirectMessage.create({ roomId, senderId, receiverId, text });
  io.to(`user:${receiverId}`).emit("direct:message", msg);
  io.to(`user:${senderId}`).emit("direct:message", msg);
  res.status(201).json({ message: msg });
}));

app.get("/api/notifications", authMiddleware, asyncHandler(async (req, res) => {
  const items = await Notification.find({ userId: req.auth.userId }).sort({ createdAt: -1 }).limit(50).lean();
  res.json({ notifications: items });
}));

app.post("/api/ai/chat", authMiddleware, validateBody(aiChatSchema), asyncHandler(async (req, res) => {
  const { prompt, mode } = req.body;
  const rolePromptByMode = {
    tutor: "You are a strict but helpful tutor. Explain step-by-step with short examples.",
    notes: "You generate structured notes with headings, bullet points, and a quick recap.",
    flashcards: "You generate flashcards strictly as Q/A pairs for revision."
  };

  const recentMessages = await DirectMessage.find({
    $or: [{ senderId: req.auth.userId }, { receiverId: req.auth.userId }]
  })
    .sort({ createdAt: -1 })
    .limit(5)
    .lean();

  const historyText = recentMessages
    .reverse()
    .map((m) => `${String(m.senderId) === req.auth.userId ? "User" : "Assistant"}: ${m.text}`)
    .join("\n");

  const system = `${rolePromptByMode[mode]}\nRespond with clear headings, bullet points, and one example.`;
  const promptWithContext = `${historyText ? `Conversation context:\n${historyText}\n\n` : ""}User question: ${prompt}`;

  let reply = "";
  try {
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), OLLAMA_TIMEOUT_MS);
    const response = await fetch(`${OLLAMA_URL}/api/generate`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ model: OLLAMA_MODEL, prompt: promptWithContext, system, stream: false }),
      signal: controller.signal
    });
    clearTimeout(timeout);
    if (!response.ok) return res.status(response.status).json({ error: `Ollama error ${response.status}` });
    const data = await response.json();
    reply = data.response || "";
  } catch (error) {
    // Fallback keeps AI endpoint usable even if Ollama is down.
    const fallbackByMode = {
      tutor: "## Quick Tutor Fallback\n- I could not reach the AI engine.\n- Start by listing what you know.\n- Then solve one simple example.\n\n### Example\n- Topic: Newton's 2nd law\n- Formula: F = m * a",
      notes: "## Structured Notes Fallback\n- **Definition:** Add core concept in one line.\n- **Key Points:** 3 bullets.\n- **Example:** one practical example.\n- **Recap:** two-line summary.",
      flashcards: "## Flashcards Fallback\n- Q: What is the core idea?\n  A: The main definition in simple words.\n- Q: Give one example.\n  A: Use a real-world short example."
    };
    reply = fallbackByMode[mode] || fallbackByMode.tutor;
  }

  await DirectMessage.create({
    roomId: `ai:${req.auth.userId}`,
    senderId: req.auth.userId,
    receiverId: req.auth.userId,
    text: prompt
  });
  await DirectMessage.create({
    roomId: `ai:${req.auth.userId}`,
    senderId: req.auth.userId,
    receiverId: req.auth.userId,
    text: reply
  });

  res.json({ reply, mode });
}));

io.on("connection", (socket) => {
  socket.on("auth:join", async ({ userId }) => {
    socket.join(`user:${userId}`);
    await User.findByIdAndUpdate(userId, { isOnline: true, availability: "online" }).catch(() => null);
    io.emit("presence:update", { userId, isOnline: true });
  });

  socket.on("group:join", ({ groupId }) => {
    socket.join(`group:${groupId}`);
  });

  socket.on("chat:typing", ({ roomId, userId, isTyping }) => {
    socket.to(roomId).emit("chat:typing", { userId, isTyping });
  });

  socket.on("group:message", async ({ groupId, senderId, text }) => {
    const msg = await GroupMessage.create({ groupId, senderId, text });
    io.to(`group:${groupId}`).emit("group:message", msg);
  });

  socket.on("direct:message", async ({ senderId, receiverId, text }) => {
    const roomId = [senderId, receiverId].sort().join(":");
    const msg = await DirectMessage.create({ roomId, senderId, receiverId, text });
    io.to(`user:${receiverId}`).emit("direct:message", msg);
    io.to(`user:${senderId}`).emit("direct:message", msg);
  });

  socket.on("disconnecting", async () => {
    const userRoom = Array.from(socket.rooms).find((r) => r.startsWith("user:"));
    if (!userRoom) return;
    const userId = userRoom.replace("user:", "");
    await User.findByIdAndUpdate(userId, { isOnline: false, availability: "offline" }).catch(() => null);
    io.emit("presence:update", { userId, isOnline: false });
  });
});

app.use((err, req, res, _next) => {
  console.error(`[${new Date().toISOString()}]`, err);
  if (res.headersSent) return;
  const status = err.statusCode || 500;
  const message = status >= 500 ? "Something went wrong. Please try again." : err.message;
  res.status(status).json({ error: message });
});

app.use((req, res) => {
  res.status(404).json({ error: "Route not found" });
});

async function start() {
  const envValidation = requiredEnvSchema.validate({
    MONGODB_URI,
    JWT_SECRET,
    JWT_EXPIRES_IN,
    OLLAMA_URL,
    OLLAMA_MODEL,
    PORT
  });
  if (envValidation.error) {
    throw new Error(`Invalid environment: ${envValidation.error.details.map((d) => d.message).join("; ")}`);
  }

  const maxAttempts = 5;
  let connected = false;
  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try {
      await mongoose.connect(MONGODB_URI, {
        serverSelectionTimeoutMS: 8000
      });
      connected = true;
      console.log(`MongoDB connected on attempt ${attempt}`);
      break;
    } catch (error) {
      const waitMs = Math.min(15000, attempt * 3000);
      console.error(`MongoDB connection failed (attempt ${attempt}/${maxAttempts}): ${error.message}`);
      if (attempt < maxAttempts) {
        await new Promise((resolve) => setTimeout(resolve, waitMs));
      }
    }
  }
  if (!connected) {
    throw new Error("Failed to connect to MongoDB after retries");
  }

  httpServer.listen(PORT, () => {
    console.log(`Stdili API running on port ${PORT}`);
  });
}

start().catch((error) => {
  console.error("Failed to start server:", error.message);
  process.exit(1);
});
