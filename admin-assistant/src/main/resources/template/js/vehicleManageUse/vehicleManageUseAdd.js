
// 用车申请
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'laydate', 'form'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;

	var selOption = getFileContent('tpl/template/select-option.tpl');
	var loadDriver = false;

	laydate.render({
		elem: '#departureTime',
		type: 'datetime',
		range: '~'
	});

	// 乘车人数进行变动
	$("body").on("change", "#passengerNum", function() {
		var passengerNum = $(this).val();
		if(!isNull(passengerNum)){
			initDesignatedVehicle(passengerNum);
		} else {
			$("#designatedVehicleId").html("");
			form.render("select");
		}
	});

	// 是否自驾进行变动
	form.on('radio(isSelfDrive)', function (data) {
		var val = data.value;
		if(val == '1'){//自驾
			$("#ishasDriver").addClass("layui-hide");
		}else if(val == '2'){//安排司机
			$("#ishasDriver").removeClass("layui-hide");
			if(!loadDriver){
				initDriver();
			}
		} else {
			winui.window.msg('状态值错误', {icon: 2, time: 2000});
		}
	});

	skyeyeEnclosure.init('enclosureUpload');
	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
		$("#useTitle").html("用车申请单-" + getYMDFormatDate() + '-' + data.bean.userName);
		$("#useName").html(data.bean.userName);
	});

	//初始化指定用车
	function initDesignatedVehicle(num) {
		AjaxPostUtil.request({url: flowableBasePath + "vehicle011", params: {passengerNum: num}, type: 'json', callback: function(json) {
			var designatedVehicleHtml = getDataUseHandlebars(selOption, json); //加载类别数据
			$("#designatedVehicleId").html(designatedVehicleHtml);
			//渲染
			form.render("select");
		}});
	}

	//初始化司机选择
	function initDriver() {
		loadDriver = true;
		AjaxPostUtil.request({url: flowableBasePath + "vehicle012", params: {}, type: 'json', callback: function(json) {
			var driverHtml = getDataUseHandlebars(selOption, json); //加载类别数据
			$("#driverId").html(driverHtml);
			//渲染
			form.render("select");
		}});
	}

	matchingLanguage();
	// 保存为草稿
	form.on('submit(formAddBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData("1", "");
		}
		return false;
	});

	// 提交审批
	form.on('submit(formSubBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(sysActivitiModel["vehicleManageUse"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId){
		var params = {
			title: $("#useTitle").html(),
			passengerNum: $("#passengerNum").val(),
			passengerInfo: $("#passengerInfo").val(),
			isSelfDrive: $("input[name='isSelfDrive']:checked").val(),
			driverId: $("#driverId").val(),
			departureTime: $("#departureTime").val().split('~')[0].trim(),
			returnTime: $("#departureTime").val().split('~')[1].trim(),
			designatedVehicleId: $("#designatedVehicleId").val(),
			timeAndPlaceOfTravel: $("#timeAndPlaceOfTravel").val(),
			reasonsForUsingCar: $("#reasonsForUsingCar").val(),
			remark: $("#remark").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "vehicle013", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	$("body").on("click", ".enclosureItem", function() {
		download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});