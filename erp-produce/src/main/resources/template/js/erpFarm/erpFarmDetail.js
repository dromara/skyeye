
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
            url: flowableBasePath + "erpfarm006",
            params: {rowId: parent.rowId},
            pagination: false,
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter: function(json){
            	matchingLanguage();
                form.render();
            }
        });
		
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});