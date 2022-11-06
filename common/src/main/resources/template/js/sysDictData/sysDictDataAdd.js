
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

		skyeyeClassEnumUtil.showEnumDataListByClassName("commonIsDefault", 'radio', "isDefault", '', form);
		skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);
		// 加载数据字典分类
		sysDictDataUtil.queryDictTypeListByEnabled(1, function (json) {
			$("#dictTypeId").html(getDataUseHandlebars(selOption, json));
			if (!isNull(parent.parentNode)) {
				$("#dictTypeId").val(parent.parentNode.dictTypeId);
				$("#dictTypeId").attr("disabled", "disabled");
			}
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
					isDefault: $("#isDefault input:radio:checked").val(),
					enabled: $("#enabled input:radio:checked").val(),
					parentId: isNull(parent.parentNode) ? '0' : parent.parentNode.id,
					level: isNull(parent.parentNode) ? 1 : parent.parentNode.level + 1,
					remark: $("#remark").val(),
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "writeDictDataMation", params: params, type: 'json', method: "POST", callback: function (json) {
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