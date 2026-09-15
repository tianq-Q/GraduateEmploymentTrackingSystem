@echo off
setlocal
set PATH=C:\Users\qqq\.workbuddy\binaries\node\versions\22.22.2;%PATH%
cd /d "%~dp0frontend"
if not exist node_modules call npm install
if errorlevel 1 goto :fail
echo Starting frontend on http://localhost:5173 ...
call npm run dev
goto :eof
:fail
echo [ERROR] npm install failed. Check network and retry.
pause
endlocal
