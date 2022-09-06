var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
		
	//设置提示信息
	var s = '加工单选择规则：1.单选，双击指定行数据即可选中；2.已审批通过的未完成的加工单。如没有查到要选择的单据，请检查单据信息是否满足当前规则。';
	$("#showInfo").html(s);
	
	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'erpmachin010',
		    where: getTableParams(),
			even: true,
		    page: false,
		    cols: [[
		    	{ type: 'radio', rowspan: '2'},
	            { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
	            { field: 'orderNum', rowspan: '2', title: '单据编号', align: 'center', width: 180, templet: function (d) {
			        return '<a lay-event="details" class="notice-title-click">' + d.orderNum + '</a>';
			    }},
	            { field: 'productionNumber', rowspan: '2', title: '生产计划单', align: 'center', width: 200},
	            { colspan: '3', title: '加工成品信息', align: 'center'},
	            { field: 'state', rowspan: '2', title: '状态', align: 'left', width: 80, templet: function (d) {
			        if(d.state == '1'){
		        		return "<span class='state-down'>未审核</span>";
		        	} else if (d.state == '2'){
		        		return "<span class='state-up'>审核中</span>";
		        	} else if (d.state == '3'){
		        		return "<span class='state-new'>审核通过</span>";
		        	} else if (d.state == '4'){
		        		return "<span class='state-down'>拒绝通过</span>";
		        	} else if (d.state == '5'){
		        		return "<span class='state-new'>已完成</span>";
		        	} else {
		        		return "参数错误";
		        	}
			    }},
	            { colspan: '3', title: '加工信息', align: 'center'},
	            { field: 'createName', rowspan: '2', title: '录入人', align: 'left', width: 100},
	            { field: 'createTime', rowspan: '2', title: '录入日期', align: 'center', width: 150 }
	        ],[
		        { field: 'materialName', title: '名称', align: 'left', width: 120},
		    	{ field: 'unitName', title: '规格', align: 'center', width: 80},
		        { field: 'needNum', title: '加工数量', align: 'center', width: 80},
		        { field: 'departmentName', title: '部门', align: 'left', width: 100},
		    	{ field: 'startTime', title: '开始时间', align: 'center', width: 150 },
		        { field: 'endTime', title: '结束时间', align: 'center', width: 150}
	        ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
		    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					
					//根据加工单id获取该单据下的所有单据中商品以及剩余领料数量
					AjaxPostUtil.request({url: flowableBasePath + "erpmachin011", params: {rowId: obj.id}, type: 'json', callback: function(json) {
						obj.norms = [].concat(json.rows);
						parent.machinMation = obj;

						parent.refreshCode = '0';
						parent.layer.close(index);
					}});
				});
				
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
					var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				})
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details') { //详情
	        	details(data);
	        }
	    });
		
		form.render();
	}
	
	//详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpMachin/erpMachinDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "erpMachinDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	
	form.on('submit(formSearch)', function (data) {
    	
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams() {
		return {
			defaultNumber: $("#defaultNumber").val(), 
    		materialName: $("#materialName").val()
		};
	}
	
    exports('erpMachinStateIsPassNoComplateChoose', {});
});