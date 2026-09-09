/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dsform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.service.AttrDefinitionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.entity.BusinessApi;
import com.skyeye.business.service.BusinessApiService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dsform.classenum.DsFormPageType;
import com.skyeye.dsform.dao.DsFormPageDao;
import com.skyeye.dsform.entity.*;
import com.skyeye.dsform.service.DsFormPageContentService;
import com.skyeye.dsform.service.DsFormPageService;
import com.skyeye.dsform.service.TableColumnService;
import com.skyeye.exception.CustomException;
import com.skyeye.operate.entity.Operate;
import com.skyeye.operate.service.OperateService;
import com.skyeye.sdk.data.service.IDataService;
import com.skyeye.server.entity.ServiceBeanCustom;
import com.skyeye.server.service.ServiceBeanCustomService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DsFormPageServiceImpl
 * @Description: 表单布局管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:36
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "表单布局管理", groupName = "动态表单", tenant = TenantEnum.NO_ISOLATION, allowDynamicAttrKey = false)
public class DsFormPageServiceImpl extends SkyeyeBusinessServiceImpl<DsFormPageDao, DsFormPage> implements DsFormPageService {

    @Autowired
    private DsFormPageContentService dsFormPageContentService;

    @Autowired
    private AttrDefinitionService attrDefinitionService;

    @Autowired
    private OperateService operateService;

    @Autowired
    private ServiceBeanCustomService serviceBeanCustomService;

    @Autowired
    private BusinessApiService businessApiService;

    @Autowired
    private TableColumnService tableColumnService;

    @Autowired
    private IDataService iDataService;

    @Override
    protected QueryWrapper<DsFormPage> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DsFormPage> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // serviceAppId / serviceClassName 为空时查询全部，供业务对象资源总览使用
        if (StrUtil.isNotBlank(commonPageInfo.getServiceAppId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getAppId), commonPageInfo.getServiceAppId());
        }
        if (StrUtil.isNotBlank(commonPageInfo.getServiceClassName())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getClassName), commonPageInfo.getServiceClassName());
        }
        return queryWrapper;
    }

    @Override
    public void queryDsFormPageList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String className = params.get("className").toString();
        String appId = params.get("appId").toString();
        ServiceBeanCustom serviceBeanCustom = serviceBeanCustomService.selectServiceBeanCustom(appId, className);

        QueryWrapper<DsFormPage> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getClassName), className);
        wrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getAppId), appId);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(DsFormPage::getCreateTime));
        List<DsFormPage> dsFormPageList = list(wrapper);
        iAuthUserService.setName(dsFormPageList, "createId", "createName");
        iAuthUserService.setName(dsFormPageList, "lastUpdateId", "lastUpdateName");
        dsFormPageList.forEach(dsFormPage -> {
            dsFormPage.setServiceBeanCustom(serviceBeanCustom);
        });
        outputObject.setBeans(dsFormPageList);
        outputObject.settotal(dsFormPageList.size());
    }

    @Override
    public void validatorEntity(DsFormPage entity) {
        super.validatorEntity(entity);
        if (entity.getType().equals(DsFormPageType.PROCESS_ATTR.getKey())) {
            // 流程属性布局，每一个业务对象只能绑定一个
            QueryWrapper<DsFormPage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getAppId), entity.getAppId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getClassName), entity.getClassName());
            queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getType), entity.getType());
            if (StringUtils.isNotEmpty(entity.getId())) {
                queryWrapper.ne(CommonConstants.ID, entity.getId());
            }
            DsFormPage dsFormPage = getOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(dsFormPage)) {
                throw new CustomException("the process has been bound to layout.");
            }
        }
    }

    @Override
    public void createPrepose(DsFormPage entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        entity.setNumCode(oddNumber);
    }

    @Override
    protected void updatePrepose(DsFormPage entity) {
        DsFormPage oldDsFormPage = selectById(entity.getId());
        entity.setContent(oldDsFormPage.getContent());
    }

    @Override
    public void writePostpose(DsFormPage entity, String userId) {
        super.writePostpose(entity, userId);
        if (!entity.getType().equals(DsFormPageType.TAB_CHOOSE.getKey())) {
            // 选项卡布局之外的布局，需要保存请求事件
            BusinessApi businessApi = entity.getBusinessApi();
            businessApi.setObjectId(entity.getId());
            businessApi.setObjectKey(getServiceClassName());
            businessApiService.createEntity(businessApi, userId);
        }
    }

    @Override
    public void deletePostpose(DsFormPage dsFormPage) {
        if (dsFormPage.getType().equals(DsFormPageType.CREATE.getKey())
            || dsFormPage.getType().equals(DsFormPageType.EDIT.getKey())
            || dsFormPage.getType().equals(DsFormPageType.DETAILS.getKey())
            || dsFormPage.getType().equals(DsFormPageType.PROCESS_ATTR.getKey())) {
            // 删除页面内容项信息
            dsFormPageContentService.deleteDsFormContentByPageId(dsFormPage.getId());
        }
        if (dsFormPage.getType().equals(DsFormPageType.SIMPLE_TABLE.getKey())) {
            // 删除表单布局(表格类型关联的列信息)
            tableColumnService.deleteByPageId(dsFormPage.getId(), getServiceClassName());
        }
    }

    @Override
    public DsFormPage selectById(String id) {
        DsFormPage dsFormPage = super.selectById(id);
        if (dsFormPage.getType().equals(DsFormPageType.CREATE.getKey())
            || dsFormPage.getType().equals(DsFormPageType.EDIT.getKey())
            || dsFormPage.getType().equals(DsFormPageType.DETAILS.getKey())
            || dsFormPage.getType().equals(DsFormPageType.PROCESS_ATTR.getKey())) {
            // 查询表单布局的内容信息
            selectPageContent(dsFormPage);
        }
        if (dsFormPage.getType().equals(DsFormPageType.SIMPLE_TABLE.getKey())) {
            // 查询表格的字段列信息
            selectTableColumn(dsFormPage);
        }
        if (CollectionUtil.isNotEmpty(dsFormPage.getOperateIdList())) {
            // 获取操作信息
            List<Operate> operateList = operateService.getOperatesByClassName(dsFormPage.getAppId(), dsFormPage.getClassName());
            operateList = operateList.stream().filter(operate -> dsFormPage.getOperateIdList().contains(operate.getId())).collect(Collectors.toList());
            dsFormPage.setOperateList(operateList);
        }
        if (!dsFormPage.getType().equals(DsFormPageType.TAB_CHOOSE.getKey())) {
            // 选项卡布局之外的其他布局，需要查询接口信息
            BusinessApi businessApi = businessApiService.selectByObjectId(dsFormPage.getId());
            dsFormPage.setBusinessApi(businessApi);
        }

        // 服务类的信息
        ServiceBeanCustom serviceBeanCustom = serviceBeanCustomService.selectServiceBeanCustom(dsFormPage.getAppId(), dsFormPage.getClassName());
        dsFormPage.setServiceBeanCustom(serviceBeanCustom);

        return dsFormPage;
    }

    private void selectPageContent(DsFormPage dsFormPage) {
        List<DsFormPageContent> dsFormPageContents = dsFormPageContentService.getDsFormPageContentByPageId(dsFormPage.getId());
        dsFormPage.setDsFormPageContents(dsFormPageContents);
        // 获取属性信息
        List<String> attrKeys = dsFormPageContents.stream().filter(bean -> StrUtil.isNotEmpty(bean.getAttrKey())).map(DsFormPageContent::getAttrKey).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(attrKeys)) {
            Map<String, AttrDefinition> attrDefinitionMap = attrDefinitionService.queryAttrDefinitionMap(dsFormPage.getAppId(), dsFormPage.getClassName(), attrKeys);
            dsFormPageContents.forEach(dsFormPageContent -> {
                if (StrUtil.isNotEmpty(dsFormPageContent.getAttrKey())) {
                    dsFormPageContent.setAttrDefinition(attrDefinitionMap.get(dsFormPageContent.getAttrKey()));
                }
            });
        }
    }

    private void selectTableColumn(DsFormPage dsFormPage) {
        List<TableColumn> tableColumnList = tableColumnService.getTableColumnByPageId(dsFormPage.getId(), getServiceClassName());
        // 获取属性信息
        List<String> attrKeys = tableColumnList.stream().map(TableColumn::getAttrKey).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(attrKeys)) {
            Map<String, AttrDefinition> attrDefinitionMap = attrDefinitionService.queryAttrDefinitionMap(dsFormPage.getAppId(), dsFormPage.getClassName(), attrKeys);
            tableColumnList.forEach(tableColumn -> {
                tableColumn.setAttrDefinition(attrDefinitionMap.get(tableColumn.getAttrKey()));
            });
        }
        dsFormPage.setTableColumnList(tableColumnList);
    }

    /**
     * 保存表单布局关联的组件信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeDsFormPageContent(InputObject inputObject, OutputObject outputObject) {
        DsFormPageContentVo dsFormPageContentVo = inputObject.getParams(DsFormPageContentVo.class);
        List<DsFormPageContent> dsFormPageContentList = dsFormPageContentVo.getDsFormPageContentList();
        dsFormPageContentList.forEach(dsFormPageContent -> {
            dsFormPageContent.setPageId(dsFormPageContentVo.getPageId());
        });
        // 删除之前已经保存的
        dsFormPageContentService.deleteDsFormContentByPageId(dsFormPageContentVo.getPageId());
        // 保存新的组件关联信息
        String userId = inputObject.getLogParams().get("id").toString();
        dsFormPageContentService.createEntity(dsFormPageContentList, userId);
        // 刷新表单布局的缓存
        refreshCache(dsFormPageContentVo.getPageId());
    }

    /**
     * 保存表格类型的布局的信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeDsFormPageTable(InputObject inputObject, OutputObject outputObject) {
        TableColumnVo tableColumnVo = inputObject.getParams(TableColumnVo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        tableColumnService.createList(tableColumnVo.getTableColumnList(), userId, tableColumnVo.getPageId(), getServiceClassName());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeDsFormPageContentAttr(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String content = params.get("content").toString();

        UpdateWrapper<DsFormPage> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DsFormPage::getContent), content);
        update(updateWrapper);
        refreshCache(id);
    }

    /**
     * 根据业务数据id获取业务数据信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryBusinessDataByObject(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        // 获取业务数据
        Map<String, Object> businessData = iDataService.getDataByObjectId(null, objectId, objectKey);
        outputObject.setBean(businessData);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 根据业务对象的serviceClassName查找表单布局
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryDsFormPageForProcess(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String serviceClassName = params.get("serviceClassName").toString();
        // 根据业务对象的serviceClassName和流程模型id查询
        QueryWrapper<DsFormPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getClassName), serviceClassName);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getType), DsFormPageType.PROCESS_ATTR.getKey());
        queryWrapper.select(CommonConstants.ID);
        DsFormPage dsFormPage = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(dsFormPage)) {
            throw new CustomException("未配置流程布局.");
        }
        dsFormPage = selectById(dsFormPage.getId());
        outputObject.setBean(dsFormPage);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public List<DsFormPage> queryDsFormPageList(String appId, String serviceClassName) {
        QueryWrapper<DsFormPage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getClassName), serviceClassName);
        return list(queryWrapper);
    }

    @Override
    public void deleteDsFormPage(String appId, String className) {
        QueryWrapper<DsFormPage> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getClassName), className);
        wrapper.eq(MybatisPlusUtil.toColumns(DsFormPage::getAppId), appId);
        List<DsFormPage> list = list(wrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        // 表格布局id集合
        List<String> tableIds = list.stream()
            .filter(dsFormPage -> StrUtil.equals(dsFormPage.getType(), DsFormPageType.SIMPLE_TABLE.getKey()))
            .map(DsFormPage::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(tableIds)) {
            // 删除表格布局关联的列信息
            tableColumnService.deleteByPageId(tableIds, getServiceClassName());
        }
        // 其他布局id集合
        List<String> otherIds = list.stream()
            .filter(dsFormPage -> !StrUtil.equals(dsFormPage.getType(), DsFormPageType.SIMPLE_TABLE.getKey()))
            .map(DsFormPage::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(otherIds)) {
            // 删除其他布局关联的组件信息
            dsFormPageContentService.deleteDsFormContentByPageId(otherIds);
        }
    }
}
