package com.skyeye.finance.template.service.impl;

import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.finance.template.dao.VoucherTemplateLineDao;
import com.skyeye.finance.template.entity.VoucherTemplateLine;
import com.skyeye.finance.template.service.VoucherTemplateLineService;
import org.springframework.stereotype.Service;

@Service
public class VoucherTemplateLineServiceImpl extends SkyeyeLinkDataServiceImpl<VoucherTemplateLineDao, VoucherTemplateLine> implements VoucherTemplateLineService {

}
