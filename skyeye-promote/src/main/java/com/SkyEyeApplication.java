package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAutoConfiguration(exclude={
	org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class, 
	org.activiti.spring.boot.SecurityAutoConfiguration.class
})
@EnableTransactionManagement
@EnableScheduling
public class SkyEyeApplication {
	
	public static void main(String[] args) {
		System.setProperty("spring.devtools.restart.enabled", "false");
		SpringApplication.run(SkyEyeApplication.class, args);
	}

}
