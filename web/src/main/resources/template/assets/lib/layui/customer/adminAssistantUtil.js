
// 行政管理模块相关工具
var adminAssistantUtil = {

    /**
     * 获取已经上线的用品类别列表
     *
     * @param callback 回执函数
     */
    queryAssetArticlesTypeUpStateList: function (callback){
        AjaxPostUtil.request({url: flowableBasePath + "assetarticles010", params: {}, type: 'json', method: "GET", callback: function(json) {
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
