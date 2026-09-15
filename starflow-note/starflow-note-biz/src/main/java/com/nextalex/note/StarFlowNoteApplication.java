package com.nextalex.note;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 笔记服务启动类.
 *
 * @author NextAlex
 * @date 2026/07/09
 * @since 0.0.1
 */
@SpringBootApplication
@MapperScan("com.nextalex.note.domain.mapper")
public class StarFlowNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarFlowNoteApplication.class, args);
    }
}
