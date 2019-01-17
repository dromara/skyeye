layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form,
	    layedit = layui.layedit;
	    
	    var layContent;
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "planproject004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/planproject/planprojecteditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		//是否共享
		 		hdb.registerHelper("compare2", function(v1, options){
					if(v1 == '2'){//公开分享
						return 'checked';
					}else if(v1 == '1'){//不分享
						return '';
					}else{
						return '';
					}
				});
		 		hdb.registerHelper("compare3", function(v1, options){
					if(v1 == '2'){//公开分享
						return 'true';
					}else if(v1 == '1'){//不分享
						return 'false';
					}else{
						return 'false';
					}
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		layContent = layedit.build('content', {
			    	tool: [
		    	       'strong' //加粗
		    	       ,'italic' //斜体
		    	       ,'underline' //下划线
		    	       ,'del' //删除线
		    	       ,'|' //分割线
		    	       ,'left' //左对齐
		    	       ,'center' //居中对齐
		    	       ,'right' //右对齐
		    	       ,'link' //超链接
		    	       ,'unlink' //清除链接
		    	       ,'face' //表情
		    	     ]
			    });
		 		
		 		form.render();
		 		
		 		//是否共享
		 		form.on('switch(isShare)', function (data) {
		 			//同步开关值
		 			$(data.elem).val(data.elem.checked);
		 		});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			projectName: $("#projectName").val(),
		        			projectDesc: encodeURI(layedit.getContent(layContent)),
		        			rowId: parent.rowId
			        	};
			        	if(data.field.isShare){
			        		params.isShare = '2';
			        	}else{
			        		params.isShare = '1';
			        	}
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "planproject005", params:params, type:'json', callback:function(json){
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
		
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});