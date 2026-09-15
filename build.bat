@echo off
chcp 65001 >nul
set JAVA_HOME=D:\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "d:\vs cont\employment-tracking\backend"

echo.
echo ============================================
echo  Building backend with Maven...
echo ============================================
"D:\apache-maven-3.9.6\bin\mvn.cmd" package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo  BUILD_OK - target\employment-tracking-1.0.0.jar updated
    echo ============================================
    echo.
    echo  Next steps:
    echo    1. Close the OLD backend window / java process
    echo    2. Run run-backend.bat to start the new jar
) else (
    echo.
    echo ============================================
    echo  BUILD_FAILED - check errors above
    echo ============================================
)
echo.
pause
