# 构建阶段
FROM node:20-bullseye-slim AS builder

# 设置容器用户为 root
USER root

# 确保全局安装 vite
RUN npm install -g vite --unsafe-perm

# 设置工作目录并复制依赖文件
WORKDIR /app

# 在 npm install 前清理缓存
RUN npm cache clean --force && \
    rm -rf node_modules && \
    npm install --force --legacy-peer-deps

# 复制并安装依赖
COPY package.json package-lock.json ./
RUN npm config set unsafe-perm true && \
    npm ci --omit=dev --force --legacy-peer-deps && \
    chmod -R 755 node_modules

# 复制源码并构建
COPY . .
RUN chmod -R 755 src && \
    chmod 755 node_modules/.bin/*
RUN npm run build

# 生产镜像
FROM nginx:1.25-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY /data/EZTools/ShortLink/var/lib/nginx/conf/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80