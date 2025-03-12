package com.eztools.sl_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

/**
 * 短链接实体类
 * @author <Rezven>
 * - 使用JPA注解映射数据库表
 * - 包含乐观锁版本控制
 */
@Entity
@Table(name = "short_links")
@Getter @Setter
@NoArgsConstructor  // 必须保留无参构造
@AllArgsConstructor // 全参构造函数
@Builder  // 启用Builder模式
public class ShortLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "short_code", unique = true, length = 8)
    private String shortCode;
    
    @Lob
    @Column(name = "original_url", nullable = false)
    private String originalUrl;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @Column(name = "expires_at")
    private Instant expiresAt;
    
    @Column(name = "is_expired")
    private Boolean isExpired = false;
}