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
	
	// 新增车辆
	authBtn('1565850421887');
	
	showVehicleList();
	// 车辆信息管理
	function showVehicleList(){
		table.render({
		    id: 'vehicleTable',
		    elem: '#vehicleTable',
		    method: 'post',
		    url: flowableBasePath + "vehicle001",
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'vehicleName', title: '名称', width: 170, templet: function(d){
		        	return '<a lay-event="vehicledetails" class="notice-title-click">' + d.vehicleName + '</a>';
		        }},
		        { field: 'vehicleCompany', title: '所属公司', align: 'left', width: 170 },
		        { field: 'vehicleImg', title: '图片', align: 'center', width: 60, templet: function(d){
		        	if(isNull(d.vehicleImg)){
		        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
		        	}else{
		        		return '<img src="' + fileBasePath + d.vehicleImg + '" class="photo-img" lay-event="vehicleImg">';
		        	}
		        }},
		        { field: 'licensePlate', title: '车牌', align: 'center', width: 120 },
		        { field: 'state', title: '状态', width: 80, align: 'center', templet: function(d){
		        	if(d.state == '1'){
		        		return "<span class='state-up'>正常</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-down'>维修</span>";
		        	}else if(d.state == '3'){
		        		return "<span class='state-down'>报废</span>";
		        	}else{
		        		return "参数错误";
		        	}
		        }},
		        { field: 'nextInspectionTime', title: '下次年检日期', align: 'center', width: 100 },
		        { field: 'insuranceDeadline', title: '保险截止日期', align: 'center', width: 100 },
		        { field: 'prevMaintainTime', title: '上次保养日期', align: 'center', width: 100 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 220, toolbar: '#vehicletableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(vehicleTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'vehiclerepair') { //维修
        	vehiclerepair(data);
        }else if (layEvent === 'vehicleImg') { //图片预览
        	layer.open({
        		type:1,
        		title:false,
        		closeBtn:0,
        		skin: 'demo-class',
        		shadeClose:true,
        		content:'<img src="' + fileBasePath + data.vehicleImg + '" style="max-height:600px;max-width:100%;">',
        		scrollbar:false
            });
        }else if (layEvent === 'vehicledetails') { //详情
        	vehicledetails(data);
        }else if (layEvent === 'vehiclescrap'){ //报废
        	vehiclescrap(data);
        }else if (layEvent === 'vehiclenormal'){ //恢复正常
        	vehiclenormal(data);
        }else if (layEvent === 'vehicledelet'){ //删除
        	vehicledelet(data);
        }else if (layEvent === 'vehicleedit'){ //编辑
        	vehicleedit(data);
        }
    });
	
	// 车辆详情
	function vehicledetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManage/vehicleManageDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "vehicleManageDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 车辆维修
	function vehiclerepair(data){
		layer.confirm('确认维修该车辆吗？', {icon: 3, title: '维修操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "vehicle005", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadVehicleTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 车辆报废
	function vehiclescrap(data){
		layer.confirm('确认报废该车辆吗？', { icon: 3, title: '报废操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "vehicle006", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadVehicleTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	// 车辆恢复正常
	function vehiclenormal(data){
		layer.confirm('确认对该车辆恢复正常吗？', { icon: 3, title: '恢复操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "vehicle004", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadVehicleTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 删除车辆
	function vehicledelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "vehicle003", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadVehicleTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 登记车辆
	$("body").on("click", "#addVehicleBean", function(){
    	_openNewWindows({
			url: "../../tpl/vehicleManage/vehicleManageAdd.html", 
			title: "登记车辆",
			pageId: "vehicleManageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadVehicleTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 编辑车辆
	function vehicleedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManage/vehicleManageEdit.html", 
			title: "编辑车辆",
			pageId: "vehicleManageEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadVehicleTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	$("body").on("click", "#reloadVehicleTable", function(){
    	loadVehicleTable();
    });
	
    $("body").on("click", "#vehicleformSearch", function(){
    	table.reload("vehicleTable", {page: {curr: 1}, where: getTableParams()});
    });
    
    function loadVehicleTable(){
    	table.reload("vehicleTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		vehicleName:$("#vehicleName").val(),
    		licensePlate:$("#licensePlate").val(),
    		state:$("#state").val()
    	};
    }
	
    exports('vehicleManageList', {});
});
