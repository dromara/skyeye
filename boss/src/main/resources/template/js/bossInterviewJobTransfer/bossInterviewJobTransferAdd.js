layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'textool', 'laydate', 'dtree'], function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate,
        textool = layui.textool,
        form = layui.form,
        dtree = layui.dtree;
    var serviceClassName = sysServiceMation["bossInterviewJobTransfer"]["key"];
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 调岗类型
    $("#transferType").html(getDataUseHandlebars(selOption, {rows: bossUtil.getTransferTypeList()}));

    textool.init({eleId: 'remark', maxlength: 200});

    // 加载组织结构
    organizationUtil.initAddOrganization(dtree);

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
            activitiUtil.startProcess(serviceClassName, function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    function saveData(subType, approvalId) {
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
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: flowableBasePath + "insertBossInterviewJobTransfer", params: params, type: 'json', method: "POST", callback: function(json) {
            parent.layer.close(index);
            parent.refreshCode = '0';
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