/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.skill.report;

import com.skyeye.skill.dao.ReportPageDao;
import com.skyeye.skill.entity.ReportPage;
import com.skyeye.skill.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName: ReportPagePersistService
 * @Description: 把转换后的 content 写入本地 report_page（对齐 write + editContent）
 */
@Service
public class ReportPagePersistService {

    private static final String LOCAL_USER = "local";
    private static final int NOT_DELETE = 0;

    @Autowired
    private ReportPageDao reportPageDao;

    @Autowired
    private ScreenToReportContentConverter converter;

    @Autowired
    private ReportPersistProperties reportPersistProperties;

    public boolean isEnabled() {
        return reportPersistProperties.isEnabled();
    }

    /**
     * 新建报表页并写入 content，返回实体。
     */
    public ReportPage createFromScreen(Map<String, Object> screen, String userInput) {
        String contentJson = converter.convertJson(screen);
        String title = screen.get("title") == null ? "AI大屏" : String.valueOf(screen.get("title"));
        String now = now();
        ReportPage page = new ReportPage();
        page.setId(converter.newId());
        page.setName(trimName(title));
        page.setRemark(StringUtils.hasText(userInput) ? ("AI Skills 生成：" + userInput) : "AI Skills 生成");
        page.setContent(contentJson);
        page.setDeleteFlag(NOT_DELETE);
        page.setCreateId(LOCAL_USER);
        page.setCreateTime(now);
        page.setLastUpdateId(LOCAL_USER);
        page.setLastUpdateTime(now);
        reportPageDao.insert(page);
        return page;
    }

    public ReportPage selectById(String id) {
        ReportPage page = reportPageDao.selectById(id);
        if (page == null) {
            throw new CustomException("报表页不存在: " + id);
        }
        return page;
    }

    private String trimName(String title) {
        if (!StringUtils.hasText(title)) {
            return "AI大屏";
        }
        return title.length() > 90 ? title.substring(0, 90) : title;
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
