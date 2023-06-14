
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
    },

    // 离职类型状态
    leaveTypeList: {
        "automaticResignation": {"id": 1, "name": "自动离职"},
        "retire": {"id": 2, "name": "退休"},
        "diseaseWords": {"id": 3, "name": "病辞"},
        "dismiss": {"id": 4, "name": "辞退"},
        "resignation": {"id": 5, "name": "辞职"}
    },
    getLeaveTypeList: function () {
        var list = [];
        $.each(bossUtil.leaveTypeList, function (key, value) {
            list.push(value);
        });
        return list;
    },
    getLeaveTypeNameById: function (id){
        var list = bossUtil.getLeaveTypeList();
        return getInPoingArr(list, "id", id, "name");
    },

    // 调岗类型
    transferTypeList: {
        "flatTone": {"id": 1, "name": "平调"},
        "promotion": {"id": 2, "name": "晋升"},
        "demotion": {"id": 3, "name": "降职"},
    },
    getTransferTypeList: function () {
        var list = [];
        $.each(bossUtil.transferTypeList, function (key, value) {
            list.push(value);
        });
        return list;
    },
    getTransferTypeNameById: function (id){
        var list = bossUtil.getTransferTypeList();
        return getInPoingArr(list, "id", id, "name");
    }

};