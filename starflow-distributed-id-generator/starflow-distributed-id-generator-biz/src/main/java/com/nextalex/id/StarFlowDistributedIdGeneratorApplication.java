package com.nextalex.id;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 分布式 ID 生成服务启动类.
 *
 * @author NextAlex
 * @date 2026/07/08
 * @since 0.0.1
 */
@SpringBootApplication
public class StarFlowDistributedIdGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarFlowDistributedIdGeneratorApplication.class, args);
    }
}
