var rowId = "";

// 事故管理
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
	
	// 新增车辆事故
	authBtn('1597468393714');
	
	// 查询所有的车牌号用于下拉选择框
	adminAssistantUtil.queryAllVehicleList(function (data){
		$("#accidentPlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
		form.render('select');
	});

	showAccidentList();
	function showAccidentList(){
		table.render({
		    id: 'accidentTable',
		    elem: '#accidentTable',
		    method: 'post',
		    url: flowableBasePath + "accident001",
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'accidentTitle', title: '主题', align: 'left', width: 300, templet: function (d) {
		        	return '<a lay-event="accidentdetails" class="notice-title-click">' + d.accidentTitle + '</a>';
		        }},
		        { field: 'licensePlate', title: '车牌号', align: 'center', width: 120},
		        { field: 'driver', title: '驾驶员', align: 'center', width: 80 },
		        { field: 'accidentTime', title: '事故时间', align: 'center', width: 100},
		        { field: 'accidentArea', title: '事故地点', width: 300 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#accidenttableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(accidentTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'accidentdetails') { //详情
        	accidentdetails(data);
        }else if (layEvent === 'accidentdelet'){ //删除
        	accidentdelet(data);
        }else if (layEvent === 'accidentedit'){	//编辑
        	accidentedit(data);
        }
    });
	
	form.render();
	
	// 事故信息详情
	function accidentdetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageAccident/vehicleManageAccidentDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "vehicleManageAccidentDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
			}});
	}
	
	// 删除事故信息
	function accidentdelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "accident003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadAccidentTable();
    		}});
		});
	}

	// 登记事故信息
	$("body").on("click", "#addAccidentBean", function() {
    	_openNewWindows({
			url: "../../tpl/vehicleManageAccident/vehicleManageAccidentAdd.html", 
			title: "车辆事故登记单",
			pageId: "vehicleManageAccidentAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadAccidentTable();
			}});
    });
	
	// 编辑事故信息
	function accidentedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageAccident/vehicleManageAccidentEdit.html", 
			title: "编辑车辆事故信息",
			pageId: "vehicleManageAccidentEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadAccidentTable();
			}});
	}
	
	// 搜索表单
	$("body").on("click", "#accidentformSearch", function() {
    	table.reloadData("accidentTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    $("body").on("click", "#reloadAccidentTable", function() {
    	loadAccidentTable();
    });
    
    function loadAccidentTable(){
    	table.reloadData("accidentTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		accidentTitle: $("#accidentTitle").val(),
    		accidentPlate: $("#accidentPlate").val()
    	};
    }
    
    exports('vehicleManageAccidentList', {});
});
