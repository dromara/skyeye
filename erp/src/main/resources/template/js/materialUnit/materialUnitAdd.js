
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
	    
		initTableChooseUtil.initTable({
			id: "unitList",
			cols: [
				{id: 'name', title: '单位名称', formType: 'input', width: '80', className: 'change-input', verify: 'required'},
				{id: 'number', title: '数量', formType: 'input', width: '80', className: 'change-input', verify: 'required|number'}
			],
			deleteRowCallback: function (trcusid) {},
			addRowCallback: function (trcusid) {},
			form: form
		});
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var result = initTableChooseUtil.getDataList('unitList');
				if (!result.checkResult) {
					return false;
				}
				var dataList = result.dataList;
				$.each(result.dataList, function(i, item) {
					item["baseUnit"] = 2;
				});
				dataList.push({
					name: $("#unitName").val(),
					number: 1,
					baseUnit: 1
				});
				var params = {
					name: $("#name").val(),
	 	        	unitList: JSON.stringify(dataList)
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "writeMaterialUnitMation", params: params, type: 'json', method: 'POST', callback: function (json) {
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