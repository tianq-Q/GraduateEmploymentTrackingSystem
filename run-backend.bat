@echo off
setlocal
set JAVA_HOME=D:\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d "%~dp0backend"
if exist "target\employment-tracking-1.0.0.jar" (
    echo [1/2] Found built jar, starting directly...
    "%JAVA_HOME%\bin\java" -jar -Dfile.encoding=UTF-8 "target\employment-tracking-1.0.0.jar"
) else (
    echo [1/2] No jar found, building via mvn spring-boot:run...
    D:\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
)
endlocal
