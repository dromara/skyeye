layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'textool', 'laydate', 'dtree'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate,
        textool = layui.textool,
        form = layui.form,
        dtree = layui.dtree;
    var selOption = getFileContent('tpl/template/select-option.tpl');

    showGrid({
        id: "showForm",
        url: flowableBasePath + "queryBossInterviewJobTransferToEditById",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendAfter: function (json) {
            // 调岗类型
            $("#transferType").html(getDataUseHandlebars(selOption, {rows: bossUtil.getTransferTypeList()}));
            $("#transferType").val(json.bean.transferType);

            textool.init({
                eleId: 'remark',
                maxlength: 200,
                tools: ['count', 'copy', 'reset']
            });

            var organization = {
                companyId: json.bean.currentCompanyId,
                departmentId: json.bean.currentDepartmentId,
                jobId: json.bean.currentJobId,
                jobScoreId: json.bean.currentJobScoreId
            };
            // 加载组织结构
            organizationUtil.initEditOrganization(dtree, organization);

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
            activitiUtil.startProcess(sysActivitiModel["bossInterviewJobTransfer"]["key"], function (approvalId) {
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
        if(isNull(organizationUtil.getCompanyMation(dtree)['nodeId'])){
            winui.window.msg('请选择企业', {icon: 2, time: 2000});
            return false;
        }
        if(isNull(organizationUtil.getDepartmentMation(dtree)['nodeId'])){
            winui.window.msg('请选择部门', {icon: 2, time: 2000});
            return false;
        }
        if(isNull(organizationUtil.getJobMation(dtree)['nodeId'])){
            winui.window.msg('请选择岗位', {icon: 2, time: 2000});
            return false;
        }
        var params = {
            transferType: $("#transferType").val(),
            transferStaffId: $("#transferStaffId").attr("staffId"),
            primaryCompanyId: $("#primaryCompanyId").attr("companyId"),
            primaryCompanyName: $("#primaryCompanyId").html(),
            primaryDepartmentId: $("#primaryDepartmentId").attr("departmentId"),
            primaryDepartmentName: $("#primaryDepartmentId").html(),
            primaryJobId: $("#primaryJobId").attr("jobId"),
            primaryJobName: $("#primaryJobId").html(),
            primaryJobScoreId: $("#primaryJobScoreId").attr("jobScoreId"),
            primaryJobScoreName: $("#primaryJobScoreId").html(),

            currentCompanyId: organizationUtil.getCompanyMation(dtree)['nodeId'],
            currentCompanyName: organizationUtil.getCompanyMation(dtree)['context'],
            currentDepartmentId: organizationUtil.getDepartmentMation(dtree)['nodeId'],
            currentDepartmentName: organizationUtil.getDepartmentMation(dtree)['context'],
            currentJobId: organizationUtil.getJobMation(dtree)['nodeId'],
            currentJobName: organizationUtil.getJobMation(dtree)['context'],
            currentJobScoreId: organizationUtil.getJobScoreMation(dtree)['nodeId'],
            currentJobScoreName: organizationUtil.getJobScoreMation(dtree)['context'],

            remark: $("#remark").val(),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId,
            id: parent.rowId
        };
        AjaxPostUtil.request({url: flowableBasePath + "updateBossInterviewJobTransfer", params: params, type: 'json', method: "PUT", callback: function(json) {
            if(json.returnCode == 0) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    }

    // 人员选择
    $("body").on("click", ".transferStaffIdBtn", function() {
        systemCommonUtil.userStaffCheckType = false; // 选择类型，默认单选，true:多选，false:单选
        systemCommonUtil.openSysAllUserStaffChoosePage(function (checkStaffMation){
            $("#transferStaffId").attr("staffId", checkStaffMation.id);
            $("#transferStaffId").val(checkStaffMation.jobNumber + '_' + checkStaffMation.userName);
            $("#primaryCompanyId").attr("companyId", checkStaffMation.companyId);
            $("#primaryCompanyId").html(checkStaffMation.companyName);
            $("#primaryDepartmentId").attr("departmentId", checkStaffMation.departmentId);
            $("#primaryDepartmentId").html(checkStaffMation.departmentName);
            $("#primaryJobId").attr("jobId", checkStaffMation.jobId);
            $("#primaryJobId").html(checkStaffMation.jobName);
            $("#primaryJobScoreId").attr("jobScoreId", checkStaffMation.jobScoreId);
            $("#primaryJobScoreId").html(checkStaffMation.jobScoreName);
        });
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});