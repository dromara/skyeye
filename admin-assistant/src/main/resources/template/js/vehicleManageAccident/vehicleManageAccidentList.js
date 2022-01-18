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
	
	// 新增车辆事故
	authBtn('1597468393714');
	
    // 事故管理开始
	// 车牌号
    showGrid({
	 	id: "accidentPlate",
	 	url: reqBasePath + "vehicle010",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(json){
	 		form.render('select');
	 		showAccidentList();
	 	}
	});
	
	function showAccidentList(){
		table.render({
		    id: 'accidentTable',
		    elem: '#accidentTable',
		    method: 'post',
		    url: reqBasePath + 'accident001',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'accidentTitle', title: '主题', align: 'left', width: 300, templet: function(d){
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
			callBack: function(refreshCode){
			}});
	}
	
	// 删除事故信息
	function accidentdelet(data){
		layer.confirm('确认删除该事故信息吗？', {icon: 3, title: '删除操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "accident003", params: {rowId: data.id}, type:'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadAccidentTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 登记事故信息
	$("body").on("click", "#addAccidentBean", function(){
    	_openNewWindows({
			url: "../../tpl/vehicleManageAccident/vehicleManageAccidentAdd.html", 
			title: "车辆事故登记单",
			pageId: "vehicleManageAccidentAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadAccidentTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
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
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadAccidentTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	// 搜索表单
	$("body").on("click", "#accidentformSearch", function(){
    	table.reload("accidentTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    $("body").on("click", "#reloadAccidentTable", function(){
    	loadAccidentTable();
    });
    
    function loadAccidentTable(){
    	table.reload("accidentTable", {where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		accidentTitle: $("#accidentTitle").val(),
    		accidentPlate: $("#accidentPlate").val()
    	};
    }
    
    exports('vehicleManageAccidentList', {});
});
