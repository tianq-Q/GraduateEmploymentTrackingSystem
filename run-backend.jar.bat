@echo off
setlocal
set JAVA_HOME=D:\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0backend"
if not exist "target\employment-tracking-1.0.0.jar" (
    echo [ERROR] target\employment-tracking-1.0.0.jar not found - run build.bat first!
    pause
    exit /b 1
)
echo Starting backend on http://localhost:8080 ...
echo Wait for "Started EmploymentApplication" ...
"%JAVA_HOME%\bin\java" -jar -Dfile.encoding=UTF-8 "target\employment-tracking-1.0.0.jar"
pause
endlocal
