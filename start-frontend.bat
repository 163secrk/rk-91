@echo off
cd /d "%~dp0frontend"
echo 正在安装前端依赖...
if not exist "node_modules" (
    npm install
)
echo 正在启动前端服务（端口 3091）...
npm run dev
pause
