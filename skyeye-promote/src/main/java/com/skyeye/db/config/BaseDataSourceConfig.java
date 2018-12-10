package com.skyeye.db.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Configuration
@MapperScan(basePackages = {"com.skyeye.eve.dao"}, sqlSessionTemplateRef = "baseSqlSessionTemplate")
public class BaseDataSourceConfig {
	
	@Bean(name="baseDataSource")
	@Primary//必须加此注解，不然报错，下一个类则不需要添加
	@ConfigurationProperties(prefix = "spring.datasource") // prefix值必须是application.properteis中对应属性的前缀
	public DataSource baseDataSource(){
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	public SqlSessionFactory baseSqlSessionFactory(@Qualifier("baseDataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(dataSource);
		//添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
        	bean.setMapperLocations(resolver.getResources("classpath*:dbmapper/*.xml"));
        	bean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
        	return bean.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Bean
	public SqlSessionTemplate baseSqlSessionTemplate(@Qualifier("baseSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory); // 使用上面配置的Factory
		return template;
	}
	
}
