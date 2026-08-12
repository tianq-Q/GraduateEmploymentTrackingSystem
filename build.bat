@echo off
set JAVA_HOME=D:\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "d:\vs cont\employment-tracking\backend"
D:\apache-maven-3.9.6\bin\mvn.cmd package -DskipTests
if %ERRORLEVEL% EQU 0 (
    echo BUILD_OK
    java -jar target\employment-tracking-1.0.0.jar
) else (
    echo BUILD_FAILED
)
