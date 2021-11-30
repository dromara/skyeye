package com.skyeye.common.constans;
/**
 *
 * @ClassName: IntervieweeStatusEnum
 * @Description: 面试状态
 * @author: skyeye云系列--郑杰
 * @date: 2021/11/30 23:04
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public enum IntervieweeStatusEnum {
    PENDING_INTERVIEW_STATUS(0, "待面试"),
    INTERVIEW_STATUS(1, "面试中"),
    INTERVIEW_PASS_STATUS(2, "面试通过"),
    INTERVIEW_FAILED_STATUS(3, "待面试"),
    REJECTED_STATUS(4, "拒绝入职状态");

    private Integer status;

    private String name;

    IntervieweeStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }
}
