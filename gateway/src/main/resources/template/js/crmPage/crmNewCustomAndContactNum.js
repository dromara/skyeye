
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
 			initNewInsertNumByYear(value);
 		}
	});
	
	initNewInsertNumByYear(year);
	// 获取指定年度的客户新增量，联系人新增量
	function initNewInsertNumByYear(year){
		AjaxPostUtil.request({url:reqBasePath + "crmpage001", params: {year: year}, type: 'json', callback: function(json){
   			if(json.returnCode == 0){
 	   			renderCharts1(json.rows);
 	   			matchingLanguage();
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	}
	
	function renderCharts1(rows) {
		charts1 = echarts.init(document.getElementById("charts1"));
		var nameStr = new Array();
		var insertCustomerNum = new Array();
		var insertContactsNum = new Array();
		$.each(rows, function(i, item){
			nameStr.push(item.yearMonth);
			insertCustomerNum.push(item.insertCustomerNum);
			insertContactsNum.push(item.insertContactsNum);
		});
		var option = getOption('指定年度新增量', '指定年度客户月增量与联系人月增量', nameStr, '客户月增量', insertCustomerNum, 'line');
		option["legend"] = {
	        data: ['客户月增量', '联系人月增量'],
	        y: 'bottom'
	    };
	    option.series.push({
			name: '联系人月增量',
			type: 'line',
			smooth: true,
			data: insertContactsNum
		});
		charts1.setOption(option);
	}
	
	window.onresize = function(){
	    charts1.resize();
	}
	
    exports('crmNewCustomAndContactNum', {});
});
