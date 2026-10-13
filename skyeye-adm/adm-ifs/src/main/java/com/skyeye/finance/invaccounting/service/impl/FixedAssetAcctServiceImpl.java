package com.skyeye.finance.invaccounting.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.finance.event.classenum.BizAcctEventType;
import com.skyeye.finance.event.service.BizAcctEventService;
import com.skyeye.finance.invaccounting.service.FixedAssetAcctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FixedAssetAcctServiceImpl implements FixedAssetAcctService {

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @Override
    public void postDepreciation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String eventType = BizAcctEventType.FIXED_ASSET_DEPREC.getKey();
        params.put("eventType", eventType);
        params.put("sourceType", StrUtil.blankToDefault((String) params.get("sourceType"), "IFS_FIXED_ASSET_DEPREC"));
        params.put("voucherDate", DateUtil.getYmdTimeAndToString());
        if (params.get("summary") == null || StrUtil.isBlank(params.get("summary").toString())) {
            params.put("summary", "固定资产折旧-" + params.get("sourceNo").toString());
        }
        outputObject.setBean(bizAcctEventService.acceptEvent(params));
    }
}
