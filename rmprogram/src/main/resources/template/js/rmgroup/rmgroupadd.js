
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
		 	id: "rmTypeId",
		 	url: sysMainMation.rmprogramBasePath + "common001",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter:function (json) {
		 		matchingLanguage();
		 		form.render();
		 		form.on('select(selectParent)', function(data) {
		 			
		 		});
				
			    form.on('submit(formAddBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			rmTypeId: $("#rmTypeId").val(),
		        			rmGroupName: $("#rmGroupName").val(),
		        			icon: $("#rmGroupIcon").val(),
			        	};
			        	
			        	AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmxcx009", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
			 	   		}});
			        }
			        return false;
			    });
		 	}
	    });
	    
	    // 菜单图标选中事件
 	    $("body").on("focus", "#rmGroupIcon", function (e) {
			systemCommonUtil.openSysEveIconChoosePage(function(sysIconChooseClass){
				$("#rmGroupIcon").val(sysIconChooseClass);
			});
 	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});