# Banco UdeA - Laboratorio 1 y 2

[![CI/CD Pipeline](https://github.com/JuanOcampo2024/banco-lab1/actions/workflows/build.yml/badge.svg)](https://github.com/JuanOcampo2024/banco-lab1/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JuanOcampo2024_banco-lab1&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JuanOcampo2024_banco-lab1)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=JuanOcampo2024_banco-lab1&metric=coverage)](https://sonarcloud.io/summary/new_code?id=JuanOcampo2024_banco-lab1)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=JuanOcampo2024_banco-lab1&metric=bugs)](https://sonarcloud.io/summary/new_code?id=JuanOcampo2024_banco-lab1)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=JuanOcampo2024_banco-lab1&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=JuanOcampo2024_banco-lab1)

Aplicación bancaria: API REST (Spring Boot) + frontend (React).

## Estructura
- `backend/`  API REST Spring Boot 3.3 / Java 17 / MySQL
- `frontend/` React + Vite + Axios
- `.github/workflows/build.yml` pipeline CI/CD

## Pipeline
tests (JUnit + JaCoCo) -> SonarCloud -> build JAR -> imagen Docker (Docker Hub) -> deploy en Render

## Ejecutar el backend
Crea `backend/src/main/resources/application-local.properties` con:

    spring.datasource.password=TU_CLAVE_MYSQL

y luego:

    cd backend
    mvn spring-boot:run

## Pruebas y cobertura

    cd backend
    mvn clean verify     # reporte en target/site/jacoco/index.html

## Docker

    docker run -p 8080:8080 -e DB_URL=... -e DB_USER=... -e DB_PASSWORD=... TU_USUARIO_DOCKER/bancoudea:latest

## Frontend

    cd frontend
    npm install
    npm run dev
