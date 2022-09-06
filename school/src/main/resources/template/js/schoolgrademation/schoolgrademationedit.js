
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    layedit = layui.layedit;
	    
	    var layContent;
	    
		showGrid({
		 	id: "showForm",
		 	url: schoolBasePath + "grademation004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/schoolgrademation/schoolgrademationeditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter:function (json) {
		 		if(json.bean.parentName == '0' || isNull(json.bean.parentName)){
		 			$("#parentIdBox").addClass("layui-hide");
		 		} else {
		 			$("#yearNBox").addClass("layui-hide");
		 		}
		 		
		 		matchingLanguage();
		 		form.render();
		        
		 		form.on('submit(formEditBean)', function (data) {
			    	
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
			        		rowId: parent.rowId,
		        			gradeName: $("#gradeName").val(),
		        			yearN: $("#yearN").val()
			        	};
			        	
			        	AjaxPostUtil.request({url:schoolBasePath + "grademation005", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
			 	   		}});
			        }
			        return false;
			    });
		 		
		 	}
	    });
		
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
	    
});