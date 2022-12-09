
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	form = layui.form;
	var id = GetUrlParam("id");
	var objectKey = GetUrlParam("objectKey");

	tabPageUtil.init({
		id: 'tab',
		prefixData: [{
			title: '详情',
			pageUrl: '../../tpl/customerManage/customerDetails.html'
		}],
		suffixData: [],
		element: layui.element,
		objectType: "1",
		object: {
			objectId: id,
			objectKey: objectKey,
		}
	});

	form.render();

});