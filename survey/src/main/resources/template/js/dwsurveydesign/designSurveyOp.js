layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    laydate = layui.laydate;
	    
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
		 		hdb.registerHelper('compare3', function(v1, v2, v3, options) {
		 			if(v1 == v2){
		 				return v3;
		 			}else{
		 				return "";
		 			}
		 		});
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		laydate.render({
		 			elem: '#endTime', //指定元素
		 			format: 'yyyy-MM-dd HH:mm:ss',
		 			type: 'datetime',
		 			min:minDate(),
		 			theme: 'grid'
		 		});
		 		
		 		if(json.bean.ynEndTime == '1'){
		 			$("#endTimeHide").hide();
		 		}else{
		 			$("#endTimeHide").show();
		 		}
		 		
		 		matchingLanguage();
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
			    		$("#endTimeHide").hide();
			    		$("#endTime").attr("readonly", false);
			    	}else{
			    		$("#endTimeHide").show();
			    		$("#endTime").val("");
			    		$("#endTime").attr("readonly", true);
			    	}
		        });
				
			    form.on('submit(formAddBean)', function (data) {
			    	
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
			        		rowId: parent.parent.rowId
			        	};
			        	
			        	if($('input[name=effective]').get(0).checked){
			        		params.effective = '4';
			        	}else{
			        		params.effective = '1';
			        	}
			        	
			        	if($('input[name=effectiveIp]').get(0).checked){
			        		params.effectiveIp = '1';
			        	}else{
			        		params.effectiveIp = '0';
			        	}
			        	
			        	if($('input[name=rule]').get(0).checked){
			        		params.rule = '3';
			        		params.ruleCode = $("#ruleCode").val();
			        		if(isNull(params.ruleCode)){
			        			winui.window.msg('请填写答卷密码', {icon: 2,time: 2000});
			        			return false;
			        		}
			        	}else{
			        		params.rule = '1';
			        		params.ruleCode = '';
			        	}
			        	
			        	if($('input[name=refresh]').get(0).checked){
			        		params.refresh = '1';
			        	}else{
			        		params.refresh = '0';
			        	}
			        	
			        	if($('input[name=ynEndNum]').get(0).checked){
			        		params.ynEndNum = '1';
			        		params.endNum = $("#endNum").val();
			        		if(isNull(params.endNum)){
			        			winui.window.msg('请填写答卷份数', {icon: 2,time: 2000});
			        			return false;
			        		}
			        	}else{
			        		params.ynEndNum = '0';
			        		params.endNum = '0';
			        	}
			        	
			        	if($('input[name=ynEndTime]').get(0).checked){
			        		params.ynEndTime = '1';
			        		params.endTime = $("#endTime").val();
			        		if(isNull(params.endTime)){
			        			winui.window.msg('请填写答卷结束时间', {icon: 2,time: 2000});
			        			return false;
			        		}
			        	}else{
			        		params.ynEndTime = '0';
			        		params.endTime = '';
			        	}
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "dwsurveydirectory005", params:params, type: 'json', callback: function(json){
			 	   			if(json.returnCode == 0){
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			}else{
			 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			 	   			}
			 	   		}});
			        }
			        return false;
			    });
		 	}
	    });
	    
	    // 设置最小可选的日期
	    function minDate(){
	        var now = new Date();
	        return now.getFullYear()+"-" + (now.getMonth()+1) + "-" + now.getDate();
	    }
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});