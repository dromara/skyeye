
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

    laydate.render({elem: '#interviewTime'});

    matchingLanguage();
    // 保存为草稿
    form.on('submit(formAddBean)', function(data) {
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
            state: state
        };
        AjaxPostUtil.request({url: flowableBasePath + "insertBossInterviewArrangement", params: params, type: 'json', method: "POST", callback: function(json) {
            parent.layer.close(index);
            parent.refreshCode = '0';
        }});
    }

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