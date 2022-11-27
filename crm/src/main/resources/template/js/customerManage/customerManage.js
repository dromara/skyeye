
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	form = layui.form,
    	element = layui.element;
	var objectType = "1";
	var id = GetUrlParam("id");
	var objectKey = GetUrlParam("objectKey");

	var pageList = teamObjectPermissionUtil.getPageUrl(objectType);
	pageList.unshift({
		title: '详情',
		pageUrl: '../../tpl/customerManage/customerDetails.html'
	});

	$("#manageTab").find(".layui-tab-title").html(getDataUseHandlebars($('#headerTemplate').html(), {rows: pageList}));
	$("#manageTab").find(".layui-tab-title").find('li').eq(0).addClass('layui-this');

	$("#manageTab").find(".layui-tab-content").html(getDataUseHandlebars($('#contentTemplate').html(), {rows: pageList}));
	$("#manageTab").find(".layui-tab-content").find('.layui-tab-item').eq(0).addClass('layui-show');
	setPageUrl(pageList[0]);

	element.on('tab(manageTab)', function (obj) {
		var mation = pageList[obj.index];
		if (!isNull(mation)) {
			setPageUrl(mation);
		}
	});

	function setPageUrl(mation) {
		var url = mation.pageUrl + "?id=" + id + "&objectKey=" + objectKey;
		$("#manageTab").find(".layui-tab-content").find('.layui-show').find('iframe').attr('src', url);
	}

	form.render();

});