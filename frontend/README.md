# MediSur Frontend

A modern React frontend for the MediSur insurance management system.

## Tech Stack

- **React 18** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Utility-first CSS framework
- **React Query (TanStack Query)** - Data fetching and caching
- **React Router** - Client-side routing
- **React Hook Form** - Form handling
- **Zod** - Schema validation
- **Axios** - HTTP client

## Features

- 🎨 Modern, responsive UI with Tailwind CSS
- 🔒 Type-safe development with TypeScript
- ⚡ Fast development with Vite
- 📡 Efficient data fetching with React Query
- 📝 Form validation with React Hook Form + Zod
- 🧭 Client-side routing with React Router
- 🔄 Real-time data synchronization

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm or pnpm

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

3. Open [http://localhost:5173](http://localhost:5173) in your browser.

### Backend Integration

Make sure your Spring Boot backend is running on `http://localhost:8080` before starting the frontend.

## Project Structure

```
src/
├── components/     # Reusable UI components
│   ├── UserCard.tsx
│   └── UserForm.tsx
├── pages/         # Page components
│   └── UsersPage.tsx
├── hooks/         # Custom React hooks
│   └── useUsers.ts
├── services/      # API service layer
│   └── api.ts
├── types/         # TypeScript type definitions
│   └── user.ts
├── utils/         # Utility functions
└── stores/        # State management (if needed)
```

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint

## API Integration

The frontend integrates with the Spring Boot backend through:

- **Base URL**: `http://localhost:8080/api`
- **Authentication**: JWT tokens (stored in localStorage)
- **Endpoints**:
  - `GET /users` - Get all users
  - `GET /users/{id}` - Get user by ID
  - `POST /users` - Create new user
  - `PUT /users/{id}` - Update user
  - `DELETE /users/{id}` - Delete user

## Environment Variables

Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

## Contributing

1. Follow TypeScript best practices
2. Use Tailwind CSS for styling
3. Write tests for new components
4. Follow the existing code structure

## License

This project is part of the MediSur insurance management system.