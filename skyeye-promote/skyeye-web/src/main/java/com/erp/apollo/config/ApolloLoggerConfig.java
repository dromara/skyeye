/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
package com.erp.apollo.config;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Configuration;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;

@Configuration
public class ApolloLoggerConfig {
	
	private static Logger logger = LoggerFactory.getLogger(ApolloLoggerConfig.class);
	private static final String LOGGER_TAG = "logging.level.";

	@Autowired
	private LoggingSystem loggingSystem;

	@ApolloConfig
	private Config config;

	@ApolloConfigChangeListener
	private void configChangeListter(ConfigChangeEvent changeEvent) {
		refreshLoggingLevels();
	}

	@PostConstruct
	private void refreshLoggingLevels() {
		Set<String> keyNames = config.getPropertyNames();
		for (String key : keyNames) {
			if (StringUtils.containsIgnoreCase(key, LOGGER_TAG)) {
				String strLevel = config.getProperty(key, "info");
				LogLevel level = LogLevel.valueOf(strLevel.toUpperCase());
				loggingSystem.setLogLevel(key.replace(LOGGER_TAG, ""), level);
				logger.info("{}:{}", key, strLevel);
			}
		}
	}
}
