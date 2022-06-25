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
	
	// 新增车辆加油
	authBtn('1597469406320');

	// 查询所有的车牌号用于下拉选择框
	adminAssistantUtil.queryAllVehicleList(function (data){
		$("#oilPlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
		form.render('select');
	});

	laydate.render({
		elem: '#oilTime',
		type: 'datetime',
		range: true
	});
	
	showOilingList();
	// 加油管理开始
	function showOilingList(){
		table.render({
		    id: 'oilingTable',
		    elem: '#oilingTable',
		    method: 'post',
		    url: flowableBasePath + "oiling001",
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'oilTitle', title: '主题', align: 'left', width: 300, templet: function(d){
		        	return '<a lay-event="oilingdetails" class="notice-title-click">' + d.oilTitle + '</a>';
		        }},
		        { field: 'licensePlate', title: '车牌号', align: 'center', width: 120},
		        { field: 'oilTime', title: '加油日期', align: 'center', width: 170 },
		        { field: 'oilCapacity', title: '加油容量（升）', width: 120},
                { field: 'oilPrice', title: '加油金额（元）', width: 150 },
		        { field: 'userName', title: '申请人', align: 'center', width: 100 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#oilingtableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(oilingTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'oilingdetails') { //详情
        	oilingdetails(data);
        }else if (layEvent === 'oilingdelet'){ //删除
        	oilingdelet(data);
        }else if (layEvent === 'oilingedit'){	//编辑
        	oilingedit(data);
        }
    });
	
	form.render();
	
	// 加油信息详情
	function oilingdetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageOiling/vehicleManageOilingDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "vehicleManageOilingDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 删除加油信息
	function oilingdelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "oiling003", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadOilingTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 登记加油信息
	$("body").on("click", "#addOilingBean", function(){
    	_openNewWindows({
			url: "../../tpl/vehicleManageOiling/vehicleManageOilingAdd.html", 
			title: "车辆加油登记单",
			pageId: "vehicleManageOilingAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadOilingTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 编辑加油信息
	function oilingedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageOiling/vehicleManageOilingEdit.html", 
			title: "编辑车辆加油信息",
			pageId: "vehicleManageOilingEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadOilingTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	// 搜索表单
	$("body").on("click", "#oilingformSearch", function(){
    	table.reload("oilingTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    $("body").on("click", "#reloadOilingTable", function(){
    	loadOilingTable();
    });
    
    function loadOilingTable(){
    	table.reload("oilingTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	var startTime = "", endTime = "";
    	if(!isNull($("#oilTime").val())){
    		startTime = $("#oilTime").val().split(' - ')[0].trim();
    		endTime = $("#oilTime").val().split(' - ')[1].trim();
    	}
    	return {
    		oilTitle: $("#oilTitle").val(),
    		oilPlate: $("#oilPlate").val(),
    		startTime: startTime,
    		endTime: endTime
    	};
    }
	
    exports('vehicleManageOilingList', {});
});
