
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;
		    
		var selOption = getFileContent('tpl/template/select-option.tpl');
	    
		// 提交类型，1.工序验收；2.生成验收单
		var subType = 1;
		
		subType = parent.subType;
		
		if(subType == 1){
			$("#depotIdBox").remove();
		}else{
			erpOrderUtil.getDepotList(function (json){
				// 加载仓库数据
				$("#depotId").html(getDataUseHandlebars(selOption, json));
				form.render('select');
			});
		}
		
		var needNum = parent.needNum;
		$("#needNum").val(needNum);
		$("#acceptNum").val(needNum);
		
		$("body").on("input", "#acceptNum", function() {
			calcNum(1);
		});
		$("body").on("change", "#acceptNum", function() {
			calcNum(1);
		});
		
		$("body").on("input", "#belowNum", function() {
			calcNum(2);
		});
		$("body").on("change", "#belowNum", function() {
			calcNum(2);
		});
		
		function calcNum(type){
			var changeNum = 0;
			if(type == 1){
				changeNum = parseInt(isNull($("#acceptNum").val()) ? "0" : $("#acceptNum").val());
				$("#belowNum").val(needNum - changeNum);
			}else{
				changeNum = parseInt(isNull($("#belowNum").val()) ? "0" : $("#belowNum").val());
				$("#acceptNum").val(needNum - changeNum);
			}
		}
		
		matchingLanguage();
	    form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	if(parseInt(isNull($("#acceptNum").val()) ? "0" : $("#acceptNum").val()) < 0){
	        		winui.window.msg('合格数量不能小于0.', {icon: 2,time: 2000});
	        		return false;
	        	}
	        	if(parseInt(isNull($("#belowNum").val()) ? "0" : $("#belowNum").val()) < 0){
	        		winui.window.msg('不合格数量不能小于0.', {icon: 2,time: 2000});
	        		return false;
	        	}
	        	
				var params = {
			    	childId: parent.childId,
			    	acceptNum: $("#acceptNum").val(),
			    	belowNum: $("#belowNum").val(),
			    	depotId: (subType == 1) ? "" : $("#depotId").val()
			    };
	        	AjaxPostUtil.request({url:reqBasePath + "erpmachin012", params: params, type:'json', callback:function(json){
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
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});