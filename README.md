# 🏠 HostelCare — Smart Complaint Management System

A full-stack web application for managing hostel complaints with role-based access control for Students, Wardens, and Admins.

## 🌐 Live Demo

| Service | URL |
|---------|-----|
| **Frontend** | [https://hostelcare-frontend.onrender.com](https://hostelcare-frontend.onrender.com) |
| **Backend API** | [https://hostelcare-api.onrender.com](https://hostelcare-api.onrender.com) |

---

## 🔐 Demo Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@hostel.com | admin123 |
| Warden | warden@hostel.com | warden123 |
| Student | student@hostel.com | student123 |

---

## ✨ Features

### Student
- Register and login
- Submit complaints with title, description, priority, category and optional image
- View all personal complaints with status tracking
- View full status change history per complaint

### Warden
- View all complaints
- Filter complaints by status and category
- Update complaint status (PENDING → IN_PROGRESS → RESOLVED)
- Assign complaints to staff members

### Admin
- All Warden features
- Add new complaint categories
- Full complaint management dashboard with stats

---

## 🛠️ Tech Stack

### Backend
| Technology | Version |
|------------|---------|
| Java | 24 |
| Spring Boot | 3.2.4 |
| Spring Security | JWT Auth |
| Spring Data JPA | Hibernate 6 |
| PostgreSQL | Neon Cloud |
| Lombok | 1.18.38 |
| JJWT | 0.11.5 |

### Frontend
| Technology | Version |
|------------|---------|
| React | 19 |
| Vite | 8 |
| Tailwind CSS | 3 |
| Axios | Latest |
| React Router | v6 |
| React Hot Toast | Latest |
| Lucide React | Latest |

### Deployment
| Service | Platform |
|---------|----------|
| Backend | [Render](https://render.com) (Docker) |
| Frontend | [Render](https://render.com) (Static Site) |
| Database | [Neon](https://neon.tech) (PostgreSQL) |

---

## 📁 Project Structure

```
Smart_Complaint_Management/
├── src/
│   └── main/
│       ├── java/com/hostelcare/
│       │   ├── config/          # Security, CORS, Web config
│       │   ├── controller/      # REST controllers
│       │   ├── dto/             # Request/Response DTOs
│       │   ├── entity/          # JPA entities
│       │   ├── enums/           # Role, Status, Priority
│       │   ├── exception/       # Global exception handling
│       │   ├── repository/      # JPA repositories
│       │   ├── security/        # JWT filter and utils
│       │   ├── service/         # Business logic
│       │   └── DataInitializer.java
│       └── resources/
│           └── application.properties
├── frontend/
│   └── src/
│       ├── api/             # Axios instance
│       ├── components/      # Navbar, Badge, ProtectedRoute
│       ├── context/         # AuthContext
│       └── pages/           # Login, Register, Dashboards
├── Dockerfile
├── render.yaml
└── build.gradle
```

---

## 🚀 Run Locally

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL running locally

### Backend
```bash
# Create database
psql -U postgres -c "CREATE DATABASE hostelcare;"

# Run backend
./gradlew bootRun
# Backend starts at http://localhost:8080
```

### Frontend
```bash
cd frontend
npm install
npm run dev
# Frontend starts at http://localhost:5173
```

---

## 🔌 API Endpoints

### Auth
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |

### Complaints
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/api/complaints` | STUDENT |
| GET | `/api/complaints` | ADMIN, WARDEN |
| GET | `/api/complaints/{id}` | Authenticated |
| GET | `/api/complaints/user/{userId}` | STUDENT |
| PUT | `/api/complaints/{id}/status` | ADMIN, WARDEN |
| PUT | `/api/complaints/{id}/assign` | ADMIN, WARDEN |
| GET | `/api/complaints/{id}/history` | Authenticated |

### Categories
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/api/categories` | Authenticated |
| POST | `/api/categories` | ADMIN |

---

## ⚙️ Environment Variables

### Backend (Render)
| Key | Description |
|-----|-------------|
| `DATABASE_URL` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | Database username |
| `DATABASE_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |

### Frontend (Render)
| Key | Description |
|-----|-------------|
| `VITE_API_URL` | Backend API base URL |

---

## 👨‍💻 Author

**Sabareeshwaran**
- GitHub: [@sabareeshwaran16](https://github.com/sabareeshwaran16)
