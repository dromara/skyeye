
// 资产采购
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
	var serviceClassName = sysServiceMation["assetManagePurchase"]["key"];
	var fromHtml = "";

	var allChooseAsset = {};
	var selOption = getFileContent('tpl/template/select-option.tpl');

	AjaxPostUtil.request({url: flowableBasePath + "asset023", params: {rowId: parent.rowId}, type: 'json', callback: function(json) {
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

		// 资产来源
		sysDictDataUtil.queryDictDataListByDictTypeCode(sysDictData["admAssetFrom"]["key"], function (data) {
			fromHtml = getDataUseHandlebars(selOption, data);
		});

		// 资产
		initTableChooseUtil.initTable({
			id: "assetList",
			cols: [
				{id: 'assetId', title: '资产', formType: 'chooseInput', width: '150', iconClassName: 'chooseAssetBtn', verify: 'required'},
				{id: 'fromId', title: '来源', formType: 'select', width: '100', verify: 'required', modelHtml: fromHtml},
				{id: 'number', title: '采购数量', formType: 'input', width: '100', className: 'change-input number', verify: 'required|number', value: '1'},
				{id: 'unitPrice', title: '采购单价', formType: 'input', width: '100', className: 'change-input unitPrice', verify: 'required|money'},
				{id: 'amountOfMoney', title: '采购金额', formType: 'detail', width: '100'},
				{id: 'remark', title: '备注', formType: 'input', width: '100'}
			],
			deleteRowCallback: function (trcusid) {
				delete allChooseAsset[trcusid];
				calculatedTotalPrice();
			},
			addRowCallback: function (trcusid) {
				calculatedTotalPrice();
			},
			form: form,
			minData: 1
		});

		initTableChooseUtil.deleteAllRow('assetList');
		$.each(json.bean.goods, function(i, item) {
			var params = {
				"assetId": item.assetName,
				"fromId": {
					"value": item.fromId
				},
				"number": item.number,
				"unitPrice": item.unitPrice.toFixed(2),
				"amountOfMoney": item.amountOfMoney.toFixed(2),
				"remark": item.remark
			};
			var trcusid = initTableChooseUtil.resetData('assetList', params);
			// 将规格所属的商品信息加入到对象中存储
			allChooseAsset[trcusid] = item;
		});

		matchingLanguage();
		form.render();
	}});

	// 数量，单价变化
	$("body").on("input", ".number, .unitPrice", function () {
		calculatedTotalPrice();
	});
	$("body").on("change", ".number, .unitPrice", function () {
		calculatedTotalPrice();
	});

	// 计算总价
	function calculatedTotalPrice() {
		var allPrice = 0;
		$.each(initTableChooseUtil.getDataRowIndex('assetList'), function (i, item) {
			// 获取行坐标
			var thisRowKey = item;
			// 获取数量
			var number = parseInt(isNull($("#number" + thisRowKey).val()) ? "0" : $("#number" + thisRowKey).val());
			// 获取单价
			var unitPrice = parseFloat(isNull($("#unitPrice" + thisRowKey).val()) ? "0" : $("#unitPrice" + thisRowKey).val());
			// 输出金额
			$("#amountOfMoney" + thisRowKey).html((number * unitPrice).toFixed(2));
			allPrice += parseFloat($("#amountOfMoney" + thisRowKey).html());
		});
		$("#allPrice").html(allPrice.toFixed(2));
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
		var result = initTableChooseUtil.getDataList('assetList');
		if (!result.checkResult) {
			return false;
		}
		var tableData = new Array();
		var noError = false;
		$.each(result.dataList, function(i, item) {
			//获取行编号
			var thisRowKey = item["trcusid"].replace("tr", "");
			if (parseInt(item.rkNum) == 0) {
				$("#number" + thisRowKey).addClass("layui-form-danger");
				$("#number" + thisRowKey).focus();
				winui.window.msg('数量不能为0', {icon: 2, time: 2000});
				noError = true;
				return false;
			}

			var product = allChooseAsset["tr" + thisRowKey];
			item["assetId"] = product.id;
			if (inTableDataArrayByAssetarId(item.assetId, item.fromId, tableData)) {
				winui.window.msg('一张单中不允许出现相同来源的资产信息.', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
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
			allPrice: $("#allPrice").html(),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		AjaxPostUtil.request({url: flowableBasePath + "asset024", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	// 判断选中的资产以及来源是否也在数组中
	function inTableDataArrayByAssetarId(assetId, fromId, array) {
		var isIn = false;
		$.each(array, function(i, item) {
			if(item.assetId === assetId && item.fromId === fromId) {
				isIn = true;
				return false;
			}
		});
		return isIn;
	}

	$("body").on("click", ".chooseAssetBtn", function() {
		var trId = $(this).parent().parent().attr("trcusid");
		adminAssistantUtil.assetCheckType = false; // 选择类型，默认单选，true:多选，false:单选
		adminAssistantUtil.openAssetChoosePage(function (checkAssetMation){
			// 获取表格行号
			var thisRowKey = trId.replace("tr", "");
			$("#assetId" + thisRowKey.toString()).val(checkAssetMation.assetName);
			$("#unitPrice" + thisRowKey.toString()).val(checkAssetMation.readPrice);
			allChooseAsset[trId] = checkAssetMation;
			calculatedTotalPrice();
		});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});