package com.skyeye.rest.erp.piecework.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.rest.erp.piecework.rest.IPieceworkSystemRest;
import com.skyeye.rest.erp.piecework.service.IPieceworkSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IPieceworkSystemServiceImpl implements IPieceworkSystemService {

    @Autowired
    private IPieceworkSystemRest iPieceworkSystemRest;

    @Override
    public List<Map<String, Object>> queryByStaffAndMonths(String staffId, String farmId,
                                                           String startMonth, String endMonth) {
        if (StrUtil.hasBlank(staffId, startMonth, endMonth)) {
            return new ArrayList<>();
        }
        return ExecuteFeignClient.get(() -> iPieceworkSystemRest.queryPieceworkByStaffAndMonths(
            staffId, StrUtil.blankToDefault(farmId, StrUtil.EMPTY), startMonth, endMonth)).getRows();
    }
}
