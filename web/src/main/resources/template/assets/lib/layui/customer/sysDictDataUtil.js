

var sysDictDataUtil = {

    /**
     * 获取指定状态的数据字典分类
     *
     * @param status 状态（0正常 1停用）
     * @param callback 回执函数
     */
    queryDictTypeListByStatus: function (status, callback) {
        var params = {
            status: status
        };
        AjaxPostUtil.request({url: reqBasePath + "queryDictTypeListByStatus", params: params, type: 'json', method: "GET", callback: function(json) {
            if (json.returnCode == 0) {
                if (typeof(callback) == "function") {
                    callback(json);
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

};
