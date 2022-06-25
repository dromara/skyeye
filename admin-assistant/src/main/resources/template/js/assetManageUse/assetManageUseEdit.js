var assetList = new Array(); //资产集合

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
	var rowNum = 1; //表格的序号
	var typeHtml = "";

	var usetableTemplate = $("#usetableTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	var sTableData = "";

	AjaxPostUtil.request({url: flowableBasePath + "asset014", params: {rowId: parent.rowId}, type: 'json', callback: function(json) {
		if(json.returnCode == 0) {
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
		} else {
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});

	// 初始化资产类别
	function initTypeHtml() {
		AjaxPostUtil.request({url: flowableBasePath + "assettype006", params: {}, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				typeHtml = getDataUseHandlebars(selOption, json); //加载类别数据
				//渲染
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
							AjaxPostUtil.request({url: flowableBasePath + "asset011", params: {typeId: thisRowValue}, type: 'json', callback: function(json) {
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
						$("#assetNum" + thisRowNum).html(""); //重置库存为空
					}
				});
				//加载表格数据
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
			activitiUtil.startProcess(sysActivitiModel["assetManageUse"]["key"], function (approvalId) {
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
			if(inTableDataArrayByAssetarId($("#assetId" + rowNum).val(), tableData)){
				winui.window.msg('领用单存在相同的资产', {icon: 2, time: 2000});
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
			remark: $("#remark").val(),
			assetListStr: JSON.stringify(tableData),
			rowId: parent.rowId,
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		AjaxPostUtil.request({url: flowableBasePath + "asset015", params: params, type: 'json', callback: function(json) {
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

	//加载数据行行
	function addDataRow(item) {
		var thisRowNum = rowNum.toString();
		var par = {
			id: "row" + thisRowNum, //checkbox的id
			trId: "tr" + thisRowNum, //行的id
			typeId: "typeId" + thisRowNum, //类型id
			assetId: "assetId" + thisRowNum, //资产id
			specificationsName: "specificationsName" + thisRowNum, //规格id
			assetNum: "assetNum" + thisRowNum, //编号
			remark: "remark" + thisRowNum //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));

		//赋值给资产类别
		$("#" + "typeId" + thisRowNum).html(typeHtml);

		//数据回显
		$("#typeId" + thisRowNum).val(item.typeId);
		$("#specificationsName" + thisRowNum).html(item.specificationsName);
		$("#remark" + thisRowNum).val(item.remark);
		$("#assetNum" + thisRowNum).html(item.assetNum);
		var thisRowValue = item.typeId;
		if(!isNull(thisRowValue) && thisRowValue != '请选择') {
			if(inPointArray(thisRowValue, assetList)) {
				//类型对应的资产存在js对象中
				var list = getListPointArray(thisRowValue, assetList);
				//重置选择行的资产列表
				var sHtml = getDataUseHandlebars(selOption, {rows: list});
				$("#assetId" + thisRowNum).html(sHtml); //重置商品列表下拉框
				$("#assetId" + thisRowNum).val(item.assetId);
				form.render('select');
			} else {
				//类型对应的资产不存在js对象中
				AjaxPostUtil.request({url: flowableBasePath + "asset011", params: {typeId: thisRowValue}, type: 'json', callback: function(json) {
					if(json.returnCode == 0) {
						assetList.push({
							id: thisRowValue,
							list: json.rows
						});
						//重置选择行的资产列表
						var sHtml = getDataUseHandlebars(selOption, json);
						$("#assetId" + thisRowNum).html(sHtml); //重置商品列表下拉框
						$("#assetId" + thisRowNum).val(item.assetId);
						form.render('select');
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}, async: false});
			}
		}

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
			assetId: "assetId" + rowNum.toString(), //资产id
			specificationsName: "specificationsName" + rowNum.toString(), //规格id
			assetNum: "assetNum" + rowNum.toString(), //编号
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
		//赋值给资产类别
		$("#" + "typeId" + rowNum.toString()).html(typeHtml);
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

	//根据类型重置用户列表
	function resetAssetList(thisRowNum, list) {
		var sHtml = getDataUseHandlebars(selOption, {
			rows: list
		});
		$("#assetId" + thisRowNum).html(sHtml); //重置商品列表下拉框
		$("#specificationsName" + thisRowNum).html(""); //重置规格为空
		$("#assetNum" + thisRowNum).html(""); //重置库存为空
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