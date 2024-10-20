
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
	AjaxPostUtil.request({url: reqBasePath + "companymation009", params: {}, type: 'json', method: "GET", callback: function (json) {
		$('#chart-container').orgchart({
			'data' : json.rows[0],
			'nodeTitle': 'title',
			'nodeContent': 'name',
			'pan': true,
			'zoom': true,
			'interactive': false
		});
		form.render();
	}});
    exports('companyOrganization', {});

});
