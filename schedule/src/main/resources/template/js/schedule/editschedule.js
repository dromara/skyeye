
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
		 	url: reqBasePath + "syseveschedule015",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/schedule/editscheduleTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		//编辑节假日选取时间段
				laydate.render({
					elem: '#holidayTime', //指定元素
					range: '~',
					value: json.bean.scheduleStartTime + ' ~ ' + json.bean.scheduleEndTime
				});
				matchingLanguage();
				form.render();
		 	    form.on('submit(formBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	theStartTime = $("#holidayTime").val().split('~')[0].trim() + ' 00:00:00';
		 	    		theEndTime = $("#holidayTime").val().split('~')[1].trim() + ' 23:59:59';
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		title: $("#title").val(),
		 	        		startTime: theStartTime,
		 	        		endTime: theEndTime
		 	        	};
		 	        	AjaxPostUtil.request({url: reqBasePath + "syseveschedule016", params: params, type: 'json', callback: function (json) {
			 	   			if (json.returnCode == 0) {
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			} else {
			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			 	   			}
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