package com.eztools.sl_backend.repository;

import com.eztools.sl_backend.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * 短链接数据访问接口
 * @author <Rezven>
 */
public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {
    /**
     * 根据短码查询记录
     * 等价SQL: SELECT * FROM short_links WHERE short_code = ?
     */
    Optional<ShortLink> findByShortCode(String shortCode);
    
    /**
     * 检查短码是否存在
     * 等价SQL: SELECT COUNT(*) FROM short_links WHERE short_code = ?
     */
    boolean existsByShortCode(String shortCode);
    
    /**
     * 获取所有短码（用于初始化布隆过滤器）
     * 等价SQL: SELECT short_code FROM short_links
     */
    @Query("SELECT s.shortCode FROM ShortLink s")
    List<String> findAllShortCodes();
    
    /**
     * 根据短码删除记录
     * 等价SQL: DELETE FROM short_links WHERE short_code = ?
     */
    void deleteByShortCode(String shortCode);
    @Modifying
    @Query("UPDATE ShortLink SET isExpired = true WHERE shortCode = :shortCode")
    void markAsExpired(@Param("shortCode") String shortCode);
}