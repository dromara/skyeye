package com.skyeye.finance.journal.service.impl;

import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.finance.journal.dao.JournalEntryDao;
import com.skyeye.finance.journal.entity.JournalEntry;
import com.skyeye.finance.journal.service.JournalEntryService;
import org.springframework.stereotype.Service;

@Service
public class JournalEntryServiceImpl extends SkyeyeLinkDataServiceImpl<JournalEntryDao, JournalEntry>
    implements JournalEntryService {
}
