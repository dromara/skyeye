layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'textool', 'laydate'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate,
        textool = layui.textool,
        form = layui.form;
    var selOption = getFileContent('tpl/template/select-option.tpl');

    showGrid({
        id: "showForm",
        url: flowableBasePath + "queryBossInterviewQuitToEditById",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendAfter: function(json){
            // 离职类型
            $("#leaveType").html(getDataUseHandlebars(selOption, {rows: bossUtil.getLeaveTypeList()}));
            $("#leaveType").val(json.bean.leaveType);

            // 离职日期
            laydate.render({elem: '#leaveTime', type: 'date'});

            textool.init({
                eleId: 'remark',
                maxlength: 200,
                tools: ['count', 'copy', 'reset']
            });

            // 附件回显
            skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

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
            activitiUtil.startProcess(sysActivitiModel["bossInterviewQuit"]["key"], function (approvalId) {
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
            leaveTime: $("#leaveTime").val(),
			leaveType: $("#leaveType").val(),
			remark: $("#remark").val(),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId,
            id: parent.rowId
        };
        AjaxPostUtil.request({url: flowableBasePath + "updateBossInterviewQuit", params: params, type: 'json', method: "PUT", callback: function(json) {
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