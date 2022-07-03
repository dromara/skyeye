
// 项目管理相关工具类
var proUtil = {

    /**
     * 获取我参与的项目列表
     *
     * @param callback 回执函数
     */
    queryMyProjectsList: function (callback){
        AjaxPostUtil.request({url: flowableBasePath + "queryMyProjectsList", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

};
