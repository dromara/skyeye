package com.skyeye.receivable.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.PayTypeEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.service.CrmContractService;
import com.skyeye.customer.service.CustomerService;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.receivable.classenum.CrmPayStateEnum;
import com.skyeye.receivable.classenum.CrmReceivableAuthEnum;
import com.skyeye.receivable.dao.ReceivableDao;
import com.skyeye.receivable.entity.Receivable;
import com.skyeye.receivable.service.ReceivableService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ReceivableServiceImpl
 * @Description: 应收事项服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "应收事项管理", groupName = "应收事项管理", flowable = true, teamAuth = true)
public class ReceivableServiceImpl extends SkyeyeBusinessServiceImpl<ReceivableDao, Receivable> implements ReceivableService {

    @Autowired
    private CrmContractService crmContractService;

    @Autowired
    private IContactsService iContactsService;

    @Autowired
    private CustomerService customerService;


    @Override
    public Class getAuthEnumClass() {
        return CrmReceivableAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(CrmReceivableAuthEnum.ADD.getKey(), CrmReceivableAuthEnum.EDIT.getKey(), CrmReceivableAuthEnum.DELETE.getKey(),
                CrmReceivableAuthEnum.REVOKE.getKey(), CrmReceivableAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                CrmReceivableAuthEnum.LIST.getKey());
    }

    @Override
    public void validatorEntity(Receivable entity) {
        super.validatorEntity(entity);
        entity.setPaidPrice(String.valueOf(CommonNumConstants.NUM_ZERO));
    }

    @Override
    public QueryWrapper<Receivable> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Receivable> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if(StrUtil.isNotEmpty(commonPageInfo.getObjectId())){
            queryWrapper.eq(MybatisPlusUtil.toColumns(Receivable::getObjectId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        crmContractService.setMationForMap(beans, "contractId", "contractMation");
        iContactsService.setMationForMap(beans, "contactId", "contactMation");
        customerService.setMationForMap(beans,"objectId","objectMation");
        return beans;
    }

    @Override
    public Receivable selectById(String id) {
        Receivable receivable = super.selectById(id);
        receivable.setName(receivable.getOddNumber());
        crmContractService.setDataMation(receivable, Receivable::getContractId);
        iContactsService.setDataMation(receivable, Receivable::getContactId);
        customerService.setDataMation(receivable, Receivable::getObjectId);
        return receivable;
    }

    @Override
    public List<Receivable> selectByIds(String... ids) {
        List<Receivable> receivableList = super.selectByIds(ids);
        receivableList.forEach(receivable -> {
            receivable.setName(receivable.getOddNumber());
        });
        crmContractService.setDataMation(receivableList, Receivable::getContractId);
        iContactsService.setDataMation(receivableList, Receivable::getContactId);
        return receivableList;
    }

    @Override
    public void queryReceivableByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<Receivable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Receivable::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Receivable::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.ne(MybatisPlusUtil.toColumns(Receivable::getPayState),CrmPayStateEnum.PAID_STATE.getKey());
        List<Receivable> receivableList = list(queryWrapper);
        receivableList.forEach(item -> {
            item.setName(item.getOddNumber());
        });
        outputObject.setBeans(receivableList);
        outputObject.settotal(receivableList.size());
    }

    @Override
    public void updateReceivablePaidPrice(String receivableId, String price) {
        Receivable receivable = selectById(receivableId);
        price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                StrUtil.isEmpty(receivable.getPaidPrice()) ? "0" : receivable.getPaidPrice(),
                price);
        UpdateWrapper<Receivable> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, receivableId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Receivable::getPaidPrice), price);
        if (Double.parseDouble(price) >= Double.parseDouble(receivable.getAmountPrice())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Receivable::getPayState), CrmPayStateEnum.PAID_STATE.getKey());
        } else if (Double.parseDouble(price) > CommonNumConstants.NUM_ZERO) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Receivable::getPayState), CrmPayStateEnum.PART_PAY_STATE.getKey());
        }else {
            updateWrapper.set(MybatisPlusUtil.toColumns(Receivable::getPayState), CrmPayStateEnum.PAY_STATE.getKey());
        }
        update(updateWrapper);
    }

    @Override
    public void updateReceivableById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String price = params.get("price").toString();
        updateReceivablePaidPrice(id, price);
    }

    @Override
    public void queryReceivableCount(InputObject inputObject, OutputObject outputObject) {
        outputObject.settotal(count());
    }
}
