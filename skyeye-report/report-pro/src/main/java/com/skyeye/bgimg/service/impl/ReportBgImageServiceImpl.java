/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bgimg.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bgimg.dao.ReportBgImageDao;
import com.skyeye.bgimg.entity.BgImage;
import com.skyeye.bgimg.service.ReportBgImageService;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReportBgImageServiceImpl
 * @Description: 背景图片管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/3 8:35
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "背景图片管理", groupName = "背景图片管理")
public class ReportBgImageServiceImpl extends SkyeyeBusinessServiceImpl<ReportBgImageDao, BgImage> implements ReportBgImageService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iSysDictDataService.setNameForMap(beans, "typeId", "typeName");
        return beans;
    }

    @Override
    public void deletePostpose(BgImage entity) {
        String basePath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY);
        FileUtil.deleteFile(basePath + entity.getImgPath());
    }

    /**
     * 获取所有背景图片列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void getEnabledBgImageList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<BgImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BgImage::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<BgImage> bgImageList = list(queryWrapper);
        iSysDictDataService.setName(bgImageList, "typeId", "typeName");
        outputObject.setBeans(bgImageList);
        outputObject.settotal(bgImageList.size());
    }

}
