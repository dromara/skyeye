
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

		dsFormUtil.loadDsFormPageTypeByPId("firstTypeId", "0");

		form.on('select(firstTypeId)', function(data) {
			var thisRowValue = data.value;
			dsFormUtil.loadDsFormPageTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
			form.render('select');
		});

	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		pageName: $("#pageName").val(),
 	        		pageDesc: $("#pageDesc").val(),
					firstTypeId: $("#firstTypeId").val(),
					secondTypeId: $("#secondTypeId").val()
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "dsformpage002", params:params, type: 'json', method: "POST", callback: function(json){
 	        		if (json.returnCode == 0) {
 	        			parent.layer.close(index);
 	        			parent.refreshCode = '0';
 	        		}else{
 	        			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	        		}
 	        	}});
 	        }
 	        return false;
 	    });

	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});