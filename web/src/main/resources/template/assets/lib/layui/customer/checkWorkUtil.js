

// 考勤相关工具类
var checkWorkUtil = {

    /**
     * 获取当前登陆人的考勤班次
     *
     * @param callback 回执函数
     */
    getCurrentUserCheckWorkTimeList: function (callback){
        AjaxPostUtil.request({url: flowableBasePath + "checkworktime007", params: {}, type: 'json', method: "GET", callback: function(json) {
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