/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.entity.AttrDefinitionCustom;
import com.skyeye.attr.service.AttrDefinitionCustomService;
import com.skyeye.attr.service.AttrDefinitionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.DynamicCondition;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.VerificationParamsEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.impexp.dao.ImportExportConfigDao;
import com.skyeye.impexp.entity.ImportExportConfig;
import com.skyeye.impexp.entity.ImportExportFieldOption;
import com.skyeye.impexp.enums.ImportExportConfigTypeEnum;
import com.skyeye.impexp.service.ImportExportConfigService;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ColumnSpec;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ParsedConfig;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.SheetLayoutOptions;
import com.skyeye.sdk.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ImportExportConfigServiceImpl
 * @Description: 业务对象导入导出配置服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/8 22:10
 */
@Service
@SkyeyeService(name = "导入导出配置", groupName = "系统公共模块", tenant = TenantEnum.WEAK_ISOLATION)
public class ImportExportConfigServiceImpl extends SkyeyeBusinessServiceImpl<ImportExportConfigDao, ImportExportConfig> implements ImportExportConfigService {

    @Autowired
    private AttrDefinitionService attrDefinitionService;

    @Autowired
    private AttrDefinitionCustomService attrDefinitionCustomService;

    @Autowired
    private IDataService iDataService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Override
    protected void validatorEntity(ImportExportConfig entity) {
        // 同一业务对象下配置名称唯一，避免用户选择配置时出现重名歧义。
        QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), entity.getAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), entity.getClassName());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getConfigType), entity.getConfigType());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getName), entity.getName());
        if (StrUtil.isNotBlank(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        ImportExportConfig check = getOne(queryWrapper, false);
        if (check != null) {
            throw new CustomException("该业务对象导入导出配置的名称已存在.");
        }
    }

    @Override
    public void writePostpose(ImportExportConfig entity, String userId) {
        super.writePostpose(entity, userId);
        if (Objects.equals(entity.getIsDefault(), IsDefaultEnum.IS_DEFAULT.getKey())) {
            // 一个业务对象只能有一个默认配置：将其他配置统一更新为非默认。
            UpdateWrapper<ImportExportConfig> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), entity.getAppId());
            updateWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), entity.getClassName());
            updateWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getConfigType), entity.getConfigType());
            updateWrapper.ne(CommonConstants.ID, entity.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(ImportExportConfig::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
            update(updateWrapper);
        }
    }

    @Override
    public void queryImportExportConfigList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Integer configType = Integer.parseInt(params.get("configType").toString());
        // 列表按“默认优先 -> 排序号 -> 最近更新时间”排序，方便前端直接展示与选择。
        QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), className);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getConfigType), configType);
        List<ImportExportConfig> list = list(queryWrapper);
        List<ImportExportConfig> configList = list.stream().sorted(Comparator
                .comparing((ImportExportConfig item) -> item.getIsDefault(), Comparator.reverseOrder())
                .thenComparing(item -> item.getSortNo())
                .thenComparing(item -> item.getLastUpdateTime(), Comparator.reverseOrder()))
            .collect(Collectors.toList());

        iAuthUserService.setName(configList, "createId", "createName");
        iAuthUserService.setName(configList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(configList);
        outputObject.settotal(configList.size());
    }

    @Override
    public void queryImportExportFieldOptions(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        // 字段来源于业务对象属性定义，保证导入导出配置项与模型字段保持一致。
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            outputObject.setBeans(CollectionUtil.newArrayList());
            outputObject.settotal(0);
            return;
        }
        List<String> attrKeyList = attrDefinitionList.stream().map(AttrDefinition::getAttrKey).collect(Collectors.toList());
        Map<String, AttrDefinitionCustom> customMap = attrDefinitionCustomService.queryAttrDefinitionCustomMap(appId, className, attrKeyList);

        List<ImportExportFieldOption> result = attrDefinitionList.stream().map(attrDefinition -> {
            ImportExportFieldOption option = new ImportExportFieldOption();
            option.setAttrKey(attrDefinition.getAttrKey());
            option.setAttrType(attrDefinition.getAttrType());
            option.setFieldType(attrDefinition.getFieldType());
            option.setAttrModelType(attrDefinition.getAttrModelType());
            option.setWhetherInputParams(attrDefinition.getWhetherInputParams());
            option.setRemark(attrDefinition.getRemark());

            AttrDefinitionCustom custom = customMap.get(attrDefinition.getAttrKey());
            // 优先展示自定义属性名称，没有自定义则回退到原始属性名。
            if (custom != null && StrUtil.isNotBlank(custom.getName())) {
                option.setName(custom.getName());
            } else {
                option.setName(attrDefinition.getName());
            }

            boolean canImport = attrDefinition.getWhetherInputParams() != null
                && attrDefinition.getWhetherInputParams().equals(WhetherEnum.ENABLE_USING.getKey());
            // required 规则命中时，标记为“导入必填且前端不可取消”。
            boolean importRequiredFixed = canImport && StrUtil.containsIgnoreCase(attrDefinition.getRequired(), VerificationParamsEnum.REQUIRED.getKey());
            // 默认仅勾选必填字段；非必填需用户手动选择。
            option.setDefaultImportChecked(importRequiredFixed);
            option.setImportRequiredFixed(importRequiredFixed);
            option.setDefaultExportChecked(false);
            return option;
        }).collect(Collectors.toList());

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void downloadImportTemplate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params, ImportExportConfigTypeEnum.IMPORT.getKey());
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        ParsedConfig parsed = ImportExportConfigJsonHelper.parseConfig(config.getConfigJson());
        List<ColumnSpec> specs = parsed.getItems();
        SheetLayoutOptions layout = parsed.getLayout();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Map<String, String> titleMap = buildAttrKeyTitleMap(appId, className);
        if (CollectionUtil.isEmpty(specs)) {
            specs = buildDefaultImportColumnSpecs(appId, className, titleMap);
            layout = new SheetLayoutOptions();
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导入列且无可用属性，无法生成模板。");
        }
        writeExcelTemplate(config.getName(), "导入模板", specs, titleMap, buildSheetExportStyle(specs, layout));
    }

    @Override
    public void exportByConfig(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params, ImportExportConfigTypeEnum.EXPORT.getKey());
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Map<String, String> titleMap = buildAttrKeyTitleMap(appId, className);
        ParsedConfig parsed = ImportExportConfigJsonHelper.parseConfig(config.getConfigJson());
        List<ColumnSpec> specs = parsed.getItems();
        SheetLayoutOptions layout = parsed.getLayout();
        if (CollectionUtil.isEmpty(specs)) {
            specs = buildDefaultExportColumnSpecs(appId, className, titleMap);
            layout = new SheetLayoutOptions();
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导出列且无可用属性，无法导出。");
        }
        Map<String, Object> filters = parseFilters(params.get("filters").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        CommonPageInfo pageInfo = buildExportCommonPageInfo(appId, className, filters, limit);
        ResultEntity result = iDataService.queryExportAllData(appId, className, pageInfo);
        Map<String, Object> bean = result.getBean();
        if (bean != null && "file".equals(String.valueOf(bean.get("storageType")))) {
            String filePath = String.valueOf(bean.get("filePath"));
            sendImportExportJsonToExcelJob(config, specs, layout, titleMap, filePath, inputObject);
            outputObject.setBean(buildAsyncExportTip(bean));
            outputObject.settotal(0);
            return;
        }
        List<Map<String, Object>> rows = result.getRows();
        if (rows == null) {
            rows = CollectionUtil.newArrayList();
        }
        writeExcelTemplate(config.getName(), "导出数据", specs, titleMap, rows, buildSheetExportStyle(specs, layout));
    }

    private Map<String, Object> buildAsyncExportTip(Map<String, Object> exportBean) {
        Map<String, Object> tip = new LinkedHashMap<>();
        tip.put("async", true);
        tip.put("message", "数据量较大，已提交后台生成 Excel，请稍后在「我的输出」查看任务进度。");
        if (exportBean != null) {
            tip.put("total", exportBean.get("total"));
            tip.put("threshold", exportBean.get("threshold"));
        }
        return tip;
    }

    private void sendImportExportJsonToExcelJob(ImportExportConfig config, List<ColumnSpec> specs, SheetLayoutOptions layout,
                                                Map<String, String> titleMap, String filePath, InputObject inputObject) {
        int n = specs.size();
        String[] keys = new String[n];
        String[] columnNames = new String[n];
        for (int i = 0; i < n; i++) {
            ColumnSpec spec = specs.get(i);
            keys[i] = spec.getAttrKey();
            String header = StrUtil.isNotBlank(spec.getColumnTitle()) ? spec.getColumnTitle() : titleMap.get(spec.getAttrKey());
            if (StrUtil.isBlank(header)) {
                header = spec.getAttrKey();
            }
            columnNames[i] = header;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        String safeName = StrUtil.blankToDefault(config.getName(), "导入导出");
        Map<String, Object> json = new HashMap<>();
        json.put("title", safeName + "导出");
        json.put("type", MqConstants.JobMateMationJobType.IMPORT_EXPORT_JSON_TO_EXCEL.getJobType());
        json.put("filePath", filePath);
        json.put("keys", keys);
        json.put("columnNames", columnNames);
        json.put("exportStyleJson", JSONUtil.toJsonStr(buildSheetExportStyle(specs, layout)));
        json.put("userId", userId);
        json.put("tenantId", tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY);
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(json));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    private ExcelUtil.SheetExportStyle buildSheetExportStyle(List<ColumnSpec> specs, SheetLayoutOptions layout) {
        int n = specs.size();
        ExcelUtil.SheetExportStyle s = new ExcelUtil.SheetExportStyle();
        s.columnWidths = new int[n];
        s.headerBackgroundColors = new String[n];
        s.headerFontColors = new String[n];
        for (int i = 0; i < n; i++) {
            ColumnSpec sp = specs.get(i);
            if (sp.getColumnWidth() != null && sp.getColumnWidth() > 0) {
                s.columnWidths[i] = sp.getColumnWidth();
            } else {
                s.columnWidths[i] = -1;
            }
            String bg = StrUtil.firstNonBlank(sp.getHeaderBackgroundColor(),
                layout != null ? layout.getDefaultHeaderBackgroundColor() : null);
            s.headerBackgroundColors[i] = StrUtil.isBlank(bg) ? null : bg;
            String fg = StrUtil.firstNonBlank(sp.getHeaderFontColor(),
                layout != null ? layout.getDefaultHeaderFontColor() : null);
            s.headerFontColors[i] = StrUtil.isBlank(fg) ? null : fg;
        }
        if (layout != null) {
            if (layout.getHeaderRowHeight() != null && layout.getHeaderRowHeight() > 0) {
                s.headerRowHeight = layout.getHeaderRowHeight();
            }
            if (layout.getDataRowHeight() != null && layout.getDataRowHeight() > 0) {
                s.dataRowHeight = layout.getDataRowHeight();
            }
        }
        return s;
    }

    /**
     * 按 appId、className、可选 id 解析配置；未传 id 时取默认，再取排序第一条。
     */
    private ImportExportConfig resolveConfigForDownload(Map<String, Object> params, Integer configType) {
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        String id = params.get("id") == null ? null : params.get("id").toString();
        ImportExportConfig config = null;
        if (StrUtil.isNotBlank(id)) {
            config = selectById(id);
            if (config != null && (!StrUtil.equals(appId, config.getAppId()) || !StrUtil.equals(className, config.getClassName()))) {
                throw new CustomException("该配置不属于当前业务对象.");
            }
            if (config != null && !Objects.equals(configType, config.getConfigType())) {
                throw new CustomException("该配置类型与当前操作不匹配.");
            }
        }
        if (config == null) {
            QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), appId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), className);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getConfigType), configType);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
            config = getOne(queryWrapper, false);
        }
        if (config == null) {
            QueryWrapper<ImportExportConfig> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getAppId), appId);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getClassName), className);
            queryWrapper.eq(MybatisPlusUtil.toColumns(ImportExportConfig::getConfigType), configType);
            List<ImportExportConfig> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                return null;
            }
            list.sort(Comparator
                .comparing((ImportExportConfig item) -> item.getIsDefault(), Comparator.reverseOrder())
                .thenComparing(ImportExportConfig::getSortNo, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ImportExportConfig::getLastUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())));
            config = list.get(0);
        }
        return config;
    }

    private Map<String, String> buildAttrKeyTitleMap(String appId, String className) {
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return new LinkedHashMap<>();
        }
        List<String> attrKeyList = attrDefinitionList.stream().map(AttrDefinition::getAttrKey).collect(Collectors.toList());
        Map<String, AttrDefinitionCustom> customMap = attrDefinitionCustomService.queryAttrDefinitionCustomMap(appId, className, attrKeyList);
        Map<String, String> map = new LinkedHashMap<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            AttrDefinitionCustom custom = customMap.get(attrDefinition.getAttrKey());
            String title = custom != null && StrUtil.isNotBlank(custom.getName()) ? custom.getName() : attrDefinition.getName();
            map.put(attrDefinition.getAttrKey(), title);
        }
        return map;
    }

    /**
     * 导入配置未配 items 时：默认可导入字段
     */
    private List<ColumnSpec> buildDefaultImportColumnSpecs(String appId, String className, Map<String, String> titleMap) {
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return new ArrayList<>();
        }
        List<ColumnSpec> list = new ArrayList<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            boolean canImport = attrDefinition.getWhetherInputParams() != null
                && attrDefinition.getWhetherInputParams().equals(WhetherEnum.ENABLE_USING.getKey());
            if (!canImport) {
                continue;
            }
            ColumnSpec spec = new ColumnSpec();
            spec.setAttrKey(attrDefinition.getAttrKey());
            spec.setColumnTitle(titleMap.get(attrDefinition.getAttrKey()));
            list.add(spec);
        }
        return list;
    }

    /**
     * 导出配置未配 items 时：默认导出全部属性列
     */
    private List<ColumnSpec> buildDefaultExportColumnSpecs(String appId, String className, Map<String, String> titleMap) {
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return new ArrayList<>();
        }
        List<ColumnSpec> list = new ArrayList<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            ColumnSpec spec = new ColumnSpec();
            spec.setAttrKey(attrDefinition.getAttrKey());
            spec.setColumnTitle(titleMap.get(attrDefinition.getAttrKey()));
            list.add(spec);
        }
        return list;
    }

    private void writeExcelTemplate(String configName, String sheetName, List<ColumnSpec> specs, Map<String, String> titleMap,
                                    ExcelUtil.SheetExportStyle exportStyle) {
        writeExcelTemplate(configName, sheetName, specs, titleMap, null, exportStyle);
    }

    private void writeExcelTemplate(String configName, String sheetName, List<ColumnSpec> specs, Map<String, String> titleMap,
                                    List<Map<String, Object>> rows, ExcelUtil.SheetExportStyle exportStyle) {
        int n = specs.size();
        String[] keys = new String[n];
        String[] columnNames = new String[n];
        for (int i = 0; i < n; i++) {
            ColumnSpec spec = specs.get(i);
            keys[i] = spec.getAttrKey();
            String header = StrUtil.isNotBlank(spec.getColumnTitle()) ? spec.getColumnTitle() : titleMap.get(spec.getAttrKey());
            if (StrUtil.isBlank(header)) {
                header = spec.getAttrKey();
            }
            columnNames[i] = header;
        }
        String[] dataType = new String[0];
        String safeName = StrUtil.blankToDefault(configName, "导入导出");
        ExcelUtil.createWorkBook(safeName + sheetName, sheetName, rows, keys, columnNames, dataType, PutObject.getResponse(), exportStyle);
    }

    private List<Map<String, Object>> convertColumnSpec(List<ColumnSpec> specs, Map<String, String> titleMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ColumnSpec spec : specs) {
            Map<String, Object> one = new LinkedHashMap<>();
            one.put("attrKey", spec.getAttrKey());
            String header = StrUtil.isNotBlank(spec.getColumnTitle()) ? spec.getColumnTitle() : titleMap.get(spec.getAttrKey());
            if (StrUtil.isBlank(header)) {
                header = spec.getAttrKey();
            }
            one.put("columnTitle", header);
            if (spec.getColumnWidth() != null) {
                one.put("columnWidth", spec.getColumnWidth());
            }
            if (StrUtil.isNotBlank(spec.getHeaderBackgroundColor())) {
                one.put("headerBackgroundColor", spec.getHeaderBackgroundColor());
            }
            if (StrUtil.isNotBlank(spec.getHeaderFontColor())) {
                one.put("headerFontColor", spec.getHeaderFontColor());
            }
            result.add(one);
        }
        return result;
    }

    private Map<String, Object> parseFilters(String filtersStr) {
        if (StrUtil.isEmpty(filtersStr)) {
            return Collections.emptyMap();
        }
        if (!JSONUtil.isTypeJSON(filtersStr)) {
            throw new CustomException("filters必须是json对象字符串.");
        }
        return JSONUtil.parseObj(filtersStr);
    }

    /**
     * 组装 {@link com.skyeye.sdk.data.service.DataApiService#queryExportAllData} 所需的 CommonPageInfo；路由 URI 由 {@link IDataService#getUriByServiceClassName(String, String)} 解析。
     *
     * @param limit 本页/本次拉取条数；-1 表示不按条数截断（与 DataApi 一致：全部下载）
     */
    private CommonPageInfo buildExportCommonPageInfo(String appId, String className, Map<String, Object> filters, int limit) {
        CommonPageInfo pageInfo = new CommonPageInfo();
        pageInfo.setServiceAppId(appId);
        pageInfo.setServiceClassName(className);
        pageInfo.setPage(1);
        pageInfo.setLimit(limit);
        if (CollectionUtil.isEmpty(filters)) {
            return pageInfo;
        }
        // 关键词
        if (filters.containsKey("keyword")) {
            pageInfo.setKeyword(String.valueOf(filters.get("keyword")));
        }
        // 高级搜索
        Object dc = filters.get("dynamicCondition");
        if (dc != null) {
            String json = dc instanceof String ? (String) dc : JSONUtil.toJsonStr(dc);
            if (StrUtil.isNotBlank(json) && json.trim().startsWith("[")) {
                pageInfo.setDynamicCondition(JSONUtil.toList(json, DynamicCondition.class));
            }
        }
        // 自定义查询
        Object cpm = filters.get("customParamsMap");
        if (cpm instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) cpm;
            pageInfo.setCustomParamsMap(map);
        } else if (cpm instanceof String && StrUtil.isNotBlank((String) cpm) && JSONUtil.isTypeJSON((String) cpm)) {
            Map<String, Object> map = JSONUtil.toBean((String) cpm, Map.class);
            pageInfo.setCustomParamsMap(map);
        }
        return pageInfo;
    }

}

