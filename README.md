# ShortLinkURL 短链生成器系统

[![License](https://img.shields.io/badge/license-MIT-green)](https://github.com/yourname/shortlinkx/blob/main/LICENSE)
[![Docker Build](https://img.shields.io/docker/cloud/build/yourname/shortlinkx)](https://hub.docker.com/r/yourname/shortlinkx)
[![GitHub stars](https://img.shields.io/github/stars/yourname/shortlinkx)](https://github.com/yourname/shortlinkx/stargazers)

## 📖 项目背景
针对传统长URL分享不便、统计能力缺失等问题，本项目实现高可用短链服务。相比商业方案（如Bitly），本项目**轻量级、可定制**，适合开发者二次开发或学习分布式系统设计。

---

## 🔍 需求分析
### 核心功能
| 模块           | 功能描述                                                                 |
|----------------|--------------------------------------------------------------------------|
| 短链生成       | 支持长URL转短链（6-8字符）、自定义别名、有效期设置（默认30天）            |
| 短链跳转       | 301/302重定向、实时状态检测（是否失效/封禁）                              |
| 访问统计       | 总访问量、地域分布、设备类型、时间趋势图                                  |
| 管理后台       | 短链CRUD、数据看板、用户权限分级（普通用户/管理员）                       |

### 性能指标
- **生成服务**：QPS ≥ 2000（单节点） | 平均延迟 ≤ 20ms
- **跳转服务**：QPS ≥ 5000（单节点） | 平均延迟 ≤ 50ms
- **系统可用性**：≥ 99.9%（基于Docker+K8S多副本部署）

### 功能性需求
- **短链唯一性**：通过Snowflake ID+布隆过滤器保障
- **防滥用机制**：IP限流（100次/分钟）、敏感词过滤
- **数据看板**：支持CSV导出访问日志

### 非功能性需求
- **安全性**：HTTPS全站加密、JWT鉴权、SQL注入防护
- **可扩展性**：无状态服务设计，支持水平扩展
- **监控**：Prometheus采集指标，Grafana可视化

---

## 🛠 技术选型对比
| 技术栈          | 候选方案           | 选型理由                                                                 |
|----------------|--------------------|--------------------------------------------------------------------------|
| **前端**       | React/Angular      | Vue轻量易上手，生态完善（Vue Router + ElementUI）                        |
| **后端**       | Spring Cloud/Go    | Spring Boot快速开发，整合MyBatis-Plus/JWT等组件便捷                      |
| **数据库**     | MySQL/PostgreSQL   | MySQL事务性强，配合分库分表方案成熟                                      |
| **缓存**       | Redis/Memcached    | Redis支持数据结构更丰富（String/Hash），持久化能力可靠                  |
| **消息队列**   | RabbitMQ/Kafka     | RabbitMQ轻量，适合日志异步处理场景（与Kafka相比延迟更低）                |
| **ID生成器**   | Snowflake/UUID     | Snowflake保证分布式ID有序性，空间占用更小（64bit vs 128bit）             |
| **容器化**     | Docker             | 镜像轻量化（Alpine基础镜像），快速部署                                   |

---

## 🏗 架构设计
```plaintext
                                +---------------------+
                                |      Nginx          |
                                | (负载均衡+SSL终结)  |
                                +----------+----------+
                                           |
                  +------------------------+------------------------+
                  |                        |                        |
           +------v------+          +------v------+          +------v------+
           |   Web前端   |          |  API网关    |          | 静态资源    |
           |  (Vue.js)   |          | (Spring Boot)|          | (CDN)      |
           +------+------+          +------+------+          +-------------+
                  |                        |
                  |               +--------v--------+       +------------+
                  |               |  业务服务层      |       | Redis      |
                  |               | (短链生成/跳转)  <-------+ (缓存热点) |
                  |               +--------+---------+       +------------+
                  |                        |
                  |               +--------v---------+      +------------+
                  |               |   数据访问层      |      | MySQL      |
                  |               | (MyBatis-Plus)    <------+ (分库分表) |
                  |               +--------+----------+      +------------+
                  |                        |
                  |               +--------v---------+
                  |               |  异步任务队列     |
                  |               | (RabbitMQ)       |
                  +---------------+------------------+
```

---

## ⚙️ 实现细节
### 关键模块
1. **ID生成服务**
- 基于Snowflake算法生成12位ID（时间戳+机器ID+序列号）
- 使用布隆过滤器预判ID是否存在，降低数据库查询压力
2. **短链跳转优化**
```java
   // 伪码：跳转逻辑（含缓存优化）
   public String redirect(String shortKey) {
   // 1. 布隆过滤器预判（防缓存穿透）
   if (!bloomFilter.mightContain(shortKey)) return "404";

   // 2. 查询Redis缓存
   String cacheValue = redis.get(shortKey);
   if (cacheValue != null) return cacheValue;

   // 3. 查询数据库
   Link link = linkMapper.selectById(shortKey);
   if (link == null || link.isExpired()) return "404";

   // 4. 异步记录访问日志（通过RabbitMQ）
   mq.send(new AccessLog(shortKey, getClientIP()));

   // 5. 回填缓存
   redis.setex(shortKey, 3600, link.getOriginalUrl());
   return link.getOriginalUrl();
   }
```
3. **统计异步处理**
- RabbitMQ消费者批量入库，减少数据库写入压力
- 使用HyperLogLog统计UV，String自增统计PV


---

## 📊 测试结果
### 压测环境
- **机器配置**：4核8G云服务器 × 3（分别部署API/DB/缓存）
  - **测试工具**：JMeter 500并发线程

| 测试场景            | 平均响应时间 | QPS  | 错误率   |
|-----------------|--------|------|-------|
| **短链生成**        | 18ms   | 2350 | 0%    |
| **短链跳转（缓存命中）**  | 32ms   | 4800 | 0%    |
| **短链跳转（缓存未命中）** | 56ms   | 1200 | 0.20% |



---

## ✨ 项目亮点
1. **防击穿设计**：布隆过滤器+Redis空值缓存，防止恶意请求穿透数据库
2. **轻量分布式**：Snowflake ID生成器支持多节点部署
3. **异步解耦**：通过RabbitMQ异步处理统计日志，提升主流程性能
4. **监控一体化**：集成Prometheus+Grafana，实时监控系统健康状态


---

## 🚀 后续优化方向
1. **分库分表**：基于短链ID哈希拆分MySQL表
2. **热点缓存**：动态识别高频短链，延长Redis过期时间
3. **安全增强**：支持人机验证（Captcha）、IP黑名单自动封禁
4. **全球化部署**：基于GeoDNS实现多区域就近访问


---

## 💻 快速启动
```bash
# 1. 克隆项目
git clone https://github.com/yourname/shortlinkx.git

# 2. 启动服务（依赖Docker）
cd deploy
docker-compose up -d

# 3. 访问
前端：http://localhost:8080
API文档：http://localhost:8081/swagger-ui.html
```
**欢迎贡献代码或提出建议！ 🌟**


---

### 关键设计说明
1. **布隆过滤器应用**：在跳转查询前增加预判层，避免无效请求穿透到数据库
2. **Snowflake参数配置**：根据机器ID动态调整（K8S环境下可通过StatefulSet分配有序ID）
3. **缓存策略**：采用Cache-Aside模式，设置合理的TTL避免冷启动问题