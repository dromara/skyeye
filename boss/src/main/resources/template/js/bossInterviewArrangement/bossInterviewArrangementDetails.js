
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function(exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;

    var type = GetUrlParam('type');

    showGrid({
        id: "showForm",
        url: flowableBasePath + "queryBossInterviewArrangementById",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json){
            json.bean.basicResume = stringManipulation.textAreaShow(json.bean.basicResume);
            json.bean.evaluation = stringManipulation.textAreaShow(json.bean.evaluation);
            json.bean.reason = stringManipulation.textAreaShow(json.bean.reason);
            if (type == 'interviewerResult') {
                // 面试官查看
                json.bean.stateName = bossUtil.showArrangeInterviewerStateName(json.bean.state);
            } else {
                // HR以及部门经理查看
                json.bean.stateName = bossUtil.showStateName(json.bean.state);
                // 入职信息的状态
                json.bean.inductionResultStateName = bossUtil.showInductionResultStateName(json.bean.state);
            }
        },
        ajaxSendAfter: function (json) {
            skyeyeEnclosure.showDetails({'enclosureUpload': json.bean.enclosureInfo});

            console.log(type)
            if (type == 'interviewerResult') {
                // 面试官查看
                $("#inductionResultMation").hide();
            } else {
                // HR以及部门经理查看
            }

            matchingLanguage();
            form.render();
        }
    });

});