
// 资产领用
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'form'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	var serviceClassName = sysServiceMation["assetManageUse"]["key"];

	var allChooseAssetReport = {};

	AjaxPostUtil.request({url: flowableBasePath + "asset014", params: {rowId: parent.rowId}, type: 'json', callback: function(json) {
		$("#useTitle").html(json.bean.title);
		$("#useName").html(json.bean.userName);
		$("#remark").val(json.bean.remark);
		// 附件回显
		skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

		if(json.bean.state == '1'){
			$(".typeTwo").removeClass("layui-hide");
		} else {
			$(".typeOne").removeClass("layui-hide");
		}

		// 资产
		initTableChooseUtil.initTable({
			id: "assetReportList",
			cols: [
				{id: 'assetReportId', title: '资产', formType: 'chooseInput', width: '150', iconClassName: 'chooseAssetReportBtn', verify: 'required'},
				{id: 'specifications', title: '规格', formType: 'detail', width: '150'},
				{id: 'assetNum', title: '编号', formType: 'detail', width: '150'},
				{id: 'assetImg', title: '图片', formType: 'detail', width: '80'},
				{id: 'storageArea', title: '存放区域', formType: 'detail', width: '150'},
				{id: 'remark', title: '备注', formType: 'input', width: '100'}
			],
			deleteRowCallback: function (trcusid) {
				delete allChooseAssetReport[trcusid];
			},
			addRowCallback: function (trcusid) {
			},
			form: form,
			minData: 1
		});

		initTableChooseUtil.deleteAllRow('assetReportList');
		$.each(json.bean.goods, function(i, item) {
			var params = {
				"assetReportId": item.assetName,
				"specifications": item.specifications,
				"assetNum": item.assetNum,
				"assetImg": '<img src="' + systemCommonUtil.getFilePath(item.assetImg) + '" class="photo-img">',
				"storageArea": item.storageArea,
				"remark": item.remark
			};
			var trcusid = initTableChooseUtil.resetData('assetReportList', params);
			item["id"] = item.assetReportId;
			allChooseAssetReport[trcusid] = item;
		});

		matchingLanguage();
	}});

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
		var result = initTableChooseUtil.getDataList('assetReportList');
		if (!result.checkResult) {
			return false;
		}
		var tableData = new Array();
		var noError = false;
		$.each(result.dataList, function(i, item) {
			// 获取行编号
			var thisRowKey = item["trcusid"].replace("tr", "");

			var assetReport = allChooseAssetReport["tr" + thisRowKey];
			if (judgeInPoingArr(tableData, "assetReportId", assetReport.id)) {
				winui.window.msg('领用单存在相同的资产', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			item["assetReportId"] = assetReport.id;
			tableData.push(item);
		});
		if (noError) {
			return false;
		}

		var params = {
			remark: $("#remark").val(),
			assetListStr: JSON.stringify(tableData),
			rowId: parent.rowId,
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		AjaxPostUtil.request({url: flowableBasePath + "asset015", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	$("body").on("click", ".chooseAssetReportBtn", function() {
		var trId = $(this).parent().parent().attr("trcusid");
		adminAssistantUtil.assetReportCheckType = false; // 选择类型，默认单选，true:多选，false:单选
		adminAssistantUtil.openAssetReportChoosePage(function (checkAssetReportMation){
			// 获取表格行号
			var thisRowKey = trId.replace("tr", "");
			$("#assetReportId" + thisRowKey.toString()).val(checkAssetReportMation.assetName);
			$("#specifications" + thisRowKey.toString()).html(checkAssetReportMation.specifications);
			$("#assetNum" + thisRowKey.toString()).html(checkAssetReportMation.assetNum);
			$("#assetImg" + thisRowKey.toString()).html('<img src="' + systemCommonUtil.getFilePath(checkAssetReportMation.assetImg) + '" class="photo-img">');
			$("#storageArea" + thisRowKey.toString()).html(checkAssetReportMation.storageArea);
			allChooseAssetReport[trId] = checkAssetReportMation;
		});
	});

	// 图片查看
	$("body").on("click", ".photo-img", function() {
		systemCommonUtil.showPicImg($(this).attr("src"));
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});