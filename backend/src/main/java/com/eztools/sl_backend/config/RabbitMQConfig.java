package com.eztools.sl_backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <Rezven>
 * RabbitMQ队列和交换机配置
 * - 定义延迟队列实现链接自动过期
 * - 配置访问日志队列
 */
@Configuration
public class RabbitMQConfig {
    
    @Value("${spring.rabbitmq.host}")
    private String host;
    
    @Value("${spring.rabbitmq.port}")
    private int port;
    
    @Value("${spring.rabbitmq.username}")
    private String username;
    
    @Value("${spring.rabbitmq.password}")
    private String password;
    
    // 队列和交换机命名常量
    public static final String DELAY_QUEUE = "shortener.delay.queue";
    public static final String EXPIRE_QUEUE = "shortener.expire.queue";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLX_ROUTING_KEY = "expire.route";
    
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost("/");
        return factory;
    }
    
    // 延迟队列定义
    @Bean
    public Queue delayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", DLX_ROUTING_KEY);
        return new Queue(DELAY_QUEUE, true, false, false, args);
    }
    
    // 死信队列定义（实际处理过期的队列）
    @Bean
    public Queue expireQueue() {
        return new Queue(EXPIRE_QUEUE);
    }
    
    // 死信交换机定义
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }
    
    // 绑定死信队列到交换机
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(expireQueue())
                .to(dlxExchange())
                .with(DLX_ROUTING_KEY);
    }
}