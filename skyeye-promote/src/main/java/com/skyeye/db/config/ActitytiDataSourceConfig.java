package com.skyeye.db.config;

import javax.sql.DataSource;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import com.alibaba.druid.pool.DruidDataSource;

/**
 * activiti工作流数据源配置
 * @author Administrator
 *
 */
@Configuration
public class ActitytiDataSourceConfig extends AbstractProcessEngineAutoConfiguration {

	@Bean(name = "activitiDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.activiti")
	public DataSource activitiDataSource() {
		return new DruidDataSource();
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(activitiDataSource());
	}

	@Bean
	public SpringProcessEngineConfiguration springProcessEngineConfiguration() {
		SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
		configuration.setDataSource(activitiDataSource());
		configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		configuration.setJobExecutorActivate(true);
		configuration.setTransactionManager(transactionManager());
		return configuration;
	}

}
