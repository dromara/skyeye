
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	textool = layui.textool;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "customergroup003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/customerGroup/customerGroupEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function (json) {
		 		textool.init({eleId: 'desc', maxlength: 200});
			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	        			groupName: $("#groupName").val(),
 	        				desc: $("#desc").val()
	 	 	        	};
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "customergroup004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});