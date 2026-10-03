package com.skyeye.finance.template.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.finance.journal.entity.JournalVoucher;
import com.skyeye.finance.template.entity.VoucherTemplate;

import java.util.Map;

public interface VoucherTemplateService extends SkyeyeBusinessService<VoucherTemplate> {

    VoucherTemplate getEnabledTemplate(String eventType, String setOfBooksId);

    VoucherTemplate findEnabledTemplate(String eventType, String setOfBooksId);

    JournalVoucher buildVoucherFromTemplate(VoucherTemplate template, Map<String, Object> payload);
}
