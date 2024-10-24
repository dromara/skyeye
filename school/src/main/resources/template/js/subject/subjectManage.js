
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
	var objectId = GetUrlParam("objectId");
	var objectKey = GetUrlParam("objectKey");

	tabPageUtil.init({
		id: 'tab',
		prefixData: [{
			title: '详情',
			pageUrl: systemCommonUtil.getUrl('FP2023082800004&id=' + objectId, null)
		}],
		suffixData: [{
			title: '班级',
			pageUrl: '../../tpl/subjectClass/list.html'
		}],
		element: layui.element,
		object: {
			objectId: objectId,
			objectKey: objectKey,
		}
	});

	form.render();

});