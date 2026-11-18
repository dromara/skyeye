/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itextpdf.kernel.geom.PageSize;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.html.util.HtmlToPdfUtil;
import com.skyeye.print.dao.PrintTemplateDao;
import com.skyeye.print.entity.PrintTemplate;
import com.skyeye.print.enumclass.Orientation;
import com.skyeye.print.enumclass.PaperSize;
import com.skyeye.print.service.PrintHtmlGenerator;
import com.skyeye.print.service.PrintTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PrintTemplateServiceImpl
 * @Description: 打印模板服务层实现类--弱隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 8:33
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@Slf4j
@SkyeyeService(name = "打印模板", groupName = "打印模板", tenant = TenantEnum.WEAK_ISOLATION)
public class PrintTemplateServiceImpl extends SkyeyeBusinessServiceImpl<PrintTemplateDao, PrintTemplate> implements PrintTemplateService {

    @Autowired
    private PrintHtmlGenerator htmlGenerator;

    @Value("${webroot.fileBath}")
    private String webRootfileBath;

    @Override
    protected void validatorEntity(PrintTemplate entity) {
        super.validatorEntity(entity);
        if (StrUtil.equals(entity.getPaperSize(), PaperSize.CUSTOM.getKey())) {
            if (StrUtil.isEmpty(entity.getWidth()) || StrUtil.isEmpty(entity.getHeight())) {
                throw new CustomException("自定义纸张的宽高尺寸不能为空");
            }
        }
    }

    @Override
    protected void updatePrepose(PrintTemplate entity) {
        PrintTemplate oldPrintTemplate = selectById(entity.getId());
        entity.setConfigContent(oldPrintTemplate.getConfigContent());
    }

    @Override
    protected QueryWrapper<PrintTemplate> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PrintTemplate> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // objectKey 为空时查询全部，供业务对象资源总览使用
        if (StrUtil.isNotBlank(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PrintTemplate::getObjectKey), commonPageInfo.getObjectKey());
        }
        return queryWrapper;
    }

    @Override
    public PrintTemplate selectById(String id) {
        PrintTemplate printTemplate = super.selectById(id);
        if (!StrUtil.equals(printTemplate.getPaperSize(), PaperSize.CUSTOM.getKey())) {
            PageSize pageSize = PaperSize.getPageSizeByKey(printTemplate.getPaperSize());
            printTemplate.setWidth(StrUtil.toString(pageSize.getWidth()));
            printTemplate.setHeight(StrUtil.toString(pageSize.getHeight()));
        }
        return printTemplate;
    }

    @Override
    @IgnoreTenant
    public void queryPrintTemplateListByPageId(InputObject inputObject, OutputObject outputObject) {
        String pageId = inputObject.getParams().get("pageId").toString();
        QueryWrapper<PrintTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PrintTemplate::getPageId), pageId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(PrintTemplate::getCreateTime));
        List<PrintTemplate> printTemplateList = list(queryWrapper);
        outputObject.setBeans(printTemplateList);
        outputObject.settotal(printTemplateList.size());
    }

    @Override
    public void queryPreviewPrintTemplateById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String businessData = params.get("businessData").toString();
        // 获取打印数据
        Map<String, Object> printData = new HashMap<>();
        if (StrUtil.isNotEmpty(businessData)) {
            printData = JSONUtil.toBean(businessData, null);
        }

        // 获取模板详情
        PrintTemplate template = selectById(id);
        if (template == null) {
            throw new CustomException("打印模板不存在");
        }

        // 与设计器一致：纸面用 96dpi px，避免 iText pt 被当成 CSS 像素
        normalizeTemplateSizeForHtml(template);

        try {
            // 渲染HTML
            String previewHtml = htmlGenerator.generateHtml(template, printData);

            // 生成预览图
            String previewImageUrl = htmlGenerator.generatePreviewImage(previewHtml);

            // 设置输出
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("previewHtml", previewHtml);
            resultMap.put("previewImageUrl", previewImageUrl);
            resultMap.put("template", template);

            outputObject.setBean(resultMap);
        } catch (Exception e) {
            log.error("生成打印预览失败", e);
            throw new CustomException("生成打印预览失败: " + e.getMessage());
        }
    }

    @Override
    public void generatePdfPrintTemplateById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String businessData = params.get("businessData").toString();
        // 获取打印数据
        Map<String, Object> printData = new HashMap<>();
        if (StrUtil.isNotEmpty(businessData)) {
            printData = JSONUtil.toBean(businessData, null);
        }

        // 获取模板详情
        PrintTemplate template = selectById(id);
        if (template == null) {
            throw new CustomException("打印模板不存在");
        }

        // 与设计器一致：纸面用 96dpi px，避免 iText pt 被当成 CSS 像素
        normalizeTemplateSizeForHtml(template);

        try {
            // 渲染HTML
            String html = htmlGenerator.generateHtml(template, printData);

            // 生成PDF（纸张尺寸仍按 paperSize+orientation 取标准页，不依赖上面的 px 字符串）
            byte[] pdfBytes = HtmlToPdfUtil.convertHtmlToPdfBytes(html, webRootfileBath, template.getPaperSize(), template.getOrientation(),
                template.getWidth(), template.getHeight());

            // 设置响应头信息
            String filename = template.getName() + ".pdf";
            HttpServletResponse response = InputObject.getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" +
                URLEncoder.encode(filename, "UTF-8"));

            // 写入响应流
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("生成PDF失败", e);
            throw new CustomException("生成PDF失败: " + e.getMessage());
        }
    }

    /**
     * 将模板宽高规范为与前端设计器一致的 96dpi 像素（带 px），供 HTML/PDF 渲染使用。
     * 标准纸张按 mm 换算；自定义纸张字段存的是 mm。
     */
    private void normalizeTemplateSizeForHtml(PrintTemplate template) {
        float widthMm;
        float heightMm;
        if (StrUtil.equals(template.getPaperSize(), PaperSize.CUSTOM.getKey())) {
            widthMm = parseMm(template.getWidth(), 210f);
            heightMm = parseMm(template.getHeight(), 297f);
        } else if (StrUtil.equals(template.getPaperSize(), PaperSize.A3.getKey())) {
            widthMm = 297f;
            heightMm = 420f;
        } else if (StrUtil.equals(template.getPaperSize(), PaperSize.A5.getKey())) {
            widthMm = 148f;
            heightMm = 210f;
        } else if (StrUtil.equals(template.getPaperSize(), PaperSize.LETTER.getKey())) {
            widthMm = 215.9f;
            heightMm = 279.4f;
        } else {
            // 默认 A4
            widthMm = 210f;
            heightMm = 297f;
        }
        if (StrUtil.equals(template.getOrientation(), Orientation.LANDSCAPE.getKey())) {
            float tmp = widthMm;
            widthMm = heightMm;
            heightMm = tmp;
        }
        int widthPx = Math.round(widthMm * 96f / 25.4f);
        int heightPx = Math.round(heightMm * 96f / 25.4f);
        template.setWidth(widthPx + "px");
        template.setHeight(heightPx + "px");
    }

    private float parseMm(String value, float defaultMm) {
        if (StrUtil.isBlank(value)) {
            return defaultMm;
        }
        try {
            String v = value.trim().toLowerCase().replace("mm", "").replace("px", "").trim();
            return Float.parseFloat(v);
        } catch (NumberFormatException e) {
            return defaultMm;
        }
    }

    @Override
    public void copyPrintTemplateById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        // 获取模板详情
        PrintTemplate template = getById(id);
        if (template == null) {
            throw new CustomException("打印模板不存在");
        }

        String userId = inputObject.getLogParams().get("id").toString();
        // 复制模板
        PrintTemplate newTemplate = new PrintTemplate();
        BeanUtil.copyProperties(template, newTemplate);
        newTemplate.setName(template.getName() + "-副本");
        newTemplate.setId(null);
        createEntity(newTemplate, userId);
    }

    @Override
    public void editConfigContentById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String configContent = params.get("configContent").toString();
        String paperSize = params.get("paperSize").toString();
        String orientation = params.get("orientation").toString();
        String margin = params.get("margin").toString();
        String width = params.get("width").toString();
        String height = params.get("height").toString();
        // 获取模板详情
        PrintTemplate template = selectById(id);
        if (template == null) {
            throw new CustomException("打印模板不存在");
        }
        template.setConfigContent(configContent);
        template.setPaperSize(paperSize);
        template.setOrientation(orientation);
        template.setMargin(margin);
        template.setWidth(width);
        template.setHeight(height);
        updateById(template);
        refreshCache(id);
    }
}
