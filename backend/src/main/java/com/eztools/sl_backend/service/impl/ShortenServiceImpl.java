package com.eztools.sl_backend.service.impl;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import cn.hutool.core.lang.Snowflake;
import com.eztools.sl_backend.entity.ShortLink;
import com.eztools.sl_backend.exception.BadRequestException;
import com.eztools.sl_backend.exception.NotFoundException;
import com.eztools.sl_backend.repository.ShortLinkRepository;
import com.eztools.sl_backend.service.mq.MessageProducer;
import com.eztools.sl_backend.service.ShortenService;
import com.eztools.sl_backend.util.Base62Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * 短链接服务核心实现
 * 功能特性：
 * 1. 分布式ID生成（使用Hutool Snowflake）
 * 2. 布隆过滤器快速排重
 * 3. 数据库兜底校验
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortenServiceImpl implements ShortenService {
    private final Snowflake snowflake;
    private final ShortLinkRepository repository;
    private final BitMapBloomFilter bloomFilter;
    private final MessageProducer messageProducer;
    
    // 最大重试次数
    private static final int MAX_RETRY = 3;
    
    /**
     * 生成短链接实现
     * @param originalUrl 原始URL
     * @param ttlSeconds 过期时间（秒）
     */
    @Override
    public String generateShortUrl(String originalUrl, long ttlSeconds) {
        int retryCount = 0;
        String shortCode;
        
        do {
            // 生成分布式ID并转换
            long id = snowflake.nextId();
            shortCode = Base62Converter.encode(id);
            
            // 冲突检测
            if (!isCodeAvailable(shortCode)) {
                log.warn("短码冲突: {} 进行第{}次重试", shortCode, retryCount+1);
                continue;
            }
            
            // 保存记录
            saveShortLink(shortCode, originalUrl, ttlSeconds);
            return shortCode;
            
        } while (retryCount++ < MAX_RETRY);
        
        throw new BadRequestException("生成短码失败，请重试");
    }
    
    /**
     * 解析短码获取原始URL实现
     * @param shortCode 短码
     */
    @Override
    @Cacheable(value = "shortLinks", key = "#shortCode")
    public String resolveShortCode(String shortCode) {
        // 通过布隆过滤器快速判断是否存在
        if (!bloomFilter.contains(shortCode)) {
            throw new NotFoundException("短码不存在");
        }
        
        // 数据库精确查询
        return repository.findByShortCode(shortCode)
                .map(ShortLink::getOriginalUrl)
                .orElseThrow(() -> new NotFoundException("短码已失效"));
    }
    
    /**
     * 检查短码可用性
     * @param shortCode 短码
     */
    private boolean isCodeAvailable(String shortCode) {
        // 布隆过滤器快速判断
        if (bloomFilter.contains(shortCode)) {
            // 数据库兜底检查
            return !repository.existsByShortCode(shortCode);
        }
        //添加到布隆过滤器
        bloomFilter.add(shortCode);
        return true;
    }
    
    /**
     * 持久化短链接记录
     * @param code 短码
     * @param url 原始URL
     * @param ttl 过期时间（秒）
     */
    private void saveShortLink(String code, String url, long ttl) {
        ShortLink link = ShortLink.builder()
                .shortCode(code)
                .originalUrl(url)
                .expiresAt(Instant.now().plusSeconds(ttl))
                .build();
        
        repository.save(link);
        // 发送过期消息到MQ
        messageProducer.sendExpireMessage(code, ttl * 1000);
    }
}