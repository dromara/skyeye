/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.BarCodeUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.BarCodeDao;
import com.skyeye.eve.entity.barcode.BarCodeApiMation;
import com.skyeye.eve.entity.barcode.BarCodeMation;
import com.skyeye.eve.service.BarCodeService;
import com.skyeye.sdk.data.service.IDataService;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: BarCodeServiceImpl
 * @Description: 条形码服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/28 9:42
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "条形码管理", groupName = "条形码管理", tenant = TenantEnum.STRONG_ISOLATION)
public class BarCodeServiceImpl extends SkyeyeBusinessServiceImpl<BarCodeDao, BarCodeMation> implements BarCodeService {

    @Autowired
    private IDataService iDataService;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void writeBarCode(InputObject inputObject, OutputObject outputObject) {
        BarCodeApiMation barCodeApiMation = inputObject.getParams(BarCodeApiMation.class);
        List<BarCodeMation> barCodeList = barCodeApiMation.getBarCodeList();
        String createTime = DateUtil.getTimeAndToString();
        barCodeList.forEach(barCodeMation -> {
            barCodeMation.setSpringApplicationName(barCodeApiMation.getSpringApplicationName());
            barCodeMation.setCodeImplClass(barCodeApiMation.getCodeImplClass());
            barCodeMation.setCreateTime(createTime);
        });
        this.saveBatch(barCodeList);
    }

    @Override
    public void getDataByBarCode(InputObject inputObject, OutputObject outputObject) {
        String barCode = inputObject.getParams().get("barCode").toString();
        QueryWrapper<BarCodeMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BarCodeMation::getCodeNum), barCode);
        BarCodeMation barCodeMation = getOne(queryWrapper, false);
        if (barCodeMation == null) {
            return;
        }

        // 根据服务名获取服务实例
        List<ServiceInstance> allInstances = discoveryClient.getInstances(barCodeMation.getSpringApplicationName());
        if (CollectionUtils.isEmpty(allInstances)) {
            outputObject.setreturnMessage("this service[{}] has no instance.", barCodeMation.getSpringApplicationName());
            return;
        }

        // 调用SDK服务获取数据信息
        Map<String, Object> result = iDataService.getDataByObjectId(allInstances.get(0).getUri(), barCodeMation.getObjectId(),
            barCodeMation.getCodeImplClass());
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryBarCodeByObjectIds(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String springApplicationName = params.get("springApplicationName").toString();
        String codeImplClass = params.get("codeImplClass").toString();
        List<String> objectIds = JSONArray.fromObject(params.get("objectIds").toString());
        QueryWrapper<BarCodeMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BarCodeMation::getSpringApplicationName), springApplicationName);
        queryWrapper.eq(MybatisPlusUtil.toColumns(BarCodeMation::getCodeImplClass), codeImplClass);
        queryWrapper.in(MybatisPlusUtil.toColumns(BarCodeMation::getObjectId), objectIds);
        List<BarCodeMation> barCodeMationList = super.list(queryWrapper);
        outputObject.setBeans(barCodeMationList);
        outputObject.settotal(barCodeMationList.size());
    }

    @Override
    public void deleteBarCodeByObjectId(InputObject inputObject, OutputObject outputObject) {
        String objectId = inputObject.getParams().get("objectId").toString();
        QueryWrapper<BarCodeMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BarCodeMation::getObjectId), objectId);
        BarCodeMation barCodeMation = getOne(queryWrapper, false);
        // 删除图片路径
        String basePath = tPath + barCodeMation.getImagePath().replace("/images/", StrUtil.EMPTY);
        FileUtil.deleteFile(basePath);
        // 删除数据
        deleteById(barCodeMation.getId());
    }

    @Override
    public void regenerateImageByBarCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String barCode = params.get("barCode").toString();
        String codeImplClass = params.get("codeImplClass").toString();
        QueryWrapper<BarCodeMation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BarCodeMation::getCodeNum), barCode);
        queryWrapper.eq(MybatisPlusUtil.toColumns(BarCodeMation::getCodeImplClass), codeImplClass);
        BarCodeMation barCodeMation = getOne(queryWrapper, false);
        if (barCodeMation == null) {
            return;
        }
        BufferedImage image = BarCodeUtil.insertWords(barCodeMation.getCodeNum());
        try {
            ImageIO.write(image, "jpg", new File(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + barCodeMation.getImagePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
