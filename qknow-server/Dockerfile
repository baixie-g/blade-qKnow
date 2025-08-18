# 使用 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE="dev"
ENV NACOS_SERVER_ADDR="127.0.0.1:8848"
ENV NACOS_NAMESPACE="blade-dev"
ENV NACOS_FILE_EXTENSION="yaml"

# 复制 JAR 文件
COPY target/qknow-server.jar app.jar

# 暴露端口
EXPOSE 8090

# 创建日志目录
RUN mkdir -p /app/logs

# 设置时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 启动命令
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE --spring.cloud.nacos.config.server-addr=$NACOS_SERVER_ADDR --spring.cloud.nacos.config.namespace=$NACOS_NAMESPACE --spring.cloud.nacos.config.file-extension=$NACOS_FILE_EXTENSION"]
