package com.eztools.sl_backend.config;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import com.eztools.sl_backend.repository.ShortLinkRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置
 * @author <Rezven>
 */
@Configuration
public class BloomFilterConfig {
    @Bean
    public BitMapBloomFilter bloomFilter(
            ShortLinkRepository repository
    ) {
        BitMapBloomFilter filter = new BitMapBloomFilter(1000000);
        repository.findAllShortCodes().forEach(filter::add);
        return filter;
    }
}