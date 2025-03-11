package com.eztools.sl_backend.service.mq;

import com.eztools.sl_backend.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 过期消息处理器
 * @author <Rezven>
 * - 处理过期链接清理
 */
@Component
@RequiredArgsConstructor
public class MessageConsumer {
    private final ShortLinkRepository repository;
    
    @Transactional
    @RabbitListener(queues = "${mq.expire-queue}")
    public void handleExpireMessage(String shortCode) {
        repository.markAsExpired(shortCode);
    }
}