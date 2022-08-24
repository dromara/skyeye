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
	
	// 新增车辆年检
	authBtn('1597470685417');

	// 查询所有的车牌号用于下拉选择框
	adminAssistantUtil.queryAllVehicleList(function (data) {
		$("#inspectionPlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
		form.render('select');
	});

	showInspectionList();
	// 年检管理开始
	function showInspectionList(){
		table.render({
		    id: 'inspectionTable',
		    elem: '#inspectionTable',
		    method: 'post',
		    url: flowableBasePath + "inspection001",
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'inspectionTitle', title: '主题', align: 'left', width: 300, templet: function (d) {
		        	return '<a lay-event="inspectiondetails" class="notice-title-click">' + d.inspectionTitle + '</a>';
		        }},
		        { field: 'licensePlate', title: '车牌号', align: 'center', width: 120},
		        { field: 'thisInspectionTime', title: '本次年检时间', align: 'center', width: 150 },
		        { field: 'nextInspectionTime', title: '下次年检时间', align: 'center', width: 150 },
		        { field: 'inspectionPrice', title: '年检费用（元）', width: 120},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#inspectiontableBar'}
		    ]],
		    done: function(json) {
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(inspectionTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'inspectiondetails') { //详情
        	inspectiondetails(data);
        } else if (layEvent === 'inspectiondelet'){ //删除
        	inspectiondelet(data);
        } else if (layEvent === 'inspectionedit'){	//编辑
        	inspectionedit(data);
        }
    });
	
	form.render();
	
	// 年检信息详情
	function inspectiondetails(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageInspection/vehicleManageInspectionDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "vehicleManageInspectionDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 删除年检信息
	function inspectiondelet(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "inspection003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadInspectionTable();
    		}});
		});
	}

	// 登记年检信息
	$("body").on("click", "#addInspectionBean", function() {
    	_openNewWindows({
			url: "../../tpl/vehicleManageInspection/vehicleManageInspectionAdd.html", 
			title: "车辆年检登记单",
			pageId: "vehicleManageInspectionAdd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadInspectionTable();
			}});
    });
	
	// 编辑年检信息
	function inspectionedit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageInspection/vehicleManageInspectionEdit.html", 
			title: "编辑车辆年检信息",
			pageId: "vehicleManageInspectionEdit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadInspectionTable();
			}});
	}
	
	// 搜索表单
	$("body").on("click", "#inspectionformSearch", function() {
    	table.reloadData("inspectionTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    $("body").on("click", "#reloadInspectionTable", function() {
    	loadInspectionTable();
    });
    
    function loadInspectionTable(){
    	table.reloadData("inspectionTable", {where: getTableParams()});
    }
    
    function getTableParams() {
    	return {
    		inspectionTitle: $("#inspectionTitle").val(),
    		inspectionPlate: $("#inspectionPlate").val()
    	};
    }
    
    exports('vehicleManageInspectionList', {});
});
