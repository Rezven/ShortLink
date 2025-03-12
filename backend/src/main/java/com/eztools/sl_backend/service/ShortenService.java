package com.eztools.sl_backend.service;

/**
 * @author <Rezven>
 * 服务实现
 */
public interface ShortenService {
    /**
     * 生成短链接
     * @param originalUrl 原始URL
     * @param ttlSeconds 有效期（秒）
     * @return 短码
     */
    String generateShortUrl(String originalUrl, long ttlSeconds);
    
    /**
     * 解析短码获取原始URL
     * @param shortCode 短码
     * @return 原始URL
     */
    String resolveShortCode(String shortCode);
}