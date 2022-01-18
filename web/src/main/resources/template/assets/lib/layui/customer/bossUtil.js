
// 招聘模块工具函数
var bossUtil = {

    /**
     * 打开面试者来源选择页面
     *
     * @param callback 回调函数
     */
    bossIntervieweeFromChooseMation: {}, // 已经选择的面试者来源信息
    openBossIntervieweeFromChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/bossIntervieweeFrom/bossIntervieweeFromListChoose.html",
            title: "面试者来源",
            pageId: "bossIntervieweeFromListChoosePage",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(bossUtil.bossIntervieweeFromChooseMation);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    }

};