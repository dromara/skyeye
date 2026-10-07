package com.skyeye.rest.ifs.bizacct.service;

import java.util.Map;

public interface IfsBizAcctEventService {

    Map<String, Object> acceptBizAcctEvent(Map<String, Object> map);

    /**
     * 业务完成后推财务事件；失败由财务事件台记录，不打断业务。
     */
    default void pushIfsBizAcctEvent(Map<String, Object> map) {
        try {
            acceptBizAcctEvent(map);
        } catch (Exception ignored) {
        }
    }
}
