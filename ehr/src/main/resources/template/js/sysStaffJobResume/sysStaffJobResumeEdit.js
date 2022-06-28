
// 员工工作履历
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysstaffjobresume003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
		 		// 任职开始时间
			    var insStart = laydate.render({ 
		 			elem: '#startTime',
		 	 		trigger: 'click',
		 	 		done: function(value, date){
		 	 			insEnd.config.min = lay.extend({}, date, {
		 	 				month: date.month - 1
		 	 			});
		 	 			insEnd.config.elem[0].focus();
		 	 		}
		 		});
			    
			    // 任职结束时间
			    var insEnd = laydate.render({ 
		 			elem: '#endTime',
		 	 		trigger: 'click',
		 	 		done: function(value, date){
		 	 			insStart.config.max = lay.extend({}, date, {
		 	 				month: date.month - 1
		 	 			});
		 	 		}
		 		});
				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

 	        	matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var params = {
		        			workUnit: $("#workUnit").val(),
		 	        		department: $("#department").val(),
		 	        		job: $("#job").val(),
		 	        		station: $("#station").val(),
		 	        		startTime: $("#startTime").val(),
		 	        		endTime: $("#endTime").val(),
			 	        	rowId: parent.rowId,
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
		 	        	};
		 	        	
		 	        	AjaxPostUtil.request({url: reqBasePath + "sysstaffjobresume004", params: params, type: 'json', callback: function (json) {
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
		 		form.render();
		 	}
	    });
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});