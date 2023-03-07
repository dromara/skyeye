
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
    var $ = layui.$,
        form = layui.form,
        flow = layui.flow;
    var processInstanceId = parent.processInstanceId;

    // 时间线审批历史列表模板
	var timeTreeApprovalHistory = $("#timeTreeApprovalHistory").html();
	
    AjaxPostUtil.request({url: flowableBasePath + "queryProcessInstance", params: {processInstanceId: processInstanceId}, type: 'json', method: 'POST', callback: function(data) {
		// 加载业务数据
		activitiUtil.loadBusiness(data.bean.objectId, data.bean.objectKey, data.bean.actFlowId, 'details');
		// 加载流程图片
		$("#processInstanceIdImg").attr("src", fileBasePath + 'images/upload/activiti/' + processInstanceId + ".png?cdnversion=" + Math.ceil(new Date()/3600000));
		matchingLanguage();
	}, async: false});

	// 加载审批历史
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

    // 图片查看
    $("body").on("click", "#processInstanceIdImg, .photo-img", function() {
		systemCommonUtil.showPicImg($(this).attr("src"));
    });
    
    form.render();
    
    exports('processInstanceDetails', {});
});
