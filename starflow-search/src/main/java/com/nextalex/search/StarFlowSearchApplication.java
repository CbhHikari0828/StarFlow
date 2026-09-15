package com.nextalex.search;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 搜索服务启动类.
 *
 * <p>基于 Elasticsearch 提供笔记、用户的分词搜索能力，并消费笔记/用户变更事件做实时增量索引，
 * 注册到 Nacos，端口 8089。
 *
 * @author NextAlex
 * @date 2026/07/13
 * @since 0.0.1
 */
@SpringBootApplication
@MapperScan("com.nextalex.search.domain.mapper")
public class StarFlowSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarFlowSearchApplication.class, args);
    }
}
