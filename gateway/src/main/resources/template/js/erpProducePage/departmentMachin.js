
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
    echarts: '../echarts/echarts',
    echartsTheme: '../echarts/echartsTheme'
}).define(['window', 'jquery', 'winui', 'form', 'echarts', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		laydate = layui.laydate,
		form = layui.form;
	var charts1;
	
	var myDate = new Date();
	year = myDate.getFullYear();
	laydate.render({ 
		elem: '#year',
		type: 'year',
		value: year,
 		trigger: 'click',
 		done: function(value, date){
 			initCustomDocumentaryByType(value);
 		}
	});
	
	initCustomDocumentaryByType(year);
	// 统计当前部门月度新建加工单图
	function initCustomDocumentaryByType(year){
		AjaxPostUtil.request({url:reqBasePath + "erpproduce004", params: {year: year}, type: 'json', callback: function(json){
   			if (json.returnCode == 0) {
 	   			renderCharts1(json.rows);
 	   			matchingLanguage();
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	function renderCharts1(rows) {
		charts1 = echarts.init(document.getElementById("charts1"));
		var name = new Array();
		var num = new Array();
		$.each(rows, function(i, item){
			name.push(item.yearMonth);
			num.push(item.orderNum);
		});
		var option = getOption('当前部门新增加工单分析', '统计在指定年份当前部门新建加工单的数量分析', name, "部门新建加工单数量", num, 'line');
		charts1.setOption(option);
		
		// 加载表格
		$("#tHead").html(getDataUseHandlebars($("#tableHeader").html(), {rows: name}));
		$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: rows}));
    	form.render();
	}
	
	window.onresize = function(){
	    charts1.resize();
	}
	
    exports('departmentMachin', {});
});
