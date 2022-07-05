
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	form = layui.form;
    
    showGrid({
	 	id: "showForm",
	 	url: flowableBasePath + "assetarticles006",
	 	params: {rowId: parent.rowId},
	 	pagination: false,
	 	template: getFileContent('tpl/assetArticlesType/assetArticlesTypeEditTemplate.tpl'),
	 	ajaxSendAfter: function (json) {
	 		matchingLanguage();
	 		
	 	    form.on('submit(formEditBean)', function (data) {
	 	        if (winui.verifyForm(data.elem)) {
	 	        	var params = {
	 	        		rowId: parent.rowId,
	 	        		typeName: $("#typeName").val()
	 	        	};
	 	        	AjaxPostUtil.request({url: flowableBasePath + "assetarticles007", params: params, type: 'json', callback: function (json) {
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