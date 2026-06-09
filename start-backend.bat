@echo off
cd /d "%~dp0backend"
echo 正在启动后端服务（端口 8091）...
mvn spring-boot:run
pause
