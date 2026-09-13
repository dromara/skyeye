/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.ExcelUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.CascadeItem;
import com.skyeye.impexp.support.ImportExportConfigJsonHelper.ColumnSpec;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 导入导出列级联（企业版约定）：
 * <ul>
 *   <li>Excel 单元格与命名区域一律使用稳定编码 {@code code}，禁止用中文展示名做 INDIRECT 匹配；</li>
 *   <li>命名区域名 = {@link #NAME_PREFIX} + code（如 cas_GD），公式：{@code INDIRECT("cas_"&$父列行)}；</li>
 *   <li>{@code name} 仅用于设计器展示/备注，不参与 Excel 联动。</li>
 * </ul>
 */
public final class ImportExportCascadeHelper {

    /** Excel 命名区域前缀，避免与保留名（C/R 等）冲突 */
    public static final String NAME_PREFIX = "cas_";

    /** 编码规则：字母/数字/下划线，1~80（允许纯数字业务 id；命名区域统一加 cas_ 前缀） */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Za-z0-9_]{1,80}$");

    private ImportExportCascadeHelper() {
    }

    public static boolean isValidCode(String code) {
        return StrUtil.isNotBlank(code) && CODE_PATTERN.matcher(code.trim()).matches();
    }

    public static String toRangeName(String code) {
        return NAME_PREFIX + code.trim();
    }

    /**
     * 解析 cascadeItems JSON 数组。
     */
    public static List<CascadeItem> parseCascadeItems(Object raw) {
        if (raw == null) {
            return Collections.emptyList();
        }
        JSONArray arr;
        if (raw instanceof JSONArray) {
            arr = (JSONArray) raw;
        } else if (raw instanceof String) {
            String s = ((String) raw).trim();
            if (StrUtil.isBlank(s)) {
                return Collections.emptyList();
            }
            arr = JSONUtil.parseArray(s);
        } else if (raw instanceof List) {
            arr = JSONUtil.parseArray(JSONUtil.toJsonStr(raw));
        } else {
            return Collections.emptyList();
        }
        List<CascadeItem> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject row = arr.getJSONObject(i);
            if (row == null) {
                continue;
            }
            CascadeItem item = parseCascadeItem(row);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    private static CascadeItem parseCascadeItem(JSONObject row) {
        String code = StrUtil.trim(row.getStr("code"));
        if (StrUtil.isBlank(code)) {
            code = StrUtil.trim(row.getStr("id"));
        }
        if (StrUtil.isBlank(code)) {
            return null;
        }
        CascadeItem item = new CascadeItem();
        item.setCode(code);
        item.setName(StrUtil.blankToDefault(row.getStr("name"), null));
        JSONArray children = row.getJSONArray("children");
        if (children != null && !children.isEmpty()) {
            List<CascadeItem> childList = new ArrayList<>();
            for (int i = 0; i < children.size(); i++) {
                JSONObject c = children.getJSONObject(i);
                if (c == null) {
                    continue;
                }
                CascadeItem child = parseCascadeItem(c);
                if (child != null) {
                    childList.add(child);
                }
            }
            item.setChildren(childList);
        }
        return item;
    }

    /**
     * 保存前校验：依赖列存在、无环、同 Sheet；有编码树则校验编码，或仅配置了数据来源绑定。
     */
    public static void validateCascadeConfig(List<ColumnSpec> specs) {
        if (CollectionUtil.isEmpty(specs)) {
            return;
        }
        Map<String, ColumnSpec> byKey = new LinkedHashMap<>();
        for (ColumnSpec spec : specs) {
            if (spec != null && StrUtil.isNotBlank(spec.getAttrKey())) {
                byKey.put(spec.getAttrKey(), spec);
            }
        }
        for (ColumnSpec spec : specs) {
            if (spec == null || StrUtil.isBlank(spec.getDependAttrKey())) {
                continue;
            }
            String dep = spec.getDependAttrKey().trim();
            String childTitle = displayTitle(spec);
            ColumnSpec parent = byKey.get(dep);
            if (parent == null) {
                throw new CustomException("列【" + childTitle + "】的依赖列未在本模板中勾选，请先勾选父列。");
            }
            if (dep.equals(spec.getAttrKey())) {
                throw new CustomException("列【" + childTitle + "】不能依赖自身。");
            }
            String childSheet = StrUtil.blankToDefault(spec.getSheetKey(), ImportExportConfigJsonHelper.MAIN_SHEET_KEY);
            String parentSheet = StrUtil.blankToDefault(parent.getSheetKey(), ImportExportConfigJsonHelper.MAIN_SHEET_KEY);
            if (!StrUtil.equals(childSheet, parentSheet)) {
                throw new CustomException("列【" + childTitle + "】与依赖列【" + displayTitle(parent)
                    + "】必须在同一工作表才能级联。");
            }
            boolean hasBind = hasCascadeBind(spec);
            boolean hasItems = CollectionUtil.isNotEmpty(spec.getCascadeItems());
            if (!hasItems && !hasBind) {
                throw new CustomException("列【" + childTitle + "】已选择依赖列，请填写「关联父值字段」"
                    + "（本列数据来源中对应父列值的字段，如 parentId、materialId）。");
            }
            if (hasItems) {
                validateCascadeItems(childTitle, spec.getCascadeItems(), new HashSet<>());
            }
        }
        detectCycle(specs, byKey);
    }

    public static boolean hasCascadeBind(ColumnSpec spec) {
        return spec != null && spec.getCascadeBind() != null
            && StrUtil.isNotBlank(spec.getCascadeBind().getLinkField());
    }

    private static void validateCascadeItems(String ownerTitle, List<CascadeItem> items, Set<String> pathCodes) {
        if (CollectionUtil.isEmpty(items)) {
            return;
        }
        Set<String> sibling = new HashSet<>();
        for (CascadeItem item : items) {
            if (item == null || StrUtil.isBlank(item.getCode())) {
                throw new CustomException("列【" + ownerTitle + "】的级联数据存在空编码，请检查数据来源。");
            }
            String code = item.getCode().trim();
            if (!isValidCode(code)) {
                throw new CustomException("列【" + ownerTitle + "】的编码【" + code
                    + "】不合法：仅允许字母/数字/下划线，最长 80（禁止中文）。");
            }
            if (!sibling.add(code)) {
                throw new CustomException("列【" + ownerTitle + "】同级编码重复：【" + code + "】。");
            }
            if (!pathCodes.add(code)) {
                throw new CustomException("列【" + ownerTitle + "】级联数据中编码重复：【" + code + "】。");
            }
            validateCascadeItems(ownerTitle, item.getChildren(), pathCodes);
            pathCodes.remove(code);
        }
    }

    private static void detectCycle(List<ColumnSpec> specs, Map<String, ColumnSpec> byKey) {
        Map<String, String> depMap = new HashMap<>();
        for (ColumnSpec spec : specs) {
            if (spec != null && StrUtil.isNotBlank(spec.getAttrKey()) && StrUtil.isNotBlank(spec.getDependAttrKey())) {
                depMap.put(spec.getAttrKey(), spec.getDependAttrKey().trim());
            }
        }
        for (String start : depMap.keySet()) {
            Set<String> seen = new HashSet<>();
            String cur = start;
            while (StrUtil.isNotBlank(cur)) {
                if (!seen.add(cur)) {
                    ColumnSpec startSpec = byKey.get(start);
                    throw new CustomException("列级联存在循环依赖，请检查【"
                        + displayTitle(startSpec) + "】的依赖设置。");
                }
                cur = depMap.get(cur);
            }
        }
    }

    /**
     * 将本 Sheet 列的级联配置写入 {@link ExcelUtil.SheetExportStyle}。
     */
    public static void applyToExportStyle(ExcelUtil.SheetExportStyle style, List<ColumnSpec> specs, String[] keys) {
        if (style == null || keys == null || keys.length == 0 || CollectionUtil.isEmpty(specs)) {
            return;
        }
        Map<String, Integer> keyIndex = new HashMap<>();
        for (int i = 0; i < keys.length; i++) {
            if (StrUtil.isNotBlank(keys[i])) {
                keyIndex.put(keys[i], i);
            }
        }
        Map<String, ColumnSpec> specMap = new HashMap<>();
        for (ColumnSpec spec : specs) {
            if (spec != null && StrUtil.isNotBlank(spec.getAttrKey())) {
                specMap.put(spec.getAttrKey(), spec);
            }
        }

        // rangeName -> values（合并同名时必须一致，否则报错）
        Map<String, String[]> namedLists = new LinkedHashMap<>();
        // 父列 attrKey -> 作为级联根的 code 列表（有序去重）
        Map<String, LinkedHashSet<String>> parentRootCodes = new LinkedHashMap<>();
        int[] cascadeParent = new int[keys.length];
        Arrays.fill(cascadeParent, -1);

        for (ColumnSpec spec : specs) {
            if (spec == null || StrUtil.isBlank(spec.getDependAttrKey()) || CollectionUtil.isEmpty(spec.getCascadeItems())) {
                continue;
            }
            Integer childIdx = keyIndex.get(spec.getAttrKey());
            Integer parentIdx = keyIndex.get(spec.getDependAttrKey().trim());
            if (childIdx == null || parentIdx == null) {
                continue;
            }
            cascadeParent[childIdx] = parentIdx;
            LinkedHashSet<String> roots = parentRootCodes.computeIfAbsent(spec.getDependAttrKey().trim(),
                k -> new LinkedHashSet<>());
            for (CascadeItem root : spec.getCascadeItems()) {
                if (root != null && isValidCode(root.getCode())) {
                    roots.add(root.getCode().trim());
                }
            }
            collectNamedLists(spec.getCascadeItems(), namedLists, spec.getAttrKey());
        }

        if (namedLists.isEmpty()) {
            return;
        }

        List<ExcelUtil.CascadeNamedList> list = new ArrayList<>();
        for (Map.Entry<String, String[]> e : namedLists.entrySet()) {
            ExcelUtil.CascadeNamedList one = new ExcelUtil.CascadeNamedList();
            one.rangeName = e.getKey();
            one.values = e.getValue();
            list.add(one);
        }
        style.cascadeNamedLists = list;
        style.columnCascadeParentColumns = cascadeParent;

        // 父列下拉改为根编码（覆盖中文枚举等，保证与 cas_ 编码一致）
        if (style.columnDropdownOptions == null || style.columnDropdownOptions.length != keys.length) {
            style.columnDropdownOptions = new String[keys.length][];
        }
        for (Map.Entry<String, LinkedHashSet<String>> e : parentRootCodes.entrySet()) {
            Integer idx = keyIndex.get(e.getKey());
            if (idx == null) {
                continue;
            }
            style.columnDropdownOptions[idx] = e.getValue().toArray(new String[0]);
        }
        // 子列不再使用显式列表，改由 INDIRECT 公式
        for (int i = 0; i < cascadeParent.length; i++) {
            if (cascadeParent[i] >= 0) {
                style.columnDropdownOptions[i] = null;
            }
        }
    }

    private static void collectNamedLists(List<CascadeItem> items, Map<String, String[]> namedLists, String ownerAttrKey) {
        if (CollectionUtil.isEmpty(items)) {
            return;
        }
        for (CascadeItem item : items) {
            if (item == null || !isValidCode(item.getCode())) {
                continue;
            }
            List<CascadeItem> children = item.getChildren();
            if (CollectionUtil.isEmpty(children)) {
                continue;
            }
            String rangeName = toRangeName(item.getCode());
            String[] childCodes = children.stream()
                .filter(c -> c != null && isValidCode(c.getCode()))
                .map(c -> c.getCode().trim())
                .distinct()
                .toArray(String[]::new);
            if (childCodes.length == 0) {
                continue;
            }
            String[] exists = namedLists.get(rangeName);
            if (exists != null) {
                if (!Arrays.equals(exists, childCodes)) {
                    throw new CustomException("级联命名区域冲突：【" + rangeName + "】在列【" + ownerAttrKey
                        + "】与其它列定义不一致，请保证同一编码的子项列表相同。");
                }
            } else {
                namedLists.put(rangeName, childCodes);
            }
            collectNamedLists(children, namedLists, ownerAttrKey);
        }
    }

    /**
     * 导入行校验：子编码必须属于父编码对应的子项。
     *
     * @return 错误信息列表（空表示通过）
     */
    public static List<String> validateImportRow(Map<String, String> cellByAttrKey, List<ColumnSpec> specs, int excelRowNo) {
        List<String> errors = new ArrayList<>();
        if (cellByAttrKey == null || CollectionUtil.isEmpty(specs)) {
            return errors;
        }
        for (ColumnSpec spec : specs) {
            if (spec == null || StrUtil.isBlank(spec.getDependAttrKey()) || CollectionUtil.isEmpty(spec.getCascadeItems())) {
                continue;
            }
            String childVal = StrUtil.trim(cellByAttrKey.get(spec.getAttrKey()));
            String parentVal = StrUtil.trim(cellByAttrKey.get(spec.getDependAttrKey().trim()));
            if (StrUtil.isBlank(childVal)) {
                continue;
            }
            if (StrUtil.isBlank(parentVal)) {
                errors.add("第" + excelRowNo + "行：【" + displayTitle(spec) + "】有值时，依赖列【"
                    + spec.getDependAttrKey() + "】不能为空。");
                continue;
            }
            if (!isValidCode(parentVal) || !isValidCode(childVal)) {
                errors.add("第" + excelRowNo + "行：级联列只允许编码/编号（字母数字下划线，禁止中文），当前父="
                    + parentVal + " 子=" + childVal);
                continue;
            }
            Set<String> allowed = findChildCodes(spec.getCascadeItems(), parentVal);
            if (allowed == null) {
                errors.add("第" + excelRowNo + "行：父编码【" + parentVal + "】不在【"
                    + displayTitle(spec) + "】的级联树中。");
            } else if (!allowed.contains(childVal)) {
                errors.add("第" + excelRowNo + "行：子编码【" + childVal + "】不属于父编码【"
                    + parentVal + "】（列 " + displayTitle(spec) + "）。");
            }
        }
        return errors;
    }

    /**
     * @return null=父编码不存在；空集合=存在但无子项
     */
    private static Set<String> findChildCodes(List<CascadeItem> items, String parentCode) {
        if (CollectionUtil.isEmpty(items)) {
            return null;
        }
        for (CascadeItem item : items) {
            if (item == null || !StrUtil.equals(parentCode, StrUtil.trim(item.getCode()))) {
                // 继续在子树中找（多级时父可能是中间节点）
                Set<String> deeper = findChildCodes(item != null ? item.getChildren() : null, parentCode);
                if (deeper != null) {
                    return deeper;
                }
                continue;
            }
            if (CollectionUtil.isEmpty(item.getChildren())) {
                return Collections.emptySet();
            }
            return item.getChildren().stream()
                .filter(c -> c != null && isValidCode(c.getCode()))
                .map(c -> c.getCode().trim())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return null;
    }

    private static String displayTitle(ColumnSpec spec) {
        if (spec == null) {
            return "";
        }
        return StrUtil.blankToDefault(spec.getColumnTitle(), spec.getAttrKey());
    }
}
