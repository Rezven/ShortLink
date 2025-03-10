# 构建阶段
FROM node:20-bullseye AS builder

# 设置容器用户为 root
USER root

# 设置工作目录并复制依赖文件
WORKDIR /app

# 复制并安装依赖
COPY package*.json package-lock.json ./
RUN npm ci --unsafe-perm
# 修正权限问题,确保 vite 可执行
RUN chmod -R 755 node_modules/.bin/vite && \
#RUN npm npm ci --omit=dev

# 复制源码并构建
COPY . .
RUN npm run build

# 生产镜像
FROM nginx:1.25-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY /data/EZTools/ShortLink/var/lib/nginx/conf/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80