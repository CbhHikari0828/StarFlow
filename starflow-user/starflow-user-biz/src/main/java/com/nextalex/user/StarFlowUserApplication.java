package com.nextalex.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务启动类.
 *
 * @author NextAlex
 * @date 2026/07/07
 * @since 0.0.1
 */
@SpringBootApplication
@MapperScan("com.nextalex.user.domain.mapper")
public class StarFlowUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarFlowUserApplication.class, args);
    }
}
