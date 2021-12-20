
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
			AjaxPostUtil.request({url:reqBasePath + "erpstockinventory003", params:{page: page, limit: 15, normsId: parent.normsId, depotId: parent.depotId}, type:'json', callback:function(json){
	   			if(json.returnCode == 0){
	   				var jsonStr = "";//实体json对象
	   				$.each(json.rows, function(index, bean) {
   						bean.showClass = 'date02';
	   					jsonStr = {
   							bean: bean
   						};
						lis.push(getDataUseHandlebars($("#treeHistory").html(), jsonStr));
					});
	   				//执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
					//pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
					next(lis.join(''), (page * 1000) < json.total);
	   			}else{
	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	   			}
	   		}});
		}
	});
	
	matchingLanguage();
    
    exports('storeHouseInventoryHistory', {});
});
