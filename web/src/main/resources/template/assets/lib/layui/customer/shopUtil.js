
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
        } else {
            return "<span class='state-error'>参数错误</span>";
        }
    },

    /**
     * 根据启用/禁用状态获取对应的名称--适用于车辆
     *
     * @param enabled 状态
     * @returns {string}
     */
    getMemberCarEnableStateName: function (enabled){
        if(enabled == 1){
            return "<span class='state-up'>启用</span>";
        }else if(enabled == 2){
            return "<span class='state-down'>禁用</span>";
        }else if(enabled == 3){
            return "<span class='state-down'>已过户</span>";
        } else {
            return "<span class='state-error'>参数错误</span>";
        }
    },

    /**
     * 获取套餐订单已启用的性质管理列表
     *
     * @param callback 回执函数
     */
    queryMealOrderNatureList: function (callback){
        AjaxPostUtil.request({url: shopBasePath + "queryMealOrderNatureList", params: {enabled: 1}, type: 'json', method: "POST", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 获取套餐订单是否赠送的字段信息
     *
     * @param data
     * @returns {string}
     */
    getMealOrderWhetherGiveName: function (data){
        if (data.whetherGive == 1) {
            return "是";
        } else if (data.whetherGive == 2) {
            return "否";
        } else {
            return "";
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
            } else if (data.state == 2) {
                return "<span class='state-up'>已支付</span>";
            } else if (data.state == 3) {
                return "<span class=''>已收货</span>";
            } else if (data.state == 4) {
                return "<span class=''>已关闭</span>";
            } else if (data.state == 5) {
                return "<span class=''>已退款</span>";
            } else if (data.state == 0) {
                return "<span class=''>已提交订单</span>";
            } else if (data.state == 51) {
                return "<span class=''>退款中</span>";
            } else if (data.state == 6) {
                return "<span class=''>退款驳回</span>";
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
        } else {
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
            if(typeof(callback) == "function") {
                callback(json);
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
            if(typeof(callback) == "function") {
                callback(json);
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
            if(typeof(callback) == "function") {
                callback(json);
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
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 获取所有门店列表
     *
     * @param callback 回执函数
     */
    queryAllStoreList: function (callback){
        var params = {
            limit: 1000,
            page: 1
        };
        AjaxPostUtil.request({url: shopBasePath + "store001", params: params, type: 'json', method: "POST", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
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
                if(typeof(callback) == "function") {
                    callback(shopUtil.staffMation);
                }
            }});
    },

};
