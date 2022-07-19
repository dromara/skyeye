
// 行政管理模块相关工具
var adminAssistantUtil = {

    /**
     * 车辆管理---查询所有的车牌号用于下拉选择框
     *
     * @param callback 回执函数
     */
    queryAllVehicleList: function (callback){
        AjaxPostUtil.request({url: flowableBasePath + "vehicle010", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

};
