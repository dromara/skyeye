package com.skyeye.finance.invaccounting.service.impl;

import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.finance.invaccounting.dao.InvAcctItemDao;
import com.skyeye.finance.invaccounting.entity.InvAcctItem;
import com.skyeye.finance.invaccounting.service.InvAcctItemService;
import org.springframework.stereotype.Service;

@Service
public class InvAcctItemServiceImpl extends SkyeyeLinkDataServiceImpl<InvAcctItemDao, InvAcctItem>
    implements InvAcctItemService {
}
