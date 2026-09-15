@echo off
echo ===== Starting Backend + Frontend =====
echo.
echo [1/2] Starting backend...
start "Backend-8080" /d "%~dp0" cmd /k "call run-backend.bat"
echo [2/2] Starting frontend...
start "Frontend-5173" /d "%~dp0" cmd /k "call run-frontend.bat"
echo.
echo Backend : http://localhost:8080
echo Frontend: http://localhost:5173
echo.
echo Wait for "Started EmploymentApplication" in backend window.
pause
