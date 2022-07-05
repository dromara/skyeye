
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

		skyeyeEnclosure.init('enclosureUpload');
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			workUnit: $("#workUnit").val(),
 	        		department: $("#department").val(),
 	        		job: $("#job").val(),
 	        		station: $("#station").val(),
 	        		startTime: $("#startTime").val(),
 	        		endTime: $("#endTime").val(),
	 	        	staffId: parent.staffId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	
 	        	AjaxPostUtil.request({url: reqBasePath + "sysstaffjobresume002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});