var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
	    laydate = layui.laydate;
	
	// 新增车辆维修保养
	authBtn('1597478766486');

	// 查询所有的车牌号用于下拉选择框
	adminAssistantUtil.queryAllVehicleList(function (data){
		$("#maintenancePlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
		form.render('select');
	});

	showMaintenanceList();
	// 维修保养管理
	function showMaintenanceList(){
		table.render({
		    id: 'maintenanceTable',
		    elem: '#maintenanceTable',
		    method: 'post',
		    url: flowableBasePath + "maintenance001",
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'maintenanceTitle', title: '主题', align: 'left', width: 300, templet: function(d){
		        	return '<a lay-event="maintenancedetails" class="notice-title-click">' + d.maintenanceTitle + '</a>';
		        }},
		        { field: 'licensePlate', title: '车牌号', align: 'center', width: 120 },
		        { field: 'maintenanceType', title: '类型', align: 'center', width: 120 },
		        { field: 'Time', title: '时间段', align: 'center', width: 300 },
		        { field: 'maintenancePrice', title: '费用（元）', width: 150 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#maintenancetableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(maintenanceTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'maintenancedetails') { //详情
        	maintenancedetails(data);
        }else if (layEvent === 'maintenancedelet'){ //删除
        	maintenancedelet(data);
        }else if (layEvent === 'maintenanceedit'){	//编辑
        	maintenanceedit(data);
        }
    });
	
	form.render();
	
	// 维修保养信息详情
	function maintenancedetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageMaintenance/vehicleManageMaintenanceDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "vehicleManageMaintenanceDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 删除维修保养信息
	function maintenancedelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "maintenance003", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadMaintenanceTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 登记维修保养信息
	$("body").on("click", "#addMaintenanceBean", function(){
    	_openNewWindows({
			url: "../../tpl/vehicleManageMaintenance/vehicleManageMaintenanceAdd.html", 
			title: "车辆维修保养登记单",
			pageId: "vehicleManageMaintenanceAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadMaintenanceTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 编辑维修保养信息
	function maintenanceedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageMaintenance/vehicleManageMaintenanceEdit.html", 
			title: "编辑车辆维修保养信息",
			pageId: "vehicleManageMaintenanceEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadMaintenanceTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	// 搜索表单
	$("body").on("click", "#maintenanceformSearch", function(){
    	table.reload("maintenanceTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    $("body").on("click", "#reloadMaintenanceTable", function(){
    	loadMaintenanceTable();
    });
    
    function loadMaintenanceTable(){
    	table.reload("maintenanceTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		maintenanceTitle: $("#maintenanceTitle").val(),
    		maintenancePlate: $("#maintenancePlate").val()
    	};
    }
    
    exports('vehicleManageMaintenanceList', {});
});
