/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.classenum.AttrKeyDataType;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.attr.entity.AttrDefinitionCustom;
import com.skyeye.attr.service.AttrDefinitionCustomService;
import com.skyeye.attr.service.AttrDefinitionService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.clazz.service.SkyeyeClassEnumService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.DynamicCondition;
import com.skyeye.common.enumeration.*;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.object.ResultEntity;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.common.util.ImportExportRowUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.entity.dict.SysDictData;
import com.skyeye.eve.entity.dict.SysDictType;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.SysDictDataService;
import com.skyeye.eve.service.SysDictTypeService;
import com.skyeye.exception.CustomException;
import com.skyeye.impexp.dao.ImportExportConfigDao;
import com.skyeye.impexp.entity.ImportExportConfig;
import com.skyeye.impexp.entity.ImportExportFieldOption;
import com.skyeye.impexp.enums.ImportExportConfigTypeEnum;
import com.skyeye.impexp.service.ImportExportConfigService;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ColumnSpec;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.HeaderGroupStyle;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ParsedConfig;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.SheetLayoutOptions;
import com.skyeye.sdk.data.service.IDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.IOException;
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

    @Autowired
    private SkyeyeClassEnumService skyeyeClassEnumService;

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @Autowired
    private SysDictDataService sysDictDataService;

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
        // 字段来源于业务对象属性定义；对象/集合展开一层子字段为点路径列（parent.child）。
        // 集合拆列后导出/导入按「一行一明细」处理（主表字段重复 / 主表空行续明细）。
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            outputObject.setBeans(CollectionUtil.newArrayList());
            outputObject.settotal(0);
            return;
        }
        List<String> attrKeyList = attrDefinitionList.stream().map(AttrDefinition::getAttrKey).collect(Collectors.toList());
        Map<String, AttrDefinitionCustom> customMap = attrDefinitionCustomService.queryAttrDefinitionCustomMap(appId, className, attrKeyList);

        List<ImportExportFieldOption> result = new ArrayList<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            String parentName = resolveAttrDisplayName(attrDefinition, customMap.get(attrDefinition.getAttrKey()));
            Integer modelType = attrDefinition.getAttrModelType() == null
                ? AttrModelType.SCALAR.getKey() : attrDefinition.getAttrModelType();

            if (AttrModelType.OBJECT.getKey().equals(modelType) || AttrModelType.COLLECTION.getKey().equals(modelType)) {
                List<AttrDefinition> children = attrDefinitionService.queryChildAttrDefinitionList(
                    appId, className, attrDefinition.getAttrKey());
                if (CollectionUtil.isNotEmpty(children)) {
                    // 有子属性：只暴露拆列后的点路径字段，不再暴露整对象/整集合 JSON 列
                    for (AttrDefinition child : children) {
                        result.add(buildChildFieldOption(attrDefinition, parentName, child, modelType));
                    }
                    continue;
                }
                // 无子属性：整对象/集合一列（单元格 JSON）
            }

            ImportExportFieldOption option = buildTopFieldOption(attrDefinition, parentName);
            result.add(option);
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    private String resolveAttrDisplayName(AttrDefinition attrDefinition, AttrDefinitionCustom custom) {
        if (custom != null && StrUtil.isNotBlank(custom.getName())) {
            return custom.getName();
        }
        return attrDefinition.getName();
    }

    private ImportExportFieldOption buildTopFieldOption(AttrDefinition attrDefinition, String displayName) {
        ImportExportFieldOption option = new ImportExportFieldOption();
        option.setAttrKey(attrDefinition.getAttrKey());
        option.setName(displayName);
        option.setAttrType(attrDefinition.getAttrType());
        option.setFieldType(attrDefinition.getFieldType());
        option.setAttrModelType(attrDefinition.getAttrModelType() == null
            ? AttrModelType.SCALAR.getKey() : attrDefinition.getAttrModelType());
        option.setParentAttrKey(null);
        option.setDepth(0);
        option.setWhetherInputParams(attrDefinition.getWhetherInputParams());
        option.setRemark(attrDefinition.getRemark());
        fillImportExportFlags(option, attrDefinition);
        return option;
    }

    private ImportExportFieldOption buildChildFieldOption(AttrDefinition parent, String parentName, AttrDefinition child,
                                                          Integer parentAttrModelType) {
        ImportExportFieldOption option = new ImportExportFieldOption();
        String childName = child.getAttrDefinitionCustom() != null
            && StrUtil.isNotBlank(child.getAttrDefinitionCustom().getName())
            ? child.getAttrDefinitionCustom().getName()
            : child.getName();
        option.setAttrKey(parent.getAttrKey() + "." + child.getAttrKey());
        option.setName(parentName + "." + childName);
        option.setAttrType(child.getAttrType());
        option.setFieldType(child.getFieldType());
        option.setAttrModelType(child.getAttrModelType() == null
            ? AttrModelType.SCALAR.getKey() : child.getAttrModelType());
        option.setParentAttrKey(parent.getAttrKey());
        option.setParentAttrModelType(parentAttrModelType);
        option.setDepth(1);
        // 子字段入参/必填：父可导入且子自身规则
        boolean parentCanImport = parent.getWhetherInputParams() != null
            && parent.getWhetherInputParams().equals(WhetherEnum.ENABLE_USING.getKey());
        option.setWhetherInputParams(parentCanImport ? child.getWhetherInputParams() : WhetherEnum.DISABLE_USING.getKey());
        option.setRemark(child.getRemark());
        fillImportExportFlags(option, child, parentCanImport);
        return option;
    }

    private void fillImportExportFlags(ImportExportFieldOption option, AttrDefinition attrDefinition) {
        fillImportExportFlags(option, attrDefinition, true);
    }

    private void fillImportExportFlags(ImportExportFieldOption option, AttrDefinition attrDefinition, boolean parentCanImport) {
        boolean canImport = parentCanImport && attrDefinition.getWhetherInputParams() != null
            && attrDefinition.getWhetherInputParams().equals(WhetherEnum.ENABLE_USING.getKey());
        boolean importRequiredFixed = canImport
            && StrUtil.containsIgnoreCase(attrDefinition.getRequired(), VerificationParamsEnum.REQUIRED.getKey());
        option.setDefaultImportChecked(importRequiredFixed);
        option.setImportRequiredFixed(importRequiredFixed);
        option.setDefaultExportChecked(false);
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
            parsed.setItems(specs);
            parsed.setLayout(layout);
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导入列且无可用属性，无法生成模板。");
        }
        writeExcelByParsedConfig(config.getName(), "导入模板", parsed, titleMap, appId, className, null);
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
            parsed.setItems(specs);
            parsed.setLayout(layout);
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导出列且无可用属性，无法导出。");
        }
        Map<String, Object> filters = parseFilters(params.get("filters").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        CommonPageInfo pageInfo = buildExportCommonPageInfo(appId, className, filters, limit);
        ResultEntity result = iDataService.queryExportAllData(appId, className, pageInfo);
        List<String> collectionRoots = resolveCollectionRootsForSpecs(appId, className, specs);
        Map<String, Object> bean = result.getBean();
        if (bean != null && "file".equals(String.valueOf(bean.get("storageType")))) {
            String filePath = String.valueOf(bean.get("filePath"));
            sendImportExportJsonToExcelJob(config, parsed, titleMap, filePath, collectionRoots, inputObject);
            outputObject.setBean(buildAsyncExportTip(bean));
            outputObject.settotal(0);
            return;
        }
        List<Map<String, Object>> rows = result.getRows();
        if (rows == null) {
            rows = CollectionUtil.newArrayList();
        }
        writeExcelByParsedConfig(config.getName(), "导出数据", parsed, titleMap, appId, className, rows);
    }

    @Override
    public void importByConfig(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        ImportExportConfig config = resolveConfigForDownload(params, ImportExportConfigTypeEnum.IMPORT.getKey());
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        Map<String, String> titleMap = buildAttrKeyTitleMap(appId, className);
        ParsedConfig parsed = ImportExportConfigJsonHelper.parseConfig(config.getConfigJson());
        List<ColumnSpec> specs = parsed.getItems();
        if (CollectionUtil.isEmpty(specs)) {
            specs = buildDefaultImportColumnSpecs(appId, className, titleMap);
            parsed.setItems(specs);
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导入列且无可用属性，无法导入。");
        }

        Map<String, Integer> attrModelTypeMap = buildAttrModelTypeMap(appId, className);
        List<String> collectionRoots = resolveCollectionRootsForSpecs(appId, className, specs);
        boolean useMulti = shouldUseMultiSheet(parsed, collectionRoots);
        List<Map<String, Object>> dataRows;
        if (useMulti) {
            dataRows = importRowsFromMultiSheet(parsed, titleMap, collectionRoots, attrModelTypeMap);
        } else {
            String collectionRoot = collectionRoots.isEmpty() ? null : collectionRoots.get(0);
            dataRows = importRowsFromSingleSheet(specs, collectionRoot, attrModelTypeMap);
        }
        if (CollectionUtil.isEmpty(dataRows)) {
            throw new CustomException("Excel 无有效数据行。");
        }
        if (dataRows.size() > 2000) {
            throw new CustomException("单次最多导入 2000 条主表数据，请拆分后重试.");
        }

        ResultEntity result = iDataService.importBatchData(appId, className, dataRows);
        Map<String, Object> bean = result.getBean() == null ? new LinkedHashMap<>() : new LinkedHashMap<>(result.getBean());
        bean.put("uploadNum", bean.getOrDefault("successNum", dataRows.size()));
        bean.put("configId", config.getId());
        bean.put("configName", config.getName());
        outputObject.setBean(bean);
        outputObject.settotal(result.getTotal() == null ? 0 : result.getTotal());
    }

    private MultipartFile readUploadExcelFile() {
        CommonsMultipartResolver multipartResolver =
            new CommonsMultipartResolver(PutObject.getRequest().getSession().getServletContext());
        if (!multipartResolver.isMultipart(PutObject.getRequest())) {
            throw new CustomException("请上传 Excel 文件。");
        }
        MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) PutObject.getRequest();
        Iterator<String> iter = multiRequest.getFileNames();
        while (iter.hasNext()) {
            MultipartFile file = multiRequest.getFile(iter.next());
            if (file == null || file.isEmpty()) {
                continue;
            }
            String fileName = file.getOriginalFilename();
            if (StrUtil.isNotBlank(fileName) && !StrUtil.endWithIgnoreCase(fileName, ".xls")) {
                throw new CustomException("请上传 .xls 格式的 Excel（与导入模板一致）。");
            }
            return file;
        }
        throw new CustomException("请上传 Excel 文件。");
    }

    private List<Map<String, Object>> importRowsFromSingleSheet(List<ColumnSpec> specs, String collectionRoot,
                                                                Map<String, Integer> attrModelTypeMap) {
        List<List<String>> excelRows;
        try {
            String[] keys = specs.stream().map(ColumnSpec::getAttrKey).toArray(String[]::new);
            int headerRows = ExcelUtil.needTwoLevelHeader(keys) ? 2 : 1;
            excelRows = ExcelUtil.readExcelContent(readUploadExcelFile().getInputStream(), headerRows);
        } catch (IOException e) {
            throw new CustomException(e);
        }
        if (CollectionUtil.isEmpty(excelRows)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> dataRows = new ArrayList<>();
        for (List<String> excelRow : excelRows) {
            if (isBlankExcelRow(excelRow)) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            for (int col = 0; col < specs.size(); col++) {
                ColumnSpec spec = specs.get(col);
                String cell = col < excelRow.size() ? excelRow.get(col) : StrUtil.EMPTY;
                putImportCellValue(row, spec.getAttrKey(), cell, attrModelTypeMap.get(spec.getAttrKey()));
            }
            if (!row.isEmpty()) {
                dataRows.add(row);
            }
        }
        if (dataRows.size() > 2000) {
            throw new CustomException("单次最多导入 2000 行（含明细行），请拆分后重试.");
        }
        if (StrUtil.isNotBlank(collectionRoot)) {
            List<String> masterKeys = specs.stream()
                .map(ColumnSpec::getAttrKey)
                .filter(key -> StrUtil.isNotBlank(key)
                    && !key.equals(collectionRoot)
                    && !key.startsWith(collectionRoot + "."))
                .collect(Collectors.toList());
            try {
                dataRows = ImportExportRowUtil.assembleByCollection(dataRows, collectionRoot, masterKeys);
            } catch (IllegalArgumentException ex) {
                throw new CustomException(ex.getMessage());
            }
        }
        return dataRows;
    }

    private List<Map<String, Object>> importRowsFromMultiSheet(ParsedConfig parsed, Map<String, String> titleMap,
                                                               List<String> collectionRoots,
                                                               Map<String, Integer> attrModelTypeMap) {
        Map<String, List<List<String>>> allSheets;
        try {
            allSheets = ExcelUtil.readExcelAllSheets(readUploadExcelFile().getInputStream());
        } catch (IOException e) {
            throw new CustomException(e);
        }
        List<List<String>> masterExcel = allSheets.get(ImportExportConfigJsonHelper.MAIN_SHEET_NAME);
        if (masterExcel == null) {
            throw new CustomException("多 Sheet 导入缺少「" + ImportExportConfigJsonHelper.MAIN_SHEET_NAME + "」页。");
        }
        List<ColumnSpec> masterSpecs = filterMasterSpecs(parsed.getItems(), collectionRoots);
        List<Map<String, Object>> masterRows = parseSheetRowsWithLink(masterExcel, masterSpecs, attrModelTypeMap);
        Map<String, List<Map<String, Object>>> detailRowsByCollection = new LinkedHashMap<>();
        Set<String> usedSheetNames = new HashSet<>();
        usedSheetNames.add(ImportExportConfigJsonHelper.MAIN_SHEET_NAME);
        for (String collectionRoot : collectionRoots) {
            String detailSheetName = resolveDetailSheetName(collectionRoot, titleMap, usedSheetNames);
            List<List<String>> detailExcel = allSheets.get(detailSheetName);
            if (detailExcel == null) {
                throw new CustomException("多 Sheet 导入缺少明细页「" + detailSheetName + "」。");
            }
            List<ColumnSpec> detailSpecs = filterDetailSpecs(parsed.getItems(), collectionRoot);
            detailRowsByCollection.put(collectionRoot,
                parseSheetRowsWithLink(detailExcel, detailSpecs, attrModelTypeMap));
        }
        try {
            return ImportExportRowUtil.assembleFromMultiSheets(masterRows, detailRowsByCollection, collectionRoots);
        } catch (IllegalArgumentException ex) {
            throw new CustomException(ex.getMessage());
        }
    }

    private List<Map<String, Object>> parseSheetRowsWithLink(List<List<String>> excelRows, List<ColumnSpec> specs,
                                                             Map<String, Integer> attrModelTypeMap) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (CollectionUtil.isEmpty(excelRows)) {
            return rows;
        }
        // 列顺序：主表序号 + 配置列
        for (List<String> excelRow : excelRows) {
            if (isBlankExcelRow(excelRow)) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            String link = excelRow.isEmpty() ? StrUtil.EMPTY : excelRow.get(0);
            if (StrUtil.isNotBlank(link)) {
                row.put(ImportExportConfigJsonHelper.LINK_ATTR_KEY, link.trim());
            }
            for (int i = 0; i < specs.size(); i++) {
                int col = i + 1;
                ColumnSpec spec = specs.get(i);
                String cell = col < excelRow.size() ? excelRow.get(col) : StrUtil.EMPTY;
                putImportCellValue(row, spec.getAttrKey(), cell, attrModelTypeMap.get(spec.getAttrKey()));
            }
            if (!row.isEmpty()) {
                rows.add(row);
            }
        }
        return rows;
    }

    private boolean isBlankExcelRow(List<String> excelRow) {
        if (CollectionUtil.isEmpty(excelRow)) {
            return true;
        }
        for (String cell : excelRow) {
            if (StrUtil.isNotBlank(cell)) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Integer> buildAttrModelTypeMap(String appId, String className) {
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        Map<String, Integer> map = new HashMap<>();
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return map;
        }
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            Integer modelType = attrDefinition.getAttrModelType() == null
                ? AttrModelType.SCALAR.getKey() : attrDefinition.getAttrModelType();
            map.put(attrDefinition.getAttrKey(), modelType);
            // 对象/集合拆列：点路径叶子类型一并写入，导入时按叶子类型解析单元格
            if (AttrModelType.OBJECT.getKey().equals(modelType) || AttrModelType.COLLECTION.getKey().equals(modelType)) {
                List<AttrDefinition> children = attrDefinitionService.queryChildAttrDefinitionList(
                    appId, className, attrDefinition.getAttrKey());
                if (CollectionUtil.isEmpty(children)) {
                    continue;
                }
                for (AttrDefinition child : children) {
                    Integer childType = child.getAttrModelType() == null
                        ? AttrModelType.SCALAR.getKey() : child.getAttrModelType();
                    map.put(attrDefinition.getAttrKey() + "." + child.getAttrKey(), childType);
                }
            }
        }
        return map;
    }

    /**
     * 写入导入单元格：对象/集合字段若为 JSON 则反序列化；attrKey 含点号时按嵌套路径写入。
     */
    @SuppressWarnings("unchecked")
    private void putImportCellValue(Map<String, Object> row, String attrKey, String cell, Integer attrModelType) {
        if (StrUtil.isBlank(attrKey)) {
            return;
        }
        Object value = cell;
        if (StrUtil.isBlank(cell)) {
            value = null;
        } else if (AttrModelType.OBJECT.getKey().equals(attrModelType) || AttrModelType.COLLECTION.getKey().equals(attrModelType)) {
            String trimmed = cell.trim();
            if (JSONUtil.isTypeJSON(trimmed)) {
                if (trimmed.startsWith("[")) {
                    value = JSONUtil.toList(trimmed, Object.class);
                } else {
                    value = JSONUtil.toBean(trimmed, Map.class);
                }
            }
        }
        if (!attrKey.contains(".")) {
            if (value != null) {
                row.put(attrKey, value);
            }
            return;
        }
        String[] parts = attrKey.split("\\.");
        Map<String, Object> cursor = row;
        for (int i = 0; i < parts.length - 1; i++) {
            Object child = cursor.get(parts[i]);
            if (!(child instanceof Map)) {
                Map<String, Object> nested = new LinkedHashMap<>();
                cursor.put(parts[i], nested);
                cursor = nested;
            } else {
                cursor = (Map<String, Object>) child;
            }
        }
        if (value != null) {
            cursor.put(parts[parts.length - 1], value);
        }
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

    private void sendImportExportJsonToExcelJob(ImportExportConfig config, ParsedConfig parsed, Map<String, String> titleMap,
                                                String filePath, List<String> collectionRoots, InputObject inputObject) {
        List<ColumnSpec> specs = parsed.getItems();
        SheetLayoutOptions layout = parsed.getLayout();
        String userId = inputObject.getLogParams().get("id").toString();
        String safeName = StrUtil.blankToDefault(config.getName(), "导入导出");
        Map<String, Object> json = new HashMap<>();
        json.put("title", safeName + "导出");
        json.put("type", MqConstants.JobMateMationJobType.IMPORT_EXPORT_JSON_TO_EXCEL.getJobType());
        json.put("filePath", filePath);
        json.put("userId", userId);
        json.put("tenantId", tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY);
        boolean useMulti = shouldUseMultiSheet(parsed, collectionRoots);
        json.put("sheetMode", useMulti ? ImportExportConfigJsonHelper.SHEET_MODE_MULTI : ImportExportConfigJsonHelper.SHEET_MODE_SINGLE);
        if (useMulti) {
            List<ColumnSpec> masterSpecs = filterMasterSpecs(specs, collectionRoots);
            String[] masterKeys = buildKeysWithLink(masterSpecs);
            ExcelUtil.SheetExportStyle masterStyle = buildSheetExportStyleWithLink(masterSpecs, layout);
            applyColumnDropdownOptions(masterStyle, masterKeys, config.getAppId(), config.getClassName());
            json.put("collectionAttrKeys", collectionRoots);
            json.put("masterKeys", masterKeys);
            json.put("masterColumnNames", buildNamesWithLink(masterSpecs, titleMap));
            json.put("masterExportStyleJson", JSONUtil.toJsonStr(masterStyle));
            List<Map<String, Object>> detailSheets = new ArrayList<>();
            Set<String> usedSheetNames = new HashSet<>();
            usedSheetNames.add(ImportExportConfigJsonHelper.MAIN_SHEET_NAME);
            for (String collectionRoot : collectionRoots) {
                List<ColumnSpec> detailSpecs = filterDetailSpecs(specs, collectionRoot);
                String[] detailKeys = buildKeysWithLink(detailSpecs);
                ExcelUtil.SheetExportStyle detailStyle = buildSheetExportStyleWithLink(detailSpecs, layout);
                applyColumnDropdownOptions(detailStyle, detailKeys, config.getAppId(), config.getClassName());
                Map<String, Object> one = new LinkedHashMap<>();
                one.put("collectionAttrKey", collectionRoot);
                one.put("sheetName", resolveDetailSheetName(collectionRoot, titleMap, usedSheetNames));
                one.put("keys", detailKeys);
                one.put("columnNames", buildNamesWithLink(detailSpecs, titleMap));
                one.put("exportStyleJson", JSONUtil.toJsonStr(detailStyle));
                detailSheets.add(one);
            }
            json.put("detailSheets", detailSheets);
        } else {
            int n = specs.size();
            String[] keys = new String[n];
            String[] columnNames = new String[n];
            for (int i = 0; i < n; i++) {
                ColumnSpec spec = specs.get(i);
                keys[i] = spec.getAttrKey();
                columnNames[i] = resolveColumnTitle(spec, titleMap);
            }
            json.put("keys", keys);
            json.put("columnNames", columnNames);
            if (CollectionUtil.isNotEmpty(collectionRoots)) {
                json.put("collectionAttrKey", collectionRoots.get(0));
            }
            ExcelUtil.SheetExportStyle exportStyle = buildSheetExportStyle(specs, layout);
            applyHeaderGroupNames(keys, columnNames, exportStyle, titleMap, layout);
            applyColumnDropdownOptions(exportStyle, keys, config.getAppId(), config.getClassName());
            json.put("exportStyleJson", JSONUtil.toJsonStr(exportStyle));
        }
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(json));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    private List<String> resolveCollectionRootsForSpecs(String appId, String className, List<ColumnSpec> specs) {
        Set<String> collectionKeys = new HashSet<>();
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isNotEmpty(attrDefinitionList)) {
            for (AttrDefinition attrDefinition : attrDefinitionList) {
                if (AttrModelType.COLLECTION.getKey().equals(attrDefinition.getAttrModelType())) {
                    collectionKeys.add(attrDefinition.getAttrKey());
                }
            }
        }
        List<String> attrKeys = specs.stream().map(ColumnSpec::getAttrKey).collect(Collectors.toList());
        return ImportExportRowUtil.resolveCollectionRoots(attrKeys, collectionKeys);
    }

    private boolean shouldUseMultiSheet(ParsedConfig parsed, List<String> collectionRoots) {
        if (CollectionUtil.isEmpty(collectionRoots)) {
            return false;
        }
        // 多个明细集合必须走多 Sheet；单个集合则尊重配置的 sheetMode
        if (collectionRoots.size() > 1) {
            return true;
        }
        return ImportExportConfigJsonHelper.isMultiSheet(parsed);
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
     * 单 Sheet 父子点路径列：写入一级父标题（连续同父横向合并），供 ExcelUtil 生成两级表头。
     */
    private void applyHeaderGroupNames(String[] keys, String[] columnNames, ExcelUtil.SheetExportStyle style,
                                       Map<String, String> titleMap, SheetLayoutOptions layout) {
        if (style == null || keys == null || columnNames == null || keys.length != columnNames.length) {
            return;
        }
        if (!ExcelUtil.needTwoLevelHeader(keys)) {
            return;
        }
        Map<String, HeaderGroupStyle> groupStyleMap = layout != null ? layout.getHeaderGroups() : null;
        String[] groups = new String[keys.length];
        String[] groupBgs = new String[keys.length];
        String[] groupFgs = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (StrUtil.isBlank(key) || !key.contains(".")) {
                groups[i] = null;
                continue;
            }
            String parentKey = key.substring(0, key.indexOf('.'));
            HeaderGroupStyle gs = groupStyleMap != null ? groupStyleMap.get(parentKey) : null;
            String parentTitle = gs != null ? gs.getTitle() : null;
            if (StrUtil.isBlank(parentTitle)) {
                parentTitle = titleMap != null ? titleMap.get(parentKey) : null;
            }
            if (StrUtil.isBlank(parentTitle)) {
                String title = columnNames[i];
                parentTitle = StrUtil.isNotBlank(title) && title.contains(".")
                    ? title.substring(0, title.indexOf('.')) : parentKey;
            }
            groups[i] = parentTitle;
            if (gs != null) {
                groupBgs[i] = StrUtil.blankToDefault(gs.getHeaderBackgroundColor(), null);
                groupFgs[i] = StrUtil.blankToDefault(gs.getHeaderFontColor(), null);
            }
        }
        style.headerGroupNames = groups;
        style.headerGroupBackgroundColors = groupBgs;
        style.headerGroupFontColors = groupFgs;
    }

    /**
     * 按属性数据来源（枚举 / 字典 / 自定义 JSON）为列附加 Excel 下拉选项。
     */
    private void applyColumnDropdownOptions(ExcelUtil.SheetExportStyle style, String[] keys,
                                            String appId, String className) {
        if (style == null || keys == null || keys.length == 0) {
            return;
        }
        String[][] options = new String[keys.length][];
        boolean any = false;
        for (int i = 0; i < keys.length; i++) {
            String[] labels = resolveDropdownLabels(appId, className, keys[i]);
            if (labels != null && labels.length > 0) {
                options[i] = labels;
                any = true;
            }
        }
        if (any) {
            style.columnDropdownOptions = options;
        }
    }

    private String[] resolveDropdownLabels(String appId, String className, String attrKey) {
        if (StrUtil.isBlank(attrKey) || ImportExportConfigJsonHelper.LINK_ATTR_KEY.equals(attrKey)) {
            return null;
        }
        AttrDefinition attr = resolveAttrDefinitionForColumn(appId, className, attrKey);
        if (attr == null) {
            return null;
        }
        // 1) 属性上直接配置的枚举类
        List<String> fromEnum = loadEnumLabels(attr.getEnumClassStr());
        if (CollectionUtil.isNotEmpty(fromEnum)) {
            return fromEnum.toArray(new String[0]);
        }
        AttrDefinitionCustom custom = attr.getAttrDefinitionCustom();
        if (custom != null && custom.getDataType() != null) {
            Integer dataType = custom.getDataType();
            // 2) 自定义：枚举
            if (AttrKeyDataType.ENUM_DATA.getKey().equals(dataType)) {
                List<String> labels = loadEnumLabels(custom.getObjectId());
                if (CollectionUtil.isEmpty(labels)) {
                    labels = loadEnumLabels(custom.getEnumClassStr());
                }
                if (CollectionUtil.isNotEmpty(labels)) {
                    return labels.toArray(new String[0]);
                }
            }
            // 3) 数据字典
            if (AttrKeyDataType.DICT_DATA.getKey().equals(dataType) && StrUtil.isNotBlank(custom.getObjectId())) {
                List<String> labels = loadDictLabels(custom.getObjectId());
                if (CollectionUtil.isNotEmpty(labels)) {
                    return labels.toArray(new String[0]);
                }
            }
            // 4) 自定义 JSON
            if (AttrKeyDataType.CUSTOM.getKey().equals(dataType) && StrUtil.isNotBlank(custom.getDefaultData())) {
                List<String> labels = loadCustomJsonLabels(custom.getDefaultData());
                if (CollectionUtil.isNotEmpty(labels)) {
                    return labels.toArray(new String[0]);
                }
            }
        }
        // 5) 兼容「参考#EnumClass」写在名称/备注中的情况
        String ref = extractEnumRefFromText(attr.getName());
        if (StrUtil.isBlank(ref)) {
            ref = extractEnumRefFromText(attr.getRemark());
        }
        if (StrUtil.isBlank(ref) && custom != null) {
            ref = extractEnumRefFromText(custom.getRemark());
        }
        fromEnum = loadEnumLabels(ref);
        return CollectionUtil.isEmpty(fromEnum) ? null : fromEnum.toArray(new String[0]);
    }

    /** 从「询价状态，参考#PurchaseRequestChildInquiry」一类文案提取枚举类名 */
    private String extractEnumRefFromText(String text) {
        if (StrUtil.isBlank(text) || !text.contains("#")) {
            return null;
        }
        int idx = text.lastIndexOf('#');
        if (idx < 0 || idx >= text.length() - 1) {
            return null;
        }
        String ref = text.substring(idx + 1).trim();
        ref = ref.replaceAll("[，,;；\\s].*$", "");
        return StrUtil.isBlank(ref) ? null : ref;
    }

    private AttrDefinition resolveAttrDefinitionForColumn(String appId, String className, String attrKey) {
        if (!attrKey.contains(".")) {
            return attrDefinitionService.queryAttrDefinition(appId, className, attrKey);
        }
        String parentKey = attrKey.substring(0, attrKey.indexOf('.'));
        String childKey = attrKey.substring(attrKey.indexOf('.') + 1);
        List<AttrDefinition> children = attrDefinitionService.queryChildAttrDefinitionList(appId, className, parentKey);
        if (CollectionUtil.isEmpty(children)) {
            return null;
        }
        for (AttrDefinition child : children) {
            if (StrUtil.equals(childKey, child.getAttrKey())) {
                return child;
            }
        }
        return null;
    }

    private List<String> loadEnumLabels(String enumClassStr) {
        List<String> labels = new ArrayList<>();
        for (String className : normalizeEnumClassNames(enumClassStr)) {
            try {
                List<Map<String, Object>> list = skyeyeClassEnumService.queryEnumDataList(className, StrUtil.EMPTY, StrUtil.EMPTY);
                if (CollectionUtil.isEmpty(list)) {
                    continue;
                }
                for (Map<String, Object> one : list) {
                    if (one == null) {
                        continue;
                    }
                    Object name = one.get("name");
                    if (name == null) {
                        name = one.get("value");
                    }
                    if (name != null && StrUtil.isNotBlank(String.valueOf(name))) {
                        labels.add(String.valueOf(name).trim());
                    }
                }
                if (CollectionUtil.isNotEmpty(labels)) {
                    return labels;
                }
            } catch (Exception ignore) {
                // 枚举未注册时忽略，继续尝试其它 className 形态
            }
        }
        return labels;
    }

    private List<String> normalizeEnumClassNames(String enumClassStr) {
        List<String> names = new ArrayList<>();
        if (StrUtil.isBlank(enumClassStr)) {
            return names;
        }
        String s = enumClassStr.trim();
        if (s.contains("#")) {
            s = s.substring(s.indexOf('#') + 1).trim();
        }
        if (StrUtil.isNotBlank(s)) {
            names.add(s);
            int dot = s.lastIndexOf('.');
            if (dot >= 0 && dot < s.length() - 1) {
                String simple = s.substring(dot + 1);
                if (!names.contains(simple)) {
                    names.add(simple);
                }
            }
        }
        return names;
    }

    private List<String> loadDictLabels(String dictTypeCode) {
        List<String> labels = new ArrayList<>();
        try {
            SysDictType dictType = sysDictTypeService.queryDictTypeIdByDictCode(dictTypeCode, EnableEnum.ENABLE_USING.getKey());
            if (dictType == null) {
                return labels;
            }
            QueryWrapper<SysDictData> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysDictData::getDictTypeId), dictType.getId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(SysDictData::getEnabled), EnableEnum.ENABLE_USING.getKey());
            queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SysDictData::getDictSort));
            List<SysDictData> dictDataList = sysDictDataService.list(queryWrapper);
            if (CollectionUtil.isEmpty(dictDataList)) {
                return labels;
            }
            for (SysDictData data : dictDataList) {
                if (data != null && StrUtil.isNotBlank(data.getDictName())) {
                    labels.add(data.getDictName().trim());
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
        return labels;
    }

    private List<String> loadCustomJsonLabels(String defaultData) {
        List<String> labels = new ArrayList<>();
        try {
            if (!JSONUtil.isTypeJSONArray(defaultData)) {
                return labels;
            }
            JSONArray arr = JSONUtil.parseArray(defaultData);
            for (int i = 0; i < arr.size(); i++) {
                Object item = arr.get(i);
                if (item instanceof JSONObject) {
                    JSONObject obj = (JSONObject) item;
                    String name = obj.getStr("name");
                    if (StrUtil.isBlank(name)) {
                        name = obj.getStr("label");
                    }
                    if (StrUtil.isBlank(name)) {
                        name = obj.getStr("title");
                    }
                    if (StrUtil.isBlank(name) && obj.get("id") != null) {
                        name = String.valueOf(obj.get("id"));
                    }
                    if (StrUtil.isNotBlank(name)) {
                        labels.add(name.trim());
                    }
                } else if (item != null) {
                    labels.add(String.valueOf(item).trim());
                }
            }
        } catch (Exception ignore) {
            // ignore
        }
        return labels;
    }

    /**
     * 按 appId、className、配置id
     */
    private ImportExportConfig resolveConfigForDownload(Map<String, Object> params, Integer configType) {
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        String id = params.get("id").toString();
        ImportExportConfig config = selectById(id);
        if (config != null && (!StrUtil.equals(appId, config.getAppId()) || !StrUtil.equals(className, config.getClassName()))) {
            throw new CustomException("该配置不属于当前业务对象.");
        }
        if (config != null && !Objects.equals(configType, config.getConfigType())) {
            throw new CustomException("该配置类型与当前操作不匹配.");
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
            Integer modelType = attrDefinition.getAttrModelType() == null
                ? AttrModelType.SCALAR.getKey() : attrDefinition.getAttrModelType();
            if (!AttrModelType.OBJECT.getKey().equals(modelType) && !AttrModelType.COLLECTION.getKey().equals(modelType)) {
                continue;
            }
            List<AttrDefinition> children = attrDefinitionService.queryChildAttrDefinitionList(
                appId, className, attrDefinition.getAttrKey());
            if (CollectionUtil.isEmpty(children)) {
                continue;
            }
            for (AttrDefinition child : children) {
                String childTitle = child.getAttrDefinitionCustom() != null
                    && StrUtil.isNotBlank(child.getAttrDefinitionCustom().getName())
                    ? child.getAttrDefinitionCustom().getName()
                    : child.getName();
                map.put(attrDefinition.getAttrKey() + "." + child.getAttrKey(), title + "." + childTitle);
            }
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

    private void writeExcelByParsedConfig(String configName, String fileSuffix, ParsedConfig parsed,
                                          Map<String, String> titleMap, String appId, String className,
                                          List<Map<String, Object>> rows) {
        List<ColumnSpec> specs = parsed.getItems();
        SheetLayoutOptions layout = parsed.getLayout();
        List<String> collectionRoots = resolveCollectionRootsForSpecs(appId, className, specs);
        String safeName = StrUtil.blankToDefault(configName, "导入导出") + fileSuffix;
        if (shouldUseMultiSheet(parsed, collectionRoots)) {
            writeMultiSheetExcel(safeName, specs, titleMap, layout, rows, collectionRoots, appId, className);
            return;
        }
        // 单 Sheet：0 或 1 个明细集合
        if (!ImportExportConfigJsonHelper.isMultiSheet(parsed) && collectionRoots.size() > 1) {
            throw new CustomException("单 Sheet 模式仅支持一个明细集合，当前勾选了多个: " + collectionRoots
                + "。请切换为「多 Sheet」模式。");
        }
        String collectionRoot = collectionRoots.isEmpty() ? null : collectionRoots.get(0);
        List<Map<String, Object>> outRows = rows;
        if (outRows != null && StrUtil.isNotBlank(collectionRoot)) {
            outRows = ImportExportRowUtil.flattenByCollection(outRows, collectionRoot);
        }
        String[] keys = new String[specs.size()];
        String[] columnNames = new String[specs.size()];
        for (int i = 0; i < specs.size(); i++) {
            keys[i] = specs.get(i).getAttrKey();
            columnNames[i] = resolveColumnTitle(specs.get(i), titleMap);
        }
        ExcelUtil.SheetExportStyle exportStyle = buildSheetExportStyle(specs, layout);
        applyHeaderGroupNames(keys, columnNames, exportStyle, titleMap, layout);
        // 导入模板 / 导出均按数据来源加下拉，便于选择枚举、字典等
        applyColumnDropdownOptions(exportStyle, keys, appId, className);
        ExcelUtil.createWorkBook(safeName, fileSuffix, outRows, keys, columnNames, new String[0],
            PutObject.getResponse(), exportStyle);
    }

    private void writeMultiSheetExcel(String fileName, List<ColumnSpec> specs, Map<String, String> titleMap,
                                      SheetLayoutOptions layout, List<Map<String, Object>> rows,
                                      List<String> collectionRoots, String appId, String className) {
        List<ColumnSpec> masterSpecs = filterMasterSpecs(specs, collectionRoots);
        if (CollectionUtil.isEmpty(masterSpecs)) {
            throw new CustomException("多 Sheet 模式至少需要勾选一个主表字段。");
        }
        boolean hasAnyDetail = false;
        for (String root : collectionRoots) {
            if (CollectionUtil.isNotEmpty(filterDetailSpecs(specs, root))) {
                hasAnyDetail = true;
                break;
            }
        }
        if (!hasAnyDetail) {
            throw new CustomException("多 Sheet 模式至少需要勾选一个明细字段。");
        }
        ImportExportRowUtil.MultiSheetRows split = rows == null
            ? ImportExportRowUtil.splitToMultiSheets(null, collectionRoots)
            : ImportExportRowUtil.splitToMultiSheets(rows, collectionRoots);
        List<Map<String, Object>> sheetDefs = new ArrayList<>();
        String[] masterKeys = buildKeysWithLink(masterSpecs);
        ExcelUtil.SheetExportStyle masterStyle = buildSheetExportStyleWithLink(masterSpecs, layout);
        applyColumnDropdownOptions(masterStyle, masterKeys, appId, className);
        sheetDefs.add(buildSheetDef(ImportExportConfigJsonHelper.MAIN_SHEET_NAME,
            masterKeys, buildNamesWithLink(masterSpecs, titleMap),
            split.getMasterRows(), masterStyle));
        Set<String> usedSheetNames = new HashSet<>();
        usedSheetNames.add(ImportExportConfigJsonHelper.MAIN_SHEET_NAME);
        for (String collectionRoot : collectionRoots) {
            List<ColumnSpec> detailSpecs = filterDetailSpecs(specs, collectionRoot);
            if (CollectionUtil.isEmpty(detailSpecs)) {
                continue;
            }
            List<Map<String, Object>> detailRows = split.getDetailRowsByCollection()
                .getOrDefault(collectionRoot, new ArrayList<>());
            String[] detailKeys = buildKeysWithLink(detailSpecs);
            ExcelUtil.SheetExportStyle detailStyle = buildSheetExportStyleWithLink(detailSpecs, layout);
            applyColumnDropdownOptions(detailStyle, detailKeys, appId, className);
            sheetDefs.add(buildSheetDef(resolveDetailSheetName(collectionRoot, titleMap, usedSheetNames),
                detailKeys, buildNamesWithLink(detailSpecs, titleMap),
                detailRows, detailStyle));
        }
        ExcelUtil.createMultiSheetWorkBook(fileName, sheetDefs, PutObject.getResponse());
    }

    private Map<String, Object> buildSheetDef(String sheetName, String[] keys, String[] columnNames,
                                              List<Map<String, Object>> rows, ExcelUtil.SheetExportStyle style) {
        Map<String, Object> def = new LinkedHashMap<>();
        def.put("sheetName", sheetName);
        def.put("keys", keys);
        def.put("columnNames", columnNames);
        def.put("rows", rows);
        def.put("exportStyle", style);
        return def;
    }

    private List<ColumnSpec> filterMasterSpecs(List<ColumnSpec> specs, List<String> collectionRoots) {
        List<ColumnSpec> list = new ArrayList<>();
        for (ColumnSpec spec : specs) {
            if (!isAnyDetailSpec(spec, collectionRoots)) {
                list.add(spec);
            }
        }
        return list;
    }

    private List<ColumnSpec> filterDetailSpecs(List<ColumnSpec> specs, String collectionRoot) {
        List<ColumnSpec> list = new ArrayList<>();
        for (ColumnSpec spec : specs) {
            if (isDetailSpec(spec, collectionRoot)) {
                list.add(spec);
            }
        }
        return list;
    }

    private boolean isAnyDetailSpec(ColumnSpec spec, List<String> collectionRoots) {
        if (CollectionUtil.isEmpty(collectionRoots)) {
            return false;
        }
        for (String root : collectionRoots) {
            if (isDetailSpec(spec, root)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDetailSpec(ColumnSpec spec, String collectionRoot) {
        if (StrUtil.isBlank(collectionRoot) || spec == null || StrUtil.isBlank(spec.getAttrKey())) {
            return false;
        }
        if (collectionRoot.equals(spec.getSheetKey())) {
            return true;
        }
        return spec.getAttrKey().startsWith(collectionRoot + ".");
    }

    private String resolveDetailSheetName(String collectionRoot, Map<String, String> titleMap, Set<String> usedNames) {
        String name = titleMap.get(collectionRoot);
        String base = StrUtil.blankToDefault(name, collectionRoot);
        String finalName = base;
        int idx = 2;
        while (usedNames.contains(finalName)) {
            finalName = base + "(" + idx++ + ")";
        }
        usedNames.add(finalName);
        return finalName;
    }

    private String resolveColumnTitle(ColumnSpec spec, Map<String, String> titleMap) {
        String header = StrUtil.isNotBlank(spec.getColumnTitle()) ? spec.getColumnTitle() : titleMap.get(spec.getAttrKey());
        return StrUtil.blankToDefault(header, spec.getAttrKey());
    }

    private String[] buildKeysWithLink(List<ColumnSpec> specs) {
        String[] keys = new String[specs.size() + 1];
        keys[0] = ImportExportConfigJsonHelper.LINK_ATTR_KEY;
        for (int i = 0; i < specs.size(); i++) {
            keys[i + 1] = specs.get(i).getAttrKey();
        }
        return keys;
    }

    private String[] buildNamesWithLink(List<ColumnSpec> specs, Map<String, String> titleMap) {
        String[] names = new String[specs.size() + 1];
        names[0] = ImportExportConfigJsonHelper.LINK_COLUMN_TITLE;
        for (int i = 0; i < specs.size(); i++) {
            names[i + 1] = resolveColumnTitle(specs.get(i), titleMap);
        }
        return names;
    }

    private ExcelUtil.SheetExportStyle buildSheetExportStyleWithLink(List<ColumnSpec> specs, SheetLayoutOptions layout) {
        List<ColumnSpec> withLink = new ArrayList<>();
        ColumnSpec link = new ColumnSpec();
        link.setAttrKey(ImportExportConfigJsonHelper.LINK_ATTR_KEY);
        link.setColumnTitle(ImportExportConfigJsonHelper.LINK_COLUMN_TITLE);
        withLink.add(link);
        withLink.addAll(specs);
        return buildSheetExportStyle(withLink, layout);
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

