
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'form'], function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 获取当前登录员工信息
    systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
        var departmentId = data.bean.departmentId;
        $("#recruitDepartmentId").attr("departmentId", departmentId);
        $("#recruitDepartmentId").html(data.bean.departmentName);
        $("#applyDepartmentId").attr("departmentId", departmentId);
        $("#applyDepartmentId").html(data.bean.departmentName);
        $("#applyUser").html(data.bean.jobNumber + '_' + data.bean.userName);
        // 根据部门id获取岗位集合
        systemCommonUtil.queryJobListByDepartmentId(departmentId, function(data) {
            $("#recruitJobId").html(getDataUseHandlebars(selOption, data));
            form.render('select');
        });
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
            activitiUtil.startProcess(sysActivitiModel["bossPersonRequire"]["key"], null, function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    function saveData(subType, approvalId) {
        var params = {
            recruitDepartmentId: $("#recruitDepartmentId").attr("departmentId"),
            recruitJobId: $("#recruitJobId").val(),
            wages: $("#wages").val(),
            recruitNum: $("#recruitNum").val(),
            jobRequire: $("#jobRequire").val(),
            remark: $("#remark").val(),
            applyDepartmentId: $("#applyDepartmentId").attr("departmentId"),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: flowableBasePath + "insertBossPersonRequire", params: params, type: 'json', method: "POST", callback: function(json) {
            parent.layer.close(index);
            parent.refreshCode = '0';
        }});
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});