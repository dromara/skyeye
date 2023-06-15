
// 招聘模块工具函数
var bossUtil = {

    /**
     * 打开我负责的未入职的面试者选择页面--表单组件中使用
     *
     * @param callback 回调函数
     */
    openBossIntervieweeChoosePage: function (callback) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2023061200001', null),
            title: "面试者",
            pageId: "myChargeBossIntervieweeListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(chooseItemMation);
                }
            }});
    },

    /**
     * 打开我负责的人员需求选择页面--表单组件中使用
     *
     * @param callback 回调函数
     */
    bossPersonRequireChooseMation: {}, // 已经选择的人员需求信息
    openBossPersonRequireChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/bossPersonRequire/bossPersonRequireMyChargeListChoose.html",
            title: "人员需求",
            pageId: "bossPersonRequireMyChargeListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(bossUtil.bossPersonRequireChooseMation);
                }
            }});
    }

};