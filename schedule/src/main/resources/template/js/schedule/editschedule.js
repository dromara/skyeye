
var laydate;
var theStartTime = "", theEndTime = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'laydate', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    laydate = layui.laydate;
	    
	    showGrid({
		 	id: "showForm",
		 	url: sysMainMation.scheduleBasePath + "syseveschedule006",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: getFileContent('tpl/schedule/editscheduleTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		// 编辑节假日选取时间段
				laydate.render({elem: '#holidayTime', type: 'datetime', range: '~', value: json.bean.startTime + ' ~ ' + json.bean.endTime});
				matchingLanguage();
				form.render();
		 	    form.on('submit(formBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	theStartTime = $("#holidayTime").val().split('~')[0].trim();
		 	    		theEndTime = $("#holidayTime").val().split('~')[1].trim();
		 	        	var params = {
		 	        		id: parent.rowId,
							name: $("#name").val(),
		 	        		startTime: theStartTime,
		 	        		endTime: theEndTime
		 	        	};
		 	        	AjaxPostUtil.request({url: sysMainMation.scheduleBasePath + "syseveschedule005", params: params, type: 'json', method: 'POST', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
			 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});