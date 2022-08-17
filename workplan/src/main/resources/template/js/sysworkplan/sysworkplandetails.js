
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    var executorId = "";
	    if (!isNull(parent.executorId)){
			executorId = parent.executorId;
		}
	    AjaxPostUtil.request({url: sysMainMation.workplanBasePath + "sysworkplan011", params: {planId: parent.rowId, executorId: executorId}, type: 'json', callback: function (json) {
			$("#showForm").html(getDataUseHandlebars($("#beanTemplate").html(), json));
			// 计划周期名称展示
			$("#nowCheckTypeBox").html(getNowCheckTypeName(json.bean.planCycle));
			// 定时通知
			if(json.bean.whetherTime === '是'){//是
				$("#notifyTimeBox").removeClass("layui-hide");
				$("#notifyTime").html(json.bean.notifyTime);
			}
			// 计划执行人
			var carryPeopleName = [];
			$.each(json.bean.executors, function(i, item){
				carryPeopleName.push(item.userName);
			});
			$("#carryPeople").html(carryPeopleName.toString());

			var str = "";
			$.each([].concat(json.bean.enclosures), function(i, item){
				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
			});
			$("#enclosureUploadBtn").html(str);

			// 执行情况
			if (!isNull(executorId)){
				$(".myExecutor").removeClass("layui-hide");
				$("#state").html(getWorkPlanStateNameExecutor(json.bean));
			}
			matchingLanguage();
			form.render();
   		}});

	    function getWorkPlanStateNameExecutor(d){
			if(d.state == '1'){
				return "<span class='state-new'>待执行</span>";
			}else if(d.state == '2'){
				return "<span class='state-up'>执行完成</span>";
			}else if(d.state == '3'){
				return "<span class='state-down'>延期</span>";
			}else if(d.state == '4'){
				return "<span class='state-error'>转任务</span>";
			}
		}

	});
});