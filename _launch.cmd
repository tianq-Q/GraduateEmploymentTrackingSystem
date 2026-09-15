@echo off
cd /d "%~dp0"
start "Frontend-5173" cmd /c "call run-frontend.bat > frontend_run.log 2>&1"
