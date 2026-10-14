# Informe Laboratorio 2: CI/CD con Spring Boot, GitHub Actions, SonarCloud, Docker y Render

**Estudiante:** Juan Pablo Ocampo
**Repositorio:** https://github.com/JuanOcampo2024/banco-lab1
**URL en la nube:** https://bancoudea-h22c.onrender.com
**Docker Hub:** https://hub.docker.com/r/juanocampo2024/bancoudea   

## 1. Arquitectura del pipeline
Push/PR -> GitHub Actions -> [tests + JaCoCo] -> [SonarCloud] -> [build JAR] -> [imagen Docker en Docker Hub] -> [deploy en Render]

## 2. Paso a paso realizado
1. Se agregó al backend: JaCoCo, H2 (pruebas), `Dockerfile`, variables de entorno para la BD y las pruebas unitarias/integración.
2. Se creó `.github/workflows/build.yml` con los jobs: `tests`, `sonar`, `build`, `docker`, `deploy`.
3. SonarCloud: se importó el repo, se desactivó Automatic Analysis, se creó el token `SONAR_TOKEN`.
4. Docker Hub: se creó el repositorio `bancoudea` y un access token.
5. Secrets en GitHub: `SONAR_TOKEN`, `DOCKER_USERNAME`, `DOCKER_PASSWORD`, `RENDER_DEPLOY_HOOK`.
6. Base de datos en la nube y servicio web en Render con las variables `DB_URL`, `DB_USER`, `DB_PASSWORD` (y `DB_DRIVER` si aplica).
7. Estrategia trunk based: rama `feature/health-endpoint`, Pull Request hacia `main`, merge y despliegue automático.

## 3. Evidencias 

En carpeta capturas

