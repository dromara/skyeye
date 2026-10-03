package com.skyeye.finance.event.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.finance.event.entity.BizAcctEventAccept;
import com.skyeye.finance.event.service.BizAcctEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "业务会计事件", tags = "业务会计事件", modelName = "财务中枢")
public class BizAcctEventController {

    @Autowired
    private BizAcctEventService bizAcctEventService;

    @ApiOperation(id = "acceptBizAcctEvent", value = "受理业务会计事件并生成凭证", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = BizAcctEventAccept.class)
    @RequestMapping("/post/BizAcctEventController/acceptBizAcctEvent")
    public void acceptBizAcctEvent(InputObject inputObject, OutputObject outputObject) {
        bizAcctEventService.acceptEvent(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBizAcctEventList", value = "分页查询业务会计事件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/BizAcctEventController/queryBizAcctEventList")
    public void queryBizAcctEventList(InputObject inputObject, OutputObject outputObject) {
        bizAcctEventService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "retryBizAcctEvent", value = "重试失败的业务会计事件", method = "POST", allUse = "1")
    @ApiImplicitParams({@ApiImplicitParam(id = "id", name = "id", value = "事件id", required = "required")})
    @RequestMapping("/post/BizAcctEventController/retryBizAcctEvent")
    public void retryBizAcctEvent(InputObject inputObject, OutputObject outputObject) {
        bizAcctEventService.retryFailedEvent(inputObject, outputObject);
    }
}
