/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dsform.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.attr.entity.AttrDefinition;
import com.skyeye.common.entity.features.OperatorUserInfo;
import lombok.Data;

import java.util.List;

import com.skyeye.dsform.classenum.DateTimeType;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: DsFormPageContent
 * @Description: 表单布局关联的组件实体类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/10 17:45
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@TableName(value = "ds_form_page_content", autoResultMap = true)
@ApiModel("表单布局关联的组件实体类")
public class DsFormPageContent extends OperatorUserInfo {

    @TableId("id")
    @ApiModelProperty(value = "主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("title")
    @ApiModelProperty(value = "左侧标题，例如：姓名")
    private String title;

    @TableField("placeholder")
    @ApiModelProperty(value = "文本提示语")
    private String placeholder;

    @TableField(value = "`require`", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "组件限制条件")
    private List<String> require;

    @TableField("default_value")
    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    @TableField("width")
    @ApiModelProperty(value = "宽度", required = "required")
    private String width;

    @TableField("pre_attribute")
    @ApiModelProperty(value = "前置属性")
    private String preAttribute;

    @TableField("post_attribute")
    @ApiModelProperty(value = "后置属性")
    private String postAttribute;

    @TableField("form_content_id")
    @ApiModelProperty(value = "动态表单组件id", required = "required")
    private String formContentId;

    @TableField(exist = false)
    @Property("表单布局内的组件信息")
    private DsFormComponent dsFormComponent;

    @TableField("page_id")
    @Property("表单布局id")
    private String pageId;

    @TableField(value = "order_by")
    @ApiModelProperty(value = "排序", required = "required,num")
    private Integer orderBy;

    @TableField("attr_key")
    @ApiModelProperty(value = "属性key")
    private String attrKey;

    @TableField(exist = false)
    @Property("属性信息")
    private AttrDefinition attrDefinition;

    @TableField("upload_num")
    @ApiModelProperty(value = "限制上传的文件数量", required = "num")
    private Integer uploadNum;

    @TableField(value = "upload_data_type", typeHandler = JacksonTypeHandler.class)
    @ApiModelProperty(value = "文件上传类型", required = "json")
    private List<String> uploadDataType;

    @TableField(value = "upload_type")
    @ApiModelProperty(value = "文件上传类型", required = "num")
    private Integer uploadType;

    @TableField(value = "data_show_type")
    @ApiModelProperty(value = "枚举/数据字典类的数据展示类型")
    private String dataShowType;

    @TableField(value = "team_object_type")
    @ApiModelProperty(value = "团队适用对象(团队组件拥有)")
    private String teamObjectType;

    @TableField("is_edit")
    @ApiModelProperty(value = "是否可以编辑", enumClass = WhetherEnum.class)
    private Integer isEdit;

    @TableField("date_time_type")
    @ApiModelProperty(value = "日期组件的类型", enumClass = DateTimeType.class)
    private String dateTimeType;

    @TableField("choose_or_not_my")
    @ApiModelProperty(value = "人员列表中是否包含自己--1.包含；其他参数不包含")
    private Integer chooseOrNotMy;

    @TableField("choose_or_not_email")
    @ApiModelProperty(value = "人员列表中是否必须绑定邮箱--1.必须；其他参数没必要")
    private Integer chooseOrNotEmail;

    @TableField("check_type")
    @ApiModelProperty(value = "人员选择类型，1.多选；其他。单选")
    private Integer checkType;

    @TableField(value = "attr_transform_table_list", typeHandler = AttrTransformTableListTypeHandler.class)
    @ApiModelProperty(value = "展示类型为表格展示时的表格数据", required = "json")
    private List<AttrTransformTable> attrTransformTableList;

    @TableField("min_data")
    @ApiModelProperty(value = "表格组件最小数据行数")
    private Integer minData;

    @TableField("delete_row_callback")
    @ApiModelProperty(value = "删除行之后的回调函数")
    private String deleteRowCallback;

    @TableField("add_row_callback")
    @ApiModelProperty(value = "新增行之后的回调函数")
    private String addRowCallback;

    @TableField("before_script")
    @ApiModelProperty(value = "组件加载前执行的脚本")
    private String beforeScript;

    @TableField("after_script")
    @ApiModelProperty(value = "组件加载完成后执行的脚本")
    private String afterScript;

    @TableField("after_html")
    @ApiModelProperty(value = "组件加载完成后执行的HTML脚本")
    private String afterHtml;

    @TableField("remark")
    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("class_name")
    @ApiModelProperty(value = "class属性")
    private String className;

    @TableField("edit_echo_script")
    @ApiModelProperty(value = "数据编辑回显时执行的脚本")
    private String editEchoScript;

    @TableField("data_change")
    @ApiModelProperty(value = "数据变化监听的JS")
    private String dataChange;

    @TableField("data_echo_after_script")
    @ApiModelProperty(value = "数据回显完之后执行的脚本")
    private String dataEchoAfterScript;

    @TableField("config")
    @ApiModelProperty(value = "其他配置信息，一般为json字符串")
    private String config;

}
