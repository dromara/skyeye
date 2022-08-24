
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'table'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    table = layui.table;
		    
		var procedureList = new Array();
		var rowNum = 1;
		
		showGrid({
            id: "showForm",
            url: flowableBasePath + "erpwayprocedure008",
            params: {rowId: parent.rowId},
            pagination: false,
			method: "GET",
            template: $("#usetableTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter: function (json) {
            	$.each(json.bean.procedureList, function(j, item){
            		item.id = rowNum;
					procedureList.push(item);
					rowNum++;
				});
				
				table.render({
			        id: 'messageTable',
			        elem: '#messageTable',
			        method: 'get',
			        data: procedureList,
			        even: true,
			        page: false,
			        rowDrag: {
			        	trigger: 'row',
			        	done: function(obj) {}
			        },
			        cols: [[
			            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			            { field: 'number', title: '工序编号', align: 'left', width: 120},
			            { field: 'procedureName', title: '工序名称', align: 'left', width: 120},
			            { field: 'unitPrice', title: '加工单价', align: 'left', width: 120},
			            { field: 'departmentName', title: '加工部门', align: 'left', width: 120},
			            { field: 'farmName', title: '加工车间', align: 'left', width: 120}
			        ]],
			        done: function(json) {
			        }
			    });
				
			    matchingLanguage();
			    form.render();
				
            }
		});
	});
});