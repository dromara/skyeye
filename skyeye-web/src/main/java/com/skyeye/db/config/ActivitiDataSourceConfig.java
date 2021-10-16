/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.db.config;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * activiti工作流数据源配置
 * @author Administrator
 *
 */
@Configuration
@MapperScan(basePackages = {"com.skyeye.activiti.mapper"}, sqlSessionFactoryRef = "activitiSqlSessionFactory")
public class ActivitiDataSourceConfig extends AbstractProcessEngineAutoConfiguration {

	@Bean(name = "activitiDataSource")
	@ConfigurationProperties(prefix = "spring.datasource.activiti")
	public DataSource activitiDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "activitiSqlSessionFactory")
	public SqlSessionFactory baseSqlSessionFactory(@Qualifier("activitiDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		try {
			return bean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Bean(name = "activitiTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("activitiDataSource") DataSource activitiDataSource) {
		return new DataSourceTransactionManager(activitiDataSource);
	}

	public SqlSessionTemplate activitiSqlsessionTemplate(@Qualifier("activitiSqlsessionTemplate") SqlSessionFactory sqlSessionFactory) throws Exception {
		// 使用上面配置的Factory
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean
	public SpringProcessEngineConfiguration springProcessEngineConfiguration(@Qualifier("activitiDataSource") DataSource activitiDataSource) {
		SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
		configuration.setDataSource(activitiDataSource());
		configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		// activiti的任务查询定时任务
		configuration.setJobExecutorActivate(false);
		configuration.setTransactionManager(transactionManager(activitiDataSource));
		return configuration;
	}

}
