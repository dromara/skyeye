/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.db.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.yulichang.injector.MPJSqlInjector;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.exception.CustomException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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
    "com.skyeye.eve.*.dao",
    "com.skyeye.*.dao",
    "com.skyeye.*.*.dao"}, sqlSessionFactoryRef = "baseSqlSessionFactory")
public class BaseDataSourceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDataSourceConfig.class);

    protected static Properties databaseTypeMappings = CommonConstants.getDefaultDatabaseTypeMappings();

    @Autowired
    protected ResourceLoader resourceLoader;

    /**
     * Primary 必须加此注解，不然报错，下一个类则不需要添加，表示这个数据源是默认数据源
     * ConfigurationProperties(prefix)值必须是application.properteis中对应属性的前缀
     *
     * @return
     */
    @Bean(name = "baseDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource baseDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * a、SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，没有任何理由对它进行清除或重建。
     * b、使用 SqlSessionFactory 的最佳实践是在应用运行期间不要重复创建多次，多次重建 SqlSessionFactory 被视为一种代码“坏味道（bad smell）”。
     * c、因此 SqlSessionFactory 的最佳作用域是应用作用域。有很多方法可以做到，最简单的就是使用单例模式或者静态单例模式。
     *
     * @param dataSource
     * @return
     */
    @Primary
    @Bean(name = "baseSqlSessionFactory")
    public SqlSessionFactory baseSqlSessionFactory(@Qualifier("baseDataSource") DataSource dataSource) {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();

        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSqlInjector(new MPJSqlInjector());
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig);

        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        String databaseType = this.initDatabaseType(dataSource);
        if (databaseType == null) {
            throw new CustomException("couldn't deduct database type");
        } else {
            try {
                // 添加XML目录
                mybatisSqlSessionFactoryBean.setMapperLocations(resolveMapperLocations());
                mybatisSqlSessionFactoryBean.afterPropertiesSet();
                return mybatisSqlSessionFactoryBean.getObject();
            } catch (Exception var5) {
                throw new CustomException("Could not create sqlSessionFactory", var5);
            }
        }
    }

    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath*:mapper/**/*.xml");
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

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("baseDataSource") DataSource dataSourceOne) {
        return new DataSourceTransactionManager(dataSourceOne);
    }

    @Primary
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("baseSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        // 使用上面配置的Factory
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 初始化
     *
     * @param dataSource
     * @return
     */
    protected String initDatabaseType(@Qualifier("baseDataSource") DataSource dataSource) {
        String databaseType = null;
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String databaseProductName = databaseMetaData.getDatabaseProductName();
            LOGGER.info("database product name: '{}'", databaseProductName);
            databaseType = databaseTypeMappings.getProperty(databaseProductName);
            if (databaseType == null) {
                throw new CustomException("couldn't deduct database type from database product name '" + databaseProductName + "'");
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

}
