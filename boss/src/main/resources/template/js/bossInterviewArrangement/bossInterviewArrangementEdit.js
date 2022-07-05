
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'laydate'], function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate,
        form = layui.form;

    showGrid({
        id: "showForm",
        url: flowableBasePath + "queryBossInterviewArrangementToEditById",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendLoadBefore: function(hdb, json){
            json.bean.basicResume = stringManipulation.textAreaShow(json.bean.basicResume);
        },
        ajaxSendAfter: function (json) {
            laydate.render({elem: '#interviewTime'});
            skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

            bossUtil.bossIntervieweeChooseMation = {
                id: json.bean.interviewId
            };

            bossUtil.bossPersonRequireChooseMation = {
                id: json.bean.personRequireId,
                recruitDepartmentId: json.bean.interviewDepartmentId,
                recruitJobId: json.bean.interviewJobId
            };

            matchingLanguage();
            // 保存为草稿
            form.on('submit(formEditBean)', function(data) {
                if(winui.verifyForm(data.elem)) {
                    saveData("1");
                }
                return false;
            });

            // 提交
            form.on('submit(formSubBean)', function(data) {
                if(winui.verifyForm(data.elem)) {
                    saveData("2");
                }
                return false;
            });

            function saveData(state){
                var params = {
                    interviewId: bossUtil.bossIntervieweeChooseMation.id,
                    interviewTime: $("#interviewTime").val(),
                    interviewDepartmentId: bossUtil.bossPersonRequireChooseMation.recruitDepartmentId,
                    interviewJobId: bossUtil.bossPersonRequireChooseMation.recruitJobId,
                    personRequireId: bossUtil.bossPersonRequireChooseMation.id,
                    state: state,
                    id: parent.rowId
                };
                AjaxPostUtil.request({url: flowableBasePath + "editBossInterviewArrangement", params: params, type: 'json', method: "PUT", callback: function(json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }});
            }
        }
    });

    // 面试者选择
    $("body").on("click", "#toHandsInterviewId", function() {
        bossUtil.openBossIntervieweeChoosePage(function (bossIntervieweeChooseMation) {
            $("#interviewId").val(bossIntervieweeChooseMation.name);
            $("#phone").html(bossIntervieweeChooseMation.phone);
            $("#workYears").html(bossIntervieweeChooseMation.workYears);
            $("#favoriteJob").html(bossIntervieweeChooseMation.favoriteJob);
            $("#basicResume").html(stringManipulation.textAreaShow(bossIntervieweeChooseMation.basicResume));
            skyeyeEnclosure.showDetails({"enclosureUpload": bossIntervieweeChooseMation.enclosureInfo});
        });
    });

    // 人员需求选择
    $("body").on("click", "#personRequireId", function() {
        bossUtil.openBossPersonRequireChoosePage(function (bossPersonRequireChooseMation) {
            $("#interviewDepartmentId").html(bossPersonRequireChooseMation.recruitDepartmentName);
            $("#interviewJobId").html(bossPersonRequireChooseMation.recruitJobName);
        });
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});