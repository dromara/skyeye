/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.attr.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.dao.AttrDefinitionDao;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.entity.AttrDefinitionCustom;
import com.skyeye.attr.service.AttrDefinitionCustomService;
import com.skyeye.attr.service.AttrDefinitionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.AttrModelType;
import com.skyeye.common.enumeration.ServiceBeanType;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dsform.entity.DsFormPage;
import com.skyeye.dsform.service.DsFormPageContentService;
import com.skyeye.dsform.service.DsFormPageService;
import com.skyeye.exception.CustomException;
import com.skyeye.server.entity.ServiceBean;
import com.skyeye.server.service.ServiceBeanService;
import com.skyeye.table.entity.TableFieldInfo;
import com.skyeye.table.sdk.service.TableApiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AttrDefinitionServiceImpl
 * @Description: 服务类属性管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/18 13:11
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "属性管理", groupName = "系统公共模块", tenant = TenantEnum.NO_ISOLATION)
public class AttrDefinitionServiceImpl extends SkyeyeBusinessServiceImpl<AttrDefinitionDao, AttrDefinition> implements AttrDefinitionService {

    @Autowired
    private ServiceBeanService serviceBeanService;

    @Autowired
    private AttrDefinitionCustomService attrDefinitionCustomService;

    @Autowired
    private DsFormPageService dsFormPageService;

    @Autowired
    private DsFormPageContentService dsFormPageContentService;

    @Autowired
    private TableApiService tableApiService;

    @Override
    protected void validatorEntity(AttrDefinition entity) {
        if (StrUtil.equals(CommonConstants.TENANT_ID_FIELD, entity.getDbFieldName())) {
            throw new CustomException("租户字段默认存在，不可新增/编辑。");
        }
        QueryWrapper<AttrDefinition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), entity.getAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), entity.getClassName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAttrKey), entity.getAttrKey());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        AttrDefinition checkAttrDefinition = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(checkAttrDefinition)) {
            throw new CustomException("该属性键已存在.");
        }

        if (entity.getType() == ServiceBeanType.VIRTUAL_MODEL.getKey()) {
            if (StrUtil.isEmpty(entity.getApplicationName()) || StrUtil.isEmpty(entity.getTableName())
                || StrUtil.isEmpty(entity.getDbFieldName()) || StrUtil.isEmpty(entity.getFieldType())
                || entity.getFieldLength() == null || entity.getDecimalPlaces() == null) {
                throw new CustomException("虚拟模型属性缺少必要字段.");
            }
        }

        entity.setWhetherInputParams(WhetherEnum.ENABLE_USING.getKey());
        entity.setModelAttribute(WhetherEnum.DISABLE_USING.getKey());
        // 虚拟模型属性无实体反射信息，未传时默认按简单类型处理
        if (entity.getAttrModelType() == null) {
            entity.setAttrModelType(AttrModelType.SCALAR.getKey());
        }
        entity.setCreateTime(DateUtil.getTimeAndToString());
        entity.setLastUpdateTime(DateUtil.getTimeAndToString());
    }

    @Override
    protected void createPostpose(AttrDefinition entity, String userId) {
        if (entity.getType() == ServiceBeanType.VIRTUAL_MODEL.getKey()) {
            // 创建字段
            TableFieldInfo fieldInfo = new TableFieldInfo();
            fieldInfo.setTableName(entity.getTableName());
            fieldInfo.setFieldName(entity.getDbFieldName());
            fieldInfo.setFieldComment(entity.getName());
            fieldInfo.setFieldType(entity.getFieldType());
            fieldInfo.setFieldLength(entity.getFieldLength());
            fieldInfo.setDecimalPlaces(entity.getDecimalPlaces());
            fieldInfo.setDefaultValue(entity.getDbDefaultValue());
            fieldInfo.setIsPrimaryKey(entity.getIsPrimaryKey());
            fieldInfo.setIsNotNull(entity.getIsPrimaryKey() == WhetherEnum.ENABLE_USING.getKey() ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey());
            tableApiService.addField(entity.getApplicationName(), fieldInfo);
        }
    }

    @Override
    protected void updatePostpose(AttrDefinition entity, String userId) {
        if (entity.getType() == ServiceBeanType.VIRTUAL_MODEL.getKey()) {
            // 修改字段
            TableFieldInfo fieldInfo = new TableFieldInfo();
            fieldInfo.setTableName(entity.getTableName());
            fieldInfo.setFieldName(entity.getDbFieldName());
            fieldInfo.setFieldComment(entity.getName());
            fieldInfo.setFieldType(entity.getFieldType());
            fieldInfo.setFieldLength(entity.getFieldLength());
            fieldInfo.setDecimalPlaces(entity.getDecimalPlaces());
            fieldInfo.setDefaultValue(entity.getDbDefaultValue());
            fieldInfo.setIsPrimaryKey(entity.getIsPrimaryKey());
            fieldInfo.setIsNotNull(entity.getIsPrimaryKey() == WhetherEnum.ENABLE_USING.getKey() ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey());
            tableApiService.modifyField(entity.getApplicationName(), fieldInfo);
        }
    }

    @Override
    protected void deletePreExecution(AttrDefinition entity) {
        if (entity.getModelAttribute() == WhetherEnum.ENABLE_USING.getKey()) {
            throw new CustomException("模型属性不能删除");
        }
    }

    @Override
    protected void deletePostpose(AttrDefinition entity) {
        List<DsFormPage> dsFormPageList = dsFormPageService.queryDsFormPageList(entity.getAppId(), entity.getClassName());
        if (entity.getType() == ServiceBeanType.VIRTUAL_MODEL.getKey()) {
            ServiceBean serviceBean = serviceBeanService.queryServiceClass(entity.getAppId(), entity.getClassName());
            tableApiService.dropField(serviceBean.getSpringApplicationName(), serviceBean.getTableName(), entity.getDbFieldName());
        }
        if (CollectionUtil.isEmpty(dsFormPageList)) {
            return;
        }
        List<String> ids = dsFormPageList.stream().map(DsFormPage::getId).collect(Collectors.toList());
        // 删除布局关联的属性数据
        dsFormPageContentService.deleteDsFormContent(ids, entity.getAttrKey());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void saveBarchAttrDefinition(String appId, List<AttrDefinition> attrDefinitionList) {
        // 获取数据库中的数据
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        // 必须是模型属性
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getModelAttribute), WhetherEnum.ENABLE_USING.getKey());
        // 必须是物理模型
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getType), ServiceBeanType.PHYSICAL_MODEL.getKey());
        List<AttrDefinition> oldList = super.list(wrapper);
        List<String> oldKeys = oldList.stream().map(bean -> getKey(bean)).collect(Collectors.toList());

        List<String> newKeys = attrDefinitionList.stream().map(bean -> getKey(bean)).collect(Collectors.toList());

        // (旧数据 - 新数据) 从数据库删除
        List<AttrDefinition> deleteBeans = oldList.stream()
            .filter(item -> !newKeys.contains(getKey(item))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteBeans)) {
            List<String> ids = deleteBeans.stream().map(AttrDefinition::getId).collect(Collectors.toList());
            deleteById(ids);
        }

        String currentTime = DateUtil.getTimeAndToString();

        // (新数据 - 旧数据) 添加到数据库
        List<AttrDefinition> addBeans = attrDefinitionList.stream()
            .filter(item -> !oldKeys.contains(getKey(item))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(addBeans)) {
            addBeans.forEach(attrDefinition -> {
                attrDefinition.setAppId(appId);
                attrDefinition.setModelAttribute(WhetherEnum.ENABLE_USING.getKey());
                attrDefinition.setCreateTime(currentTime);
                attrDefinition.setLastUpdateTime(currentTime);
            });
            // 新增模型属性
            createEntity(addBeans, StrUtil.EMPTY);
        }

        // 新数据与旧数据取交集 编辑
        List<AttrDefinition> editBeans = oldList.stream()
            .filter(item -> newKeys.contains(getKey(item))).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(editBeans)) {
            Map<String, AttrDefinition> collect = attrDefinitionList.stream().collect(Collectors.toMap(bean -> getKey(bean), item -> item));
            if (CollectionUtil.isEmpty(collect)) {
                return;
            }
            editBeans.forEach(bean -> {
                String key = getKey(bean);
                AttrDefinition attrDefinition = collect.get(key);
                if (attrDefinition == null) {
                    return;
                }
                bean.setRemark(attrDefinition.getRemark());
                bean.setRequired(attrDefinition.getRequired());
                bean.setWhetherInputParams(attrDefinition.getWhetherInputParams());
                bean.setEnumClassStr(attrDefinition.getEnumClassStr());
                bean.setLastUpdateTime(currentTime);
                bean.setIsUniqueField(attrDefinition.getIsUniqueField());
                bean.setIsFuzzyLike(attrDefinition.getIsFuzzyLike());

                bean.setDbFieldName(attrDefinition.getDbFieldName());
                bean.setFieldType(attrDefinition.getFieldType());
                bean.setFieldLength(attrDefinition.getFieldLength());
                bean.setDecimalPlaces(attrDefinition.getDecimalPlaces());
                bean.setDbDefaultValue(attrDefinition.getDbDefaultValue());
                bean.setIsPrimaryKey(attrDefinition.getIsPrimaryKey());
                bean.setAttrModelType(attrDefinition.getAttrModelType());
            });
            updateEntity(editBeans, StrUtil.EMPTY);
        }
    }

    @Override
    public void saveBarchAttrDefinition(List<AttrDefinition> attrDefinitionList) {
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return;
        }
        String currentTime = DateUtil.getTimeAndToString();
        attrDefinitionList.forEach(attrDefinition -> {
            attrDefinition.setModelAttribute(WhetherEnum.DISABLE_USING.getKey());
            attrDefinition.setCreateTime(currentTime);
            attrDefinition.setLastUpdateTime(currentTime);
        });
        // 新增模型属性
        createEntity(attrDefinitionList, StrUtil.EMPTY);
    }

    private String getKey(AttrDefinition attrDefinition) {
        return String.format(Locale.ROOT, "%s:%s:%s:%s:%s:%s", attrDefinition.getClassName(), attrDefinition.getAttrKey(), attrDefinition.getAttrType(),
            attrDefinition.getDbFieldName(), attrDefinition.getIsFuzzyLike(), attrDefinition.getAttrModelType());
    }

    @Override
    public void queryAttrDefinitionList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();
        List<AttrDefinition> attrDefinitionList = getAttrDefinitions(appId, className);
        setCustomDefinition(appId, className, attrDefinitionList);

        // 批量获取子属性
        setChildAttrDefinitionsBatch(appId, attrDefinitionList);

        attrDefinitionList = attrDefinitionList.stream()
            .sorted(Comparator.comparing(AttrDefinition::getWhetherInputParams, Comparator.reverseOrder())).collect(Collectors.toList());
        outputObject.setBeans(attrDefinitionList);
        outputObject.settotal(attrDefinitionList.size());
    }

    private void setCustomDefinition(String appId, String className, List<AttrDefinition> attrDefinitionList) {
        // 获取自定义属性id
        List<String> attrKey = attrDefinitionList.stream().map(AttrDefinition::getAttrKey).collect(Collectors.toList());
        Map<String, AttrDefinitionCustom> attrDefinitionCustomMap = attrDefinitionCustomService.queryAttrDefinitionCustomMap(appId, className, attrKey);
        attrDefinitionList.forEach(attrDefinition -> {
            AttrDefinitionCustom attrDefinitionCustom = attrDefinitionCustomMap.get(attrDefinition.getAttrKey());
            attrDefinition.setAttrDefinitionCustom(attrDefinitionCustom);
        });
    }

    /**
     * 批量获取子属性（性能优化版本）
     *
     * @param appId              应用ID
     * @param attrDefinitionList 属性定义列表
     */
    private void setChildAttrDefinitionsBatch(String appId, List<AttrDefinition> attrDefinitionList) {
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return;
        }

        // 1. 收集所有唯一的attrType（过滤空值）
        List<String> attrTypeList = attrDefinitionList.stream()
            .map(AttrDefinition::getAttrType)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(attrTypeList)) {
            return;
        }

        // 2. 批量查询ServiceBean（通过entityClassName）
        Map<String, ServiceBean> serviceBeanMap = serviceBeanService.getByEntityClassName(appId, attrTypeList);
        if (CollectionUtil.isEmpty(serviceBeanMap)) {
            return;
        }

        // 3. 收集所有需要查询子属性的className（去重）
        List<String> childClassNameList = serviceBeanMap.values().stream()
            .map(ServiceBean::getClassName)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());

        if (CollectionUtil.isEmpty(childClassNameList)) {
            return;
        }

        // 4. 批量查询所有子属性（按className分组）
        Map<String, List<AttrDefinition>> childAttrDefinitionMap = getAttrDefinitions(appId, childClassNameList);
        for (String childClassName : childClassNameList) {
            List<AttrDefinition> childAttrDefinitions = childAttrDefinitionMap.get(childClassName);
            if (CollectionUtil.isNotEmpty(childAttrDefinitions)) {
                // 设置自定义属性
                setCustomDefinition(appId, childClassName, childAttrDefinitions);
            }
        }

        // 5. 按关联关系设置子属性到对应的属性上
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            if (StrUtil.isBlank(attrDefinition.getAttrType())) {
                continue;
            }

            ServiceBean service = serviceBeanMap.get(attrDefinition.getAttrType());
            if (service == null) {
                continue;
            }

            List<AttrDefinition> childAttrDefinitions = childAttrDefinitionMap.get(service.getClassName());
            if (CollectionUtil.isNotEmpty(childAttrDefinitions)) {
                attrDefinition.setChildAttrDefinitions(childAttrDefinitions);
            }
        }
    }

    private List<AttrDefinition> getAttrDefinitions(String appId, String className) {
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), className);
        List<AttrDefinition> attrDefinitionList = list(wrapper);
        return attrDefinitionList;
    }

    private Map<String, List<AttrDefinition>> getAttrDefinitions(String appId, List<String> classNames) {
        if (CollectionUtil.isEmpty(classNames)) {
            return Collections.emptyMap();
        }
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        wrapper.in(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), classNames);
        List<AttrDefinition> attrDefinitionList = list(wrapper);
        return attrDefinitionList.stream().collect(Collectors.groupingBy(AttrDefinition::getClassName));
    }

    @Override
    public void queryChildAttrDefinitionList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String attrKey = params.get("attrKey").toString();
        String appId = params.get("appId").toString();
        List<AttrDefinition> attrDefinitionList = queryChildAttrDefinitionList(appId, className, attrKey);
        outputObject.setBeans(attrDefinitionList);
        outputObject.settotal(attrDefinitionList.size());
    }

    @Override
    public List<AttrDefinition> queryChildAttrDefinitionList(String appId, String className, String attrKey) {
        if (StrUtil.isBlank(appId) || StrUtil.isBlank(className) || StrUtil.isBlank(attrKey)) {
            return new ArrayList<>();
        }
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), className);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAttrKey), attrKey);
        AttrDefinition attrDefinition = getOne(wrapper);
        if (attrDefinition == null || StrUtil.isBlank(attrDefinition.getAttrType())) {
            return new ArrayList<>();
        }
        // 复用批量逻辑，单条场景也走同一路径
        fillChildAttrDefinitions(appId, CollectionUtil.newArrayList(attrDefinition));
        return attrDefinition.getChildAttrDefinitions() == null
            ? new ArrayList<>() : attrDefinition.getChildAttrDefinitions();
    }

    @Override
    public void fillChildAttrDefinitions(String appId, List<AttrDefinition> attrDefinitionList) {
        setChildAttrDefinitionsBatch(appId, attrDefinitionList);
    }

    /**
     * 批量获取业务对象指定的属性信息
     *
     * @param className
     * @param attrKey
     * @return
     */
    @Override
    public List<AttrDefinition> queryAttrDefinitionList(String appId, String className, List<String> attrKey) {
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), className);
        wrapper.in(MybatisPlusUtil.toColumns(AttrDefinition::getAttrKey), attrKey);
        List<AttrDefinition> attrDefinitionList = list(wrapper);
        setCustomDefinition(appId, className, attrDefinitionList);
        return attrDefinitionList;
    }

    @Override
    public List<AttrDefinition> queryAttrDefinitionList(String appId, String className) {
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), className);
        List<AttrDefinition> attrDefinitionList = list(wrapper);
        return attrDefinitionList;
    }

    /**
     * 批量获取业务对象指定的属性信息
     *
     * @param className
     * @param attrKey
     * @return
     */
    @Override
    public Map<String, AttrDefinition> queryAttrDefinitionMap(String appId, String className, List<String> attrKey) {
        List<AttrDefinition> attrDefinitionList = queryAttrDefinitionList(appId, className, attrKey);
        return attrDefinitionList.stream().collect(Collectors.toMap(AttrDefinition::getAttrKey, item -> item));
    }

    /**
     * 获取属性信息
     *
     * @param className
     * @param attrKey
     * @return
     */
    @Override
    public AttrDefinition queryAttrDefinition(String appId, String className, String attrKey) {
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), className);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAttrKey), attrKey);
        AttrDefinition attrDefinition = getOne(wrapper);
        setCustomDefinition(appId, className, attrDefinition);
        return attrDefinition;
    }

    private void setCustomDefinition(String appId, String className, AttrDefinition attrDefinition) {
        // 获取自定义属性id
        AttrDefinitionCustom attrDefinitionCustom = attrDefinitionCustomService.queryAttrDefinitionCustom(appId, className, attrDefinition.getAttrKey());
        attrDefinition.setAttrDefinitionCustom(attrDefinitionCustom);
    }

    @Override
    public Map<String, List<AttrDefinition>> queryAttrDefinitionList(List<String> classNameList) {
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.in("CONCAT(" + MybatisPlusUtil.toColumns(AttrDefinition::getAppId) + ", " + MybatisPlusUtil.toColumns(AttrDefinition::getClassName) + ")", classNameList);
        List<AttrDefinition> list = list(wrapper);
        Map<String, List<AttrDefinition>> map = list.stream().collect(Collectors.groupingBy(AttrDefinition::getClassName));
        return map;
    }

    @Override
    public void deleteAttrDefinition(String appId, String className) {
        QueryWrapper<AttrDefinition> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(AttrDefinition::getClassName), className);
        remove(wrapper);
    }

}
