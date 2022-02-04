
// 商城相关工具类
var shopUtil = {

    /**
     * 获取区域信息
     *
     * @param callback 回执函数
     */
    getShopAreaMation: function (callback){
        AjaxPostUtil.request({url: shopBasePath + "area007", params: {}, type: 'json', method: "GET", callback: function(json) {
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
