@echo off
chcp 65001 >nul
set JAVA_HOME=D:\Java\jdk-17
cd /d "d:\vs cont\employment-tracking\backend"
java -jar -Dfile.encoding=UTF-8 "target\employment-tracking-1.0.0.jar" >> "d:\vs cont\employment-tracking\backend.log" 2>&1
