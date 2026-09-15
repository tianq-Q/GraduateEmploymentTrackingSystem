@echo off
cd /d "%~dp0"
call run-frontend.bat > frontend_run.log 2>&1
