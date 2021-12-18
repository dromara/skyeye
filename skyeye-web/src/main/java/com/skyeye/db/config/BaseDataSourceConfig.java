/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.db.config;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.modeler.properties.FlowableModelerAppProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = {
		"com.skyeye.eve.dao",
		"com.skyeye.dao",
		"com.skyeye.school.dao",
		"com.skyeye.activiti.mapper"}, sqlSessionFactoryRef = "baseSqlSessionFactory")
public class BaseDataSourceConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataSourceConfig.class);

	protected static Properties databaseTypeMappings = getDefaultDatabaseTypeMappings();

	protected static final String LIQUIBASE_CHANGELOG_PREFIX = "ACT_ADM_";

	public static Properties getDefaultDatabaseTypeMappings() {
		Properties databaseTypeMappings = new Properties();
		databaseTypeMappings.setProperty("H2", "h2");
		databaseTypeMappings.setProperty("HSQL Database Engine", "hsql");
		databaseTypeMappings.setProperty("MySQL", "mysql");
		databaseTypeMappings.setProperty("Oracle", "oracle");
		databaseTypeMappings.setProperty("PostgreSQL", "postgres");
		databaseTypeMappings.setProperty("Microsoft SQL Server", "mssql");
		databaseTypeMappings.setProperty("db2", "db2");
		databaseTypeMappings.setProperty("DB2", "db2");
		databaseTypeMappings.setProperty("DB2/NT", "db2");
		databaseTypeMappings.setProperty("DB2/NT64", "db2");
		databaseTypeMappings.setProperty("DB2 UDP", "db2");
		databaseTypeMappings.setProperty("DB2/LINUX", "db2");
		databaseTypeMappings.setProperty("DB2/LINUX390", "db2");
		databaseTypeMappings.setProperty("DB2/LINUXX8664", "db2");
		databaseTypeMappings.setProperty("DB2/LINUXZ64", "db2");
		databaseTypeMappings.setProperty("DB2/LINUXPPC64", "db2");
		databaseTypeMappings.setProperty("DB2/400 SQL", "db2");
		databaseTypeMappings.setProperty("DB2/6000", "db2");
		databaseTypeMappings.setProperty("DB2 UDB iSeries", "db2");
		databaseTypeMappings.setProperty("DB2/AIX64", "db2");
		databaseTypeMappings.setProperty("DB2/HPUX", "db2");
		databaseTypeMappings.setProperty("DB2/HP64", "db2");
		databaseTypeMappings.setProperty("DB2/SUN", "db2");
		databaseTypeMappings.setProperty("DB2/SUN64", "db2");
		databaseTypeMappings.setProperty("DB2/PTX", "db2");
		databaseTypeMappings.setProperty("DB2/2", "db2");
		databaseTypeMappings.setProperty("DB2 UDB AS400", "db2");
		databaseTypeMappings.setProperty("DM DBMS", "dm");
		return databaseTypeMappings;
	}

	@Autowired
	protected FlowableModelerAppProperties modelerAppProperties;

	@Autowired
	protected ResourceLoader resourceLoader;

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
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();

		sqlSessionFactoryBean.setDataSource(dataSource);
		String databaseType = this.initDatabaseType(dataSource);
		if (databaseType == null) {
			throw new FlowableException("couldn't deduct database type");
		} else {
			try {
				// 添加XML目录
				sqlSessionFactoryBean.setMapperLocations(resolveMapperLocations());
				Properties properties = new Properties();
				properties.put("prefix", this.modelerAppProperties.getDataSourcePrefix());
				properties.put("blobType", "BLOB");
				properties.put("boolValue", "1");
				properties.load(this.getClass().getClassLoader().getResourceAsStream("org/flowable/db/properties/" + databaseType + ".properties"));
				sqlSessionFactoryBean.setConfigurationProperties(properties);
				sqlSessionFactoryBean.afterPropertiesSet();
				return sqlSessionFactoryBean.getObject();
			} catch (Exception var5) {
				throw new FlowableException("Could not create sqlSessionFactory", var5);
			}
		}
	}

	public Resource[] resolveMapperLocations() {
		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
		List<String> mapperLocations = new ArrayList<>();
		mapperLocations.add("classpath*:mapper/**/*.xml");
		mapperLocations.add("classpath:/META-INF/modeler-mybatis-mappings/*.xml");
		List<Resource> resources = new ArrayList();
		if (!CollectionUtils.isEmpty(mapperLocations)) {
			for (String mapperLocation : mapperLocations) {
				try {
					Resource[] mappers = resourceResolver.getResources(mapperLocation);
					resources.addAll(Arrays.asList(mappers));
				} catch (IOException e) {
					LOGGER.error("Get myBatis resources happened exception", e);
				}
			}
		}
		return resources.toArray(new Resource[resources.size()]);
	}

	@Bean
	public Liquibase liquibase(@Qualifier("baseDataSource") DataSource dataSource) {
		LOGGER.info("Configuring Liquibase");
		Liquibase liquibase = null;
		try {
			DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
			database.setDatabaseChangeLogTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogTableName());
			database.setDatabaseChangeLogLockTableName(LIQUIBASE_CHANGELOG_PREFIX + database.getDatabaseChangeLogLockTableName());

			liquibase = new Liquibase("META-INF/liquibase/flowable-modeler-app-db-changelog.xml", new ClassLoaderResourceAccessor(), database);
			liquibase.update("flowable");
			return liquibase;
		} catch (Exception var9) {
			throw new InternalServerErrorException("Error creating liquibase database", var9);
		} finally {
			this.closeDatabase(liquibase);
		}
	}

	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("baseDataSource") DataSource dataSourceOne) {
		return new DataSourceTransactionManager(dataSourceOne);
	}
	
	@Primary
	public SqlSessionTemplate sqlsessionTemplateOne(@Qualifier("sqlsessionTemplateOne") SqlSessionFactory sqlSessionFactory) throws Exception {
		// 使用上面配置的Factory
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	/**
	 * 初始化
	 *
	 * @param dataSource
	 * @return
	 */
	protected String initDatabaseType(@Qualifier("activitiDataSource") DataSource dataSource) {
		String databaseType = null;
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			DatabaseMetaData databaseMetaData = connection.getMetaData();
			String databaseProductName = databaseMetaData.getDatabaseProductName();
			LOGGER.info("database product name: '{}'", databaseProductName);
			databaseType = databaseTypeMappings.getProperty(databaseProductName);
			if (databaseType == null) {
				throw new FlowableException("couldn't deduct database type from database product name '" + databaseProductName + "'");
			}

			LOGGER.info("using database type: {}", databaseType);
		} catch (SQLException var14) {
			LOGGER.error("Exception while initializing Database connection", var14);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException var13) {
				LOGGER.error("Exception while closing the Database connection", var13);
			}

		}
		databaseType = ("dm".equals(databaseType)) ? "oracle" : databaseType;
		return databaseType;
	}

	private void closeDatabase(Liquibase liquibase) {
		if (liquibase != null) {
			Database database = liquibase.getDatabase();
			if (database != null) {
				try {
					database.close();
				} catch (DatabaseException var4) {
					LOGGER.warn("Error closing database", var4);
				}
			}
		}
	}
	
}
