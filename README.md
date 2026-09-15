# StarFlow

StarFlow 是一个面向内容社区场景的后端微服务项目，覆盖账号认证、用户资料、图文/视频笔记、点赞收藏、关注关系、评论互动、全文检索、计数统计与异步数据对齐等核心链路。项目以 Spring Cloud Alibaba 为基础，重点实践服务拆分、缓存治理、消息解耦、分布式 ID、搜索同步和高并发计数等工程能力。

## 项目定位

这个项目不是单体 CRUD 示例，而是按内容社区的真实业务边界拆分为多个服务。网关负责统一入口，认证服务处理登录态，用户、笔记、评论、关系、计数、搜索、KV 正文存储等模块分别承担独立职责；服务之间通过 HTTP Interface RPC、RocketMQ 和共享基础组件协同。

## 技术栈

| 方向 | 选型 |
|------|------|
| 开发语言 | Java 25 |
| 微服务框架 | Spring Boot 4.1.0、Spring Cloud 2025.1.2、Spring Cloud Alibaba 2025.1.0.0 |
| 构建管理 | Maven 多模块 |
| 服务治理 | Nacos 3.1.1 |
| 网关 | Spring Cloud Gateway |
| 数据库 | PostgreSQL 16、MyBatis-Plus 3.5.16 |
| 缓存 | Redis、Caffeine、本地缓存与二级缓存组合 |
| 消息队列 | Apache RocketMQ 5.3.1 |
| 搜索 | Elasticsearch 9.4.3、IK 分词 |
| 正文存储 | ScyllaDB |
| 文件存储 | S3 兼容对象存储、腾讯云 COS |
| 分布式 ID | CoSId |
| 任务调度 | PowerJob |
| 认证授权 | Spring Security、JJWT HS256 |
| 部署 | Docker / Podman Compose |

## 服务划分

| 模块 | 职责 |
|------|------|
| `starflow-gateway` | API 网关、路由转发、统一入口 |
| `starflow-auth` | 登录、注册、验证码、JWT 签发与登出 |
| `starflow-user` | 用户资料、角色权限、用户缓存 |
| `starflow-oss` | 图片、视频等文件上传 |
| `starflow-note` | 笔记发布、详情、点赞、收藏 |
| `starflow-comment` | 评论发布、删除、分页查询和互动 |
| `starflow-user-relation` | 关注、取关、粉丝与关注列表 |
| `starflow-count` | 用户、笔记、互动行为的计数聚合 |
| `starflow-search` | 用户与笔记全文检索 |
| `starflow-kv` | 笔记正文、评论正文等大文本存储 |
| `starflow-distributed-id-generator` | 全局唯一 ID 生成 |
| `starflow-data-align` | 定时补偿、统计校准、ES 同步任务 |
| `starflow-framework` | 公共响应、异常、上下文、RPC、操作日志等基础能力 |

## 架构概览

```text
Client
  |
  v
starflow-gateway
  |-- starflow-auth
  |-- starflow-user
  |-- starflow-oss
  |-- starflow-note
  |-- starflow-comment
  `-- starflow-user-relation

内部能力：
starflow-count / starflow-search / starflow-kv / starflow-distributed-id-generator

异步链路：
RocketMQ 负责互动事件、计数变更、缓存失效、搜索同步等消息流转
```

## 核心设计

- 认证链路使用验证码登录、自动注册、JWT 签发和 Redis 黑名单实现登录态管理。
- 热点读取采用 Caffeine + Redis 的二级缓存结构，通过空值缓存、随机过期时间和延迟双删降低缓存穿透与缓存击穿风险。
- 点赞、收藏、关注等高频互动通过 Redis Roaring Bitmap 和 ZSet 做快速判重与关系集合维护。
- RocketMQ 用于削峰和解耦，计数、搜索、缓存清理等非主流程动作通过异步消息完成。
- 计数服务将用户、笔记、评论等统计口径独立出来，避免业务服务直接承担复杂聚合逻辑。
- PowerJob 负责周期性数据对齐，修正缓存、数据库与搜索索引之间可能出现的短暂不一致。
- 公共模块沉淀统一响应模型、业务异常、用户上下文、RPC 调用透传和操作日志能力。

## 本地启动

先启动项目依赖的中间件：

```bash
podman-compose -f podman-compose.yml up -d
```

首次运行前，需要根据各服务的 `application-dev.yml.example` 创建本地 `application-dev.yml`，并填写数据库、Redis、RocketMQ、Nacos、对象存储等连接信息。

常用构建命令：

```bash
# 构建全部模块
mvn clean package -DskipTests

# 构建单个模块
mvn clean package -pl starflow-gateway -DskipTests

# 启动认证服务
mvn spring-boot:run -pl starflow-auth
```

Elasticsearch 索引初始化：

```bash
cd scripts/es-index
./create-indices.sh
./full_build.sh
```

## 接口入口

| 路径 | 服务 |
|------|------|
| `/auth/**` | 认证服务 |
| `/oss/**` | 文件服务 |
| `/user/**` | 用户服务 |
| `/note/**` | 笔记服务 |
| `/relation/**` | 用户关系服务 |
| `/comment/**` | 评论服务 |

## 文档索引

| 模块 | README | 设计文档 |
|------|--------|----------|
| starflow-framework | [README](starflow-framework/README.md) | [DESIGN](starflow-framework/DESIGN.md) |
| starflow-gateway | [README](starflow-gateway/README.md) | [DESIGN](starflow-gateway/DESIGN.md) |
| starflow-auth | [README](starflow-auth/README.md) | [DESIGN](starflow-auth/DESIGN.md) |
| starflow-user | [README](starflow-user/README.md) | [DESIGN](starflow-user/DESIGN.md) |
| starflow-oss | [README](starflow-oss/README.md) | [DESIGN](starflow-oss/DESIGN.md) |
| starflow-note | [README](starflow-note/README.md) | [DESIGN](starflow-note/starflow-note-biz/DESIGN.md) |
| starflow-comment | [README](starflow-comment/README.md) | [DESIGN](starflow-comment/DESIGN.md) |
| starflow-user-relation | [README](starflow-user-relation/README.md) | [DESIGN](starflow-user-relation/DESIGN.md) |
| starflow-count | [README](starflow-count/README.md) | [DESIGN](starflow-count/DESIGN.md) |
| starflow-search | [README](starflow-search/README.md) | [DESIGN](starflow-search/DESIGN.md) |
| starflow-kv | [README](starflow-kv/README.md) | [DESIGN](starflow-kv/DESIGN.md) |
| starflow-distributed-id-generator | [README](starflow-distributed-id-generator/README.md) | [DESIGN](starflow-distributed-id-generator/DESIGN.md) |
| starflow-data-align | [README](starflow-data-align/README.md) | [DESIGN](starflow-data-align/DESIGN.md) |
