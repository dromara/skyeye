
var assetArticles = new Array(); //用品集合

// 用品领用
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
	var serviceClassName = sysServiceMation["assetArticlesUse"]["key"];
	var typeHtml = "";

	var selOption = getFileContent('tpl/template/select-option.tpl');

	skyeyeEnclosure.init('enclosureUpload');
	systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
		$("#title").html("用品领用申请单-" + getYMDFormatDate() + '-' + data.bean.userName);
		$("#useName").html(data.bean.userName);
	});

	// 用品类别
	sysDictDataUtil.queryDictDataListByDictTypeCode(sysDictData["admAssetArticlesType"]["key"], function (data) {
		typeHtml = getDataUseHandlebars(selOption, data);
	});

	initTableChooseUtil.initTable({
		id: "articlesList",
		cols: [
			{id: 'typeId', title: '类别', formType: 'select', width: '150', verify: 'required', layFilter: 'selectTypeProperty', modelHtml: typeHtml},
			{id: 'articleId', title: '用品', formType: 'select', width: '150', verify: 'required', layFilter: 'selectAssetarProperty'},
			{id: 'specifications', title: '规格(单位)', formType: 'detail', width: '80'},
			{id: 'residualNum', title: '库存', formType: 'detail', width: '80'},
			{id: 'applyUseNum', title: '领用数量', formType: 'input', width: '80', verify: 'required|number', value: '1'},
			{id: 'remark', title: '备注', formType: 'input', width: '100'}
		],
		deleteRowCallback: function (trcusid) {
		},
		addRowCallback: function (trcusid) {
		},
		form: form,
		minData: 1
	});

	matchingLanguage();
	form.render();

	// 类型加载变化事件
	form.on('select(selectTypeProperty)', function(data) {
		var thisRowNum = data.elem.id.replace("typeId", "");
		var thisRowValue = data.value;
		if (!isNull(thisRowValue) && thisRowValue != '请选择') {
			if (judgeInPoingArr(assetArticles, 'id', thisRowValue)) {
				// 类型对应的用品存在js对象中
				var list = getInPoingArr(assetArticles, 'id', thisRowValue, 'list');
				resetAssetList(thisRowNum, list); //重置选择行的用品列表
			} else {
				// 类型对应的用品不存在js对象中
				AjaxPostUtil.request({url: flowableBasePath + "assetarticles018", params: {typeId: thisRowValue}, type: 'json', method: 'GET', callback: function (json) {
					assetArticles.push({
						id: thisRowValue,
						list: json.rows
					});
					resetAssetList(thisRowNum, json.rows);
				}});
			}
		}
	});

	// 根据类型重置用户列表
	function resetAssetList(thisRowNum, list) {
		var sHtml = getDataUseHandlebars(selOption, {
			rows: list
		});
		$("#articleId" + thisRowNum).html(sHtml);
		$("#specifications" + thisRowNum).html("");
		$("#residualNum" + thisRowNum).html("");
		form.render('select');
	}

	// 商品加载变化事件
	form.on('select(selectAssetarProperty)', function(data) {
		var thisRowNum = data.elem.id.replace("articleId", "");
		var thisRowValue = data.value;
		var thisRowTypeChooseId = $("#typeId" + thisRowNum).val();
		if (!isNull(thisRowValue) && thisRowValue != '请选择') {
			var list = getInPoingArr(assetArticles, 'id', thisRowTypeChooseId, 'list');
			$.each(list, function (i, item) {
				if (item.id === thisRowValue) {
					$("#specifications" + thisRowNum).html(item.specificationsName);
					$("#residualNum" + thisRowNum).html(item.residualNum);
					return false;
				}
			});
		} else {
			$("#specifications" + thisRowNum).html("");
			$("#residualNum" + thisRowNum).html("");
		}
	});

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
			activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId) {
		var result = initTableChooseUtil.getDataList('articlesList');
		if (!result.checkResult) {
			return false;
		}
		var noError = false;
		var tableData = [];
		$.each(result.dataList, function (i, item) {
			var rowNum = item["trcusid"].replace("tr", "");
			if (parseInt(item.applyUseNum) == 0) {
				$("#applyUseNum" + rowNum).addClass("layui-form-danger");
				$("#applyUseNum" + rowNum).focus();
				winui.window.msg('领用数量不能为0', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			if (parseInt(item.applyUseNum) > parseInt(item.residualNum)) {
				$("#applyUseNum" + rowNum).addClass("layui-form-danger");
				$("#applyUseNum" + rowNum).focus();
				winui.window.msg('领用数量不能超过库存数量', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			if (judgeInPoingArr(tableData, 'articleId', $("#assetarId" + rowNum).val())) {
				winui.window.msg('领用单存在相同的用品', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			tableData.push(item);
		});
		if (noError) {
			return false;
		}

		var params = {
			title: $("#title").html(),
			remark: $("#remark").val(),
			assetArticles: JSON.stringify(tableData),
			enclosureInfo: JSON.stringify({enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')}),
			formSubType: subType,
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "writeAssetArticlesApplyUse", params: params, type: 'json', method: "POST", callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});