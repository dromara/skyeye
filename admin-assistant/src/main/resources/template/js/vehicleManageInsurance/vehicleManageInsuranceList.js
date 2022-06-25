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
	
	// 新增车辆保险
	authBtn('1597479769139');

	// 查询所有的车牌号用于下拉选择框
	adminAssistantUtil.queryAllVehicleList(function (data){
		$("#insurancePlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
		form.render('select');
	});

	showInsuranceList();
	// 保险信息管理
	function showInsuranceList(){
		table.render({
		    id: 'insuranceTable',
		    elem: '#insuranceTable',
		    method: 'post',
		    url: flowableBasePath + "insurance001",
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'insuranceTitle', title: '主题', align: 'left', width: 300, templet: function(d){
		        	return '<a lay-event="insurancedetails" class="notice-title-click">' + d.insuranceTitle + '</a>';
		        }},
		        { field: 'licensePlate', title: '车牌号', align: 'center', width: 120},
		        { field: 'validityTime', title: '保险有效期', align: 'center', width: 180 },
		        { field: 'insuranceAllPrice', title: '投保总费用（元）', width: 150 },
                { field: 'insuranceCompany', title: '投保公司', width: 170 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#insurancetableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}
	
	table.on('tool(insuranceTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'insurancedetails') { //详情
        	insurancedetails(data);
        }else if (layEvent === 'insurancedelet'){ //删除
        	insurancedelet(data);
        }else if (layEvent === 'insuranceedit'){ //编辑
        	insuranceedit(data);
        }
    });
	
	form.render();
	
	// 保险详情
	function insurancedetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageInsurance/vehicleManageInsuranceDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "vehicleManageInsuranceDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 删除保险
	function insurancedelet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "insurance003", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadInsuranceTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 登记保险
	$("body").on("click", "#addInsuranceBean", function(){
    	_openNewWindows({
			url: "../../tpl/vehicleManageInsurance/vehicleManageInsuranceAdd.html", 
			title: "车辆保险登记单",
			pageId: "vehicleManageInsuranceAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadInsuranceTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	// 编辑保险
	function insuranceedit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/vehicleManageInsurance/vehicleManageInsuranceEdit.html", 
			title: "编辑车辆保险信息",
			pageId: "vehicleManageInsuranceEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadInsuranceTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	// 搜索表单
	$("body").on("click", "#insuranceformSearch", function(){
		table.reload("insuranceTable", {page: {curr: 1}, where: getTableParams()});
	});
	
    $("body").on("click", "#reloadInsuranceTable", function(){
    	loadInsuranceTable();
    });
    
    function loadInsuranceTable(){
		table.reload("insuranceTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		insuranceTitle: $("#insuranceTitle").val(),
    		insurancePlate: $("#insurancePlate").val()
    	};
    }
    
    exports('vehicleManageInsuranceList', {});
});
