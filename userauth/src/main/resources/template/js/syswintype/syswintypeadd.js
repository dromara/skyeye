
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
 		//加载一级分类
		showGrid({
		 	id: "parentId",
		 	url: reqBasePath + "sysevewintype002",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		form.render('select');
		 	}
		});
 		
 		//分类级别变化事件
 		form.on('radio(typeLevel)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//一级分类
	    		$("#parentIdDiv").hide();
	    	}else if(val == '2'){//二级分类
	    		$("#parentIdDiv").show();
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
        matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			typeName: $("#typeName").val(),
 	        	};
 	        	if(data.field.typeLevel == '1'){//一级分类
 	        		params.parentId = '0';
 	 	    	}else if(data.field.typeLevel == '2'){//二级分类
 	 	    		if(isNull($("#parentId").val())){
 	 	    			winui.window.msg('请选择一级分类', {icon: 2, time: 2000});
 	 	 	    		return false;
 	 	    		}
 	 	    		params.parentId = $("#parentId").val();
 	 	    	} else {
 	 	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
 	 	    		return false;
 	 	    	}
 	        	AjaxPostUtil.request({url: reqBasePath + "sysevewintype003", params: params, type: 'json', callback: function (json) {
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
 	    
 	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});