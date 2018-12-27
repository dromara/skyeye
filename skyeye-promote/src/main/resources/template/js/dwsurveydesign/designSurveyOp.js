layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "dwsurveydirectory004",
		 	params: {rowId: parent.parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/dwsurveydesign/designSurveyOpTemplates.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper('compare1', function(v1, v2, options) {
		 			if(v1 == v2){
		 				return "checked";
		 			}else{
		 				return "";
		 			}
		 		});
		 		hdb.registerHelper('compare2', function(v1, v2, options) {
		 			if(v1 != v2){
		 				return "readonly";
		 			}else{
		 				return "";
		 			}
		 		});
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		form.render('checkbox');
				form.render();
				
				form.on('checkbox(rule)', function (data) {
					var check = data.elem.checked;
			    	if(check){//选中
			    		$("#ruleCode").attr("readonly", false);
			    	}else{
			    		$("#ruleCode").val("");
			    		$("#ruleCode").attr("readonly", true);
			    	}
		        });
				
				form.on('checkbox(ynEndNum)', function (data) {
					var check = data.elem.checked;
			    	if(check){//选中
			    		$("#endNum").attr("readonly", false);
			    	}else{
			    		$("#endNum").val("");
			    		$("#endNum").attr("readonly", true);
			    	}
		        });
				
				form.on('checkbox(ynEndTime)', function (data) {
					var check = data.elem.checked;
			    	if(check){//选中
			    		$("#endTime").attr("readonly", false);
			    	}else{
			    		$("#endTime").val("");
			    		$("#endTime").attr("readonly", true);
			    	}
		        });
				
			    form.on('submit(formAddBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			surveyName: $("#surveyName").val(),
			        	};
		//	        	AjaxPostUtil.request({url:reqBasePath + "dwsurveydirectory002", params:params, type:'json', callback:function(json){
		//	 	   			if(json.returnCode == 0){
		//		 	   			parent.layer.close(index);
		//		 	        	parent.refreshCode = '0';
		//	 	   			}else{
		//	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		//	 	   			}
		//	 	   		}});
			        }
			        return false;
			    });
		 	}
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});