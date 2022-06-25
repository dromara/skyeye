
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'orgChart'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	// 企业组织机构图
	AjaxPostUtil.request({url:reqBasePath + "companymation009", params:{}, type: 'json', method: "GET", callback: function(json){
		if (json.returnCode == 0) {
			$('#chart-container').orgchart({
				'data' : json.rows[0],
				'nodeTitle': 'title',
				'nodeContent': 'name',
				'pan': true,
				'zoom': true,
				'interactive': false
			});
			form.render();
		}else{
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
    exports('companyOrganization', {});

});
