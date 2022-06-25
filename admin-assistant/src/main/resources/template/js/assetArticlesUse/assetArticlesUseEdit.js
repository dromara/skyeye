
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
	var rowNum = 1; //表格的序号
	var typeHtml = "";

	var usetableTemplate = $("#usetableTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	var sTableData = "";

	AjaxPostUtil.request({url: flowableBasePath + "assetarticles021", params: {rowId: parent.rowId}, type: 'json', method: "POST", callback: function(json) {
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

	//初始化用品类别
	function initTypeHtml() {
		// 获取已经上线的用品类别列表
		adminAssistantUtil.queryAssetArticlesTypeUpStateList(function (data){
			typeHtml = getDataUseHandlebars(selOption, data);
		});
		form.render();
		//类型加载变化事件
		form.on('select(selectTypeProperty)', function(data) {
			var thisRowNum = data.elem.id.replace("typeId", "");
			var thisRowValue = data.value;
			if(!isNull(thisRowValue) && thisRowValue != '请选择') {
				if(inPointArray(thisRowValue, assetArticles)) {
					//类型对应的用品存在js对象中
					var list = getListPointArray(thisRowValue, assetArticles);
					resetAssetList(thisRowNum, list); //重置选择行的用品列表
				} else {
					//类型对应的用品不存在js对象中
					AjaxPostUtil.request({url: flowableBasePath + "assetarticles018", params: {typeId: thisRowValue}, type: 'json', callback: function(json) {
						if(json.returnCode == 0) {
							assetArticles.push({
								id: thisRowValue,
								list: json.rows
							});
							resetAssetList(thisRowNum, json.rows); //重置选择行的用品列表
						} else {
							winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
						}
					}});
				}
			}
		});

		//商品加载变化事件
		form.on('select(selectAssetarProperty)', function(data) {
			var thisRowNum = data.elem.id.replace("assetarId", "");
			var thisRowValue = data.value;
			var thisRowTypeChooseId = $("#typeId" + thisRowNum).val();
			if(!isNull(thisRowValue) && thisRowValue != '请选择') {
				var list = getListPointArray(thisRowTypeChooseId, assetArticles);
				$.each(list, function(i, item) {
					if(item.id === thisRowValue) {
						$("#specificationsName" + thisRowNum).html(item.specificationsName);
						$("#residualNum" + thisRowNum).html(item.residualNum);
						return false;
					}
				});
			} else {
				$("#specificationsName" + thisRowNum).html(""); //重置规格为空
				$("#residualNum" + thisRowNum).html(""); //重置库存为空
			}
		});
		//加载表格数据
		initTableAssetList();
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
			activitiUtil.startProcess(sysActivitiModel["assetArticlesUse"]["key"], function (approvalId) {
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
		// 获取已选用品数据
		var rowTr = $("#useTable tr");
		if(rowTr.length == 0) {
			winui.window.msg('请选择需要领用的用品~', {icon: 2, time: 2000});
			return false;
		}
		var tableData = new Array();
		var noError = false; //循环遍历表格数据时，是否有其他错误信息
		$.each(rowTr, function(i, item) {
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			var residualNum = parseInt($("#residualNum" + rowNum).html());
			if(parseInt($("#useNum" + rowNum).val()) == 0) {
				$("#useNum" + rowNum).addClass("layui-form-danger");
				$("#useNum" + rowNum).focus();
				winui.window.msg('领用数量不能为0', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			if(parseInt($("#useNum" + rowNum).val()) > residualNum) {
				$("#useNum" + rowNum).addClass("layui-form-danger");
				$("#useNum" + rowNum).focus();
				winui.window.msg('领用数量不能超过库存数量', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			if(inTableDataArrayByAssetarId($("#assetarId" + rowNum).val(), tableData)){
				winui.window.msg('领用单存在相同的用品', {icon: 2, time: 2000});
				noError = true;
				return false;
			}
			var row = {
				typeId: $("#typeId" + rowNum).val(),
				assetarId: $("#assetarId" + rowNum).val(),
				useNum: $("#useNum" + rowNum).val(),
				remark: $("#remark" + rowNum).val()
			};
			tableData.push(row);
		});
		if(noError) {
			return false;
		}

		var params = {
			remark: $("#remark").val(),
			assetArticlesStr: JSON.stringify(tableData),
			rowId: parent.rowId,
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		AjaxPostUtil.request({url: flowableBasePath + "assetarticles022", params: params, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}

	// 判断选中的用品是否也在数组中
	function inTableDataArrayByAssetarId(str, array) {
		var isIn = false;
		$.each(array, function(i, item) {
			if(item.assetarId === str) {
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
			assetarId: "assetarId" + thisRowNum, //用品id
			specificationsName: "specificationsName" + thisRowNum, //规格id
			residualNum: "residualNum" + thisRowNum, //库存id
			useNum: "useNum" + thisRowNum, //领用数量id
			remark: "remark" + thisRowNum //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));

		//赋值给用品类别
		$("#" + "typeId" + thisRowNum).html(typeHtml);

		//数据回显
		$("#typeId" + thisRowNum).val(item.typeId);
		$("#specificationsName" + thisRowNum).html(item.specificationsName);
		$("#remark" + thisRowNum).val(item.remark);
		$("#residualNum" + thisRowNum).html(item.residualNum);
		$("#useNum" + thisRowNum).val(item.applyUseNum);
		var thisRowValue = item.typeId;
		if(!isNull(thisRowValue) && thisRowValue != '请选择') {
			if(inPointArray(thisRowValue, assetArticles)) {
				//类型对应的用品存在js对象中
				var list = getListPointArray(thisRowValue, assetArticles);
				//重置选择行的用品列表
				var sHtml = getDataUseHandlebars(selOption, {rows: list});
				$("#assetarId" + thisRowNum).html(sHtml); //重置商品列表下拉框
				$("#assetarId" + thisRowNum).val(item.articleId);
				form.render('select');
			} else {
				//类型对应的用品不存在js对象中
				AjaxPostUtil.request({url: flowableBasePath + "assetarticles018", params: {typeId: thisRowValue}, type: 'json', callback: function(json) {
					if(json.returnCode == 0) {
						assetArticles.push({
							id: thisRowValue,
							list: json.rows
						});
						//重置选择行的用品列表
						var sHtml = getDataUseHandlebars(selOption, json);
						$("#assetarId" + thisRowNum).html(sHtml); //重置商品列表下拉框
						$("#assetarId" + thisRowNum).val(item.articleId);
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
			assetarId: "assetarId" + rowNum.toString(), //用品id
			specificationsName: "specificationsName" + rowNum.toString(), //规格id
			residualNum: "residualNum" + rowNum.toString(), //库存id
			useNum: "useNum" + rowNum.toString(), //领用数量id
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
		//赋值给用品类别
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
		$("#assetarId" + thisRowNum).html(sHtml); //重置商品列表下拉框
		$("#specificationsName" + thisRowNum).html(""); //重置规格为空
		$("#residualNum" + thisRowNum).html(""); //重置库存为空
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