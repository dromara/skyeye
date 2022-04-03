var licenceReverts = new Array(); //证照集合

// 证照归还
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
	var nameHtml = "";

	var reverttableTemplate = $("#reverttableTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	var sTableData = ""

	AjaxPostUtil.request({url: flowableBasePath + "licencerevert004", params: {rowId: parent.rowId}, type: 'json', callback: function(json) {
		if(json.returnCode == 0) {
			$("#revertTitle").html(json.bean.title);
			$("#revertName").html(json.bean.userName);
			$("#remark").val(json.bean.remark);
			// 附件回显
			skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

			if(json.bean.state == '1'){
				$(".typeTwo").removeClass("layui-hide");
			}else{
				$(".typeOne").removeClass("layui-hide");
			}

			sTableData = json.bean.goods;
			initLicenceNameHtml();
		} else {
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});

	//初始化证照名称
	function initLicenceNameHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "licencerevert008", params: {}, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				nameHtml = getDataUseHandlebars(selOption, json); //加载名称数据
				matchingLanguage();
				form.render();
				// 加载表格数据
				initTableAssetList();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
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
			activitiUtil.startProcess(sysActivitiModel["licenceManageRevert"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	// 工作流中保存
	form.on('submit(save)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('3', "");
		}
		return false;
	});

	function saveData(subType, approvalId){
		// 获取已选证照数据
		var rowTr = $("#revertTable tr");
		if(rowTr.length == 0) {
			winui.window.msg('请选择需要归还的证照~', {icon: 2, time: 2000});
			return false;
		}
		var tableData = new Array();
		var noError = false; //循环遍历表格数据时，是否有其他错误信息
		$.each(rowTr, function(i, item) {
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			var row = {
				licenceId: $("#licenceId" + rowNum).val(),
				remark: $("#remark" + rowNum).val()
			};
			tableData.push(row);
		});
		if(noError) {
			return false;
		}

		var params = {
			remark: $("#remark").val(),
			licenceStr: JSON.stringify(tableData),
			rowId: parent.rowId,
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		AjaxPostUtil.request({url: flowableBasePath + "licencerevert005", params: params, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}

	//加载数据行
	function addDataRow(item) {
		var thisRowNum = rowNum.toString();
		var par = {
			id: "row" + thisRowNum, //checkbox的id
			trId: "tr" + thisRowNum, //行的id
			licenceId: "licenceId" + thisRowNum, //证照id
			remark: "remark" + thisRowNum //备注id
		};
		$("#revertTable").append(getDataUseHandlebars(reverttableTemplate, par));
		//赋值给证照名称
		$("#" + "licenceId" + thisRowNum).html(nameHtml);
		//数据回显
		$("#licenceId" + thisRowNum).val(item.licenceId);
		$("#remark" + thisRowNum).val(item.remark);
		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	//新增行
	$("body").on("click", "#addRow", function() {
		addRow();
	});

	//删除行
	$("body").on("click", "#deleteRow", function() {
		deleteRow();
	});

	//新增行
	function addRow() {
		var par = {
			id: "row" + rowNum.toString(), //checkbox的id
			trId: "tr" + rowNum.toString(), //行的id
			licenceId: "licenceId" + rowNum.toString(), //证照id
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#revertTable").append(getDataUseHandlebars(reverttableTemplate, par));
		//赋值给证照名称
		$("#" + "licenceId" + rowNum.toString()).html(nameHtml);
		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	//删除行
	function deleteRow() {
		var checkRow = $("#revertTable input[type='checkbox'][name='tableCheckRow']:checked");
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