
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
	var fromHtml = "";

	var purchasetableTemplate = $("#purchasetableTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data){
		$("#useTitle").html("资产采购申请单-" + getYMDFormatDate() + '-' + data.bean.userName);
		$("#useName").html(data.bean.userName);
	});

	initTypeHtml();

	//初始化资产类别
	function initTypeHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "assettype006", params: {}, type: 'json', callback: function(json) {
			initFromHtml();
			typeHtml = getDataUseHandlebars(selOption, json); //加载类别数据
			//渲染
			form.render();
			addRow();
		}});
	}
	//初始化资产来源
	function initFromHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "assetfrom006", params: {}, type: 'json', async:false, callback: function(json) {
			fromHtml = getDataUseHandlebars(selOption, json); //加载资产来源数据
			matchingLanguage();
			form.render();
		}});
	}

	skyeyeEnclosure.init('enclosureUpload');
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
			activitiUtil.startProcess(sysActivitiModel["assetManagePurchase"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId){
		// 获取已选资产数据
		var rowTr = $("#purchaseTable tr");
		if(rowTr.length == 0) {
			winui.window.msg('请选择需要采购的资产~', {icon: 2, time: 2000});
			return false;
		}
		var tableData = new Array();
		var noError = false; //循环遍历表格数据时，是否有其他错误信息
		$.each(rowTr, function(i, item) {
			var rowNum = $(item).attr("trcusid").replace("tr", "");
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
			title: $("#useTitle").html(),
			remark: $("#remark").val(),
			assetListStr: JSON.stringify(tableData),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "asset019", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
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
			typeId: "typeId" + rowNum.toString(), //类型id
			managementName: "managementName" + rowNum.toString(), //资产id
			managementImg: "managementImg" + rowNum.toString(), //图片
			assetNum: "assetNum" + rowNum.toString(),//编号
			fromId: "fromId" + rowNum.toString(), //来源id
			unitPrice: "unitPrice" + rowNum.toString(), //单价
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#purchaseTable").append(getDataUseHandlebars(purchasetableTemplate, par));
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
		var checkRow = $("#purchaseTable input[type='checkbox'][name='tableCheckRow']:checked");
		if(checkRow.length > 0) {
			$.each(checkRow, function(i, item) {
				$(item).parent().parent().remove();
			});
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
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

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});