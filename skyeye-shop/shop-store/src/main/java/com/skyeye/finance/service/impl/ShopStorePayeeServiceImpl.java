package com.skyeye.finance.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.dao.ShopStorePayeeDao;
import com.skyeye.finance.entity.ShopStorePayee;
import com.skyeye.finance.service.ShopStorePayeeService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "门店默认收款账户", groupName = "个人门店资金", tenant = TenantEnum.NO_ISOLATION)
public class ShopStorePayeeServiceImpl extends SkyeyeBusinessServiceImpl<ShopStorePayeeDao, ShopStorePayee>
    implements ShopStorePayeeService {

    @Override
    public ShopStorePayee getByStoreId(String storeId) {
        if (StrUtil.isBlank(storeId)) {
            return null;
        }
        QueryWrapper<ShopStorePayee> qw = new QueryWrapper<>();
        qw.eq(MybatisPlusUtil.toColumns(ShopStorePayee::getStoreId), storeId)
            .last("LIMIT 1");
        return getOne(qw, false);
    }

    @Override
    public ShopStorePayee upsertByStoreId(String storeId, String accountName, String accountNo, String bankName,
                                          String userId) {
        if (StrUtil.isBlank(storeId)) {
            throw new CustomException("请选择门店");
        }
        if (StrUtil.isBlank(accountName) || StrUtil.isBlank(accountNo) || StrUtil.isBlank(bankName)) {
            throw new CustomException("请填写完整收款信息");
        }
        String name = accountName.trim();
        String no = accountNo.trim();
        String bank = bankName.trim();
        ShopStorePayee exist = getByStoreId(storeId);
        if (exist != null && StrUtil.isNotBlank(exist.getId())) {
            exist.setAccountName(name);
            exist.setAccountNo(no);
            exist.setBankName(bank);
            updateEntity(exist, userId);
            return selectById(exist.getId());
        }
        ShopStorePayee payee = new ShopStorePayee();
        payee.setStoreId(storeId);
        payee.setAccountName(name);
        payee.setAccountNo(no);
        payee.setBankName(bank);
        String id = createEntity(payee, userId);
        return selectById(id);
    }
}
