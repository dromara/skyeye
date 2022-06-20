
// 学校模块相关工具类
var schoolUtil = {

    /**
     * 获取当前登陆用户所属的学校列表
     *
     * @param callback 回执函数
     */
    queryMyBelongSchoolList: function (callback) {
        AjaxPostUtil.request({url: schoolBasePath + "schoolmation008", params: {}, type: 'json', method: "POST", callback: function(json) {
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