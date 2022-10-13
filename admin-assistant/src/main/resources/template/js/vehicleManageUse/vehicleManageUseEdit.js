
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
	var serviceClassName = sysServiceMation["vehicleManageUse"]["key"];

	// 是否加载司机
	var loadDriver = false;
	var useEditTemplate = $("#useEditTemplate").html();

	AjaxPostUtil.request({url: flowableBasePath + "vehicle019", params: {rowId: parent.rowId}, type: 'json', callback: function(json) {
		$("#useTitle").html(json.bean.title);
		$("#useName").html(json.bean.userName);
		// 附件回显
		skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

		$(".vehicleEdit").html(getDataUseHandlebars(useEditTemplate, json));
		$("input:radio[name=isSelfDrive][value=" + json.bean.isSelfDrive + "]").attr("checked", true);
		$("#departureTime").val(json.bean.departureTime);
		// 时间段
		laydate.render({
			elem: '#departureTime',
			type:'datetime',
			range: '~',
			format: 'yyyy-MM-dd HH:mm:ss',
			done: function(value, date){
				$("#departureTime").val(value);
			}
		});
		initDesignatedVehicle(json.bean.passengerNum, json.bean.designatedVehicleId);
		if(json.bean.isSelfDrive == '2'){
			$("#ishasDriver").removeClass("layui-hide");
			initDriver(json.bean.driverId);
		}
		if(json.bean.state == '1'){
			$(".typeTwo").removeClass("layui-hide");
		} else {
			$(".typeOne").removeClass("layui-hide");
		}

		matchingLanguage();
		form.render();
	}});

	//是否自驾进行变动
	form.on('radio(isSelfDrive)', function (data) {
		var val = data.value;
		if(val == '1'){//自驾
			$("#ishasDriver").addClass("layui-hide");
			$("#driverId").val("");
		} else if (val == '2'){//安排司机
			$("#ishasDriver").removeClass("layui-hide");
			if(!loadDriver){
				initDriver();
			}
		} else {
			winui.window.msg('状态值错误', {icon: 2, time: 2000});
		}
	});

	//初始化指定用车
	function initDesignatedVehicle(num, id){
		showGrid({
			id: "designatedVehicleId",
			url: flowableBasePath + "vehicle011",
			params: {passengerNum: num},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendAfter: function (json) {
				$("#designatedVehicleId").val(id);
				form.render('select');
			}
		})
	}

	// 初始化司机选择
	function initDriver(id){
		loadDriver = true;
		showGrid({
			id: "driverId",
			url: flowableBasePath + "vehicle012",
			params: {},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendAfter: function (json) {
				$("#driverId").val(id);
				form.render('select');
			}
		})
	}

	// 保存为草稿
	form.on('submit(formEditBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('1', "");
		}
		return false;
	});

	// 提交审批
	form.on('submit(formSubBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	// 工作流中保存
	form.on('submit(subBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('3', "");
		}
		return false;
	});

	function saveData(subType, approvalId) {
		var params = {
			rowId: parent.rowId,
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
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		if(params.isSelfDrive == '1'){
			params.driverId = "";
		}
		AjaxPostUtil.request({url: flowableBasePath + "vehicle020", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	// 乘车人数进行变动
	$("body").on("change", "#passengerNum", function() {
		var passengerNum = $(this).val();
		if (!isNull(passengerNum)){
			initDesignatedVehicle(passengerNum);
		} else {
			$("#designatedVehicleId").html("");
			form.render("select");
		}
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});