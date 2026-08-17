/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.database.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeTeamAuthServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.database.classenum.AutoDataBaseAuthEnum;
import com.skyeye.database.dao.AutoDataBaseDao;
import com.skyeye.database.entity.AutoDataBase;
import com.skyeye.database.service.AutoDataBaseService;
import com.skyeye.sql.entity.AutoDataSource;
import com.skyeye.util.AutoConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AutoDataBaseServiceImpl
 * @Description: 数据库管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "数据库管理", groupName = "数据库管理", teamAuth = true)
public class AutoDataBaseServiceImpl extends SkyeyeTeamAuthServiceImpl<AutoDataBaseDao, AutoDataBase> implements AutoDataBaseService {

    private static Logger logger = LoggerFactory.getLogger(AutoDataBaseServiceImpl.class);

    @Override
    public Class getAuthEnumClass() {
        return AutoDataBaseAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(AutoDataBaseAuthEnum.ADD.getKey(), AutoDataBaseAuthEnum.EDIT.getKey(), AutoDataBaseAuthEnum.DELETE.getKey());
    }

    @Override
    protected QueryWrapper<AutoDataBase> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<AutoDataBase> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDataBase::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDataBase::getObjectKey), commonPageInfo.getObjectKey());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        beans.forEach(bean -> {
            String driverClass = bean.get("driverClass").toString();
            String poolClass = bean.get("poolClass").toString();
            bean.put("dataType", AutoConstants.DataBaseMation.getTypeByDricerClass(driverClass));
            bean.put("poolClassName", AutoConstants.PoolMation.getTitleByPoolClass(poolClass));
        });
        return beans;
    }

    @Override
    public void validatorEntity(AutoDataBase entity) {
        super.validatorEntity(entity);
        entity.setDriverClass(AutoConstants.DataBaseMation.getDricerClassByType(entity.getDataType()));
        entity.setQueryerClass(AutoConstants.DataBaseMation.getQueryerClassByType(entity.getDataType()));
        entity.setPoolClass(AutoConstants.PoolMation.getPoolClassByType(entity.getPoolClassType()));
    }

    @Override
    public AutoDataBase selectById(String id) {
        AutoDataBase dataBase = super.selectById(id);
        dataBase.setDataType(AutoConstants.DataBaseMation.getTypeByDricerClass(dataBase.getDriverClass()));
        dataBase.setPoolClassType(AutoConstants.PoolMation.getTypeByPoolClass(dataBase.getPoolClass()));
        return dataBase;
    }

    /**
     * 获取数据库对象
     *
     * @param dataBaseId 数据库id
     * @return
     */
    @Override
    public AutoDataSource getReportDataSource(String dataBaseId) {
        // 获取数据源信息
        AutoDataBase dataBase = selectById(dataBaseId);
        Map<String, Object> options = new HashMap<>();
        if (CollectionUtil.isNotEmpty(dataBase.getOptions())) {
            dataBase.getOptions().stream().forEach(bean -> {
                options.put(bean.get("configurationItem").toString(), bean.get("configurationValue").toString());
            });
        }
        return new AutoDataSource(
            dataBaseId,
            dataBase.getDriverClass(),
            dataBase.getJdbcUrl(), dataBase.getUser(), dataBase.getPassword(),
            dataBase.getQueryerClass(),
            dataBase.getPoolClass(),
            options);
    }

    @Override
    public void queryAllAutoDataBaseList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        QueryWrapper<AutoDataBase> queryWrapper = new QueryWrapper();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDataBase::getObjectKey), objectKey);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AutoDataBase::getObjectId), objectId);
        List<AutoDataBase> result = list(queryWrapper);

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void testAutoDbConnection(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String driverClass = params.get("driverClass").toString();
        String url = params.get("url").toString();
        String user = params.get("user").toString();
        String pass = params.containsKey("pass") ? params.get("pass").toString() : "";
        connectionDataBase(driverClass, url, user, pass, outputObject);
    }

    /**
     * 连接数据源
     *
     * @param driverClass  数据源驱动类
     * @param url          数据源连接字符串
     * @param user         用户名
     * @param password     密码
     * @param outputObject 出参以及提示信息的返回值对象
     * @return
     */
    private boolean connectionDataBase(final String driverClass, final String url, final String user, final String password, OutputObject outputObject) {
        Connection conn = null;
        try {
            Class.forName(driverClass);
            conn = DriverManager.getConnection(url, user, password);
            return true;
        } catch (final Exception e) {
            if (outputObject != null) {
                outputObject.setreturnMessage(e.getMessage());
            }
            return false;
        } finally {
            this.releaseConnection(conn);
        }
    }

    /**
     * 释放数据源
     *
     * @param conn
     */
    private void releaseConnection(final Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (final SQLException ex) {
                logger.warn("测试数据库连接后释放资源失败", ex);
            }
        }
    }

    @Override
    public void queryAutoDataBaseTypeList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = AutoConstants.DataBaseMation.getDataBaseMationList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public void queryAutoPoolTypeList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = AutoConstants.PoolMation.getPoolMationList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}
