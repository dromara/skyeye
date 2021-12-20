// 工作流工具函数
var activitiUtil = {

    /**
     * 流程id
     */
    processInstanceId: "",

    /**
     * 任务id
     */
    taskId: "",

    /**
     * 审批结果,是否通过：1.通过2.不通过
     */
    flag: "",

    /**
     * 已经选择的审批人员信息
     */
    chooseApprovalPersonMation: {},

    /**
     * 该地址为activitiNameKey.json的key，因为刚启动流程，还没有流程id和任务id,所以只能用这种方式
     */
    pageUrl: "",

    /**
     * 工作流流程详情查看
     *
     * @param data
     */
    activitiDetails: function (data){
        taskType = data.taskType;
        processInstanceId = data.processInstanceId;
        _openNewWindows({
            url: "../../tpl/activiticommon/myactivitidetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "myactivitidetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    },

    /**
     * 加载审批人选择项
     *
     * @param appendDomId 指定dom结构后面加载
     * @param processInstanceId 流程id
     * @param taskId 任务id
     * @param flag 审批结果,是否通过：1.通过2.不通过
     */
    initApprovalPerson: function (appendDomId, processInstanceId, taskId, flag){
        activitiUtil.processInstanceId = processInstanceId;
        activitiUtil.taskId = taskId;
        activitiUtil.flag = flag;
        var params = {
            processInstanceId: processInstanceId,
            taskId: taskId,
            flag: flag
        };
        // 优先请求一次获取下个用户节点的信息，如果没有审批节点信息，则不加载审批人选项
        AjaxPostUtil.request({url:reqBasePath + "activitiProcess001", params: params, type:'json', callback:function(json) {
            if (json.returnCode == 0) {
                if(!isNull(json.bean)){
                    var approvalPersonChooseDom = '<div class="layui-form-item layui-col-xs12">' +
                        '<label class="layui-form-label">下一个审批人<i class="red">*</i></label>' +
                        '<div class="layui-input-block">' +
                        '<input type="text" id="approvalPersonName" name="approvalPersonName" placeholder="请选择下一个审批人" win-verify="required" class="layui-input" readonly="readonly"/>' +
                        '<i class="fa fa-plus-circle input-icon chooseApprovalPersonBtn" style="top: 12px;"></i>' +
                        '</div>' +
                        '</div>';
                    $("#" + appendDomId).append(approvalPersonChooseDom);
                    activitiUtil.initApprovalPersonChooseBtnEvent();
                }
            }
        }, async: false});
    },

    /**
     * 初始化审批人选择按钮事件
     */
    initApprovalPersonChooseBtnEvent: function (){
        $("body").on("click", ".chooseApprovalPersonBtn", function(){
            activitiUtil.openApprovalPersonChoosePage();
        });
    },

    /**
     * 打开审批人选择页面
     */
    openApprovalPersonChoosePage: function (){
        _openNewWindows({
            url: "../../tpl/approvalActiviti/approvalPersonChoose.html",
            title: "审批人选择",
            pageId: "approvalPersonChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    $("#approvalPersonName").val(activitiUtil.chooseApprovalPersonMation.jobNumber + "_" + activitiUtil.chooseApprovalPersonMation.userName);
                    $("#approvalPersonName").attr("chooseData", JSON.stringify(activitiUtil.chooseApprovalPersonMation));
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }
        });
    },

    /**
     * 获取选择的审批人
     */
    getApprovalPersonId: function (){
        var chooseDataStr = $("#approvalPersonName").attr("chooseData");
        if(isNull(chooseDataStr)){
            return "";
        }
        var chooseData = JSON.parse(chooseDataStr);
        return chooseData.userId;
    },

    /**
     * 启动流程时选择审批人
     *
     * @param pageUrl 该地址为activitiNameKey.json的key
     * @param callback 回调函数
     */
    startProcess: function (pageUrl, callback){
        activitiUtil.pageUrl = pageUrl;
        _openNewWindows({
            url: "../../tpl/approvalActiviti/startProcessPersonChooseBtn.html",
            title: "审批人选择",
            pageId: "startProcessPersonChooseBtn",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if (typeof callback === 'function') {
                        callback(activitiUtil.chooseApprovalPersonMation.userId);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }
        });
    },

    /**
     * 用户节点转为会签节点
     *
     * @param processInstanceId 流程id
     * @param taskId 任务id
     * @param callback 回调函数
     */
    turnMultiInstance: function (processInstanceId, taskId, callback){
        var params = {
            processInstanceId: processInstanceId,
            taskId: taskId,
            sequential: false,
            userIds: JSON.stringify(["300b878c5c6744f2b48e6bc40beefd11", "0f17e3da88bc4e22841156388964e12e"])
        };
        AjaxPostUtil.request({url: reqBasePath + "activitiProcess003", params: params, method: "POST", type: 'json', callback: function(json) {
            if(json.returnCode == 0) {
                if (typeof callback === 'function') {
                    callback();
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    }

};