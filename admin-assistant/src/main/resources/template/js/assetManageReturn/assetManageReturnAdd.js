
var assetList = new Array(); //资产集合

// 资产归还
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

	var returntableTemplate = $("#returntableTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data){
		$("#useTitle").html("资产归还申请单-" + getYMDFormatDate() + '-' + data.bean.userName);
		$("#useName").html(data.bean.userName);
	});
	initTypeHtml();

	// 初始化资产类别
	function initTypeHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "assettype006", params: {}, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				typeHtml = getDataUseHandlebars(selOption, json); //加载类别数据
				// 渲染
				form.render();
				//类型加载变化事件
				form.on('select(selectTypeProperty)', function(data) {
					var thisRowNum = data.elem.id.replace("typeId", "");
					var thisRowValue = data.value;
					if(!isNull(thisRowValue) && thisRowValue != '请选择') {
						if(inPointArray(thisRowValue, assetList)) {
							//类型对应的资产存在js对象中
							var list = getListPointArray(thisRowValue, assetList);
							resetAssetList(thisRowNum, list); //重置选择行的资产列表
						} else {
							//类型对应的资产不存在js对象中
							AjaxPostUtil.request({url: flowableBasePath + "asset026", params: {typeId: thisRowValue}, type: 'json', callback: function(json) {
								if(json.returnCode == 0) {
									assetList.push({
										id: thisRowValue,
										list: json.rows
									});
									resetAssetList(thisRowNum, json.rows); //重置选择行的资产列表
								} else {
									winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
								}
							}});
						}
					}
				});

				//商品加载变化事件
				form.on('select(selectAssetarProperty)', function(data) {
					var thisRowNum = data.elem.id.replace("assetId", "");
					var thisRowValue = data.value;
					var thisRowTypeChooseId = $("#typeId" + thisRowNum).val();
					if(!isNull(thisRowValue) && thisRowValue != '请选择') {
						var list = getListPointArray(thisRowTypeChooseId, assetList);
						$.each(list, function(i, item) {
							if(item.id === thisRowValue) {
								$("#specificationsName" + thisRowNum).html(item.specificationsName);
								$("#assetNum" + thisRowNum).html(item.assetNum);
								return false;
							}
						});
					} else {
						$("#specificationsName" + thisRowNum).html(""); //重置规格为空
						$("#assetNum" + thisRowNum).html(""); //重置编号为空
					}
				});
				//初始化一行数据
				addRow();
				matchingLanguage();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
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
			activitiUtil.startProcess(sysActivitiModel["assetManageReturn"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId){
		// 获取已选资产数据
		var rowTr = $("#returnTable tr");
		if(rowTr.length == 0) {
			winui.window.msg('请选择需要归还的资产~', {icon: 2, time: 2000});
			return false;
		}
		var tableData = new Array();
		var noError = false; //循环遍历表格数据时，是否有其他错误信息
		$.each(rowTr, function(i, item) {
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			if(inTableDataArrayByAssetarId($("#assetId" + rowNum).val(), tableData)){
				winui.window.msg('归还单存在相同的资产', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			var row = {
				typeId: $("#typeId" + rowNum).val(),
				assetId: $("#assetId" + rowNum).val(),
				remark: $("#remark" + rowNum).val()
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
		AjaxPostUtil.request({url: flowableBasePath + "asset027", params: params, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}

	//判断选中的资产是否也在数组中
	function inTableDataArrayByAssetarId(str, array) {
		var isIn = false;
		$.each(array, function(i, item) {
			if(item.assetId === str) {
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

	//新增行
	function addRow() {
		var par = {
			id: "row" + rowNum.toString(), //checkbox的id
			trId: "tr" + rowNum.toString(), //行的id
			typeId: "typeId" + rowNum.toString(), //类型id
			assetId: "assetId" + rowNum.toString(), //资产id
			specificationsName: "specificationsName" + rowNum.toString(), //规格id
			assetNum: "assetNum" + rowNum.toString(), //库存id
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#returnTable").append(getDataUseHandlebars(returntableTemplate, par));
		//赋值给资产类别
		$("#" + "typeId" + rowNum.toString()).html(typeHtml);
		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	//删除行
	function deleteRow() {
		var checkRow = $("#returnTable input[type='checkbox'][name='tableCheckRow']:checked");
		if(checkRow.length > 0) {
			$.each(checkRow, function(i, item) {
				$(item).parent().parent().remove();
			});
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
	}

	//根据类型重置资产列表
	function resetAssetList(thisRowNum, list) {
		var sHtml = getDataUseHandlebars(selOption, {
			rows: list
		});
		$("#assetId" + thisRowNum).html(sHtml); //重置商品列表下拉框
		$("#specificationsName" + thisRowNum).html(""); //重置规格为空
		$("#assetNum" + thisRowNum).html(""); //重置编号为空
		form.render('select');
	}

	//判断是否在数组中
	function inPointArray(str, array) {
		var isIn = false;
		$.each(array, function(i, item) {
			if(item.id === str) {
				isIn = true;
				return false;
			}
		});
		return isIn;
	}

	//获取指定key对应的集合
	function getListPointArray(str, array) {
		var isList = [];
		$.each(array, function(i, item) {
			if(item.id === str) {
				$.extend(true, isList, item.list);
				return false;
			}
		});
		return isList;
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});