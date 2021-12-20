
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
	// 统计当前部门月度补料图
	function initCustomDocumentaryByType(year){
		AjaxPostUtil.request({url:reqBasePath + "erpproduce002", params: {year: year}, type:'json', callback:function(json){
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
		var name = new Array();
		var num = new Array();
		var materialName = "";
		var materialNameList = new Array();
		$.each(rows, function(i, item){
			if(i == 0){
				materialName = item.materialName;
				$.each(item.yearPatchMaterialNum, function(j, bean){
					name.push(bean.yearMonth);
					num.push(bean.needNum);
				});
			}
		});
		materialNameList.push(materialName);
		var option = getOption('当前部门补料分析', '统计在指定年份当前部门补料的数量分析', name, materialName, num, 'line');
		$.each(rows, function(i, item){
			if(i != 0){
				materialName = item.materialName;
				materialNameList.push(materialName);
				num = new Array();
				$.each(item.yearPatchMaterialNum, function(j, bean){
					num.push(bean.needNum);
				});
				option.series.push({
					name: materialName,
					type: 'line',
					smooth: true,
					data: [].concat(num)
				});
			}
		});
		
		option["legend"] = {
	        data: materialNameList,
	        y: 'bottom'
	    };
		charts1.setOption(option);
		
		// 加载表格
		$("#tHead").html(getDataUseHandlebars($("#tableHeader").html(), {rows: name}));
		$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {rows: rows}));
    	form.render();
	}
	
	window.onresize = function(){
	    charts1.resize();
	}
	
    exports('departmentPatchMaterial', {});
});
