
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$;
	var beanTemplate = $("#beanTemplate").html();
	
	AjaxPostUtil.request({url: flowableBasePath + "proworkload006", params: {rowId: parent.rowId}, type: 'json', callback: function (json) {
		// 附件回显
		skyeyeEnclosure.showDetails({"enclosureUploadBox": json.bean.enclosureInfo});

		var _html = getDataUseHandlebars(beanTemplate, json);//加载数据
		$("#showForm").html(_html);
		var dateArray = new Array();
		dateArray.push(json.bean.tasks[0].monTime);
		dateArray.push(json.bean.tasks[0].tuesTime);
		dateArray.push(json.bean.tasks[0].wedTime);
		dateArray.push(json.bean.tasks[0].thurTime);
		dateArray.push(json.bean.tasks[0].friTime);
		dateArray.push(json.bean.tasks[0].satuTime);
		dateArray.push(json.bean.tasks[0].sunTime);
		//设置日期
		var cells = document.getElementById('dataTr').getElementsByTagName('th');
		for(var i = 0; i < 7; i++) {
			cells[i].innerHTML = dateArray[i];
		}
		matchingLanguage();
    }});
    
});