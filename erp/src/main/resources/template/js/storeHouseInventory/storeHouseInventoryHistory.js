
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'flow'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		flow = layui.flow;
	
	flow.load({
		elem: '#historyList', //指定列表容器
		scrollElem: '#historyList',
		isAuto: true,
		done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
			var lis = [];
			//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
			var params = {
				page: page,
				limit: 15,
				normsId: parent.normsId,
				depotId: parent.depotId
			};
			AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpstockinventory003", params: params, type: 'json', method: 'POST', callback: function (json) {
				var jsonStr = "";//实体json对象
				$.each(json.rows, function(index, bean) {
					bean.showClass = 'date02';
					jsonStr = {
						bean: bean
					};
					lis.push(getDataUseHandlebars($("#treeHistory").html(), jsonStr));
				});
				next(lis.join(''), (page * 1000) < json.total);
	   		}});
		}
	});
	
	matchingLanguage();
    
    exports('storeHouseInventoryHistory', {});
});
