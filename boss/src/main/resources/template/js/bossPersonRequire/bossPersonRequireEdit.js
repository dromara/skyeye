
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
    var selOption = getFileContent('tpl/template/select-option.tpl');

    showGrid({
        id: "showForm",
        url: flowableBasePath + "queryBossPersonRequireToEditById",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendAfter: function(json){
            // 附件回显
            skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

            // 根据部门id获取岗位集合
            systemCommonUtil.queryJobListByDepartmentId(json.bean.recruitDepartmentId, function(data) {
                $("#recruitJobId").html(getDataUseHandlebars(selOption, data));
                $("#recruitJobId").val(json.bean.recruitJobId);
                form.render('select');
            });

            if(json.bean.state == '1'){
                $(".typeTwo").removeClass("layui-hide");
            } else {
                $(".typeOne").removeClass("layui-hide");
            }

            matchingLanguage();
            form.render();
        }
    });

    // 保存为草稿
    form.on('submit(formEditBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData('1', "");
        }
        return false;
    });

    // 提交审批
    form.on('submit(formSubBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            activitiUtil.startProcess(sysActivitiModel["bossPersonRequire"]["key"], function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    // 工作流中保存
    form.on('submit(subBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData('3', "");
        }
        return false;
    });

    function saveData(subType, approvalId){
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
            approvalId: approvalId,
            id: parent.rowId
        };
        AjaxPostUtil.request({url: flowableBasePath + "updateBossPersonRequire", params: params, type: 'json', method: "PUT", callback: function(json) {
            if(json.returnCode == 0) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    }

    $("body").on("click", "#cancle", function(){
        parent.layer.close(index);
    });
});