# Nginx 镜像构建
# 使用官方 Nginx 镜像作为基础
FROM nginx:1.25-alpine

# 删除默认配置
RUN rm /etc/nginx/conf.d/default.conf

# 添加自定义配置文件
COPY /data/EZTools/ShortLink/var/lib/nginx/conf/nginx.conf /etc/nginx/nginx.conf
COPY /data/EZTools/ShortLink/var/lib/nginx/conf/conf.d/ /etc/nginx/conf.d/

# 暴露端口
EXPOSE 80 443

# 启动命令（保持默认）
CMD ["nginx", "-g", "daemon off;"]

# MySQL 镜像构建
# 使用官方 MySQL 镜像
FROM mysql:8.0.36

# 不设置环境变量（root 密码、数据库名等）
# 复制初始化 SQL 脚本
COPY  ./src/main/resources/DataBase/init-scripts/ /docker-entrypoint-initdb.d/
VOLUME /var/lib/mysql

# 复制自定义配置文件（可选）
# COPY /data/EZTools/ShortLink/var/lib/mysql/conf.d/ /etc/mysql/conf.d/

# Redis 镜像构建
# 使用官方 Redis 镜像
FROM redis:7.2-alpine
CMD ["redis-server", "--requirepass", "${REDIS_PASSWORD}"]
# 复制自定义配置文件（可选）
# COPY /data/EZTools/ShortLink/var/lib/redis/conf/redis.conf /usr/local/etc/redis/redis.conf
# 设置启动命令（加载配置）
#CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]

# RabbitMQ 镜像构建
# 使用官方 RabbitMQ 镜像（带管理插件）
FROM rabbitmq:3.13-management-alpine
ENV RABBITMQ_DEFAULT_USER=${RABBITMQ_USER}
ENV RABBITMQ_DEFAULT_PASS=${RABBITMQ_PASSWORD}

# 阶段1: 前端构建
FROM registry.cn-hangzhou.aliyuncs.com/library/node:20-bullseye AS frontend-builder

WORKDIR /app

# 复制并安装依赖
COPY frontend/package*.json ./
RUN npm ci --unsafe-perm
# 修正权限问题,确保 vite 可执行
RUN chmod -R 755 node_modules/.bin/vite

COPY frontend .
RUN npm run build

# 阶段2: 后端构建
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
COPY --from=frontend-builder /app/dist ./src/main/resources/static
RUN mvn package -DskipTests

# 阶段3: 最终镜像
FROM eclipse-temurin:21-jre-alpine
COPY --from=backend-builder /app/target/*.jar /app.jar
COPY --from=frontend-builder /app/dist /app/public
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]