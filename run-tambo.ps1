# Script de inicio para Sistema Tambo (Backend + Frontend Angular)

Write-Host "Iniciando Sistema Tambo..." -ForegroundColor Purple

# 1. Levantar el Backend (Spring Boot) en el puerto 8080
Write-Host "Levantando el Backend (Spring Boot)..." -ForegroundColor Cyan
Start-Process -FilePath "cmd.exe" -ArgumentList "/c tools\maven\bin\mvn.cmd spring-boot:run" -WorkingDirectory "."

# 2. Levantar el Frontend (Angular) en el puerto 4200
Write-Host "Levantando el Frontend (Angular)..." -ForegroundColor Cyan
Start-Process -FilePath "cmd.exe" -ArgumentList "/c npm.cmd start" -WorkingDirectory ".\frontend"

Write-Host "Ambos servidores iniciados." -ForegroundColor Green
Write-Host "Backend en: http://localhost:8080" -ForegroundColor Gray
Write-Host "Frontend en: http://localhost:4200" -ForegroundColor Gray
