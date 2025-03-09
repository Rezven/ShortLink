# 构建阶段
FROM node:20-alpine AS builder

# 设置工作目录并复制依赖文件
WORKDIR /app
COPY package*.json package-lock.json ./
RUN npm ci --silent
#RUN npm npm ci --omit=dev

# 复制源码并构建
COPY . .
RUN npm run build

# 生产镜像
FROM nginx:1.25-alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY /data/EZTools/ShortLink/var/lib/nginx/conf/nginx.conf /etc/nginx/nginx.conf
EXPOSE 80