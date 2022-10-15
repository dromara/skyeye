layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'textool', 'laydate'], function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate,
        textool = layui.textool,
        form = layui.form;
    var serviceClassName = sysServiceMation["bossInterviewQuit"]["key"];
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 离职类型
    $("#leaveType").html(getDataUseHandlebars(selOption, {rows: bossUtil.getLeaveTypeList()}));

    // 离职日期
    laydate.render({elem: '#leaveTime', type: 'date'});

    textool.init({eleId: 'remark', maxlength: 200});

    if(!systemCommonUtil.judgeCurrentUserQuit()) {
        winui.window.msg('您已提交离职申请，无法重复提交', {icon: 5, time: 2000}, function() {
            parent.layer.close(index);
            parent.refreshCode = '-9999';
        });
    }

    // 获取当前登录员工信息
    systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
        $("#applyUser").html(data.bean.jobNumber + '_' + data.bean.userName);
    });

    skyeyeEnclosure.init('enclosureUpload');
    matchingLanguage();
    form.render();
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
            activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    function saveData(subType, approvalId) {
        var params = {
            leaveTime: $("#leaveTime").val(),
			leaveType: $("#leaveType").val(),
			remark: $("#remark").val(),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: flowableBasePath + "insertBossInterviewQuit", params: params, type: 'json', method: "POST", callback: function(json) {
            parent.layer.close(index);
            parent.refreshCode = '0';
        }});
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});