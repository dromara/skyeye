package com.skyeye.knowledge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI知识库独立模块启动类
 */
@SpringBootApplication
@MapperScan("com.skyeye.knowledge.dao")
public class AiKnowledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiKnowledgeApplication.class, args);
    }
}
