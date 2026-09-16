package com.skyeye.payable.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.eve.contacts.service.IContactsService;
import com.skyeye.payable.classenum.ErpPayStateEnum;
import com.skyeye.payable.classenum.ErpSupplierPayableAuthEnum;
import com.skyeye.payable.dao.PayableDao;
import com.skyeye.payable.entity.Payable;
import com.skyeye.payable.service.PayableService;
import com.skyeye.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PayableServiceImpl
 * @Description: 应付事项服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/5/2 20:34
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "应付事项管理", groupName = "应付事项管理", flowable = true, orderApproval = true, teamAuth = true)
public class PayableServiceImpl extends SkyeyeBusinessServiceImpl<PayableDao, Payable> implements PayableService {

    @Autowired
    private SupplierContractService supplierContractService;

    @Autowired
    private IContactsService iContactsService;

    @Autowired
    private SupplierService supplierService;

    @Override
    public Class getAuthEnumClass() {
        return ErpSupplierPayableAuthEnum.class;
    }

    @Override
    public List<String> getAuthPermissionKeyList() {
        return Arrays.asList(ErpSupplierPayableAuthEnum.ADD.getKey(), ErpSupplierPayableAuthEnum.EDIT.getKey(), ErpSupplierPayableAuthEnum.DELETE.getKey(),
                ErpSupplierPayableAuthEnum.REVOKE.getKey(), ErpSupplierPayableAuthEnum.SUBMIT_TO_APPROVAL.getKey(),
                ErpSupplierPayableAuthEnum.LIST.getKey());
    }

    @Override
    public void validatorEntity(Payable entity) {
        super.validatorEntity(entity);
        entity.setPaidPrice(String.valueOf(CommonNumConstants.NUM_ZERO));
    }

    @Override
    public QueryWrapper<Payable> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Payable> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if(StrUtil.isNotEmpty(commonPageInfo.getObjectId())){
            queryWrapper.eq(MybatisPlusUtil.toColumns(Payable::getObjectId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        supplierContractService.setMationForMap(beans, "contractId", "contractMation");
        iContactsService.setMationForMap(beans, "contactId", "contactMation");
        supplierService.setMationForMap(beans,"objectId","objectMation");
        return beans;
    }

    @Override
    public Payable selectById(String id) {
        Payable payable = super.selectById(id);
        payable.setName(payable.getOddNumber());
        supplierService.setDataMation(payable, Payable::getObjectId);
        supplierContractService.setDataMation(payable, Payable::getContractId);
        iContactsService.setDataMation(payable, Payable::getContactId);
        return payable;
    }

    @Override
    public List<Payable> selectByIds(String... ids) {
        List<Payable> payableList = super.selectByIds(ids);
        payableList.forEach(payable -> {
            payable.setName(payable.getOddNumber());
        });
        supplierContractService.setDataMation(payableList, Payable::getContractId);
        iContactsService.setDataMation(payableList, Payable::getContactId);
        return payableList;
    }

    @Override
    public void queryPayableByContractId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String contractId = map.get("contractId").toString();
        if (StrUtil.isEmpty(contractId)) {
            return;
        }
        QueryWrapper<Payable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payable::getContractId), contractId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Payable::getState), FlowableStateEnum.PASS.getKey());
        queryWrapper.ne(MybatisPlusUtil.toColumns(Payable::getPayState),ErpPayStateEnum.PAID_STATE.getKey());
        List<Payable> payableList = list(queryWrapper);
        payableList.forEach(item -> {
            item.setName(item.getOddNumber());
        });
        outputObject.setBeans(payableList);
        outputObject.settotal(payableList.size());
    }

    @Override
    public void updatePayablePaidPrice(String payableId, String price) {
        Payable receivable = selectById(payableId);
        price = CalculationUtil.add(CommonNumConstants.NUM_TWO,
                StrUtil.isEmpty(receivable.getPaidPrice()) ? "0" : receivable.getPaidPrice(),
                price);
        UpdateWrapper<Payable> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, payableId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Payable::getPaidPrice), price);
        if (Double.parseDouble(price) >= Double.parseDouble(receivable.getAmountPrice())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Payable::getPayState), ErpPayStateEnum.PAID_STATE.getKey());
        } else if (Double.parseDouble(price) > CommonNumConstants.NUM_ZERO) {
            updateWrapper.set(MybatisPlusUtil.toColumns(Payable::getPayState), ErpPayStateEnum.PART_PAID_STATE.getKey());
        }else {
            updateWrapper.set(MybatisPlusUtil.toColumns(Payable::getPayState), ErpPayStateEnum.PAY_STATE.getKey());
        }
        update(updateWrapper);
    }

    @Override
    public void updatePayableById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String price = params.get("price").toString();
        updatePayablePaidPrice(id, price);
    }
}
