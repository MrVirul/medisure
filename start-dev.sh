#!/bin/bash

# Start MediSur Development Environment
echo "🚀 Starting MediSur Development Environment..."

# Function to check if a port is in use
check_port() {
    lsof -ti:$1 > /dev/null 2>&1
}

# Check if backend port is available
if check_port 8080; then
    echo "⚠️  Port 8080 is already in use. Backend might already be running."
else
    echo "📦 Starting Spring Boot backend..."
    cd "/Users/virul/My Projects/medisure"
    ./mvnw spring-boot:run &
    BACKEND_PID=$!
    echo "Backend started with PID: $BACKEND_PID"
fi

# Check if frontend port is available
if check_port 5173; then
    echo "⚠️  Port 5173 is already in use. Frontend might already be running."
else
    echo "⚛️  Starting React frontend..."
    cd "/Users/virul/My Projects/medisure/frontend"
    npm run dev &
    FRONTEND_PID=$!
    echo "Frontend started with PID: $FRONTEND_PID"
fi

echo ""
echo "✅ Development environment started!"
echo "🌐 Frontend: http://localhost:5173"
echo "🔧 Backend API: http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop all services"

# Wait for user to stop
wait
