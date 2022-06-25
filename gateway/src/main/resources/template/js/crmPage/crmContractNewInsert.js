
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
 			initContractNewInsert(value);
 		}
	});
	
	initContractNewInsert(year);
	// 获取合同在指定年度的月新增量
	function initContractNewInsert(year){
		AjaxPostUtil.request({url:reqBasePath + "crmpage004", params: {year: year}, type: 'json', callback: function(json){
   			if (json.returnCode == 0) {
 	   			renderCharts1(json.rows);
 	   			matchingLanguage();
   			} else {
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	}
	
	function renderCharts1(rows) {
		charts1 = echarts.init(document.getElementById("charts1"));
		var name = new Array();
		var num = new Array();
		var price = new Array();
		$.each(rows, function(i, item){
			name.push(item.yearMonth);
			num.push(item.insertContractNum);
			price.push(isNull(item.insertContractPrice) ? 0 : item.insertContractPrice);
		});
		var option = getOption('合同月增量分析', '合同在指定年份不同的月增量分析', name, '合同数量', num, 'line');
		option["legend"] = {
	        data: ['合同增量', '合同金额'],
	        y: 'bottom'
	    };
	    option.yAxis = [{
            name: '个/月',
            type: 'value'
        }, {
            name: '元/月',
            type: 'value'
        }];
	    option.series.push({
			name: '合同金额',
			type: 'line',
			smooth: true,
			yAxisIndex: 1,
			data: price
		});
		charts1.setOption(option);
		
		// 加载表格
		$("#tHead").html(getDataUseHandlebars($("#tableHeader").html(), {rows: name}));
		$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: num}));
		$("#tBody").append(getDataUseHandlebars($("#tableBody1").html(), {rows: price}));
    	form.render();
	}
	
	window.onresize = function(){
	    charts1.resize();
	}
	
    exports('crmContractNewInsert', {});
});
