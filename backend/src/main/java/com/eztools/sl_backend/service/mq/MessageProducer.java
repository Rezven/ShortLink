package com.eztools.sl_backend.service.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 过期消息生产者
 * @author <Rezven>
 */
@Component
@RequiredArgsConstructor
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;
    
    /**
     * 发送过期消息到延迟队列
     * @param shortCode 短码
     * @param delayMillis 存活时间（毫秒）
     */
    public void sendExpireMessage(String shortCode, long delayMillis) {
        rabbitTemplate.convertAndSend(
                "shortener.exchange",
                "expire",
                shortCode,
                message -> {
                    message.getMessageProperties()
                            .setDelayLong(delayMillis);
                    return message;
                }
        );
    }
}