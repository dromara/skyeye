/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.echarts.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.echarts.dao.ReportImportModelDao;
import com.skyeye.echarts.entity.ImportModel;
import com.skyeye.echarts.entity.ReportModel;
import com.skyeye.echarts.entity.ReportModelAttr;
import com.skyeye.echarts.service.ReportImportModelService;
import com.skyeye.echarts.service.ReportModelAttrService;
import com.skyeye.echarts.service.ReportModelService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportImportModelServiceImpl
 * @Description: Echarts模型管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:21
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "Echarts模型管理", groupName = "Echarts模型管理")
public class ReportImportModelServiceImpl extends SkyeyeBusinessServiceImpl<ReportImportModelDao, ImportModel> implements ReportImportModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportImportModelServiceImpl.class);

    @Autowired
    private ReportModelService reportModelService;

    @Autowired
    private ReportModelAttrService reportModelAttrService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");

        // 获取最新版本的报表模型
        List<ReportModel> reportModelList = reportModelService.queryAllMaxVersionReportModel();
        Map<String, ReportModel> reportModelMap = reportModelList.stream()
            .collect(Collectors.toMap(ReportModel::getImportModelId, item -> item, (a, b) -> a));
        beans.forEach(bean -> {
            Object id = bean.get("id");
            bean.put("reportModel", id == null ? null : reportModelMap.get(id.toString()));
        });
        return beans;
    }

    @Override
    public void validatorEntity(ImportModel entity) {
        super.validatorEntity(entity);
        QueryWrapper<ImportModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(ImportModel::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(ImportModel::getModelCode), entity.getModelCode()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        ImportModel checkModel = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkModel)) {
            throw new CustomException("this `name` OR `modelCode` is exist.");
        }
    }

    @Override
    protected void deletePostpose(String id) {
        reportModelService.deleteByImportModelId(id);
    }

    @Override
    public void queryAllMaxVersionReportModel(InputObject inputObject, OutputObject outputObject) {
        List<ReportModel> reportModelList = reportModelService.queryAllMaxVersionReportModel();
        if (CollectionUtil.isEmpty(reportModelList)) {
            return;
        }
        Map<String, ReportModel> reportModelMap = reportModelList.stream()
            .collect(Collectors.toMap(ReportModel::getImportModelId, item -> item, (a, b) -> a));
        Map<String, String> reportModelIdMap = reportModelList.stream()
            .collect(Collectors.toMap(ReportModel::getImportModelId, ReportModel::getId, (a, b) -> a));

        List<String> importModelIds = new ArrayList<>(reportModelMap.keySet());
        List<ImportModel> models = selectByIds(importModelIds.toArray(new String[0]));
        Map<String, List<ReportModelAttr>> modelAttrsMap = reportModelAttrService.queryReportModelAttrMapByModelIds(new ArrayList<>(reportModelIdMap.values()));
        models.forEach(model -> {
            try {
                model.setReportModel(reportModelMap.get(model.getId()));
                String reportModelId = reportModelIdMap.get(model.getId());
                List<ReportModelAttr> attrs = modelAttrsMap.get(reportModelId);
                if (attrs == null) {
                    return;
                }
                // LinkedHashMap 保留按 orderBy 查询后的顺序，供设计器右侧属性面板使用
                Map<String, ReportModelAttr> attrsMap = attrs.stream()
                    .collect(Collectors.toMap(ReportModelAttr::getAttrCode, item -> item, (a, b) -> a, java.util.LinkedHashMap::new));
                model.setAttr(attrsMap);
            } catch (Exception ee) {
                LOGGER.warn("queryAllMaxVersionReportModel -> reportModelAttrDao.getReportModelAttrToEditorByModelId failed.", ee);
            }
        });
        iSysDictDataService.setName(models, "typeId", "typeName");
        outputObject.setBeans(models);
        outputObject.settotal(models.size());
    }

    @Override
    public void queryReportModelVersionList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String importModelId = params.get("importModelId").toString();
        QueryWrapper<ReportModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ReportModel::getImportModelId), importModelId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ReportModel::getSoftwareVersion));
        List<ReportModel> list = reportModelService.list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void enableReportModelVersion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String reportModelId = params.get("reportModelId").toString();

        ReportModel target = reportModelService.selectById(reportModelId);
        if (target == null) {
            throw new CustomException("版本不存在.");
        }
        String importModelId = target.getImportModelId();
        UpdateWrapper<ReportModel> disableAll = new UpdateWrapper<>();
        disableAll.eq(MybatisPlusUtil.toColumns(ReportModel::getImportModelId), importModelId);
        disableAll.set(MybatisPlusUtil.toColumns(ReportModel::getEnabled), EnableEnum.DISABLE_USING.getKey());
        reportModelService.update(disableAll);
        target.setEnabled(EnableEnum.ENABLE_USING.getKey());
        reportModelService.updateEntity(target, inputObject.getLogParams().get("id").toString());
    }

    @Override
    public void disableReportModelVersion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String reportModelId = params.get("reportModelId").toString();
        ReportModel target = reportModelService.selectById(reportModelId);
        if (target == null) {
            throw new CustomException("版本不存在.");
        }
        target.setEnabled(EnableEnum.DISABLE_USING.getKey());
        reportModelService.updateEntity(target, inputObject.getLogParams().get("id").toString());
    }

    @Override
    public void queryReportModelVersionById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String reportModelId = params.get("reportModelId").toString();
        ReportModel reportModel = reportModelService.selectById(reportModelId);
        outputObject.setBean(reportModel);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

}
