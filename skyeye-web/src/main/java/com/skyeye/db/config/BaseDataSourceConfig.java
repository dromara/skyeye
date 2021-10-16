/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.db.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.skyeye.eve.dao", "com.skyeye.dao", "com.skyeye.school.dao"}, sqlSessionFactoryRef = "baseSqlSessionFactory")
public class BaseDataSourceConfig {

	/**
	 * Primary 必须加此注解，不然报错，下一个类则不需要添加，表示这个数据源是默认数据源
	 * ConfigurationProperties(prefix)值必须是application.properteis中对应属性的前缀
	 *
	 * @return
	 */
	@Bean(name="baseDataSource")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.basedata")
	public DataSource baseDataSource(){
		HikariDataSource dataSource = new HikariDataSource();
		// 最小空闲连接，默认值10，小于0或大于maximum-pool-size，都会重置为maximum-pool-size
		dataSource.setMinimumIdle(10);
		// 最大连接数，小于等于0会被重置为默认值10；大于零小于1会被重置为minimum-idle的值
		dataSource.setMaximumPoolSize(100);
		// 连接超时时间：毫秒，小于250毫秒，否则被重置为默认值30秒
		dataSource.setConnectionTimeout(60000);
		// 空闲连接超时时间，默认值600000（10分钟），大于等于max-lifetime且max-lifetime>0，会被重置为0；不等于0且小于10秒，会被重置为10秒
		dataSource.setIdleTimeout(60000);
		// 连接最大存活时间，不等于0且小于30秒，会被重置为默认值30分钟.设置应该比mysql设置的超时时间短,这里设置30分钟
		dataSource.setMaxLifetime(60000);
		return dataSource;
	}
	
	@Primary
    @Bean(name = "baseSqlSessionFactory")
	public SqlSessionFactory baseSqlSessionFactory(@Qualifier("baseDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		// 添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
        	bean.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));
        	return bean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManagerOne(@Qualifier("baseDataSource") DataSource dataSourceOne) {
		return new DataSourceTransactionManager(dataSourceOne);
	}
	
	@Primary
	public SqlSessionTemplate sqlsessionTemplateOne(@Qualifier("sqlsessionTemplateOne") SqlSessionFactory sqlSessionFactory) throws Exception {
		// 使用上面配置的Factory
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
}
