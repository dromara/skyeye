package com.skyeye.finance.template.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.common.entity.features.BaseGeneralInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.annotation.api.Property;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@RedisCacheField(name = "ifs:voucherTemplate")
@TableName(value = "ifs_voucher_template", autoResultMap = true)
@ApiModel("凭证模板实体类")
public class VoucherTemplate extends BaseGeneralInfo {

    @TableField("event_type")
    @ApiModelProperty(value = "业务事项类型", required = "required", fuzzyLike = true, enumClass = BizAcctEventType.class)
    private String eventType;

    @TableField("set_of_books_id")
    @ApiModelProperty(value = "账套id，空表示通用")
    private String setOfBooksId;

    @TableField(exist = false)
    @Property("账套信息")
    private Map<String, Object> setOfBooksMation;

    @TableField("enabled")
    @ApiModelProperty(value = "状态", required = "required,num", enumClass = EnableEnum.class)
    private Integer enabled;

    @TableField("auto_post")
    @ApiModelProperty(value = "是否自动过账", required = "num", enumClass = WhetherEnum.class)
    private Integer autoPost;

    @TableField(exist = false)
    @ApiModelProperty(value = "模板分录", required = "required,json")
    private List<VoucherTemplateLine> lines;
}
