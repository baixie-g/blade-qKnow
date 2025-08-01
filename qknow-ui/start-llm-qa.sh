#!/bin/bash

# LLM问答功能启动脚本
# 用于快速启动和测试LLM问答交互功能

echo "🤖 LLM问答功能启动脚本"
echo "================================"

# 检查Node.js是否安装
if ! command -v node &> /dev/null; then
    echo "❌ 错误: Node.js未安装，请先安装Node.js"
    exit 1
fi

# 检查npm是否安装
if ! command -v npm &> /dev/null; then
    echo "❌ 错误: npm未安装，请先安装npm"
    exit 1
fi

# 检查是否在正确的目录
if [ ! -f "package.json" ]; then
    echo "❌ 错误: 请在qknow-ui目录下运行此脚本"
    exit 1
fi

echo "✅ 环境检查通过"

# 检查依赖是否安装
if [ ! -d "node_modules" ]; then
    echo "📦 安装依赖包..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ 依赖安装失败"
        exit 1
    fi
fi

echo "✅ 依赖检查完成"

# 检查Text2Cypher服务状态
echo "🔍 检查Text2Cypher服务状态..."
if curl -s http://localhost:8003/api/v1/health > /dev/null; then
    echo "✅ Text2Cypher服务运行正常"
else
    echo "⚠️  警告: Text2Cypher服务未运行或无法访问"
    echo "   请确保Text2Cypher服务在 http://localhost:8003 运行"
    echo "   您可以稍后手动启动Text2Cypher服务"
fi

echo ""
echo "🚀 启动前端开发服务器..."
echo "================================"
echo "📱 前端地址: http://localhost:5173"
echo "🔧 测试页面: http://localhost:5173/test-chat.html"
echo "🗺️  图谱探索: http://localhost:5173/app/graphExploration"
echo ""
echo "💡 使用提示:"
echo "   1. 打开图谱探索页面"
echo "   2. 点击'智能问答'按钮"
echo "   3. 开始与AI对话"
echo ""
echo "按 Ctrl+C 停止服务"
echo "================================"

# 启动开发服务器
npm run dev 