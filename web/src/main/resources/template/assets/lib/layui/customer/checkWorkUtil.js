

// 考勤相关工具类
var checkWorkUtil = {

    /**
     * 获取当前登陆人的考勤班次
     *
     * @param callback 回执函数
     */
    getCurrentUserCheckWorkTimeList: function (callback) {
        AjaxPostUtil.request({url: flowableBasePath + "checkworktime007", params: {}, type: 'json', method: "GET", callback: function(json) {
            if(typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

};