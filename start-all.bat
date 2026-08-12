@echo off
echo ===== 一键启动前后端 =====
echo.

echo [1/2] 启动后端...
start "后端-SpringBoot" cmd /k "d:\vs cont\employment-tracking\run-backend.bat"

echo [2/2] 启动前端...
start "前端-Vite" cmd /k "d:\vs cont\employment-tracking\run-frontend.bat"

echo.
echo 后端: http://localhost:8080
echo 前端: http://localhost:5173
echo.
echo 等后端窗口出现 'Started' 后即可使用
pause
