
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "rmxcx011",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/rmgroup/rmgroupeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		showGrid({
				 	id: "rmTypeId",
				 	url: reqBasePath + "common001",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option.tpl'),
				 	ajaxSendLoadBefore: function(hdb){
				 	},
				 	ajaxSendAfter:function(json1){
				 		$("#rmTypeId").val(json.bean.rmTypeId);
				 		
				 		matchingLanguage();
				 		form.render();
				 		form.on('select(selectParent)', function(data){
				 			
				 		});
				 		
					    form.on('submit(formEditBean)', function (data) {
					        if (winui.verifyForm(data.elem)) {
					        	var params = {
				        			rmTypeId: $("#rmTypeId").val(),
				        			rmGroupName: $("#rmGroupName").val(),
				        			icon: $("#rmGroupIcon").val(),
				        			rowId: parent.rowId
					        	};
					        	AjaxPostUtil.request({url:reqBasePath + "rmxcx012", params:params, type:'json', callback:function(json){
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
		 	}
	    });
	    
	    // 菜单图标选中事件
 	    $("body").on("focus", "#rmGroupIcon", function(e){
			systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
				$("#rmGroupIcon").val(sysIconChooseClass);
			});
 	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});