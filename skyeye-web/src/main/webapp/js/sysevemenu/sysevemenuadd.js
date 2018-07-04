layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$;
 		form.render();
 		
 		//菜单级别变化事件
 		form.on('radio(menuLevel)', function (data) {
 			var val = data.value;
	    	if(val == '1'){//创世菜单
	    		$("#parentIdBox").addClass("layui-hide");
	    	}else if(val == '2'){
	    		$("#parentIdBox").removeClass("layui-hide");
	    	}else{
	    		top.winui.window.msg('状态值错误', {icon: 2,time: 2000});
	    	}
        });
 		
 		//系统菜单同步
 		form.on('switch(isNecessary)', function (data) {
 			//同步开关值
 			$(data.elem).val(data.elem.checked);
 		});
 		
 	    form.on('submit(formAddMenu)', function (data) {
 	    	//表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	
 	        	var params = {
        			menuName: $("#menuName").val(),
        			titleName: $("#menuTitle").val(),
        			menuIcon: $("#menuIcon").val(),
        			menuUrl: $("#menuUrl").val(),
        			menuType: data.field.menuType
 	        	};
 	        	
 	        	if(data.field.menuLevel == '1'){//创世菜单
 	        		params.parentId = '0';
 	 	    	}else if(data.field.menuLevel == '2'){//子菜单
 	 	    		if(isNull($("#parentIdOne").val())){//父菜单为空
 	 	    			top.winui.window.msg("请至少选择一级父菜单", {icon: 2,time: 2000});
 	 	    			return false;
 	 	    		}else{
 	 	    			params.parentId = "";
 	 	    		}
 	 	    	}else{
 	 	    		top.winui.window.msg('状态值错误', {icon: 2,time: 2000});
 	 	    	}
 	        	
 	        	if($("#menuSysType").val()){
 	        		params.menuSysType = '1';
 	        	}else{
 	        		params.menuSysType = '2';
 	        	}
 	        	AjaxPostUtil.request({url:reqBasePath + "sys007", params:params, type:'json', callback:function(json){
	 	   			if(json.returnCode == 0){
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			}else{
	 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
 	    //初始化加载隐藏创世菜单
 	    $("#parentIdBox").addClass("layui-hide");
	    
 	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});