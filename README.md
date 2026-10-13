# Banco UdeA - Laboratorio 1

## Backend (Spring Boot 3.3 / Java 17 / MySQL)
Requisitos: JDK 17, Maven, MySQL en localhost:3306 (usuario root, clave 123456789).
La base de datos `banco2025` se crea sola.

    cd backend
    mvn spring-boot:run        # o abrir la carpeta en IntelliJ y ejecutar BancoudeaApplication

API en http://localhost:8080/api
- GET/POST            /customers
- GET/PUT/DELETE      /customers/{id}
- POST                /transactions   {senderAccountNumber, receiverAccountNumber, amount}
- GET                 /transactions, /transactions/{id}
- GET                 /transactions/account/{accountNumber}

## Frontend (React + Vite + Axios)
Requisitos: Node.js LTS.

    cd frontend
    npm install
    npm run dev                # http://localhost:5173
