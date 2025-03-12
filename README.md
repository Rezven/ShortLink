# ShortLink 短链生成器系统

---

## 📖 项目背景
针对传统长URL分享不便，本项目实现高可用短链服务。本项目**轻量级、可定制**，适合开发者二次开发或学习。

---

## 🔍 需求分析
### 核心功能
| 模块           | 功能描述                                 |
|----------------|--------------------------------------|
| 短链生成       | 支持长URL转为短链（6-8字符）、自定义别名、有效期设置（默认30天） |
| 短链跳转       | 重定向、实时状态检测（是否失效/封禁）                  |

### 性能指标
- **生成服务**：QPS ≥ 800（单节点） | 平均延迟 ≤ 50ms
- **跳转服务**：QPS ≥ 1500（单节点） | 平均延迟 ≤ 100ms
- **系统可用性**：≥ 98%（基于Docker部署）

### 功能性需求
- **短链唯一性**：通过Snowflake ID+布隆过滤器保障

### 非功能性需求
- **安全性**：HTTPS全站加密、SQL注入防护
- **可扩展性**：无状态服务设计，支持水平扩展

---

## 🛠 技术选型对比
| 技术栈          | 候选方案            | 选型理由                                  |
|----------------|-----------------|---------------------------------------|
| **前端**       | Vue             | Vue轻量易上手，生态完善（Vue Router + ElementUI） |
| **后端**       | Spring Boot     | Spring Boot快速开发，整合JPA/Lombok等组件便于开发   |
| **数据库**     | MySQL | MySQL事务性强，配合分库分表方案成熟                  |
| **缓存**       | Redis | Redis支持数据结构更丰富（String/Hash），持久化能力可靠   |
| **消息队列**   | RabbitMQ   | RabbitMQ轻量，适合日志异步处理场景（与Kafka相比延迟更低）   |
| **ID生成器**   | Snowflake  | Snowflake高性能、全局唯一性、有序性和实现简单性等方面表现突出   |
| **容器化**     | Docker          | 镜像轻量化（Alpine基础镜像），快速部署                |

---

## 🏗 架构设计
<img src=".\架构设计图.PNG">

---

## ⚙️ 实现细节
### 关键模块
1. **ID生成服务**
   - 基于Snowflake算法生成12位ID（时间戳+机器ID+序列号）,保证分布式环境下ID唯一性
   - 通过Base62转换将ID长度压缩至6-8字符
2. **冲突解决机制**
   - 布隆过滤器预判ID是否存在，降低数据库查询压力
   - 数据库二次校验解决哈希冲突
   - 最大重试次数控制（默认3次）
3. **性能优化**
   - 多级缓存设计（内存缓存 + Redis缓存）
   - 异步消息处理过期清理
4. **可靠性保障**
   - 自动过期清理防止数据膨胀
   - 异常重试提升服务可用性
   - MySQL分表存储 + 异步归档实现持久化

### **核心逻辑伪码**
```java
public class ShortLinkService {
  // 核心生成方法
  public String generateShortUrl(String originalUrl, long ttl) {
    for (int i=0; i<MAX_RETRY; i++) {
      // 1. 生成分布式ID -> 转Base62编码
      String code = Base62.encode(snowflake.nextId());

      // 2. 布隆过滤器+DB双重校验
      if (!bloomFilter.mightContain(code) || !dbExists(code)) {
        // 3. 持久化存储
        saveToDB(new Entity(code, originalUrl, now+ttl));
        // 4. 设置过期监听
        mq.sendExpireEvent(code, ttl);
        return code;
      }
    }
    throw "400";
  }

  // 核心解析方法
  public String resolveShortCode(String code) {
    // 1. 布隆过滤器快速拦截
    if (!bloomFilter.mightContain(code)) {
      throw "404";
    }

    // 2. 两级缓存查询
    if (cache.has(code)) return cache.get(code);

    // 3. 数据库精确查询
    Entity entity = dbFind(code);
    if (entity == null || expired) throw "404";

    // 4. 刷新缓存
    cache.put(code, entity.url);
    return entity.url;
  }
}
```

---

## 📊 测试结果
### 压测环境
- **机器配置**：2核4G云服务器 × 1
- **测试工具**：JMeter 

| 测试场景            | 平均响应时间 | QPS  | 错误率  |
|-----------------|--------|------|------|
| **短链生成**        | 42ms   | 1000 | 0.8% |
| **短链跳转（缓存命中）**  | 70ms   | 2400 | 0.2% |
| **短链跳转（缓存未命中）** | 162ms  | 500  | 2%   |



---

## ✨ 项目亮点
1. **防击穿设计**：布隆过滤器+Redis空值缓存，防止恶意请求穿透数据库
2. **轻量分布式**：Snowflake ID生成器支持多节点部署
3. **异步解耦**：通过RabbitMQ异步处理统计日志，提升主流程性能

---

## 🚀 后续优化方向
1. **分库分表**：基于短链ID哈希拆分MySQL表
2. **数据统计**：增加后台数据统计功能，统计总访问量、地域分布、设备类型、时间趋势图等
3. **热点缓存**：动态识别高频短链，延长Redis过期时间
4. **安全增强**：支持人机验证（Captcha）、IP黑名单自动封禁
5. **全球化部署**：基于GeoDNS实现多区域就近访问
