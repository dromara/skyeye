
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
        	id: "typeId",
        	url: reqBasePath + "sysstaffdatadictionary008",
        	params: {typeId: 6},
        	pagination: false,
        	template: getFileContent('tpl/template/select-option.tpl'),
        	ajaxSendLoadBefore: function(hdb){
        	},
        	ajaxSendAfter:function (json) {
        		form.render('select');
        	}
        });
	    
        matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
                    typeName: $("#typeName").val(),
                    typeId: $("#typeId").val()
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "sysstafflanguagelevel002", params: params, type: 'json', callback: function (json) {
 	        		if (json.returnCode == 0) {
 	        			parent.layer.close(index);
 	        			parent.refreshCode = '0';
 	        		} else {
 	        			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	        		}
 	        	}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});