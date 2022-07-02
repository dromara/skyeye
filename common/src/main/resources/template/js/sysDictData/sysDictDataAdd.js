
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
		var selOption = getFileContent('tpl/template/select-option.tpl');

		textool.init({eleId: 'remark', maxlength: 200});

		// 加载数据字典分类
		sysDictDataUtil.queryDictTypeListByStatus(0, function (json) {
			$("#dictTypeId").html(getDataUseHandlebars(selOption, json));
			form.render('select');
		});

	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
					dictName: $("#dictName").val(),
					dictTypeId: $("#dictTypeId").val(),
					dictSort: $("#dictSort").val(),
					isDefault: $("input[name='isDefault']:checked").val(),
					status: $("input[name='status']:checked").val(),
					remark: $("#remark").val(),
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "writeDictDataMation", params: params, type: 'json', method: "POST", callback: function (json) {
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