@echo off
setlocal
echo ============================================
echo   Employment Tracking System - Launcher
echo ============================================
echo.
echo [1/3] Checking MySQL service...
sc query MySQL | find "RUNNING" >nul 2>nul
if errorlevel 1 (
    sc query MySQL80 | find "RUNNING" >nul 2>nul
)
if errorlevel 1 (
    echo [WARN] MySQL not running, trying to start...
    net start MySQL 2>nul
    if errorlevel 1 (
        net start MySQL80 2>nul
    )
    if errorlevel 1 (
        echo [ERROR] Failed to start MySQL. Please start it manually!
        pause
        exit /b 1
    )
    echo      MySQL started
) else (
    echo      MySQL is running
)
echo [2/3] Starting backend Spring Boot (port 8080)...
start "Backend-8080" /d "%~dp0" cmd /k "call run-backend.bat"
echo [3/3] Starting frontend Vue3 Vite (port 5173)...
start "Frontend-5173" /d "%~dp0" cmd /k "call run-frontend.bat"
echo.
echo ============================================
echo   Backend : http://localhost:8080
echo   Frontend: http://localhost:5173
echo ============================================
echo.
echo This window can be closed; services keep running.
pause >nul
endlocal
