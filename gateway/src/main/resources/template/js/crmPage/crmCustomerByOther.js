
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'jquery', 'winui', 'form', 'echarts'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	var charts1, charts2, charts3, charts4;
	
	// 根据客户分类，客户来源，所属行业，客户分组统计客户数量
	var params = {
		crmCustomerType: sysDictData["crmCustomerType"]["key"],
		crmCustomerFrom: sysDictData["crmCustomerFrom"]["key"],
		crmCustomerIndustry: sysDictData["crmCustomerIndustry"]["key"],
		crmCustomerGroup: sysDictData["crmCustomerGroup"]["key"],
	};
	AjaxPostUtil.request({url: reqBasePath + "crmpage002", params: params, type: 'json', method: "POST", callback: function (json) {
		renderCharts(json.bean);
		matchingLanguage();
	}});

	function renderCharts(bean) {
		charts1 = echarts.init(document.getElementById("charts1"));
		charts2 = echarts.init(document.getElementById("charts2"));
		charts3 = echarts.init(document.getElementById("charts3"));
		charts4 = echarts.init(document.getElementById("charts4"));
		
		var num = new Array();
		var name = new Array();
		$.each(bean.numType, function(i, item){
			num.push(item.number);
			name.push(item.name);
		});
		charts1.setOption(getOption('客户类型分析', '根据客户类型统计客户数量', name, '客户数量', num, 'bar'));
		
		num = new Array();
		name = new Array();
		$.each(bean.numFrom, function(i, item){
			num.push(item.number);
			name.push(item.name);
		});
		charts2.setOption(getOption('客户来源分析', '根据客户来源统计客户数量', name, '客户数量', num, 'bar'));
		
		num = new Array();
		name = new Array();
		$.each(bean.numIndustry, function(i, item){
			num.push(item.number);
			name.push(item.name);
		});
		charts3.setOption(getOption('所属行业分析', '根据所属行业统计客户数量', name, '客户数量', num, 'bar'));
		
		num = new Array();
		name = new Array();
		$.each(bean.numGroup, function(i, item){
			num.push(item.number);
			name.push(item.name);
		});
		charts4.setOption(getOption('客户分组分析', '根据客户分组统计客户数量', name, '客户数量', num, 'bar'));
	}
	
	window.onresize = function(){
	    charts1.resize();
	    charts2.resize();
	    charts3.resize();
	    charts4.resize();
	}
	
    exports('crmCustomerByOther', {});
});
