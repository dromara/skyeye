
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$;
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys019",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/syseveuser/rolelist.tpl'),
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render();
		 	    form.on('submit(formEditBindRole)', function (data) {
		 	    	//表单验证
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var str = "";
	 	        		$.each($('input[type=checkbox]:checked'),function(){
		                    str += $(this).val() + ",";
		                });
	 	        		if(isNull(str)){
	 	        			top.winui.window.msg("请选择角色", {icon: 2,time: 2000});
	 	        			return false;
	 	        		}
		 	        	var params = {
		 	        		rowId: parent.rowId,
		 	        		roleIds: str,
		 	        	};
		 	        	AjaxPostUtil.request({url:reqBasePath + "sys020", params:params, type:'json', callback:function(json){
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
		 	}
	    });
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});