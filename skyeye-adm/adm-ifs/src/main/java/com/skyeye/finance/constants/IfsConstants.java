package com.skyeye.finance.constants;

import com.skyeye.common.constans.CommonNumConstants;

/**
 * 财务中枢常量
 */
public class IfsConstants {

    /**
     * 金额/数量小数点后位数，与 ERP 侧约定保持一致
     */
    public static final Integer NUM_AFTER_DOT = CommonNumConstants.NUM_TWO;

    /** 业务会计事件失败重试上限，超出后需人工处理，避免重复出凭证 */
    public static final int MAX_BIZ_ACCT_RETRY = 5;

}
