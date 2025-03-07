FROM ubuntu:latest
LABEL authors="Rezven"

# 构建阶段
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --silent
COPY . .
RUN npm run build

# 生产镜像
FROM nginx:1.25-alpine
COPY /data/EZTools/ShortLink/var/lib/nginx/conf/nginx.conf /etc/nginx/nginx.conf
COPY /data/EZTools/ShortLink/var/lib/nginx/conf/conf.d/ /etc/nginx/conf.d/
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80