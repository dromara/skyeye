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
	    
		showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "rmpropertyvalue004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/rmpropertyvalue/rmpropertyvalueeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 		form.render();
		 		
		 		//属性标签
		 		showGrid({
		 		 	id: "propertyId",
		 		 	url: reqBasePath + "rmproperty006",
		 		 	params: {},
		 		 	pagination: false,
		 		 	template: getFileContent('tpl/template/select-option.tpl'),
		 		 	ajaxSendLoadBefore: function(hdb){
		 		 	},
		 		 	ajaxSendAfter:function(data){
		 		 		$("#propertyId").val(json.bean.propertyId);
		 		 		form.render('select');
		 		 	}
		 		});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
		        			title:$("#title").val(),
		        			propertyValue:$("#propertyValue").val(),
		        			propertyId:$("#propertyId").val(),
		        			rowId:parent.rowId,
			        	};
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "rmpropertyvalue005", params:params, type: 'json', callback: function(json){
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
		
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});