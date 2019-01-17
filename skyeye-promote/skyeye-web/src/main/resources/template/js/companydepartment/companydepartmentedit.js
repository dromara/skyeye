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
		 	url: reqBasePath + "companydepartment004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/companydepartment/companydepartmenteditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
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
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			departmentName: $("#departmentName").val(),
		        			departmentDesc: encodeURI(layedit.getContent(layContent)),
		        			companyId: parent.companyId,
		        			rowId: parent.rowId,
			        	};
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "companydepartment005", params:params, type:'json', callback:function(json){
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