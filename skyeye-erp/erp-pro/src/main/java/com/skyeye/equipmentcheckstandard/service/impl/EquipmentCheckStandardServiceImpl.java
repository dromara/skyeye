package com.skyeye.equipmentcheckstandard.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentcheckstandard.dao.EquipmentCheckStandardDao;
import com.skyeye.equipmentcheckstandard.entity.EquipmentCheckStandard;
import com.skyeye.equipmentcheckstandard.service.EquipmentCheckStandardItemService;
import com.skyeye.equipmentcheckstandard.service.EquipmentCheckStandardService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: EquipmentCheckStandardServiceImpl
 * @Description: 设备点检标准服务实现层
 */
@Service
@SkyeyeService(name = "设备点检标准", groupName = "设备点检", flowable = true, allowDynamicAttrKey = false)
public class EquipmentCheckStandardServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentCheckStandardDao, EquipmentCheckStandard>
    implements EquipmentCheckStandardService {

    @Autowired
    private EquipmentCheckStandardItemService equipmentCheckStandardItemService;

//排序
    @Override
    protected QueryWrapper<EquipmentCheckStandard> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EquipmentCheckStandard> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(EquipmentCheckStandard::getCreateTime));
        return queryWrapper;
    }

    //新增时点检标准编码由后端按编码规则自动生成
    @Override
    public void createPrepose(EquipmentCheckStandard entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        entity.setOddNumber(iCodeRuleService.getNextCodeByClassName(getServiceClassName(), business));
    }

    @Override
    public void updatePrepose(EquipmentCheckStandard entity) {
        super.updatePrepose(entity);
        EquipmentCheckStandard oldEntity = getDataFromDb(entity.getId());
        entity.setOddNumber(oldEntity.getOddNumber());
    }

    @Override
    public void validatorEntity(EquipmentCheckStandard entity) {
        if (CollectionUtil.isEmpty(entity.getItemList())) {
            throw new CustomException("点检标准至少保留一条检查项.");
        }
    }

    @Override
    public EquipmentCheckStandard getDataFromDb(String id) {
        EquipmentCheckStandard standard = super.getDataFromDb(id);
        standard.setItemList(equipmentCheckStandardItemService.selectByParentId(id));
        return standard;
    }

    @Override
    public void writePostpose(EquipmentCheckStandard entity, String userId) {
        equipmentCheckStandardItemService.saveList(entity.getId(), entity.getItemList());
        super.writePostpose(entity, userId);
    }

    @Override
    public void deletePostpose(String id) {
        equipmentCheckStandardItemService.deleteByParentId(id);
    }

}

