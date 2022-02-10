
// 商城相关工具类
var shopUtil = {

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

};
