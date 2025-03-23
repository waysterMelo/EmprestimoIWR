@echo off
echo Iniciando React com host liberado...
set DANGEROUSLY_DISABLE_HOST_CHECK=true
start cmd /k "npm start"

timeout /t 8 >nul

echo Abrindo ngrok...
start cmd /k "ngrok http 3000"

timeout /t 3 >nul

REM opcional: abrir navegador automaticamente
start https://localhost:3000
