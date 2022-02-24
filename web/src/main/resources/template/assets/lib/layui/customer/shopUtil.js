
// 商城相关工具类
var shopUtil = {

    // 启用/禁用状态
    enableState: {
        "enable": {"type": 1, "name": "启用"},
        "disable": {"type": 2, "name": "禁用"}
    },

    /**
     * 根据启用/禁用状态获取对应的名称
     *
     * @param enabled 状态
     * @returns {string}
     */
    getEnableStateName: function (enabled){
        if(enabled == 1){
            return "<span class='state-up'>启用</span>";
        }else if(enabled == 2){
            return "<span class='state-down'>禁用</span>";
        }else{
            return "<span class='state-error'>参数错误</span>";
        }
    },

    /**
     * 套餐订单获取状态名称
     *
     * @param data
     * @returns {string}
     */
    getMealOrderStateName: function (data){
        if(data.cancleState == 1) {
            if (data.state == 1) {
                return "<span class='state-down'>待支付</span>";
            } else {
                return "<span class='state-up'>已支付</span>";
            }
        } else {
            return '已取消';
        }
    },

    /**
     * 保养订单获取状态名称
     *
     * @param data
     * @returns {string}
     */
    getKeepFitOrderStateName: function (data){
        if(data.cancleState == 1){
            if(data.state == 1){
                return "<span class='state-down'>保养中</span>";
            }else if(data.state == 2){
                return "<span class='state-up'>待核销</span>";
            }else if(data.state == 3){
                return "<span class='state-up'>已核销</span>";
            }
        }else{
            return '已取消';
        }
    },

    /**
     * 获取区域信息
     *
     * @param callback 回执函数
     */
    getShopAreaMation: function (callback){
        AjaxPostUtil.request({url: shopBasePath + "queryAreaList", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取当前登陆用户所属的区域列表
     *
     * @param callback 回执函数
     */
    queryStaffBelongAreaList: function (callback){
        AjaxPostUtil.request({url: shopBasePath + "storeStaff004", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取当前登陆用户所属的门店列表
     *
     * @param callback 回执函数
     */
    queryStaffBelongStoreList: function (callback){
        AjaxPostUtil.request({url: shopBasePath + "storeStaff005", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 获取指定区域下的门店列表
     *
     * @param areaId 区域id
     * @param callback 回执函数
     */
    queryStoreListByAreaId: function (areaId, callback){
        if(isNull(areaId)){
            return [];
        }
        AjaxPostUtil.request({url: shopBasePath + "queryStoreList", params: {areaId: areaId}, type: 'json', method: "GET", callback: function(json) {
            if(json.returnCode == 0) {
                if(typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 门店员工选择页面
     *
     * @param callback 回调函数
     */
    staffMation: {},
    openStoreStaffChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/storeStaff/storeStaffChoose.html",
            title: "选择店员",
            pageId: "storeStaffChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(shopUtil.staffMation);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

};
