
// 项目报销
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'form'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;

	var rowNum = 1; //表格的序号
	var costTypeList = "";//支出分类集合展示html

	var usetableTemplate = $("#usetableTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	laydate.render({elem: '#reimbursementTime', range: false, btns: ['now', 'confirm']});

	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data){
		$("#title").val('费用报销-' + data.bean.userName + '-' + getYMDFormatDate());
		$("#writePeople").html(data.bean.userName);
	});

	// 项目成本费用支出分类
	sysDictDataUtil.queryDictDataListByDictTypeCode(sysDictData["pmCostExpenseType"]["key"], function (data) {
		costTypeList = getDataUseHandlebars(selOption, data);
		addRow();
	});

	// 获取我参与的项目列表
	proUtil.queryMyProjectsList(function (data){
		$("#proId").html(getDataUseHandlebars(selOption, data));
		form.render('select');
	});

	// 获取当前登录用户所属企业的所有部门信息
	systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
		$("#departments").html(getDataUseHandlebars(selOption, data));
		form.render('select');
	});

	//价格变化
	$("body").on("input", ".priceInput", function() {
		calculatedTotalPrice();
	});

	//计算总价
	function calculatedTotalPrice(){
		var rowTr = $("#useTable tr");
		var allLoad = 0;
		$.each(rowTr, function(i, item) {
			//获取行坐标
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			//获取金额
			var price = parseFloat(isNull($("#price" + rowNum).val()) ? "0" : $("#price" + rowNum).val());
			//计算所有行的合计
			allLoad += price;
		});
		$("#thisRowLoad").html(allLoad);
	}

	skyeyeEnclosure.init('enclosureUpload');
	matchingLanguage();
	form.render();
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
			activitiUtil.startProcess(sysActivitiModel["proCostExpense"]["key"], function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	function saveData(subType, approvalId){
		// 获取数据
		var rowTr = $("#useTable tr");
		var tableData = new Array();
		$.each(rowTr, function(i, item) {
			//获取行编号
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			var row = {
				expenditureTypeId: $("#costType" + rowNum).val(),
				purposeContent: $("#purposeContent" + rowNum).val(),
				price: isNull($("#price" + rowNum).val()) ? "0" : $("#price" + rowNum).val(),
				opposUnit: $("#opposUnit" + rowNum).val(),
				experiencedPerson: $("#experiencedPerson" + rowNum).val()
			};
			tableData.push(row);
		});

		var params = {
			title: $("#title").val(),
			departmentId: $("#departments").val(),
			reimbursementTime: $("#reimbursementTime").val(),
			expensePurposeStr: JSON.stringify(tableData),
			proId: $("#proId").val(),
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 表单类型 1.保存草稿  2.提交审批
			approvalId: approvalId
		};
		AjaxPostUtil.request({url: flowableBasePath + "procostexpense002", params: params, type: 'json', callback: function(json) {
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
			trId: "tr" + rowNum.toString(), //行的id
			id: "row" + rowNum.toString(), //checkbox的id
			costType: "costType" + rowNum.toString(), //支出分类内容id
			purposeContent: "purposeContent" + rowNum.toString(), //用途说明id
			price: "price" + rowNum.toString(), //价格id
			opposUnit: "opposUnit" + rowNum.toString(), //对方单位id
			experiencedPerson: "experiencedPerson" + rowNum.toString() //经手人id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
		//赋值给支出分类
		$("#costType" + rowNum.toString()).html(costTypeList);
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
			calculatedTotalPrice();
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});