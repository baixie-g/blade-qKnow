#!/bin/bash

# QKnow后端服务启动脚本

echo "🚀 QKnow后端服务启动脚本"
echo "================================"

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "❌ 错误: Java未安装，请先安装Java 8或更高版本"
    exit 1
fi

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: Maven未安装，请先安装Maven"
    exit 1
fi

echo "✅ 环境检查通过"

# 检查是否在正确的目录
if [ ! -f "pom.xml" ]; then
    echo "❌ 错误: 请在qknow-server目录下运行此脚本"
    exit 1
fi

# 编译项目
echo "🔨 编译项目..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 编译失败"
    exit 1
fi

echo "✅ 编译成功"

# 启动服务
echo ""
echo "🚀 启动QKnow后端服务..."
echo "================================"
echo "📱 服务地址: http://localhost:8090"
echo "🔧 测试接口: http://localhost:8090/api/test/hello"
echo "🤖 LLM接口: http://localhost:8090/api/llm/health"
echo ""
echo "按 Ctrl+C 停止服务"
echo "================================"

# 启动Spring Boot应用
mvn spring-boot:run 