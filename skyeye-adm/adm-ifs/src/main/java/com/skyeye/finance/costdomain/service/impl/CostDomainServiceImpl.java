package com.skyeye.finance.costdomain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.books.service.IfsSetOfBooksService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.finance.costdomain.dao.CostDomainDao;
import com.skyeye.finance.costdomain.entity.CostDomain;
import com.skyeye.finance.costdomain.service.CostDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "成本域管理", groupName = "财务中枢")
public class CostDomainServiceImpl extends SkyeyeBusinessServiceImpl<CostDomainDao, CostDomain>
    implements CostDomainService {

    @Autowired
    private IfsSetOfBooksService ifsSetOfBooksService;

    @Override
    public QueryWrapper<CostDomain> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<CostDomain> wrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotBlank(commonPageInfo.getObjectId())) {
            wrapper.eq(MybatisPlusUtil.toColumns(CostDomain::getSetOfBooksId), commonPageInfo.getObjectId());
        }
        if (StrUtil.equals("enabled", commonPageInfo.getType())) {
            wrapper.eq(MybatisPlusUtil.toColumns(CostDomain::getEnabled), EnableEnum.ENABLE_USING.getKey());
        }
        return wrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        ifsSetOfBooksService.setMationForMap(beans, "setOfBooksId", "setOfBooksMation");
        return beans;
    }
}
