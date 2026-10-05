package com.skyeye.finance.template.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.template.entity.VoucherTemplate;

import java.util.Map;

public interface VoucherTemplateService extends SkyeyeBusinessService<VoucherTemplate> {

    VoucherTemplate getEnabledTemplate(String eventType, String setOfBooksId);

    VoucherTemplate findEnabledTemplate(String eventType, String setOfBooksId);

    JournalVoucher buildVoucherFromTemplate(VoucherTemplate template, Map<String, Object> payload);

    /**
     * 按标准科目编码补建缺失的通用默认模板（已存在事项类型跳过）。
     */
    void initDefaultTemplates(InputObject inputObject, OutputObject outputObject);
}
