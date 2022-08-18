
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	showList();
	// 我的用车历史列表
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'myhasmation005',
		    where: getTableParams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '3', type: 'numbers' },
				{ field: 'vehicleName', title: '车辆名称', align: 'left', rowspan: '3', width: 120 },
				{ field: 'licensePlate', title: '车牌', align: 'left', rowspan: '3', width: 120 },
				{ field: 'passengerNum', title: '乘车人数', align: 'left', rowspan: '3', width: 100 },
				{ field: 'isSelfDrive', title: '用车方式', align: 'left', rowspan: '3', width: 100 },
				{ field: 'driverName', title: '司机', align: 'left', rowspan: '3', width: 100 },
				{ field: 'departureTime', title: '往返时间', align: 'center', rowspan: '3', width: 300 },
				{ field: 'timeAndPlaceOfTravel', title: '乘车时间地点', align: 'left', rowspan: '3', width: 150 },
				{ field: 'reasonsForUsingCar', title: '用车事由', align: 'left', rowspan: '3', width: 140 },
				{ title: '所属单据', align: 'center', colspan: '3'}
		    ],[
				{ field: 'oddTitle', title: '单据标题', align: 'left', width: 240},
				{ field: 'oddNumber', title: '单据编号', align: 'center', width: 180},
				{ field: 'oddCreateTime', title: '单据日期', align: 'center', width: 140}
			]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}

    $("body").on("click", "#reloadmessageTable", function() {
    	loadTable();
    });
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

	function refreshTable(){
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
	}
    
    function getTableParams() {
    	return {
			vehicleName: $("#vehicleName").val(),
			licensePlate: $("#licensePlate").val()
    	};
    }
    
    exports('myVehicleHistory', {});
});
