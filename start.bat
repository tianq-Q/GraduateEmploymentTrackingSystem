@echo off
echo ============================================
echo   高校毕业生就业跟踪与分析系统 - 启动
echo ============================================
echo.

:: 检查 MySQL 是否运行
echo [1/3] 检查 MySQL 服务...
sc query MySQL | find "RUNNING" >nul
if %ERRORLEVEL% NEQ 0 (
    echo [警告] MySQL 服务未运行！正在尝试启动...
    net start MySQL 2>nul
    if %ERRORLEVEL% NEQ 0 (
        echo [错误] MySQL 启动失败，请手动启动 MySQL 服务！
        pause
        exit /b 1
    )
)
echo      MySQL 服务运行中 ^^

:: 启动后端
echo [2/3] 启动后端 Spring Boot (端口 8080)...
set JAVA_HOME=D:\Java\jdk-17
start "Backend-8080" cmd /k "cd /d "d:\vs cont\employment-tracking\backend" && D:\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run"

:: 等待后端启动
echo     等待后端启动中（约 15 秒）...
timeout /t 5 /nobreak >nul

:: 启动前端
echo [3/3] 启动前端 Vue3 Vite 开发服务器...
start "Frontend-Vite" cmd /k "cd /d "d:\vs cont\employment-tracking\frontend" && npm run dev"

echo.
echo ============================================
echo   启动完成！
echo   后端: http://localhost:8080
echo   前端: http://localhost:5173
echo ============================================
echo.
echo 按任意键关闭此窗口（不会影响前后端运行）
pause >nul
