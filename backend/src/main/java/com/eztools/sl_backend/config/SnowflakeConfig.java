package com.eztools.sl_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.hutool.core.lang.Snowflake;

/**
 * 分布式ID生成配置
 * @author <Rezven>
 * 配置参数说明：
 * - datacenter-id: 数据中心ID (0-31)
 * - machine-id: 机器ID (0-31)
 */
@Configuration
public class SnowflakeConfig {
    @Value("${snowflake.datacenter-id:1}")
    private long datacenterId;
    
    @Value("${snowflake.machine-id:1}")
    private long machineId;
    
    @Bean
    public Snowflake snowflake() {
        return new Snowflake(datacenterId, machineId);
    }
}