package com.eztools.sl_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author <Rezven>
 * 启动类
 */
@SpringBootApplication
@EnableScheduling  // 启用定时任务（用于清理过期链接）
@EnableAsync // 启用异步任务（用于处理短链接的访问请求）
public class BackendApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
    
}
