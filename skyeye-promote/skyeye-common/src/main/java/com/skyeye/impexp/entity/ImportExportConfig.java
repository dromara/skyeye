/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.impexp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.impexp.enums.ImportExportConfigTypeEnum;
import lombok.Data;

/**
 * @ClassName: ImportExportConfig
 * @Description: 业务对象导入导出配置实体类
 * @author: skyeye云系列--卫志强
 * @date: 2026/4/8 22:00
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName("skyeye_import_export_config")
@ApiModel("业务对象导入导出配置实体类")
public class ImportExportConfig extends BaseGeneralInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("app_id")
    @ApiModelProperty(value = "应用的appId", required = "required")
    private String appId;

    @TableField("class_name")
    @ApiModelProperty(value = "业务对象的className", required = "required")
    private String className;

    @TableField("is_default")
    @ApiModelProperty(value = "是否默认配置", enumClass = IsDefaultEnum.class, required = "required,num")
    private Integer isDefault;

    @TableField("sort_no")
    @ApiModelProperty(value = "排序号")
    private Integer sortNo;

    @TableField("config_type")
    @ApiModelProperty(value = "配置类型", enumClass = ImportExportConfigTypeEnum.class, required = "required,num")
    private Integer configType;

    /**
     * 导入导出配置 JSON（用于定义导出列与 Excel 样式）。
     * <p>根级字段：</p>
     * <ul>
     *     <li>headerRowHeight：表头行高（单位：磅，point）</li>
     *     <li>dataRowHeight：数据行行高（单位：磅，point）</li>
     *     <li>defaultHeaderBackgroundColor：默认表头背景色（格式：#RRGGBB）</li>
     *     <li>defaultHeaderFontColor：默认表头字体色（格式：#RRGGBB）</li>
     * </ul>
     * <p>items 数组（每一列）字段：</p>
     * <ul>
     *     <li>attrKey：字段属性键（必填；对象子字段可用点路径，如 contact.name）</li>
     *     <li>columnTitle：列标题（可选）</li>
     *     <li>columnWidth：列宽（POI Sheet#setColumnWidth 单位，1/256 字符宽）</li>
     *     <li>headerBackgroundColor：该列表头背景色（优先级高于默认背景色）</li>
     *     <li>headerFontColor：该列表头字体色（优先级高于默认字体色）</li>
     *     <li>cellDataType：列数据类型，text=文本（默认），date=日期</li>
     *     <li>cellDateFormat：日期格式，与枚举 dateTimeType 的 id 一致（year/month/date/time/datetime/timeminute）</li>
     * </ul>
     * <p>示例：</p>
     * <pre>
     * {
     *   "headerRowHeight": 22,
     *   "dataRowHeight": 18,
     *   "defaultHeaderBackgroundColor": "#4472C4",
     *   "defaultHeaderFontColor": "#FFFFFF",
     *   "items": [
     *     {
     *       "attrKey": "name",
     *       "columnTitle": "名称",
     *       "columnWidth": 5000,
     *       "headerBackgroundColor": "#E2EFDA",
     *       "headerFontColor": "#000000",
     *       "cellDataType": "text"
     *     },
     *     {
     *       "attrKey": "code",
     *       "columnTitle": "编码",
     *       "columnWidth": 4200,
     *       "cellDataType": "text"
     *     },
     *     {
     *       "attrKey": "createTime",
     *       "columnTitle": "创建时间",
     *       "columnWidth": 5000,
     *       "cellDataType": "date",
     *       "cellDateFormat": "datetime"
     *     }
     *   ]
     * }
     * </pre>
     */
    @TableField("config_json")
    @ApiModelProperty(value = "导入导出配置JSON")
    private String configJson;
}

