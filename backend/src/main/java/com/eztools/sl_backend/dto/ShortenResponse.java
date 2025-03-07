package com.eztools.sl_backend.dto;

import java.time.Instant;

/**
 * @author <Rezven>
 * 短链接生成响应结果
 * @param shortCode 生成的短码
 * @param shortUrl 完整短链接URL
 * @param expiresAt 过期时间戳
 */
public record ShortenResponse(
        String shortCode,
        String shortUrl,
        Instant expiresAt
) {}