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
		
	// 设置提示信息
	var s = '生产计划单选择规则：1.单选，单击击指定行数据即可选中；2.已审批通过的未完成的生产计划单。如没有查到要选择的单据，请检查单据信息是否满足当前规则。';
	$("#showInfo").html(s);
	
	var chooseMation = {};
	
	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'erpproduction009',
		    where: getTableParams(),
			even: true,
		    page: false,
		    cols: [[
		    	{ type: 'radio'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'defaultNumber', title: '生产单号', align: 'center', width: 200, templet: function(d){
			        return '<a lay-event="details" class="notice-title-click">' + d.defaultNumber + '</a>';
			    }},
	            { field: 'salesOrderNum', width: 200, title: '关联销售单', align: 'center'},
	            { field: 'materialName', width: 150, title: '商品名称'},
	            { field: 'materialModel', width: 150, title: '商品型号'},
	            { field: 'number', width: 80, title: '计划数量'},
		        { field: 'planStartDate', width: 140, align: 'center', title: '计划开始时间'},
		        { field: 'planComplateDate', width: 140, align: 'center', title: '计划结束时间'}
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
		    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					chooseMation = obj;
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
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpProduction/erpProductionDetail.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "erpProductionDetail",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 搜索表单
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	var $step = $("#step");
	$step.step({
		index: 0,
		time: 500,
		title: ["选择计划单", "选择工序"]
	});
	
	// 自己构造的合并相同部门后的工序
	var procedureList = new Array();
	// 下一步
	$("body").on("click", "#nextTab", function(){
		if(isNull(chooseMation.id)){
			winui.window.msg("请选择生产计划单.", {icon: 2, time: 2000});
			return false;
		}
		
		// 加载工序信息
		$.each(chooseMation.procedureMationList, function(i, item){
			var inListIndex = -1;
			$.each(procedureList, function(j, bean){
				if(bean.departmentId == item.departmentId){
					inListIndex = j;
					return false;
				}
			});
			if(inListIndex >= 0){
				procedureList[inListIndex].procedureArray.push(item);
			} else {
	    		if(item.state == 1){
	    			item.checkBoxHtml = '<input type="radio" name="procedureListName" value="' + item.departmentId + '">';
	    			item.stateName = "<span class='state-down'>待下达</span>";
	    		}else if(item.state == 2){
	    			item.checkBoxHtml = '<input type="radio" name="procedureListName" value="' + item.departmentId + '" disabled="disabled">';
	    			item.stateName = "<span class='state-up'>已下达</span>"
	    		}
	    		var procedureArray = new Array();
	    		procedureArray.push(item);
	    		//深层复制
	    		var proItem = $.extend(true, {}, item);
	    		proItem.procedureArray = procedureArray;
	    		procedureList.push(proItem);
			}
    	});
		$("#tBody").html(getDataUseHandlebars($("#tableBody").html(), {procedureList: procedureList}));
    	form.render();
		$step.nextStep();
		$("#firstTab").hide();
		$("#secondTab").show();
	});
	
	//上一步
	$("body").on("click", "#prevTab", function(){
		$step.prevStep();
		$("#firstTab").show();
		$("#secondTab").hide();
	});
	
	//保存
	$("body").on("click", "#saveChoose", function(){
		if(isNull(chooseMation.id)){
			winui.window.msg("请选择生产计划单.", {icon: 2, time: 2000});
			return false;
		}
		var chooseProcedure = $("input[name='procedureListName']:checked").val();
		if(isNull(chooseProcedure)){
			winui.window.msg("请选择工序信息.", {icon: 2, time: 2000});
			return false;
		}
		//获取选择的工序
		var chooseIndex = -1;
		$.each(procedureList, function(i, item){
			if(item.departmentId == chooseProcedure){
				chooseIndex = i;
				return false;
			}
		});
		
		//根据生产计划单id获取该单据下的所有外购商品以及剩余数量
		//chooseType：根据生产计划单选择工序时，加工单需要回显自产和外购两种类型的商品
		AjaxPostUtil.request({url: flowableBasePath + "erpproduction010", params: {orderId: chooseMation.id, chooseType: "2"}, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				chooseMation.norms = [].concat(json.rows);
				chooseMation.procedureList = procedureList[chooseIndex].procedureArray;
				parent.productionMation = chooseMation;
				
				parent.refreshCode = '0';
				parent.layer.close(index);
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	});
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams(){
		return {
			materialName: $("#materialName").val(),
    		materialModel: $("#materialModel").val(),
    		defaultNumber: $("#defaultNumber").val(),
    		salesOrderNum: $("#salesOrderNum").val()
		};
	}
	
    exports('erpProductionNoSuccessChooseProcedure', {});
});