package com.skyeye.skill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName: AiSkillApplication
 * @Description: AI技能独立模块启动类（在 erp 外，写法对齐 adm-ai）
 */
@SpringBootApplication
@MapperScan("com.skyeye.skill.dao")
public class AiSkillApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiSkillApplication.class, args);
    }
}
