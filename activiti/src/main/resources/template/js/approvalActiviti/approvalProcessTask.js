
// 以下两个参数开启团队权限时有值
var objectId = '', objectKey = '';
var pageMation;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'flow'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	flow = layui.flow,
    	form = layui.form;

    var taskId = parent.taskId;
    var processInstanceId = parent.processInstanceId;

    // 时间线审批历史列表模板
	var timeTreeApprovalHistory = $("#timeTreeApprovalHistory").html();

	AjaxPostUtil.request({url: flowableBasePath + "queryProcessInstance", params: {processInstanceId: processInstanceId}, type: 'json', method: 'POST', callback: function(data) {
		// 加载业务数据
		activitiUtil.loadBusiness(data.bean.objectId, data.bean.objectKey, data.bean.actFlowId, 'edit');

	}, async: false});
    
    AjaxPostUtil.request({url: flowableBasePath + "activitimode016", params: {taskId: taskId, processInstanceId: processInstanceId}, type: 'json', method: 'GET', callback: function(j){
		// 获取该节点的id和名称，todo 后续会使用
		var editableNodeId = j.bean.taskKey;
		var editableNodeName = j.bean.taskKeyName;

		// 加载流程图片
		$("#processInstanceIdImg").attr("src", fileBasePath + 'images/upload/activiti/' + processInstanceId + ".png?cdnversion=" + Math.ceil(new Date() / 3600000));

		// 如果是多实体会签 && 该节点已经是最后一个人审批则可以选择审批人
		// 如果是单实例节点 && 当前节点不是委派节点 && 有下个节点则可以选择审批人
		if ((j.bean.isMultiInstance && !isNull(String(j.bean.multilnStanceExecttionChild)) && !j.bean.multilnStanceExecttionChild)
			|| (!j.bean.delegation && !j.bean.isMultiInstance && !isNull(String(j.bean.nextTask)) && j.bean.nextTask)) {
			// 加载下个节点审批人选择信息
			activitiUtil.initApprovalPerson("approvalOpinionDom", processInstanceId, taskId, $("input[name='flag']:checked").val());
		}

		// 并行会签的子实例，不支持工作流的其他操作
		if (!j.bean.multilnStanceExecttionChild) {
			activitiUtil.activitiMenuOperator("otherMenuOperator", j.bean, function () {
				parent.layer.close(index);
				parent.refreshCode = '0';
			});
		} else {
			$("#otherMenuOperator").parent().hide();
		}

		// 加载会签信息
		if (j.bean.isMultiInstance) {
			$("#multiInstanceBox").html(getDataUseHandlebars($("#multiInstance").html(), j));
			$("#multiInstanceState").html('已开启');
			if(j.bean.nrOfInstances != 0){
				// 会签任务总数为0说明没有设置会签人，可以自行审批通过，如果不为0，说明设置了会签人，需要通过会签投票获取结果
				$("#resultTitle").html('会签结果');
				$("#multiInstanceState").html('已完成');
				if(j.bean.nrOfActiveInstances != 0){
					// 正在执行的会签总数不为0并且不是子实例，说明会签还未结束，不能提交到下一个审批节点
					if(!j.bean.multilnStanceExecttionChild){
						$("#approvalOpinionDom").hide();
						$("#subBtnBox").hide();
					}
					$("#multiInstanceState").html('进行中');
				}
				if (!isNull(j.bean.approvalResult + "")){
					// 如果已经获得会签结果，则可以进行提交到下一步
					if(j.bean.approvalResult){
						$("input:radio[name=flag][value='1']").attr("checked", true);
					} else {
						$("input:radio[name=flag][value='2']").attr("checked", true);
					}
					$("input[name='flag']").attr('disabled', true);
					$("#approvalOpinionDom").show();
					$("#subBtnBox").show();
				}
			}
		}

		// 加载审批历史
		inboxTimeTreeApprovalHistory();
		matchingLanguage();
		form.render();
	}});

    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	var msg = '确认提交任务吗？';
    		layer.confirm(msg, { icon: 3, title: '提交任务' }, function (i) {
    			layer.close(i);
    			var params = {
	    			taskId: taskId,
	    			opinion: $("#opinion").val(),
	    			flag: $("input[name='flag']:checked").val(),
	    			processInstanceId: processInstanceId,
					approverId: activitiUtil.getApprovalPersonId()
	            };
	            AjaxPostUtil.request({url: flowableBasePath + "activitimode005", params: params, type: 'json', method: 'POST', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
    		});
        }
        return false;
    });
    
    // 加载时间线审批历史
	function inboxTimeTreeApprovalHistory(){
		flow.load({
			elem: '#timeTreeApprovalHistoryList',
			scrollElem: '#timeTreeApprovalHistoryList',
			isAuto: true,
			done: function(page, next) {
				var lis = [];
				AjaxPostUtil.request({url: flowableBasePath + "activitimode017", params: {processInstanceId: parent.processInstanceId}, type: 'json', callback: function (json) {
					$.each(json.rows, function(index, bean) {
						bean.showClass = 'date02';
						lis.push(getDataUseHandlebars(timeTreeApprovalHistory, {bean: bean}));
					});
					next(lis.join(''), (page * 1000) < json.total);
		   		}});
			}
		});
	}
	
    $("body").on("click", "#processInstanceIdImg", function() {
		systemCommonUtil.showPicImg($(this).attr("src"));
    });
    
    // 取消
    $("body").on("click", "#cancle", function() {
    	parent.layer.close(index);
    });

    exports('approvalProcessTask', {});
});
