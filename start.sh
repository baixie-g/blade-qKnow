#!/bin/bash

# 千知平台启动脚本
# 作者: qKnow Team
# 版本: 3.8.8

echo "=========================================="
echo "        千知平台 (qKnow) 启动脚本"
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请安装Java 17或更高版本"
    exit 1
fi

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "错误: Java版本过低，需要Java 17或更高版本，当前版本: $JAVA_VERSION"
    exit 1
fi

echo "Java版本检查通过: $(java -version 2>&1 | head -n 1)"

# 检查JAR文件是否存在
JAR_FILE="qknow-server/target/qknow-server.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "错误: 未找到JAR文件: $JAR_FILE"
    echo "请先运行: mvn clean package -DskipTests"
    exit 1
fi

echo "JAR文件检查通过: $JAR_FILE"

# 设置JVM参数
JAVA_OPTS="-Xms512m -Xmx2048m -Djava.security.egd=file:/dev/./urandom"

# 设置环境变量
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}

echo "启动参数:"
echo "  JVM参数: $JAVA_OPTS"
echo "  环境配置: $SPRING_PROFILES_ACTIVE"
echo "  服务端口: 8090"
echo ""

# 启动应用
echo "正在启动千知平台..."
echo "=========================================="

java $JAVA_OPTS -jar "$JAR_FILE" --spring.profiles.active=$SPRING_PROFILES_ACTIVE

echo "=========================================="
echo "千知平台已停止"

