@echo off
chcp 65001 >nul
echo === 图谱探索LLM问答功能测试启动脚本 ===
echo.

REM 检查Node.js是否安装
node --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误：未找到Node.js，请先安装Node.js
    pause
    exit /b 1
)

REM 检查npm是否安装
npm --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ 错误：未找到npm，请先安装npm
    pause
    exit /b 1
)

for /f "tokens=*" %%i in ('node --version') do set NODE_VERSION=%%i
for /f "tokens=*" %%i in ('npm --version') do set NPM_VERSION=%%i

echo ✅ Node.js版本：%NODE_VERSION%
echo ✅ npm版本：%NPM_VERSION%
echo.

REM 检查是否在正确的目录
if not exist "package.json" (
    echo ❌ 错误：请在qknow-ui目录下运行此脚本
    pause
    exit /b 1
)

echo 📦 检查依赖包...
if not exist "node_modules" (
    echo 正在安装依赖包...
    npm install
    if %errorlevel% neq 0 (
        echo ❌ 依赖包安装失败
        pause
        exit /b 1
    )
) else (
    echo ✅ 依赖包已存在
)

echo.
echo 🔧 检查Text2Cypher API服务...
echo 请确保Text2Cypher API服务正在运行在 http://localhost:8003
echo.

REM 尝试连接API服务
echo 正在测试API连接...
curl -s http://localhost:8003/api/v1/health >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ Text2Cypher API服务连接成功
) else (
    echo ⚠️  警告：无法连接到Text2Cypher API服务
    echo 请确保服务正在运行：http://localhost:8003
    echo.
    set /p CONTINUE="是否继续启动前端服务？(y/n): "
    if /i not "%CONTINUE%"=="y" (
        pause
        exit /b 1
    )
)

echo.
echo 🚀 启动前端开发服务器...
echo 服务将在 http://localhost:5173 启动
echo.

REM 启动开发服务器
npm run dev 