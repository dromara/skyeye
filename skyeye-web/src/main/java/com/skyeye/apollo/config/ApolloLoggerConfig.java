/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.apollo.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 *
 * @ClassName: ApolloLoggerConfig
 * @Description: apollo配置信息
 * @author: skyeye云系列--卫志强
 * @date: 2021/9/25 19:10
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Configuration
public class ApolloLoggerConfig {
	
	private static Logger logger = LoggerFactory.getLogger(ApolloLoggerConfig.class);

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
			String strLevel = config.getProperty(key, "info");
			logger.info("{}:{}", key, strLevel);
		}
	}
}
