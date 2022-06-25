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
        AjaxPostUtil.request({url: flowableBasePath + "activitiProcess001", params: params, type: 'json', callback: function(json) {
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
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
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
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
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
        return chooseData.id;
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
                        callback(activitiUtil.chooseApprovalPersonMation.id);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
            }
        });
    },

    /**
     * 用户节点转为会签节点-----该功能未实现
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
        AjaxPostUtil.request({url: flowableBasePath + "activitiProcess003", params: params, method: "POST", type: 'json', callback: function(json) {
            if(json.returnCode == 0) {
                if (typeof callback === 'function') {
                    callback();
                }
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }, async: false});
    },

    /**
     * 工作流的其他操作
     *
     * @param boxId 按钮展示的位置
     * @param task 任务信息
     * @param callback 回调函数
     */
    activitiMenuOperator: function (boxId, task, callback){
        var operatorBtnHtml = '';
        if((task.nrOfInstances == 0 && !isNull(task.nrOfInstances)) || isNull(task.nrOfInstances)) {
            // 不是多实例会签||是会签但是还没有设定会签人
            if (!task.delegation) {
                // 不是委派任务节点可以委派
                operatorBtnHtml += '<a class="layui-btn layui-btn-normal" id="delegate" style="height: 30px; line-height: 30px; padding: 0 15px;">委派</a>';
            }
            operatorBtnHtml += '<a class="layui-btn layui-btn-normal" id="transfer" style="height: 30px; line-height: 30px; padding: 0 15px;">转办</a>';
            operatorBtnHtml += '<a class="layui-btn layui-btn-normal" id="beforeAddSignTask" style="height: 30px; line-height: 30px; padding: 0 15px;">前加签</a>';
            operatorBtnHtml += '<a class="layui-btn layui-btn-normal" id="afterAddSignTask" style="height: 30px; line-height: 30px; padding: 0 15px;">后加签</a>';
        }
        if(task.isMultiInstance){
            // 会签节点进行加签
            operatorBtnHtml += '<a class="layui-btn layui-btn-normal" id="jointlySign" style="height: 30px; line-height: 30px; padding: 0 15px;">会签设定</a>';
        }
        $("#" + boxId).html(operatorBtnHtml);
        // 初始化监听事件
        activitiUtil.activitiMenuEvent(task, callback);
    },

    /**
     * 工作流的其他操作监听事件
     *
     * @param task 任务信息
     * @param callback 回调函数
     */
    activitiMenuEvent: function (task, callback){
        // 委派
        $("body").on("click", "#delegate", function() {
            systemCommonUtil.userReturnList = [];
            systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
            systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
            systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
            systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
                var params = {
                    taskId: task.taskId,
                    principalUserId: userReturnList[0].id
                };
                AjaxPostUtil.request({url: flowableBasePath + "activitiTask001", params: params, method: "POST", type: 'json', callback: function(json) {
                    if(json.returnCode == 0) {
                        if (typeof callback === 'function') {
                            callback();
                        }
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }, async: false});
            });
        });

        // 转办
        $("body").on("click", "#transfer", function() {
            systemCommonUtil.userReturnList = [];
            systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
            systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
            systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
            systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
                var params = {
                    taskId: task.taskId,
                    transferredPersonId: userReturnList[0].id
                };
                AjaxPostUtil.request({url: flowableBasePath + "activitiTask002", params: params, method: "POST", type: 'json', callback: function(json) {
                    if(json.returnCode == 0) {
                        if (typeof callback === 'function') {
                            callback();
                        }
                    } else {
                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                    }
                }, async: false});
            });
        });

        // 前加签
        $("body").on("click", "#beforeAddSignTask", function() {
            _openNewWindows({
                url: "../../tpl/addSignTask/beforeAddSignTask.html?taskId=" + task.taskId,
                title: "前加签",
                pageId: "beforeAddSignTaskPage",
                area: ['90vw', '90vh'],
                callBack: function(refreshCode){
                    if (refreshCode == '0') {
                        if (typeof callback === 'function') {
                            callback();
                        }
                    } else if (refreshCode == '-9999') {
                        winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                    }
                }
            });
        });

        // 后加签
        $("body").on("click", "#afterAddSignTask", function() {
            _openNewWindows({
                url: "../../tpl/addSignTask/afterAddSignTask.html?taskId=" + task.taskId,
                title: "后加签",
                pageId: "afterAddSignTaskPage",
                area: ['90vw', '90vh'],
                callBack: function(refreshCode){
                    if (refreshCode == '0') {
                        if (typeof callback === 'function') {
                            callback();
                        }
                    } else if (refreshCode == '-9999') {
                        winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                    }
                }
            });
        });

        // 会签设定人员
        $("body").on("click", "#jointlySign", function() {
            _openNewWindows({
                url: "../../tpl/addSignTask/jointlySign.html?taskId=" + task.taskId,
                title: "会签设定",
                pageId: "jointlySignPage",
                area: ['90vw', '90vh'],
                callBack: function(refreshCode){
                    if (refreshCode == '0') {
                        if (typeof callback === 'function') {
                            callback();
                        }
                    } else if (refreshCode == '-9999') {
                        winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                    }
                }
            });
        });

    },

    showStateName: function (state, submitType){
        if(submitType == 1){
            if(state == '0'){
                return "<span class='state-down'>未审核</span>";
            }else if(state == '1'){
                return "<span class='state-up'>审核中</span>";
            }else if(state == '2'){
                return "<span class='state-new'>审核通过</span>";
            }else if(state == '3'){
                return "<span class='state-down'>拒绝通过</span>";
            }else if(state == '4'){
                return "<span class='state-new'>已完成</span>";
            }else if(state == '5'){
                return "<span class='state-error'>撤销</span>";
            } else {
                return "参数错误";
            }
        } else if(submitType == 2){
            if(state == '0'){
                return "<span class='state-down'>未提交</span>";
            }else if(state == '2'){
                return "<span class='state-new'>已提交</span>";
            }else if(state == '4'){
                return "<span class='state-new'>已完成</span>";
            } else {
                return "参数错误";
            }
        }
    },

    showStateName2: function (state, submitType){
        if(submitType == 1){
            if(state == '0'){
                return "<span>草稿</span>";
            }else if(state == '1'){
                return "<span class='state-up'>审核中</span>";
            }else if(state == '2'){
                return "<span class='state-new'>审核通过</span>";
            }else if(state == '3'){
                return "<span class='state-down'>拒绝通过</span>";
            }else if(state == '4'){
                return "<span class='state-down'>作废</span>";
            }else if(state == '5'){
                return "<span class='state-error'>撤销</span>";
            } else {
                return "参数错误";
            }
        } else if(submitType == 2){
            if(state == '0'){
                return "<span>草稿</span>";
            }else if(state == '2'){
                return "<span class='state-new'>已提交</span>";
            }else if(state == '4'){
                return "<span class='state-new'>作废</span>";
            } else {
                return "参数错误";
            }
        }
    },

};