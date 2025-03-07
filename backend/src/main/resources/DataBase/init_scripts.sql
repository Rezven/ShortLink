-- 创建数据库
CREATE DATABASE IF NOT EXISTS ShortLink
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

-- 创建普通用户并授权（使用环境变量替换）
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE TEMPORARY TABLES, LOCK TABLES, EXECUTE
    ON `ShortLink`.* TO '${DATABASE_USERNAME}'@'%' IDENTIFIED BY '${DATABASE_PASSWORD}';

-- 刷新权限
FLUSH PRIVILEGES;

-- 切换数据库
USE ShortLink;

-- 创建短链接表
CREATE TABLE IF NOT EXISTS short_links (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                                           short_code VARCHAR(8) NOT NULL UNIQUE COMMENT '短码（唯一索引）',
                                           original_url LONGTEXT NOT NULL COMMENT '原始URL',
                                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           expires_at DATETIME NULL COMMENT '过期时间',
                                           is_expired TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否过期（0-否 1-是）',

    -- 索引优化
                                           INDEX idx_short_code (short_code),
                                           INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 添加注释
ALTER TABLE short_links COMMENT = '短链接存储表';