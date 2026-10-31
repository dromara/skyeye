/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.application.service.ApplicationService;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.service.AttrDefinitionCustomService;
import com.skyeye.attr.service.AttrDefinitionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.FieldType;
import com.skyeye.common.enumeration.ServiceBeanType;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.util.MapUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dsform.service.DsFormPageService;
import com.skyeye.eve.service.IServiceBeanToEntityHelperService;
import com.skyeye.exception.CustomException;
import com.skyeye.operate.service.OperateService;
import com.skyeye.server.dao.ServiceBeanDao;
import com.skyeye.server.entity.ServiceBean;
import com.skyeye.server.entity.ServiceBeanApi;
import com.skyeye.server.service.ServiceBeanCustomService;
import com.skyeye.server.service.ServiceBeanService;
import com.skyeye.table.entity.TableFieldInfo;
import com.skyeye.table.entity.TableInfo;
import com.skyeye.table.sdk.service.TableApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ServiceBeanServiceImpl
 * @Description: 所有实现了SkyeyeBusinessService的服务类的注册服务
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/29 22:30
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "服务类注册", groupName = "系统公共模块", tenant = TenantEnum.NO_ISOLATION)
public class ServiceBeanServiceImpl extends SkyeyeBusinessServiceImpl<ServiceBeanDao, ServiceBean> implements ServiceBeanService {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AttrDefinitionService attrDefinitionService;

    @Autowired
    private AttrDefinitionCustomService attrDefinitionCustomService;

    @Autowired
    private ServiceBeanCustomService serviceBeanCustomService;

    @Autowired
    private DsFormPageService dsFormPageService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private TableApiService tableApiService;

    @Value("${spring.profiles.active}")
    private String springProfilesActive;

    @Autowired
    private IServiceBeanToEntityHelperService iServiceBeanToEntityHelperService;

    @Override
    protected void validatorEntity(ServiceBean entity) {
        super.validatorEntity(entity);
        QueryWrapper<ServiceBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), entity.getSpringApplicationName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getAppId), entity.getAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getClassName), entity.getClassName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getTableName), entity.getTableName());

        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        ServiceBean serviceBean = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(serviceBean)) {
            throw new CustomException("该服务名、应用ID、类名、表名已存在，请重新输入！");
        }
        entity.setManageShow(true);
        entity.setRemark(entity.getName());
        entity.setType(ServiceBeanType.VIRTUAL_MODEL.getKey());
    }

    @Override
    protected void createPostpose(ServiceBean entity, String userId) {
        super.createPostpose(entity, userId);
        entity.getAttrDefinitionList().forEach(attr -> {
            attr.setAppId(entity.getAppId());
            attr.setClassName(entity.getClassName());
        });
        // 保存属性信息
        attrDefinitionService.saveBarchAttrDefinition(entity.getAttrDefinitionList());
        // 创建表
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(entity.getTableName());
        tableInfo.setTableComment(entity.getName());
        List<TableFieldInfo> fieldList = new ArrayList<>();
        entity.getAttrDefinitionList().forEach(attr -> {
            TableFieldInfo fieldInfo = new TableFieldInfo();
            fieldInfo.setTableName(entity.getTableName());
            fieldInfo.setFieldName(attr.getDbFieldName());
            fieldInfo.setFieldComment(attr.getName());
            fieldInfo.setFieldType(attr.getFieldType());
            fieldInfo.setFieldLength(attr.getFieldLength());
            fieldInfo.setDecimalPlaces(attr.getDecimalPlaces());
            fieldInfo.setDefaultValue(attr.getDbDefaultValue());
            fieldInfo.setIsPrimaryKey(attr.getIsPrimaryKey());
            fieldInfo.setIsNotNull(attr.getIsPrimaryKey() == WhetherEnum.ENABLE_USING.getKey() ? WhetherEnum.ENABLE_USING.getKey() : WhetherEnum.DISABLE_USING.getKey());
            fieldList.add(fieldInfo);
        });
        // 添加租户字段
        fieldList.add(getTenantTableFieldInfo(entity));
        tableInfo.setFieldList(fieldList);
        tableApiService.createTable(entity.getSpringApplicationName(), tableInfo);
    }

    private TableFieldInfo getTenantTableFieldInfo(ServiceBean entity) {
        TableFieldInfo fieldInfo = new TableFieldInfo();
        fieldInfo.setTableName(entity.getTableName());
        fieldInfo.setFieldName(CommonConstants.TENANT_ID_FIELD);
        fieldInfo.setFieldComment("租户ID");
        fieldInfo.setFieldType(FieldType.VARCHAR.getKey());
        fieldInfo.setFieldLength("32");
        fieldInfo.setDecimalPlaces(CommonNumConstants.NUM_ZERO);
        fieldInfo.setDefaultValue(TenantTypeEnum.PLATFORM.getCode());
        fieldInfo.setIsPrimaryKey(WhetherEnum.DISABLE_USING.getKey());
        fieldInfo.setIsNotNull(WhetherEnum.DISABLE_USING.getKey());
        return fieldInfo;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void registerServiceBean(InputObject inputObject, OutputObject outputObject) {
        ServiceBeanApi serviceBeanApi = inputObject.getParams(ServiceBeanApi.class);

        // 获取数据库中的数据（物理模型 + 服务模型；虚拟模型为手工维护，不参与启动扫描覆盖）
        QueryWrapper<ServiceBean> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), serviceBeanApi.getSpringApplicationName());
        wrapper.in(MybatisPlusUtil.toColumns(ServiceBean::getType), Arrays.asList(
            ServiceBeanType.PHYSICAL_MODEL.getKey(),
            ServiceBeanType.SERVICE_MODEL.getKey()));
        List<ServiceBean> oldList = super.list(wrapper);
        List<String> oldKeys = oldList.stream().map(bean -> getKey(bean)).collect(Collectors.toList());

        // 获取入参的数据
        List<ServiceBean> classNameList = serviceBeanApi.getClassNameList();
        classNameList.forEach(className -> {
            className.setAppId(serviceBeanApi.getAppId());
            className.setSpringApplicationName(serviceBeanApi.getSpringApplicationName());
        });

        List<String> newKeys = classNameList.stream().map(bean -> getKey(bean)).collect(Collectors.toList());

        // (旧数据 - 新数据) 从数据库删除
        List<ServiceBean> deleteBeans = oldList.stream().filter(item -> !newKeys.contains(getKey(item))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteBeans)) {
            List<String> classNames = deleteBeans.stream().map(bean -> bean.getClassName()).collect(Collectors.toList());
            QueryWrapper<ServiceBean> deleteWrapper = new QueryWrapper<>();
            deleteWrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), serviceBeanApi.getSpringApplicationName());
            deleteWrapper.in(MybatisPlusUtil.toColumns(ServiceBean::getClassName), classNames);
            deleteWrapper.in(MybatisPlusUtil.toColumns(ServiceBean::getType), Arrays.asList(
                ServiceBeanType.PHYSICAL_MODEL.getKey(),
                ServiceBeanType.SERVICE_MODEL.getKey()));
            remove(deleteWrapper);
        }

        // (新数据 - 旧数据) 添加到数据库
        List<ServiceBean> addBeans = classNameList.stream().filter(item -> !oldKeys.contains(getKey(item))).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(addBeans)) {
            addBeans.forEach(bean -> {
                // 扫描端已标 SERVICE_MODEL；其余默认物理模型
                if (bean.getType() == null) {
                    bean.setType(ServiceBeanType.PHYSICAL_MODEL.getKey());
                }
            });
            createEntity(addBeans, StringUtils.EMPTY);
        }

        // 保存属性信息
        saveAttrDefinition(serviceBeanApi.getAppId(), classNameList);
    }

    private String getKey(ServiceBean bean) {
        return String.format(Locale.ROOT, "%s_%s_%s_%s_%s_%s_%s_%s_%s_%s_%s", bean.getAppId(), bean.getClassName(), bean.getManageShow(),
            bean.getTeamAuth(), bean.getTenant(), bean.getFlowable(), bean.getName(), bean.getGroupName(), bean.getTableName(), bean.getType(), bean.getAllowDynamicAttrKey());
    }

    private void saveAttrDefinition(String appId, List<ServiceBean> classNameList) {
        List<AttrDefinition> attrDefinitionList = classNameList.stream()
            .filter(className -> CollectionUtil.isNotEmpty(className.getAttrDefinitionList()))
            .flatMap(className -> className.getAttrDefinitionList().stream())
            .filter(Objects::nonNull).collect(Collectors.toList());
        attrDefinitionService.saveBarchAttrDefinition(appId, attrDefinitionList);
    }

    @Override
    public URI getServiceBean(String appId, String className) {
        ServiceBean serviceBean = queryServiceClass(appId, className);
        if (serviceBean == null) {
            throw new CustomException("未找到 service bean 对应的业务类配置信息.");
        }
        // 根据服务名获取服务实例
        List<ServiceInstance> allInstances = discoveryClient.getInstances(serviceBean.getSpringApplicationName());
        if (CollectionUtil.isEmpty(allInstances)) {
            throw new CustomException(String.format(Locale.ROOT, "this service[%s] has no instance.", serviceBean.getSpringApplicationName()));
        }
        return allInstances.get(0).getUri();
    }

    /**
     * 获取服务类信息(树结构)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryServiceClassForTree(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> applications = applicationService.queryApplicationList();
        // 查询服务类信息
        QueryWrapper<ServiceBean> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getManageShow), true);
        wrapper.likeLeft(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), springProfilesActive);
        List<Map<String, Object>> serviceClass = list(wrapper)
            .stream().map(bean -> BeanUtil.beanToMap(bean)).collect(Collectors.toList());
        List<Map<String, Object>> result = buildResult(serviceClass);
        applications.forEach(application -> {
            Map<String, Object> resultMap = getResultMap(application.get("appId").toString(), application.get("appName").toString(), "0", true);
            resultMap.put("springApplicationName", application.get("springApplicationName"));
            result.add(resultMap);
        });
        // 转为树
        List<Tree<String>> treeNodes = TreeUtil.build(result, String.valueOf(CommonNumConstants.NUM_ZERO), new TreeNodeConfig(),
            (treeNode, tree) -> {
                tree.setId(treeNode.get("key").toString());
                tree.setParentId(treeNode.get("parentKey").toString());
                tree.setName(treeNode.get("name").toString());
                tree.putExtra("appId", treeNode.get("appId"));
                tree.putExtra("isParent", treeNode.get("isParent"));
                tree.putExtra("disabled", treeNode.get("disabled"));
                tree.putExtra("nocheck", treeNode.get("nocheck"));
                tree.putExtra("classMation", treeNode.get("classMation"));
                tree.putExtra("springApplicationName", treeNode.get("springApplicationName"));
            });
        outputObject.setBeans(treeNodes);
        outputObject.settotal(treeNodes.size());

    }

    private List<Map<String, Object>> buildResult(List<Map<String, Object>> serviceClass) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, List<Map<String, Object>>> collect = serviceClass.stream().collect(Collectors.groupingBy(bean -> bean.get("appId").toString()));
        collect.forEach((appId, classNameList) -> {
            // 获取没有分组的业务对象服务类
            List<String> noGroupNameClassNameIdList = classNameList.stream()
                .filter(bean -> MapUtil.checkKeyIsNull(bean, "groupName"))
                .map(bean -> bean.get("id").toString()).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(noGroupNameClassNameIdList)) {
                String groupKey = appId + ".noGroupName";
                result.add(getResultMap(groupKey, "未分组", appId, true));
                setClassName(serviceClass, result, noGroupNameClassNameIdList, groupKey);
            }
            // 获取有分组的业务对象服务类
            Map<String, List<Map<String, Object>>> hasGroupNameIdList = classNameList.stream()
                .filter(bean -> !MapUtil.checkKeyIsNull(bean, "groupName"))
                .collect(Collectors.groupingBy(bean -> bean.get("groupName").toString()));
            hasGroupNameIdList.forEach((groupName, classNames) -> {
                String groupKey = appId + ".hasGroupName" + "." + groupName;
                result.add(getResultMap(groupKey, groupName, appId, true));
                List<String> classNameIds = classNames.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
                setClassName(classNames, result, classNameIds, groupKey);
            });
        });
        return result;
    }

    private void setClassName(List<Map<String, Object>> serviceClass, List<Map<String, Object>> result, List<String> classNameIds, String groupKey) {
        serviceClass.forEach(bean -> {
            String id = bean.get("id").toString();
            if (classNameIds.indexOf(id) >= 0) {
                String key = bean.get("appId").toString() + bean.get("className").toString();
                Map<String, Object> resultMap = getResultMap(key, bean.get("name").toString(), groupKey, false);
                resultMap.put("classMation", bean);
                result.add(resultMap);
            }
        });
    }

    private Map<String, Object> getResultMap(String key, String name, String parentKey, Boolean isParent) {
        Map<String, Object> groupName = new HashMap<>();
        groupName.put("key", key);
        groupName.put("name", name);
        groupName.put("parentKey", parentKey);
        groupName.put("isParent", isParent);
        if (isParent) {
            groupName.put("disabled", true);
            groupName.put("nocheck", true);
        } else {
            groupName.put("disabled", false);
        }
        return groupName;
    }

    @Override
    public ServiceBean queryServiceClass(String appId, String className) {
        QueryWrapper<ServiceBean> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(appId)) {
            wrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getAppId), appId);
        }
        wrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getClassName), className);
        wrapper.likeLeft(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), springProfilesActive);
        ServiceBean skyeyeClassServiceBean = getOne(wrapper);
        return skyeyeClassServiceBean;
    }

    @Override
    public Map<String, ServiceBean> queryServiceClass(List<String> classNames) {
        if (CollectionUtil.isEmpty(classNames)) {
            return cn.hutool.core.map.MapUtil.newHashMap();
        }
        // 获取所属应用信息
        List<Map<String, Object>> applications = applicationService.queryApplicationList();
        Map<String, String> applicationMap = applications.stream().collect(Collectors.toMap(bean -> bean.get("appId").toString(), bean -> bean.get("appName").toString()));
        // 查询属性列表
        classNames = classNames.stream().distinct().collect(Collectors.toList());
        QueryWrapper<ServiceBean> wrapper = new QueryWrapper<>();
        wrapper.in("CONCAT(" + MybatisPlusUtil.toColumns(ServiceBean::getAppId) + ", " + MybatisPlusUtil.toColumns(ServiceBean::getClassName) + ")", classNames);
        wrapper.likeLeft(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), springProfilesActive);
        List<ServiceBean> serviceBeans = list(wrapper);
        // 获取原始的属性列表
        Map<String, List<AttrDefinition>> attrDefinitionMap = attrDefinitionService.queryAttrDefinitionList(classNames);
        serviceBeans.forEach(serviceBean -> {
            serviceBean.setApplicationName(applicationMap.get(serviceBean.getAppId()));
            serviceBean.setAttrDefinitionList(attrDefinitionMap.get(serviceBean.getClassName()));
        });
        Map<String, ServiceBean> serviceBeanMap = serviceBeans.stream().collect(Collectors.toMap(item -> item.getAppId() + item.getClassName(), bean -> bean));
        return serviceBeanMap;
    }

    @Override
    public Map<String, ServiceBean> getByEntityClassName(String appId, List<String> entityClassNames) {
        if (CollectionUtil.isEmpty(entityClassNames)) {
            return Collections.emptyMap();
        }
        QueryWrapper<ServiceBean> wrapper = new QueryWrapper<>();
        wrapper.in(MybatisPlusUtil.toColumns(ServiceBean::getEntityClassName), entityClassNames);
        wrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getAppId), appId);
        wrapper.likeLeft(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), springProfilesActive);
        List<ServiceBean> list = list(wrapper);
        return list.stream().collect(Collectors.toMap(item -> item.getEntityClassName(), bean -> bean));
    }

    @Override
    public ServiceBean getByEntityClassName(String appId, String entityClassName) {
        QueryWrapper<ServiceBean> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getEntityClassName), entityClassName);
        wrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getAppId), appId);
        return getOne(wrapper);
    }

    @Override
    public void queryServiceBeanByAppIdAndClassName(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Integer needAttr = Integer.parseInt(params.get("needAttr").toString());
        ServiceBean serviceBean = queryServiceClass(appId, className);
        if (needAttr == WhetherEnum.ENABLE_USING.getKey()) {
            // 需要查询属性信息
            serviceBean.setAttrDefinitionList(attrDefinitionService.queryAttrDefinitionList(serviceBean.getAppId(), serviceBean.getClassName()));
        }
        outputObject.setBean(serviceBean);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void deleteServiceBeanByAppIdAndClassName(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        ServiceBean serviceBean = queryServiceClass(appId, className);
        if (serviceBean.getType().equals(ServiceBeanType.PHYSICAL_MODEL.getKey())) {
            throw new CustomException("物理模型不能删除.");
        }
        // 删除微服务的一级缓存
        iServiceBeanToEntityHelperService.removeServiceBeanToEntityHelper(serviceBean.getSpringApplicationName(),
            serviceBean.getAppId(), serviceBean.getClassName());
        // 删除表
        try {
            tableApiService.dropTable(serviceBean.getSpringApplicationName(), serviceBean.getTableName());
        } catch (Exception e) {
            log.error("删除表失败", e);
        }
        // 删除虚拟模型
        QueryWrapper<ServiceBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ServiceBean::getClassName), className);
        queryWrapper.likeLeft(MybatisPlusUtil.toColumns(ServiceBean::getSpringApplicationName), springProfilesActive);
        remove(queryWrapper);
        // 删除自定义配置信息
        serviceBeanCustomService.deleteServiceBeanCustom(appId, className);
        // 删除属性信息
        attrDefinitionService.deleteAttrDefinition(appId, className);
        attrDefinitionCustomService.deleteAttrDefinitionCustom(appId, className);
        // 删除布局信息
        dsFormPageService.deleteDsFormPage(appId, className);
        // 删除操作信息
        operateService.deleteOperate(appId, className);
    }

}
