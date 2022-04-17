
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
    },

    /**
     * 面试安排的状态
     *
     * @param state
     * @returns {string}
     */
    showStateName: function (state){
        if (state == '1') {
            return "<span>草稿</span>";
        } else if (state == '2') {
            return "<span class='state-new'>已提交(待安排面试人员)</span>";
        } else if (state == '3') {
            return "<span class='state-new'>已提交(待面试)</span>";
        } else if (state == '4') {
            return "<span class='state-up'>面试通过</span>";
        } else if (state == '5') {
            return "<span class='state-down'>面试不通过</span>";
        } else if (state == '6') {
            return "<span class='state-success'>已完成入职</span>";
        } else if (state == '7') {
            return "<span class='state-error'>已完成拒绝入职</span>";
        } else if (state == '8') {
            return "<span class='state-error'>作废</span>";
        } else {
            return "参数错误";
        }
    },

    /**
     * 入职信息的状态
     *
     * @param state
     * @returns {string}
     */
    showInductionResultStateName: function (state){
        if (state == '6') {
            return "<span class='state-success'>同意入职</span>";
        } else if (state == '7') {
            return "<span class='state-error'>拒绝入职</span>";
        }
        return '';
    },

    /**
     * 面试官列表展示的面试状态
     *
     * @param state
     * @returns {string}
     */
    showArrangeInterviewerStateName: function (state){
        if (state == '3') {
            return "<span class='state-new'>待面试</span>";
        } else if (state == '4' || state == '6' || state == '7') {
            return "<span class='state-up'>面试通过</span>";
        } else if (state == '5') {
            return "<span class='state-down'>面试不通过</span>";
        }
    },

    /**
     * 打开我负责的未入职的面试者选择页面
     *
     * @param callback 回调函数
     */
    bossIntervieweeChooseMation: {}, // 已经选择的面试者信息
    openBossIntervieweeChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/bossInterviewee/myChargeBossIntervieweeListChoose.html",
            title: "面试者",
            pageId: "myChargeBossIntervieweeListChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(bossUtil.bossIntervieweeChooseMation);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

    /**
     * 打开我负责的人员需求选择页面
     *
     * @param callback 回调函数
     */
    bossPersonRequireChooseMation: {}, // 已经选择的人员需求信息
    openBossPersonRequireChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/bossPersonRequire/bossPersonRequireMyChargeListChoose.html",
            title: "人员需求",
            pageId: "bossPersonRequireMyChargeListChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(bossUtil.bossPersonRequireChooseMation);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

};