#!/bin/bash

echo "=== 图谱探索LLM问答功能测试启动脚本 ==="
echo ""

# 检查Node.js是否安装
if ! command -v node &> /dev/null; then
    echo "❌ 错误：未找到Node.js，请先安装Node.js"
    exit 1
fi

# 检查npm是否安装
if ! command -v npm &> /dev/null; then
    echo "❌ 错误：未找到npm，请先安装npm"
    exit 1
fi

echo "✅ Node.js版本：$(node --version)"
echo "✅ npm版本：$(npm --version)"
echo ""

# 检查是否在正确的目录
if [ ! -f "package.json" ]; then
    echo "❌ 错误：请在qknow-ui目录下运行此脚本"
    exit 1
fi

echo "📦 检查依赖包..."
if [ ! -d "node_modules" ]; then
    echo "正在安装依赖包..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ 依赖包安装失败"
        exit 1
    fi
else
    echo "✅ 依赖包已存在"
fi

echo ""
echo "🔧 检查Text2Cypher API服务..."
echo "请确保Text2Cypher API服务正在运行在 http://localhost:8003"
echo ""

# 尝试连接API服务
echo "正在测试API连接..."
curl -s http://localhost:8003/api/v1/health > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Text2Cypher API服务连接成功"
else
    echo "⚠️  警告：无法连接到Text2Cypher API服务"
    echo "请确保服务正在运行：http://localhost:8003"
    echo ""
    read -p "是否继续启动前端服务？(y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "🚀 启动前端开发服务器..."
echo "服务将在 http://localhost:5173 启动"
echo ""

# 启动开发服务器
npm run dev 