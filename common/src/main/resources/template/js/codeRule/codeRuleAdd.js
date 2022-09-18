
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'ruleCode'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			textool = layui.textool;

		textool.init({eleId: 'remark', maxlength: 200});

		winui.ruleCode.init({
			id: "ruleBox"
		}, null);

	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem) && winui.ruleCode.checkDataById("ruleBox")) {
 	        	var params = {
					name: $("#name").val(),
					codeNum: $("#codeNum").val(),
					dictSort: $("#dictSort").val(),
					alarm: $("input[name='alarm']:checked").val(),
					remark: $("#remark").val(),
 	        	};
				var rule = winui.ruleCode.getDataById("ruleBox");
				params = $.extend(true, params, rule);
 	        	AjaxPostUtil.request({url: reqBasePath + "writeCodeRuleMation", params: params, type: 'json', method: "POST", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});