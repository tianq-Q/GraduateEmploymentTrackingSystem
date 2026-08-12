@echo off
chcp 65001 >nul
echo ============================================
echo   就业跟踪系统 - 数据库初始化
echo ============================================
echo.

:: 默认连接信息
set MYSQL_USER=root
set MYSQL_PASS=123456
set MYSQL_HOST=localhost
set MYSQL_PORT=3306

:: 允许通过参数传入自定义值
:parse
if "%~1"=="" goto :run
if "%~1"=="-u" set MYSQL_USER=%~2 & shift & shift & goto :parse
if "%~1"=="-p" set MYSQL_PASS=%~2 & shift & shift & goto :parse
if "%~1"=="-h" set MYSQL_HOST=%~2 & shift & shift & goto :parse
if "%~1"=="-P" set MYSQL_PORT=%~2 & shift & shift & goto :parse
echo 用法: %~nx0 [-u 用户名] [-p 密码] [-h 主机] [-P 端口]
echo 示例: %~nx0 -u root -p mypassword
pause
exit /b 1

:run
echo 数据库连接: %MYSQL_HOST%:%MYSQL_PORT%  用户: %MYSQL_USER%
echo.

:: 检查 mysql 命令是否可用
where mysql >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 mysql 命令，请确保 MySQL 已安装并添加到 PATH 环境变量
    pause
    exit /b 1
)

:: 查找 mysql.exe 的完整路径
for /f "delims=" %%i in ('where mysql') do set MYSQL_CMD=%%i
echo MySQL 路径: %MYSQL_CMD%
echo.

:: 获取脚本所在目录
set SCRIPT_DIR=%~dp0
set SQL_FILE=%SCRIPT_DIR%employment_tracking_data.sql

if not exist "%SQL_FILE%" (
    echo [错误] 找不到数据文件: %SQL_FILE%
    pause
    exit /b 1
)

echo [1/2] 正在导入数据库结构和数据...
"%MYSQL_CMD%" -u %MYSQL_USER% -p%MYSQL_PASS% -h %MYSQL_HOST% -P %MYSQL_PORT% --default-character-set=utf8mb4 < "%SQL_FILE%"

if %errorlevel% neq 0 (
    echo.
    echo [失败] 数据库导入失败！
    echo 请检查:
    echo   1. MySQL 服务是否已启动
    echo   2. 用户名和密码是否正确
    echo   3. 如果密码不同，请用: %~nx0 -u 用户名 -p 密码
    pause
    exit /b 1
)

echo 数据库结构和数据导入完成！
echo.
echo [2/2] 验证导入结果...
"%MYSQL_CMD%" -u %MYSQL_USER% -p%MYSQL_PASS% -h %MYSQL_HOST% -P %MYSQL_PORT% employment_tracking -e "SELECT '用户数' as 项目, COUNT(*) as 值 FROM sys_user UNION SELECT '院系数', COUNT(*) FROM department UNION SELECT '专业数', COUNT(*) FROM major;" 2>nul

echo.
echo ============================================
echo   初始化完成！
echo ============================================
echo.
echo 测试账号:
echo   管理员: admin / admin123
echo   教师:   teacher01 / 123456
echo   学生:   student01 / 123456
echo.
echo 接下来:
echo   1. 确认 application-dev.yml 中数据库密码正确
echo   2. 运行后端: cd backend ^&^& mvn spring-boot:run
echo   3. 运行前端: cd frontend ^&^& npm run dev
echo.
pause
