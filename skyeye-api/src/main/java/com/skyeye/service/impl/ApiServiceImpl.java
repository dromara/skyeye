package com.skyeye.service.impl;

import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.ObjectConstant;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ApiService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiServiceImpl implements ApiService {

    /**
     * 获取接口列表
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryAllSysEveReqMapping(InputObject inputObject, OutputObject outputObject) throws Exception {
        List<Map<String, Object>> beans = new ArrayList<>();//返回前端的集合类
        Map<String, Object> bean = null;
        for (Map.Entry<String, Map<String, Object>> entry : Constants.REQUEST_MAPPING.entrySet()) {
            bean = new HashMap<>();
            // url地址作为id
            bean.put("id", entry.getKey());
            // 注释作为标题
            bean.put("title", entry.getValue().get("val"));
            // 请求方式
            bean.put("method", entry.getValue().get("method"));
            // 分组
            bean.put("groupName", entry.getValue().get("groupName"));
            // 工程模块
            bean.put("modelName", entry.getValue().get("modelName"));
            beans.add(bean);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取接口详情
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void querySysEveReqMappingMation(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        String requestId = map.get("id").toString();
        Map<String, Object> jMation = JSONUtil.toBean(JSONUtil.parseObj(Constants.REQUEST_MAPPING.get(requestId)), null);
        if(jMation != null && !jMation.isEmpty()){
            String allUse = jMation.get("allUse").toString();
            String s = "";
            if("0".equals(allUse)){
                s = "无需登录，无需授权即可访问。";
            }else if("1".equals(allUse)){
                s = "需要登录，需要授权才能访问。";
            }else if("2".equals(allUse)){
                s = "需要登录，但无需授权即可访问。";
            }else{
                s = "错误参数。";
            }
            jMation.put("sq", s);//权限说明
            jMation.put("requestId", requestId);
            outputObject.setBean(jMation);
        }else{
            outputObject.setreturnMessage("该信息不存在。");
        }
    }

    /**
     * 获取限制条件
     *
     * @param inputObject
     * @param outputObject
     * @throws Exception
     */
    @Override
    public void queryLimitRestrictions(InputObject inputObject, OutputObject outputObject) throws Exception {
        List<Map<String, Object>> beans = ObjectConstant.VerificationParams.getList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}
