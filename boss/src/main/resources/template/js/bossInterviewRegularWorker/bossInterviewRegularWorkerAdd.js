layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'form', 'textool', 'laydate'], function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate,
        textool = layui.textool,
        form = layui.form;

    // 转正日期
    laydate.render({elem: '#regularTime', type: 'date'});

    textool.init({eleId: 'remark', maxlength: 200});

    if(!systemCommonUtil.judgeCurrentUserRegularWorker()) {
        winui.window.msg('您已提交转正申请，无法重复提交', {icon: 5, time: 2000}, function() {
            parent.layer.close(index);
            parent.refreshCode = '-9999';
        });
    }

    // 获取当前登录员工信息
    systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
        $("#regularWorkerId").html(data.bean.jobNumber + '_' + data.bean.userName);
        $("#departmentId").attr("departmentId", data.bean.departmentId);
        $("#departmentId").html(data.bean.departmentName);
        $("#jobId").attr("jobId", data.bean.jobId);
        $("#jobId").html(data.bean.jobName);
    });

    skyeyeEnclosure.init('enclosureUpload');
    matchingLanguage();
    // 保存为草稿
    form.on('submit(formAddBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData("1", "");
        }
        return false;
    });

    // 提交审批
    form.on('submit(formSubBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            activitiUtil.startProcess(sysActivitiModel["bossInterviewRegularWorker"]["key"], function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    function saveData(subType, approvalId){
        var params = {
            departmentId: $("#departmentId").attr("departmentId"),
			jobId: $("#jobId").attr("jobId"),
			regularTime: $("#regularTime").val(),
			remark: $("#remark").val(),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: flowableBasePath + "insertBossInterviewRegularWorker", params: params, type: 'json', method: "POST", callback: function(json) {
            if(json.returnCode == 0) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});