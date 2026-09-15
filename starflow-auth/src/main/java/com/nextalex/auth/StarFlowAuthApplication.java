package com.nextalex.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class StarFlowAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarFlowAuthApplication.class, args);
    }
}
