# syntax=docker/dockerfile:1-labs
# ============================================================
# starflow-distributed-id-generator Dockerfile
# Build context: 仓库根目录
# 构建命令: docker build -t starflow-id -f starflow-distributed-id-generator/starflow-distributed-id-generator-biz/Dockerfile .
# ============================================================

FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /build

# 递归复制所有层级的 pom.xml，保留原始目录结构（--parents 防止 BuildKit glob 扁平化覆盖）
COPY --parents **/pom.xml ./

# 阶段 1：安装 framework + 所有 *-api 模块到本地仓库，使阶段 2 解析上游依赖时走 .m2 而非 reactor 源码
COPY starflow-framework/ ./starflow-framework/
COPY starflow-user/starflow-user-api/src ./starflow-user/starflow-user-api/src
COPY starflow-oss/starflow-oss-api/src ./starflow-oss/starflow-oss-api/src
COPY starflow-kv/starflow-kv-api/src ./starflow-kv/starflow-kv-api/src
COPY starflow-distributed-id-generator/starflow-distributed-id-generator-api/src ./starflow-distributed-id-generator/starflow-distributed-id-generator-api/src
COPY starflow-note/starflow-note-api/src ./starflow-note/starflow-note-api/src
COPY starflow-user-relation/starflow-user-relation-api/src ./starflow-user-relation/starflow-user-relation-api/src
COPY starflow-count/starflow-count-api/src ./starflow-count/starflow-count-api/src
COPY starflow-comment/starflow-comment-api/src ./starflow-comment/starflow-comment-api/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn install -DskipTests -B -am -pl starflow-framework/starflow-common,starflow-framework/starflow-spring-boot-starter-biz-operationlog,starflow-framework/starflow-spring-boot-starter-biz-context,starflow-framework/starflow-spring-boot-starter-rpc,starflow-user/starflow-user-api,starflow-oss/starflow-oss-api,starflow-kv/starflow-kv-api,starflow-distributed-id-generator/starflow-distributed-id-generator-api,starflow-note/starflow-note-api,starflow-user-relation/starflow-user-relation-api,starflow-count/starflow-count-api,starflow-comment/starflow-comment-api

# 阶段 2：仅复制目标服务源码并构建。上游依赖已在阶段 1 安装到 .m2。
COPY starflow-distributed-id-generator/starflow-distributed-id-generator-api/src ./starflow-distributed-id-generator/starflow-distributed-id-generator-api/src
COPY starflow-distributed-id-generator/starflow-distributed-id-generator-biz/src ./starflow-distributed-id-generator/starflow-distributed-id-generator-biz/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl starflow-distributed-id-generator/starflow-distributed-id-generator-biz package -DskipTests -B

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=builder /build/starflow-distributed-id-generator/starflow-distributed-id-generator-biz/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
