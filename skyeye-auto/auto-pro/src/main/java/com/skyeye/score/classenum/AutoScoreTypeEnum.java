/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.score.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: AutoScoreTypeEnum
 * @Description: 需求积分流水类型
 * @author: skyeye云系列--卫志强
 * @date: 2026/8/18
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoScoreTypeEnum implements SkyeyeEnumClass {

    FRONT_GRANT("frontGrant", "前端需求积分", true, true),
    BACK_GRANT("backGrant", "后端需求积分", true, false),
    TEST_GRANT("testGrant", "测试需求积分", true, false),
    BUG_PENALTY("bugPenalty", "Bug扣分", true, false),
    BUG_NON_ISSUE("bugNonIssue", "非问题扣分", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
