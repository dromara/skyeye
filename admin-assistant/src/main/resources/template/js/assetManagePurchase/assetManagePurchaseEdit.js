
var assetList = new Array(); //资产集合

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
	var rowNum = 1; //表格的序号
	var typeHtml = "";

	var usetableTemplate = $("#usetableTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	var sTableData = "";

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
		sTableData = json.bean.goods;
		initTypeHtml();
		matchingLanguage();
	}});

	//初始化资产类别
	function initTypeHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "assettype006", params: {}, type: 'json', callback: function(json) {
			initFromHtml();
			typeHtml = getDataUseHandlebars(selOption, json); //加载类别数据
			//渲染
			form.render();
			//加载表格数据
			initTableAssetList();
		}});
	}

	//初始化资产来源
	function initFromHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "assetfrom006", params: {}, type: 'json', async:false, callback: function(json) {
			fromHtml = getDataUseHandlebars(selOption, json); //加载资产来源数据
			form.render();
		}});
	}

	//加载表格数据
	function initTableAssetList() {
		$.each(sTableData, function(i, item) {
			addDataRow(item);
		});
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
			activitiUtil.startProcess(sysActivitiModel["assetManagePurchase"]["key"], function (approvalId) {
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

	function saveData(subType, approvalId){
		// 获取已选资产数据
		var rowTr = $("#useTable tr");
		if(rowTr.length == 0) {
			winui.window.msg('请选择需要领用的资产~', {icon: 2, time: 2000});
			return false;
		}
		var tableData = new Array();
		var noError = false; //循环遍历表格数据时，是否有其他错误信息
		$.each(rowTr, function(i, item) {
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			var assetNum = parseInt($("#assetNum" + rowNum).html());
			if(isNull($("#managementImg" + rowNum).find("input[type='hidden'][name='upload']").attr("oldurl"))){
				winui.window.msg('资产图片不能为空！', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			if(inTableDataArrayByAssetarId($("#managementName" + rowNum).val(), $("#assetNum" + rowNum).val(), tableData)){
				winui.window.msg('采购单存在相同的资产', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			var row = {
				typeId: $("#typeId" + rowNum).val(),
				managementName: $("#managementName" + rowNum).val(),
				assetNum: $("#assetNum" + rowNum).val(),
				fromId: $("#fromId" + rowNum).val(),
				unitPrice: $("#unitPrice" + rowNum).val(),
				remark: $("#remark" + rowNum).val(),
				managementImg: $("#managementImg" + rowNum).find("input[type='hidden'][name='upload']").attr("oldurl")
			};
			tableData.push(row);
		});
		if(noError) {
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
		AjaxPostUtil.request({url: flowableBasePath + "asset024", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	//判断选中的资产是否也在数组中
	function inTableDataArrayByAssetarId(str1, str2, array) {
		var isIn = false;
		$.each(array, function(i, item) {
			if(item.managementName === str1 && item.assetNum === str2) {
				isIn = true;
				return false;
			}
		});
		return isIn;
	}

	//新增行
	$("body").on("click", "#addRow", function() {
		addRow();
	});

	//删除行
	$("body").on("click", "#deleteRow", function() {
		deleteRow();
	});

	//加载数据行
	function addDataRow(item) {
		var thisRowNum = rowNum.toString();
		var par = {
			id: "row" + thisRowNum, //checkbox的id
			trId: "tr" + thisRowNum, //行的id
			typeId: "typeId" + thisRowNum, //类型id
			managementName: "managementName" + thisRowNum, //资产id
			managementImg: "managementImg" + thisRowNum, //图片
			assetNum: "assetNum" + thisRowNum, //编号
			fromId: "fromId" + thisRowNum, //来源
			unitPrice: "unitPrice" + thisRowNum, //单价
			remark: "remark" + thisRowNum //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));

		// 赋值给资产类别
		$("#" + "typeId" + thisRowNum).html(typeHtml);
		// 赋值给资产来源
		$("#" + "fromId" + thisRowNum).html(fromHtml);

		// 数据回显
		$("#typeId" + thisRowNum).val(item.typeId);
		$("#fromId" + thisRowNum).val(item.fromId);
		$("#managementName" + thisRowNum).val(item.managementName);
		$("#assetNum" + thisRowNum).val(item.assetNum);
		$("#unitPrice" + thisRowNum).val(item.unitPrice);
		$("#remark" + thisRowNum).val(item.remark);

		// 初始化上传
		$("#" + "managementImg" + thisRowNum).upload(systemCommonUtil.uploadCommon003Config("managementImg" + rowNum.toString(), 6, item.managementImg, 1));

		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	//新增行
	function addRow() {
		var par = {
			id: "row" + rowNum.toString(), //checkbox的id
			trId: "tr" + rowNum.toString(), //行的id
			typeId: "typeId" + rowNum.toString(), //类型id
			managementName: "managementName" + rowNum.toString(), //资产id
			managementImg: "managementImg" + rowNum.toString(), //图片
			assetNum: "assetNum" + rowNum.toString(), //编号
			fromId: "fromId" + rowNum.toString(), //来源
			unitPrice: "unitPrice" + rowNum.toString(), //单价
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
		// 赋值给资产类别
		$("#" + "typeId" + rowNum.toString()).html(typeHtml);
		// 赋值给资产来源
		$("#" + "fromId" + rowNum.toString()).html(fromHtml);

		// 初始化上传
		$("#" + "managementImg" + rowNum.toString()).upload(systemCommonUtil.uploadCommon003Config("managementImg" + rowNum.toString(), 6, '', 1));

		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	//删除行
	function deleteRow() {
		var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
		if(checkRow.length > 0) {
			$.each(checkRow, function(i, item) {
				$(item).parent().parent().remove();
			});
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});