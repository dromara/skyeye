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
 * 业务对象导入导出配置服务。
 * <p>
 * 主流程：
 * <ol>
 *   <li>下载模板 {@link #downloadImportTemplate}：按配置列写出空 Excel（含隐藏 attrKey、两级表头、下拉）</li>
 *   <li>导出 {@link #exportByConfig}：查数据 → 小数据同步写 Excel；大数据落 JSON 后发 MQ 异步转 Excel</li>
 *   <li>导入 {@link #importByConfig}：读 Excel（按 attrKey 匹配列）→ 主从组装 → 批量入库</li>
 * </ol>
 * Sheet 模式：
 * <ul>
 *   <li>single：主表+明细同页；多集合并排列，按最大条数对齐行</li>
 *   <li>multi：主表 Sheet + 各明细 Sheet，用「主表序号」{@code __rowNo} 关联</li>
 * </ul>
 *
 * @author skyeye云系列--卫志强
 * @date 2026/4/8 22:10
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
        // 批量填充对象/集合子属性，避免循环内逐条 SQL
        attrDefinitionService.fillChildAttrDefinitions(appId, attrDefinitionList);

        List<ImportExportFieldOption> result = new ArrayList<>();
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            String parentName = resolveAttrDisplayName(attrDefinition, customMap.get(attrDefinition.getAttrKey()));
            Integer modelType = attrDefinition.getAttrModelType() == null
                ? AttrModelType.SCALAR.getKey() : attrDefinition.getAttrModelType();

            if (AttrModelType.OBJECT.getKey().equals(modelType) || AttrModelType.COLLECTION.getKey().equals(modelType)) {
                List<AttrDefinition> children = attrDefinition.getChildAttrDefinitions();
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

    /**
     * 下载导入模板（无数据行）。
     * <pre>
     * 1. 解析配置列 / 布局
     * 2. 一次加载属性元数据（标题、下拉数据源）
     * 3. 无配置列时回退默认导入字段
     * 4. 写出 Excel（rows=null，只含表头/键行/下拉）
     * </pre>
     */
    @Override
    public void downloadImportTemplate(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // ① 取导入类型配置
        ImportExportConfig config = resolveConfigForDownload(params, ImportExportConfigTypeEnum.IMPORT.getKey());
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        // ② 解析 configJson → 列定义 + 样式布局
        ParsedConfig parsed = ImportExportConfigJsonHelper.parseConfig(config.getConfigJson());
        List<ColumnSpec> specs = parsed.getItems();
        SheetLayoutOptions layout = parsed.getLayout();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        // ③ 批量加载属性（避免后续下拉时 N+1）
        AttrMetaBundle attrMeta = loadAttrMetaBundle(appId, className);
        Map<String, String> titleMap = attrMeta.titleMap;
        // ④ 未保存过列时用默认导入字段
        if (CollectionUtil.isEmpty(specs)) {
            specs = buildDefaultImportColumnSpecs(appId, className, titleMap);
            layout = new SheetLayoutOptions();
            parsed.setItems(specs);
            parsed.setLayout(layout);
        }
        if (CollectionUtil.isEmpty(specs)) {
            throw new CustomException("未配置导入列且无可用属性，无法生成模板。");
        }
        // ⑤ 写出模板（无业务数据）
        writeExcelByParsedConfig(config.getName(), "导入模板", parsed, titleMap, appId, className, null, attrMeta.attrIndex);
    }

    /**
     * 按配置导出数据。
     * <pre>
     * 1. 解析配置列
     * 2. 按筛选条件拉取导出数据
     * 3. storageType=file → 异步 MQ（JSON→Excel）；否则同步写 Excel
     * </pre>
     */
    @Override
    public void exportByConfig(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // ① 取导出类型配置
        ImportExportConfig config = resolveConfigForDownload(params, ImportExportConfigTypeEnum.EXPORT.getKey());
        if (config == null) {
            throw new CustomException("未找到导入导出配置，请先保存配置。");
        }
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        AttrMetaBundle attrMeta = loadAttrMetaBundle(appId, className);
        Map<String, String> titleMap = attrMeta.titleMap;
        // ② 解析列配置，空则回退默认导出字段
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
        // ③ 查询导出数据（可能直返 rows，也可能落盘 JSON）
        Map<String, Object> filters = parseFilters(params.get("filters").toString());
        int limit = Integer.parseInt(params.get("limit").toString());
        CommonPageInfo pageInfo = buildExportCommonPageInfo(appId, className, filters, limit);
        ResultEntity result = iDataService.queryExportAllData(appId, className, pageInfo);
        List<String> collectionRoots = resolveCollectionRootsForSpecs(appId, className, specs);
        Map<String, Object> bean = result.getBean();
        // ④ 大数据：发异步任务，前端去「我的输出」下载
        if (bean != null && "file".equals(String.valueOf(bean.get("storageType")))) {
            String filePath = String.valueOf(bean.get("filePath"));
            sendImportExportJsonToExcelJob(config, parsed, titleMap, filePath, collectionRoots, inputObject, attrMeta.attrIndex);
            outputObject.setBean(buildAsyncExportTip(bean));
            outputObject.settotal(0);
            return;
        }
        // ⑤ 小数据：当前请求直接写 Excel 到 response
        List<Map<String, Object>> rows = result.getRows();
        if (rows == null) {
            rows = CollectionUtil.newArrayList();
        }
        writeExcelByParsedConfig(config.getName(), "导出数据", parsed, titleMap, appId, className, rows, attrMeta.attrIndex);
    }

    /**
     * 按配置导入 Excel。
     * <pre>
     * 1. 解析配置列、识别明细集合根
     * 2. 按 sheetMode 读单 Sheet / 多 Sheet
     * 3. 按隐藏 attrKey 行匹配列（无键行则按列序兼容旧模板）
     * 4. 主从扁平行组装成业务对象列表
     * 5. 批量入库
     * </pre>
     */
    @Override
    public void importByConfig(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        // ① 配置与列定义
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

        // ② 属性类型（点路径叶子）+ 明细集合根 + Sheet 模式
        Map<String, Integer> attrModelTypeMap = buildAttrModelTypeMap(appId, className);
        List<String> collectionRoots = resolveCollectionRootsForSpecs(appId, className, specs);
        boolean useMulti = shouldUseMultiSheet(parsed, collectionRoots);
        // ③ 读 Excel → 扁平行 / 多 Sheet 行，再组装为主表+明细列表
        List<Map<String, Object>> dataRows;
        if (useMulti) {
            dataRows = importRowsFromMultiSheet(parsed, titleMap, collectionRoots, attrModelTypeMap);
        } else {
            dataRows = importRowsFromSingleSheet(specs, collectionRoots, attrModelTypeMap);
        }
        if (CollectionUtil.isEmpty(dataRows)) {
            throw new CustomException("Excel 无有效数据行。");
        }
        if (dataRows.size() > 2000) {
            throw new CustomException("单次最多导入 2000 条主表数据，请拆分后重试.");
        }

        // ④ 调用数据服务批量导入
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

    /**
     * 单 Sheet 导入：
     * <pre>
     * 1. 读首个 Sheet（识别隐藏 attrKey 行 + 跳过 1/2 行可见表头）
     * 2. 单元格按键（或列序）写入嵌套 Map
     * 3. 有明细集合时：扁平行 → 主表+多集合列表
     * </pre>
     */
    private List<Map<String, Object>> importRowsFromSingleSheet(List<ColumnSpec> specs, List<String> collectionRoots,
                                                                Map<String, Integer> attrModelTypeMap) {
        ExcelUtil.SheetReadResult sheetRead;
        try {
            String[] keys = specs.stream().map(ColumnSpec::getAttrKey).toArray(String[]::new);
            // 点路径列 → 两级可见表头，跳过行数=2；否则=1（均不含隐藏键行）
            int headerRows = ExcelUtil.needTwoLevelHeader(keys) ? 2 : 1;
            sheetRead = ExcelUtil.readFirstSheetForImport(readUploadExcelFile().getInputStream(), headerRows,
                java.util.Arrays.asList(keys));
        } catch (IOException e) {
            throw new CustomException(e);
        }
        // 扁平行：每行 Map 已含 collection.xxx 嵌套
        List<Map<String, Object>> dataRows = parseSheetRowsByKeys(sheetRead, specs, attrModelTypeMap, false);
        if (dataRows.size() > 2000) {
            throw new CustomException("单次最多导入 2000 行（含明细行），请拆分后重试.");
        }
        // 主表列非空开新单；各集合有值则追加
        if (CollectionUtil.isNotEmpty(collectionRoots)) {
            List<String> masterKeys = specs.stream()
                .map(ColumnSpec::getAttrKey)
                .filter(key -> StrUtil.isNotBlank(key) && !isAnyCollectionPath(key, collectionRoots))
                .collect(Collectors.toList());
            try {
                dataRows = ImportExportRowUtil.assembleByCollections(dataRows, collectionRoots, masterKeys);
            } catch (IllegalArgumentException ex) {
                throw new CustomException(ex.getMessage());
            }
        }
        return dataRows;
    }

    /**
     * 多 Sheet 导入：
     * <pre>
     * 1. 按 Sheet 名准备期望 attrKey（含 __rowNo）
     * 2. 读全部 Sheet
     * 3. 主表 / 各明细页分别解析
     * 4. 按主表序号挂回明细列表
     * </pre>
     */
    private List<Map<String, Object>> importRowsFromMultiSheet(ParsedConfig parsed, Map<String, String> titleMap,
                                                               List<String> collectionRoots,
                                                               Map<String, Integer> attrModelTypeMap) {
        // ① 主表列 + 各明细 Sheet 名/列（与导出时命名规则一致）
        List<ColumnSpec> masterSpecs = filterMasterSpecs(parsed.getItems(), collectionRoots);
        Map<String, List<String>> expectedKeysBySheet = new LinkedHashMap<>();
        List<String> masterKeys = new ArrayList<>();
        masterKeys.add(ImportExportConfigJsonHelper.LINK_ATTR_KEY);
        for (ColumnSpec spec : masterSpecs) {
            if (StrUtil.isNotBlank(spec.getAttrKey())) {
                masterKeys.add(spec.getAttrKey());
            }
        }
        expectedKeysBySheet.put(ImportExportConfigJsonHelper.MAIN_SHEET_NAME, masterKeys);
        Set<String> usedSheetNames = new HashSet<>();
        usedSheetNames.add(ImportExportConfigJsonHelper.MAIN_SHEET_NAME);
        Map<String, String> detailSheetNameByRoot = new LinkedHashMap<>();
        Map<String, List<ColumnSpec>> detailSpecsByRoot = new LinkedHashMap<>();
        for (String collectionRoot : collectionRoots) {
            String detailSheetName = resolveDetailSheetName(collectionRoot, titleMap, usedSheetNames);
            detailSheetNameByRoot.put(collectionRoot, detailSheetName);
            List<ColumnSpec> detailSpecs = filterDetailSpecs(parsed.getItems(), collectionRoot);
            detailSpecsByRoot.put(collectionRoot, detailSpecs);
            List<String> detailKeys = new ArrayList<>();
            detailKeys.add(ImportExportConfigJsonHelper.LINK_ATTR_KEY);
            for (ColumnSpec spec : detailSpecs) {
                if (StrUtil.isNotBlank(spec.getAttrKey())) {
                    detailKeys.add(spec.getAttrKey());
                }
            }
            expectedKeysBySheet.put(detailSheetName, detailKeys);
        }
        // ② 读全部 Sheet（每页识别隐藏键行）
        Map<String, ExcelUtil.SheetReadResult> allSheets;
        try {
            allSheets = ExcelUtil.readAllSheetsForImport(readUploadExcelFile().getInputStream(), 1, expectedKeysBySheet);
        } catch (IOException e) {
            throw new CustomException(e);
        }
        // ③ 解析主表
        ExcelUtil.SheetReadResult masterExcel = allSheets.get(ImportExportConfigJsonHelper.MAIN_SHEET_NAME);
        if (masterExcel == null) {
            throw new CustomException("多 Sheet 导入缺少「" + ImportExportConfigJsonHelper.MAIN_SHEET_NAME + "」页。");
        }
        List<Map<String, Object>> masterRows = parseSheetRowsByKeys(masterExcel, masterSpecs, attrModelTypeMap, true);
        // ④ 解析各明细页
        Map<String, List<Map<String, Object>>> detailRowsByCollection = new LinkedHashMap<>();
        for (String collectionRoot : collectionRoots) {
            String detailSheetName = detailSheetNameByRoot.get(collectionRoot);
            ExcelUtil.SheetReadResult detailExcel = allSheets.get(detailSheetName);
            if (detailExcel == null) {
                throw new CustomException("多 Sheet 导入缺少明细页「" + detailSheetName + "」。");
            }
            detailRowsByCollection.put(collectionRoot,
                parseSheetRowsByKeys(detailExcel, detailSpecsByRoot.get(collectionRoot), attrModelTypeMap, true));
        }
        // ⑤ 按 __rowNo 把明细挂回主表
        try {
            return ImportExportRowUtil.assembleFromMultiSheets(masterRows, detailRowsByCollection, collectionRoots);
        } catch (IllegalArgumentException ex) {
            throw new CustomException(ex.getMessage());
        }
    }

    /**
     * 把 Excel 数据行转成业务 Map。
     * <ul>
     *   <li>有 attrKey 行：按键定位列（用户调换列顺序不影响）</li>
     *   <li>无键行（旧模板）：按配置列序；多 Sheet 时第 0 列是主表序号</li>
     * </ul>
     */
    private List<Map<String, Object>> parseSheetRowsByKeys(ExcelUtil.SheetReadResult sheetRead, List<ColumnSpec> specs,
                                                           Map<String, Integer> attrModelTypeMap, boolean withLink) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (sheetRead == null || CollectionUtil.isEmpty(sheetRead.dataRows)) {
            return rows;
        }
        // attrKey → 列下标
        Map<String, Integer> colByKey = null;
        if (sheetRead.hasAttrKeys()) {
            colByKey = new HashMap<>();
            for (int i = 0; i < sheetRead.attrKeys.length; i++) {
                String key = sheetRead.attrKeys[i];
                if (StrUtil.isNotBlank(key) && !colByKey.containsKey(key)) {
                    colByKey.put(key.trim(), i);
                }
            }
        }
        for (List<String> excelRow : sheetRead.dataRows) {
            if (isBlankExcelRow(excelRow)) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            // 多 Sheet：写入关联键 __rowNo
            if (withLink) {
                String link = resolveImportCell(excelRow, colByKey, ImportExportConfigJsonHelper.LINK_ATTR_KEY, 0);
                if (StrUtil.isNotBlank(link)) {
                    row.put(ImportExportConfigJsonHelper.LINK_ATTR_KEY, link.trim());
                }
            }
            // 配置列：点路径写入嵌套 Map（如 purchaseChild.materialId）
            for (int i = 0; i < specs.size(); i++) {
                ColumnSpec spec = specs.get(i);
                // 旧模板无键行时：多 Sheet 数据列从第 1 列开始（0 是序号）
                int fallbackCol = withLink ? i + 1 : i;
                String cell = resolveImportCell(excelRow, colByKey, spec.getAttrKey(), fallbackCol);
                putImportCellValue(row, spec.getAttrKey(), cell, attrModelTypeMap.get(spec.getAttrKey()));
            }
            if (!row.isEmpty()) {
                rows.add(row);
            }
        }
        return rows;
    }

    /** 优先按 attrKey 取列；无键映射时用 fallbackCol（列序兼容）。 */
    private String resolveImportCell(List<String> excelRow, Map<String, Integer> colByKey, String attrKey, int fallbackCol) {
        if (excelRow == null) {
            return StrUtil.EMPTY;
        }
        int col = fallbackCol;
        if (colByKey != null && StrUtil.isNotBlank(attrKey) && colByKey.containsKey(attrKey)) {
            col = colByKey.get(attrKey);
        }
        if (col < 0 || col >= excelRow.size()) {
            return StrUtil.EMPTY;
        }
        return excelRow.get(col) == null ? StrUtil.EMPTY : excelRow.get(col);
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
        attrDefinitionService.fillChildAttrDefinitions(appId, attrDefinitionList);
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            Integer modelType = attrDefinition.getAttrModelType() == null
                ? AttrModelType.SCALAR.getKey() : attrDefinition.getAttrModelType();
            map.put(attrDefinition.getAttrKey(), modelType);
            // 对象/集合拆列：点路径叶子类型一并写入，导入时按叶子类型解析单元格
            if (AttrModelType.OBJECT.getKey().equals(modelType) || AttrModelType.COLLECTION.getKey().equals(modelType)) {
                List<AttrDefinition> children = attrDefinition.getChildAttrDefinitions();
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
     * 写入导入单元格。
     * <pre>
     * 1. 空单元格 → 不写（避免空串污染）
     * 2. 对象/集合整列 JSON → 反序列化
     * 3. 无点号 → 顶层 put；有点号 → 逐级创建嵌套 Map 后写叶子
     * </pre>
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
            // 整列存 JSON 字符串时解析
            String trimmed = cell.trim();
            if (JSONUtil.isTypeJSON(trimmed)) {
                if (trimmed.startsWith("[")) {
                    value = JSONUtil.toList(trimmed, Object.class);
                } else {
                    value = JSONUtil.toBean(trimmed, Map.class);
                }
            }
        }
        // 简单字段
        if (!attrKey.contains(".")) {
            if (value != null) {
                row.put(attrKey, value);
            }
            return;
        }
        // 点路径：a.b.c → row.a.b.c
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

    /**
     * 大数据异步导出：把「列配置 + 样式 + JSON 文件路径」打成 MQ 任务，
     * 由 {@code ImportExportJsonToExcelConsume} 消费后生成 Excel。
     */
    private void sendImportExportJsonToExcelJob(ImportExportConfig config, ParsedConfig parsed, Map<String, String> titleMap,
                                                String filePath, List<String> collectionRoots, InputObject inputObject,
                                                Map<String, AttrDefinition> attrIndex) {
        List<ColumnSpec> specs = parsed.getItems();
        SheetLayoutOptions layout = parsed.getLayout();
        String userId = inputObject.getLogParams().get("id").toString();
        String safeName = StrUtil.blankToDefault(config.getName(), "导入导出");
        // ① 任务公共参数
        Map<String, Object> json = new HashMap<>();
        json.put("title", safeName + "导出");
        json.put("type", MqConstants.JobMateMationJobType.IMPORT_EXPORT_JSON_TO_EXCEL.getJobType());
        json.put("filePath", filePath);
        json.put("userId", userId);
        json.put("tenantId", tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY);
        boolean useMulti = shouldUseMultiSheet(parsed, collectionRoots);
        json.put("sheetMode", useMulti ? ImportExportConfigJsonHelper.SHEET_MODE_MULTI : ImportExportConfigJsonHelper.SHEET_MODE_SINGLE);
        Map<String, AttrDefinition> index = attrIndex != null ? attrIndex
            : loadAttrMetaBundle(config.getAppId(), config.getClassName()).attrIndex;
        Map<String, String[]> enumLabelCache = new HashMap<>();
        if (useMulti) {
            List<ColumnSpec> masterSpecs = filterMasterSpecs(specs, collectionRoots);
            String[] masterKeys = buildKeysWithLink(masterSpecs);
            ExcelUtil.SheetExportStyle masterStyle = buildSheetExportStyleWithLink(masterSpecs, layout);
            applyColumnDropdownOptions(masterStyle, masterKeys, index, enumLabelCache);
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
                applyColumnDropdownOptions(detailStyle, detailKeys, index, enumLabelCache);
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
                json.put("collectionAttrKeys", collectionRoots);
                json.put("collectionAttrKey", collectionRoots.get(0));
            }
            ExcelUtil.SheetExportStyle exportStyle = buildSheetExportStyle(specs, layout);
            applyHeaderGroupNames(keys, columnNames, exportStyle, titleMap, layout);
            applyColumnDropdownOptions(exportStyle, keys, index, enumLabelCache);
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

    /** 是否走多 Sheet：无明细集合时一律单页；否则看配置 sheetMode。 */
    private boolean shouldUseMultiSheet(ParsedConfig parsed, List<String> collectionRoots) {
        if (CollectionUtil.isEmpty(collectionRoots)) {
            return false;
        }
        return ImportExportConfigJsonHelper.isMultiSheet(parsed);
    }

    /**
     * 组装导出样式：列宽、表头色、行高；并开启隐藏 attrKey 行（导入按键匹配）。
     */
    private ExcelUtil.SheetExportStyle buildSheetExportStyle(List<ColumnSpec> specs, SheetLayoutOptions layout) {
        int n = specs.size();
        ExcelUtil.SheetExportStyle s = new ExcelUtil.SheetExportStyle();
        s.writeAttrKeyRow = true;
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
     * 单 Sheet 两级表头：为连续同父列写入一级父标题名与组颜色，供 ExcelUtil 横向合并。
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
     * 使用预加载的属性索引，避免按列循环查库。
     */
    private void applyColumnDropdownOptions(ExcelUtil.SheetExportStyle style, String[] keys,
                                            Map<String, AttrDefinition> attrIndex,
                                            Map<String, String[]> enumLabelCache) {
        if (style == null || keys == null || keys.length == 0) {
            return;
        }
        if (attrIndex == null) {
            attrIndex = Collections.emptyMap();
        }
        if (enumLabelCache == null) {
            enumLabelCache = new HashMap<>();
        }
        try {
            String[][] options = new String[keys.length][];
            boolean any = false;
            for (int i = 0; i < keys.length; i++) {
                try {
                    String[] labels = resolveDropdownLabels(keys[i], attrIndex, enumLabelCache);
                    if (labels != null && labels.length > 0) {
                        options[i] = labels;
                        any = true;
                    }
                } catch (Exception ignore) {
                    // 单列失败跳过
                }
            }
            if (any) {
                style.columnDropdownOptions = options;
            }
        } catch (Exception ignore) {
            // 下拉解析失败不影响模板下载
        }
    }

    private String[] resolveDropdownLabels(String attrKey, Map<String, AttrDefinition> attrIndex,
                                           Map<String, String[]> enumLabelCache) {
        if (StrUtil.isBlank(attrKey) || ImportExportConfigJsonHelper.LINK_ATTR_KEY.equals(attrKey)) {
            return null;
        }
        AttrDefinition attr = attrIndex.get(attrKey);
        if (attr == null) {
            return null;
        }
        // 1) 属性上直接配置的枚举类
        List<String> fromEnum = loadEnumLabels(attr.getEnumClassStr(), enumLabelCache);
        if (CollectionUtil.isNotEmpty(fromEnum)) {
            return fromEnum.toArray(new String[0]);
        }
        AttrDefinitionCustom custom = attr.getAttrDefinitionCustom();
        if (custom != null && custom.getDataType() != null) {
            Integer dataType = custom.getDataType();
            // 2) 自定义：枚举
            if (AttrKeyDataType.ENUM_DATA.getKey().equals(dataType)) {
                List<String> labels = loadEnumLabels(custom.getObjectId(), enumLabelCache);
                if (CollectionUtil.isEmpty(labels)) {
                    labels = loadEnumLabels(custom.getEnumClassStr(), enumLabelCache);
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
        fromEnum = loadEnumLabels(ref, enumLabelCache);
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

    private List<String> loadEnumLabels(String enumClassStr, Map<String, String[]> enumLabelCache) {
        if (StrUtil.isBlank(enumClassStr)) {
            return Collections.emptyList();
        }
        String cacheKey = enumClassStr.trim();
        if (enumLabelCache != null && enumLabelCache.containsKey(cacheKey)) {
            String[] cached = enumLabelCache.get(cacheKey);
            if (cached == null || cached.length == 0) {
                return Collections.emptyList();
            }
            return Arrays.asList(cached);
        }
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
                    break;
                }
            } catch (Exception ignore) {
                // 枚举未注册时忽略，继续尝试其它 className 形态
            }
        }
        if (enumLabelCache != null) {
            enumLabelCache.put(cacheKey, labels.toArray(new String[0]));
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

    /** 标题映射 + 属性索引（含 parent.child），一次加载复用 */
    private static class AttrMetaBundle {
        private final Map<String, String> titleMap = new LinkedHashMap<>();
        private final Map<String, AttrDefinition> attrIndex = new HashMap<>();
    }

    private Map<String, String> buildAttrKeyTitleMap(String appId, String className) {
        return loadAttrMetaBundle(appId, className).titleMap;
    }

    /**
     * 一次查询主属性 + 批量填充子属性，同时构建标题与 attrKey 索引，供模板/导出下拉复用。
     */
    private AttrMetaBundle loadAttrMetaBundle(String appId, String className) {
        AttrMetaBundle bundle = new AttrMetaBundle();
        List<AttrDefinition> attrDefinitionList = attrDefinitionService.queryAttrDefinitionList(appId, className);
        if (CollectionUtil.isEmpty(attrDefinitionList)) {
            return bundle;
        }
        List<String> attrKeyList = attrDefinitionList.stream().map(AttrDefinition::getAttrKey).collect(Collectors.toList());
        Map<String, AttrDefinitionCustom> customMap = attrDefinitionCustomService.queryAttrDefinitionCustomMap(appId, className, attrKeyList);
        // 批量查子属性（ServiceBean + 子属性列表各一批），避免循环 SQL
        attrDefinitionService.fillChildAttrDefinitions(appId, attrDefinitionList);
        for (AttrDefinition attrDefinition : attrDefinitionList) {
            if (attrDefinition == null || StrUtil.isBlank(attrDefinition.getAttrKey())) {
                continue;
            }
            AttrDefinitionCustom custom = customMap.get(attrDefinition.getAttrKey());
            if (custom != null) {
                attrDefinition.setAttrDefinitionCustom(custom);
            }
            String title = custom != null && StrUtil.isNotBlank(custom.getName()) ? custom.getName() : attrDefinition.getName();
            bundle.titleMap.put(attrDefinition.getAttrKey(), title);
            bundle.attrIndex.put(attrDefinition.getAttrKey(), attrDefinition);
            Integer modelType = attrDefinition.getAttrModelType() == null
                ? AttrModelType.SCALAR.getKey() : attrDefinition.getAttrModelType();
            if (!AttrModelType.OBJECT.getKey().equals(modelType) && !AttrModelType.COLLECTION.getKey().equals(modelType)) {
                continue;
            }
            List<AttrDefinition> children = attrDefinition.getChildAttrDefinitions();
            if (CollectionUtil.isEmpty(children)) {
                continue;
            }
            for (AttrDefinition child : children) {
                if (child == null || StrUtil.isBlank(child.getAttrKey())) {
                    continue;
                }
                String childTitle = child.getAttrDefinitionCustom() != null
                    && StrUtil.isNotBlank(child.getAttrDefinitionCustom().getName())
                    ? child.getAttrDefinitionCustom().getName()
                    : child.getName();
                String dottedKey = attrDefinition.getAttrKey() + "." + child.getAttrKey();
                bundle.titleMap.put(dottedKey, title + "." + childTitle);
                bundle.attrIndex.put(dottedKey, child);
            }
        }
        return bundle;
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

    /**
     * 按配置写出 Excel（模板或导出数据）。
     * <pre>
     * multi  → 主表 Sheet + 各明细 Sheet
     * single → 明细并排展开后写单 Sheet（含隐藏 attrKey、两级表头、下拉、白边框）
     * </pre>
     */
    private void writeExcelByParsedConfig(String configName, String fileSuffix, ParsedConfig parsed,
                                          Map<String, String> titleMap, String appId, String className,
                                          List<Map<String, Object>> rows, Map<String, AttrDefinition> attrIndex) {
        List<ColumnSpec> specs = parsed.getItems();
        SheetLayoutOptions layout = parsed.getLayout();
        // ① 从列 attrKey 识别勾选了哪些明细集合
        List<String> collectionRoots = resolveCollectionRootsForSpecs(appId, className, specs);
        String safeName = StrUtil.blankToDefault(configName, "导入导出") + fileSuffix;
        Map<String, AttrDefinition> index = attrIndex != null ? attrIndex
            : loadAttrMetaBundle(appId, className).attrIndex;
        Map<String, String[]> enumLabelCache = new HashMap<>();
        // ② 多 Sheet
        if (shouldUseMultiSheet(parsed, collectionRoots)) {
            writeMultiSheetExcel(safeName, specs, titleMap, layout, rows, collectionRoots, index, enumLabelCache);
            return;
        }
        // ③ 单 Sheet：多集合按最大条数并排展开（模板 rows=null 跳过）
        List<Map<String, Object>> outRows = rows;
        if (outRows != null && CollectionUtil.isNotEmpty(collectionRoots)) {
            outRows = ImportExportRowUtil.flattenByCollections(outRows, collectionRoots);
        }
        // ④ 列键 / 显示标题
        String[] keys = new String[specs.size()];
        String[] columnNames = new String[specs.size()];
        for (int i = 0; i < specs.size(); i++) {
            keys[i] = specs.get(i).getAttrKey();
            columnNames[i] = resolveColumnTitle(specs.get(i), titleMap);
        }
        // ⑤ 样式：隐藏键行 + 一级组标题 + 下拉 + 白边框（在 ExcelUtil 内）
        ExcelUtil.SheetExportStyle exportStyle = buildSheetExportStyle(specs, layout);
        applyHeaderGroupNames(keys, columnNames, exportStyle, titleMap, layout);
        applyColumnDropdownOptions(exportStyle, keys, index, enumLabelCache);
        ExcelUtil.createWorkBook(safeName, fileSuffix, outRows, keys, columnNames, new String[0],
            PutObject.getResponse(), exportStyle);
    }

    /**
     * 多 Sheet 写出：
     * <pre>
     * 1. 校验至少有主表列 + 明细列
     * 2. 拆成主表行 / 各集合明细行（写入 __rowNo）
     * 3. 组装 sheetDefs 后一次性写 Workbook
     * </pre>
     */
    private void writeMultiSheetExcel(String fileName, List<ColumnSpec> specs, Map<String, String> titleMap,
                                      SheetLayoutOptions layout, List<Map<String, Object>> rows,
                                      List<String> collectionRoots, Map<String, AttrDefinition> attrIndex,
                                      Map<String, String[]> enumLabelCache) {
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
        // 拆分主从行（模板时 rows=null，只出空结构）
        ImportExportRowUtil.MultiSheetRows split = rows == null
            ? ImportExportRowUtil.splitToMultiSheets(null, collectionRoots)
            : ImportExportRowUtil.splitToMultiSheets(rows, collectionRoots);
        List<Map<String, Object>> sheetDefs = new ArrayList<>();
        // 主表页：首列主表序号
        String[] masterKeys = buildKeysWithLink(masterSpecs);
        ExcelUtil.SheetExportStyle masterStyle = buildSheetExportStyleWithLink(masterSpecs, layout);
        applyColumnDropdownOptions(masterStyle, masterKeys, attrIndex, enumLabelCache);
        sheetDefs.add(buildSheetDef(ImportExportConfigJsonHelper.MAIN_SHEET_NAME,
            masterKeys, buildNamesWithLink(masterSpecs, titleMap),
            split.getMasterRows(), masterStyle));
        // 每个明细集合一页
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
            applyColumnDropdownOptions(detailStyle, detailKeys, attrIndex, enumLabelCache);
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

    private boolean isAnyCollectionPath(String attrKey, List<String> collectionRoots) {
        if (StrUtil.isBlank(attrKey) || CollectionUtil.isEmpty(collectionRoots)) {
            return false;
        }
        for (String root : collectionRoots) {
            if (attrKey.equals(root) || attrKey.startsWith(root + ".")) {
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

