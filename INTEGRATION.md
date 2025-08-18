# blade-qKnow 集成说明

## 集成概述

本文档说明如何将 `blade-qKnow` 作为独立子模块集成到 SpringBlade 微服务架构中，并适配 Nacos 配置中心。

## 集成架构

```
SpringBlade (根项目)
├── blade-auth (认证服务)
├── blade-gateway (网关服务)
├── blade-ops (运维服务)
├── blade-service (业务服务)
├── blade-service-api (业务API)
├── blade-common (通用模块)
├── blade-mybatis (数据访问)
├── blade-neo4j (图数据库)
├── blade-qknow-common (qKnow通用模块)
├── blade-redis (缓存模块)
└── blade-qKnow (新增: qKnow知识管理平台)
    ├── qknow-framework (框架模块)
    ├── qknow-module-app (应用模块)
    ├── qknow-module-dm (数据管理模块)
    ├── qknow-module-ext (知识抽取模块)
    ├── qknow-module-kmc (知识中心模块)
    ├── qknow-module-system (系统模块)
    └── qknow-server (服务入口)
```

## 主要修改内容

### 1. 项目结构调整

- 将 `blade-qKnow` 添加到 SpringBlade 根 pom.xml 的 modules 中
- 修改所有子模块的 parent 为 SpringBlade 项目
- 调整包名从 `tech.qiantong` 到 `org.springblade`
- 更新 Java 版本从 1.8 到 17

### 2. 依赖管理

- 继承 SpringBlade 的依赖管理
- 使用 SpringBlade 的版本号 `${revision}`
- 保持 qKnow 特有的依赖配置

### 3. 配置中心适配

- 创建 Nacos 配置文件 `blade-qknow-dev.yaml`
- 配置命名空间为 `blade-dev`
- 适配 SpringBlade 的配置结构

## 配置文件说明

### Nacos 配置

**配置文件**: `doc/nacos/blade-qknow-dev.yaml`

**关键配置**:
- 数据源配置 (MySQL)
- Redis 配置
- MyBatis Plus 配置
- Swagger 配置
- 服务端口 (8090)

### 启动参数

```bash
java -jar qknow-server.jar \
  --spring.profiles.active=dev \
  --spring.cloud.nacos.config.server-addr=127.0.0.1:8848 \
  --spring.cloud.nacos.config.namespace=blade-dev \
  --spring.cloud.nacos.config.file-extension=yaml \
  --spring.cloud.nacos.config.shared-configs[0].data-id=blade-qknow-dev.yaml \
  --spring.cloud.nacos.config.shared-configs[0].refresh=true
```

## 部署方式

### 方式一：独立部署

```bash
# 1. 构建项目
mvn clean package -DskipTests

# 2. 启动服务
cd blade-qKnow/qknow-server
java -jar target/qknow-server.jar [启动参数]
```

### 方式二：使用启动脚本

```bash
# Linux/Mac
chmod +x blade-qKnow/start-dev.sh
./blade-qKnow/start-dev.sh

# Windows
blade-qKnow\start-dev.bat
```

### 方式三：集成到 SpringBlade

```bash
# 在 SpringBlade 根目录下
mvn clean install -DskipTests

# 启动所有服务
# 包括 qKnow 服务
```

## 服务访问

- **服务地址**: http://localhost:8090
- **API文档**: http://localhost:8090/swagger-ui.html
- **健康检查**: http://localhost:8090/actuator/health

## 数据库配置

### MySQL 数据库

```sql
-- 创建数据库
CREATE DATABASE qknow_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入初始数据（可选）
source /path/to/qknow_20250522.sql;
```

### 数据源配置

```yaml
blade:
  datasource:
    dev:
      url: jdbc:mysql://localhost:3306/qknow_dev?useSSL=false&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&tinyInt1isBit=false&allowMultiQueries=true&serverTimezone=GMT%2B8
      username: root
      password: 123456
```

## 注意事项

### 1. 版本兼容性

- 确保 SpringBlade 版本与 qKnow 版本兼容
- 注意 Spring Boot 版本差异 (SpringBlade 使用 Spring Boot 3.x)
- 检查 Java 版本要求 (JDK 17+)

### 2. 包名冲突

- 修改所有 Java 类的包名
- 更新组件扫描路径
- 检查配置文件中的包名引用

### 3. 配置冲突

- 避免与 SpringBlade 其他服务的配置冲突
- 使用独立的配置命名空间
- 注意端口号冲突

### 4. 依赖冲突

- 检查 Maven 依赖版本冲突
- 使用 SpringBlade 的依赖管理
- 排除冲突的传递依赖

## 故障排除

### 常见问题

1. **启动失败**
   - 检查 Java 版本 (需要 JDK 17+)
   - 验证 Maven 构建是否成功
   - 检查配置文件是否正确

2. **数据库连接失败**
   - 验证 MySQL 服务状态
   - 检查数据库连接配置
   - 确认数据库用户权限

3. **Nacos 配置加载失败**
   - 检查 Nacos 服务状态
   - 验证配置文件是否存在
   - 确认命名空间配置

4. **端口冲突**
   - 检查 8090 端口是否被占用
   - 修改配置文件中的端口号
   - 使用 `netstat -tlnp` 查看端口占用

### 日志查看

```bash
# 查看服务日志
tail -f blade-qKnow/qknow-server/qknow.log

# 查看启动日志
tail -f /tmp/qknow.log
```

## 扩展开发

### 添加新模块

1. 在 `blade-qKnow` 下创建新模块
2. 更新父 pom.xml 的 modules 配置
3. 配置新模块的依赖关系
4. 创建对应的 Nacos 配置

### 自定义配置

1. 在 Nacos 中添加新的配置文件
2. 在启动参数中引用新配置
3. 更新启动脚本和文档

## 总结

通过以上集成步骤，`blade-qKnow` 已成功集成到 SpringBlade 微服务架构中，可以作为独立服务运行，也可以与其他 SpringBlade 服务协同工作。集成后的系统保持了原有的功能特性，同时获得了 SpringBlade 框架的稳定性和扩展性。
