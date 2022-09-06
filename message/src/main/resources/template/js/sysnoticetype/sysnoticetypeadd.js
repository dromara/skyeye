var parentId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    //类型级别变化事件
 		form.on('radio(level)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//该类型为一级类型
	    		$("#parentIdBox").addClass("layui-hide");
	    	} else if (val == '2'){	//该类型为二级类型
	    		$("#parentIdBox").removeClass("layui-hide");
	    		initNoticeTypeId();
	    	} else {
	    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
	    	}
        });
 		
 		//一级类型
		function initNoticeTypeId(){
			showGrid({
			 	id: "lockParentSel",
			 	url: sysMainMation.noticeBasePath + "noticetype011",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb) {},
			 	ajaxSendAfter:function (json) {
			 		form.render('select');
			 	}
		    });
		}
		
		//一级类型监听事件
		form.on('select(lockParentSel)', function(data) {
			parentId = data.value;
		});
	    
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		typeName: $("#typeName").val(),
 	        	};
 	        	if($("input[name='level']:checked").val() === '2'){	//该类型为二级类型
	 	        	if(isNull(parentId)){
	 	        		winui.window.msg('请选择一级公告类型', {icon: 2, time: 2000});
	 	        		return false;
	 	        	} else {
	 	        		params.parentId = parentId;
	 	        	}
 	        	} else if ($("input[name='level']:checked").val() === '1'){
 	        		params.parentId = '0';
 	        	}
 	        	AjaxPostUtil.request({url: sysMainMation.noticeBasePath + "noticetype002", params: params, type: 'json', callback: function (json) {
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