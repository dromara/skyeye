/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.Pictures;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.eve.gw.classenum.GwDocumentOpenCategory;
import com.skyeye.eve.gw.classenum.GwDocumentPeriod;
import com.skyeye.eve.gw.classenum.GwDocumentSecret;
import com.skyeye.eve.gw.classenum.GwDocumentUrgency;
import com.skyeye.eve.gw.dao.GwSendDocumentDao;
import com.skyeye.eve.gw.entity.GwModel;
import com.skyeye.eve.gw.entity.GwSendDocument;
import com.skyeye.eve.gw.entity.GwTemplates;
import com.skyeye.eve.gw.service.GwModelService;
import com.skyeye.eve.gw.service.GwSendDocumentService;
import com.skyeye.eve.gw.service.GwTemplatesService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.util.OfficeUtils;
import org.ddr.poi.html.HtmlRenderPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: GwSendDocumentServiceImpl
 * @Description: 公文发文管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/26 15:44
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "公文发文管理", groupName = "公文发文管理", flowable = true)
public class GwSendDocumentServiceImpl extends SkyeyeBusinessServiceImpl<GwSendDocumentDao, GwSendDocument> implements GwSendDocumentService {

    @Autowired
    private GwTemplatesService gwTemplatesService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private GwModelService gwModelService;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setState(FlowableStateEnum.PASS.getKey());
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        pageInfo.setDepartmentId(inputObject.getLogParams().get("departmentId").toString());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryGwSendDocumentList(pageInfo);
        iDepmentService.setMationForMap(beans, "sendDepartmentId", "sendDepartmentMation");
        gwTemplatesService.setMationForMap(beans, "templateId", "templateMation");
        gwModelService.setMationForMap(beans, "modelId", "modelMation");
        return beans;
    }

    @Override
    public void createPrepose(GwSendDocument entity) {
        super.createPrepose(entity);
        saveGwFile(entity);
    }

    @Override
    protected void updatePrepose(GwSendDocument entity) {
        GwSendDocument oldMation = selectById(entity.getId());
        FileUtil.deleteFile(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + oldMation.getPath());
        entity.setOddNumber(oldMation.getOddNumber());
        saveGwFile(entity);
    }

    private void saveGwFile(GwSendDocument entity) {
        GwModel gwModel = gwModelService.selectById(entity.getModelId());
        if (ObjectUtil.isEmpty(gwModel) || StrUtil.isEmpty(gwModel.getId())) {
            throw new CustomException("公文模版不存在");
        }
        Map<String, Object> params = resetGwParams(entity);
        // 模版信息，参考：https://deepoove.com/poi-tl/#_maven
        String basePath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + gwModel.getPath();

        // html渲染插件
        HtmlRenderPolicy htmlRenderPolicy = new HtmlRenderPolicy();
        Configure configure = Configure.builder()
            // 注册html解析插件
            .bind("内容", htmlRenderPolicy)
            .build();
        // 进行编译
        XWPFTemplate render = XWPFTemplate.compile(basePath, configure).render(params);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String path = tPath + FileConstants.FileUploadPath.getSavePath(FileConstants.FileUploadPath.GW_MODEL.getType()[0]) + "/" + userId;
        FileUtil.createDirs(path);

        String saveBasePath = path + "/" + entity.getOddNumber() + ".docx";
        entity.setPath(FileConstants.FileUploadPath.getVisitPath(FileConstants.FileUploadPath.GW_MODEL.getType()[0]) + userId + "/" + entity.getOddNumber() + ".docx");
        File word = new File(saveBasePath);
        try {
            // 关键步骤，
            render.writeToFile(word.getAbsolutePath());

            // 转为图片
            // 转图片如果出现乱码，
            // 1. 拷贝出 /docs/字体 下的所有的ttc和ttf格式的文件、或者拷贝常用的几个字体文件，如SIMSUN.TTC,根据需要拷贝即可。
            // 2. 将准备好的字体文件拷贝至linux系统中，路径：/usr/share/fonts/
            // 3. 放入上述路径后，执行命令：fc-cache -fv  说明：执行fc-cache命令，fc-cache扫描字体目录并生成字体信息的缓存，然后应用程序就可以立即使用这些新安装的字体
            // 4. 重启服务，否则不生效
            File file = new File(saveBasePath);
            InputStream inStream = new FileInputStream(file);
            List<BufferedImage> wordToImg = OfficeUtils.wordToImg(inStream);
            BufferedImage mergeImage = OfficeUtils.mergeImage(false, wordToImg);
            // 保存图片（长图）
            ImageIO.write(mergeImage, "jpg", new File(saveBasePath.replace(".docx", ".png")));
            entity.setPicPath(FileConstants.FileUploadPath.getVisitPath(FileConstants.FileUploadPath.GW_MODEL.getType()[0]) + userId + "/" + entity.getOddNumber() + ".png");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> resetGwParams(GwSendDocument entity) {
        Map<String, Object> params = new HashMap<>();
        params.put("标题", entity.getTitle());
        params.put("企字", entity.getEnterprise());
        params.put("年份", entity.getYear());
        params.put("号文", entity.getNumber());
        params.put("密级", GwDocumentSecret.getShowName(entity.getSecret()));
        params.put("保密期间", GwDocumentPeriod.getShowName(entity.getPeriod()));
        params.put("紧急程度", GwDocumentUrgency.getShowName(entity.getUrgency()));
        params.put("公开类别", GwDocumentOpenCategory.getShowName(entity.getOpenCategory()));

        SimpleDateFormat sdf1 = new SimpleDateFormat(DateUtil.YYYY_MM_DD_CHINESE);
        params.put("发文日期", sdf1.format(DateUtil.getPointTime(entity.getSendTime(), DateUtil.YYYY_MM_DD)));

        if (StrUtil.isNotEmpty(entity.getSendDepartmentId())) {
            Map<String, Object> sendDepartment = iDepmentService.queryDataMationById(entity.getSendDepartmentId());
            params.put("发文部门", CollectionUtil.isEmpty(sendDepartment) ? StrUtil.EMPTY : sendDepartment.get("name").toString());
        }

        if (CollectionUtil.isNotEmpty(entity.getReceiveDepartmentId())) {
            List<Map<String, Object>> departmentMations = iDepmentService.queryDataMationByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(entity.getReceiveDepartmentId()));
            params.put("收文部门", departmentMations);
        }

        if (CollectionUtil.isNotEmpty(entity.getCcDepartmentId())) {
            List<Map<String, Object>> departmentMations = iDepmentService.queryDataMationByIds(
                Joiner.on(CommonCharConstants.COMMA_MARK).join(entity.getCcDepartmentId()));
            params.put("抄送部门", departmentMations);
        }

        params.put("内容", entity.getContent());

        List<Map<String, Object>> enclosureList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(entity.getEnclosureInfo())) {
            String enclosureInfo = entity.getEnclosureInfo().getEnclosureInfo();
            if (StrUtil.isNotEmpty(enclosureInfo)) {
                enclosureList = iEnclosureService.queryEnclosureInfoByIds(enclosureInfo);
            }
        }
        params.put("附件", enclosureList);

        // 本地图片
        GwTemplates gwTemplates = gwTemplatesService.selectById(entity.getTemplateId());
        if (ObjectUtil.isNotEmpty(gwTemplates) && StrUtil.isNotEmpty(gwTemplates.getId())) {
            params.put("红头标题", gwTemplates.getRedHead());
            if (ObjectUtil.isNotEmpty(gwTemplates.getSealMation())) {
                String logoPath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + gwTemplates.getSealMation().getLogo();
                params.put("印章", Pictures.ofLocal(logoPath).size(120, 120).create());
                params.put("企业", gwTemplates.getSealMation().getCompanyName());
            }
        }

        return params;
    }

    @Override
    public GwSendDocument selectById(String id) {
        GwSendDocument gwSendDocument = super.selectById(id);
        gwTemplatesService.setDataMation(gwSendDocument, GwSendDocument::getTemplateId);
        gwModelService.setDataMation(gwSendDocument, GwSendDocument::getModelId);

        iDepmentService.setDataMation(gwSendDocument, GwSendDocument::getSendDepartmentId);

        // 接收部门
        if (CollectionUtil.isNotEmpty(gwSendDocument.getReceiveDepartmentId())) {
            String departmentIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(gwSendDocument.getReceiveDepartmentId());
            List<Map<String, Object>> departmentList = iDepmentService.queryDataMationByIds(departmentIdStr);
            gwSendDocument.setReceiveDepartmentMation(departmentList);
        }

        // 抄送部门
        if (CollectionUtil.isNotEmpty(gwSendDocument.getCcDepartmentId())) {
            String ccDepartmentIdStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(gwSendDocument.getCcDepartmentId());
            List<Map<String, Object>> ccDepartmentList = iDepmentService.queryDataMationByIds(ccDepartmentIdStr);
            gwSendDocument.setCcDepartmentMation(ccDepartmentList);
        }
        return gwSendDocument;
    }
}
