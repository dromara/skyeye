
// 选择企业
var companyList = [];

// 选择部门
var departmentList = [];

// 选择员工，多选
var userStaffCheckType = true;
var checkStaffList = [];

// 当前选中的薪资字段
var fieldMation = {};

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;
	var rowNum = 1; //表格的序号
	var usetableTemplate = $("#usetableTemplate").html();
	var selTemplate = getFileContent('tpl/template/select-option.tpl');

	showGrid({
		id: "showForm",
		url: reqBasePath + "wagesmodel003",
		params: {rowId: parent.rowId},
		pagination: false,
		method: "GET",
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb){},
		ajaxSendAfter:function (json) {
			laydate.render({elem: '#executeMonth', type: 'month', range: '~'});

			showGrid({
				id: "typeId",
				url: reqBasePath + "wagesmodeltype008",
				params: {},
				pagination: false,
				template: selTemplate,
				method: 'GET',
				ajaxSendLoadBefore: function(hdb){},
				ajaxSendAfter:function(data){
					$("#typeId").val(json.bean.typeId);
					form.render('select');
				}
			});

			if(!isNull(json.bean.company)){
				companyList = json.bean.company;
			}
			$('#company').tagEditor({
				initialTags: getNameByList(companyList),
				placeholder: '请选择企业',
				editorTag: false,
				beforeTagDelete: function(field, editor, tags, val) {
					companyList = [].concat(arrayUtil.removeArrayPointName(companyList, val));
				}
			});

			if(!isNull(json.bean.departMent)){
				departmentList = json.bean.departMent;
			}
			$('#department').tagEditor({
				initialTags: getNameByList(departmentList),
				placeholder: '请选择部门',
				editorTag: false,
				beforeTagDelete: function(field, editor, tags, val) {
					departmentList = [].concat(arrayUtil.removeArrayPointName(departmentList, val));
				}
			});

			if(!isNull(json.bean.userStaff)){
				checkStaffList = json.bean.userStaff;
			}
			$('#userStaff').tagEditor({
				initialTags: getNameByList(checkStaffList),
				placeholder: '请选择员工',
				editorTag: false,
				beforeTagDelete: function(field, editor, tags, val) {
					checkStaffList = [].concat(arrayUtil.removeArrayPointName(checkStaffList, val));
				}
			});

			// 加载列表项
			$.each(json.bean.modelField, function(i, item){
				addRow();
				$("#fieldId" + (rowNum - 1)).val(item.nameCn + '(' + item.fieldKey + ')');
				$("#fieldId" + (rowNum - 1)).attr("rowKey", item.fieldKey);
				$("#fieldType" + (rowNum - 1)).val(item.fieldType);
				$("#defaultMoney" + (rowNum - 1)).val(item.defaultMoney);
				$("#formula" + (rowNum - 1)).val(item.formula);
				$("#remark" + (rowNum - 1)).val(item.remark);
			});

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					// 获取已选字段数据
					var rowTr = $("#useTable tr");
					if(rowTr.length == 0) {
						winui.window.msg('请选择字段.', {icon: 2, time: 2000});
						return false;
					}
					var tableData = new Array();
					$.each(rowTr, function(i, item) {
						// 获取行编号
						var rowNum = $(item).attr("trcusid").replace("tr", "");
						var row = {
							fieldKey: $("#fieldId" + rowNum).attr("rowKey"),
							fieldType: $("#fieldType" + rowNum).val(),
							defaultMoney: $("#defaultMoney" + rowNum).val(),
							formula: $("#formula" + rowNum).val(),
							sortNo: (i + 1),
							remark: $("#remark" + rowNum).val()
						};
						tableData.push(row);
					});
					var params = {
						rowId: parent.rowId,
						title: $("#title").val(),
						startMonth: $("#executeMonth").val().split('~')[0].trim(),
						endMonth: $("#executeMonth").val().split('~')[1].trim(),
						desc: $("#desc").val(),
						typeId: $("#typeId").val(),
						sortNo: $("#sortNo").val(),
						str: "",
						fieldStr: JSON.stringify(tableData)
					};
					// 公积金适用对象
					var object = [];
					$.each(companyList, function(i, item){
						object.push({
							objectId: item.id,
							objectType: 3
						});
					});
					$.each(departmentList, function(i, item){
						object.push({
							objectId: item.id,
							objectType: 2
						});
					});
					$.each(checkStaffList, function(i, item){
						object.push({
							objectId: item.id,
							objectType: 1
						});
					});
					params.str = JSON.stringify(object);
					AjaxPostUtil.request({url: reqBasePath + "wagesmodel004", params: params, type: 'json', method: "PUT", callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
		}
	});

	function getNameByList(array){
		var name = [];
		if(isNull(array)){
			return name;
		}
		$.each(array, function(i, item){
			name.push(item.name)
		});
		return name;
	}

	// 选择企业
	$("body").on("click", "#companySel", function() {
		_openNewWindows({
			url: "../../tpl/companymation/companyChooseList.html",
			title: "选择企业",
			pageId: "companyChooseList",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				// 重置数据
				companyList = [].concat(systemCommonUtil.tagEditorResetData('company', companyList));
			}});
	});

	// 选择部门
	$("body").on("click", "#departmentSel", function() {
		_openNewWindows({
			url: "../../tpl/companydepartment/companyDepartmentChooseList.html",
			title: "选择部门",
			pageId: "companyDepartmentChooseList",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				// 重置数据
				departmentList = [].concat(systemCommonUtil.tagEditorResetData('department', departmentList));
			}});
	});

	// 选择员工
	$("body").on("click", "#userStaffSel", function() {
		systemCommonUtil.userStaffCheckType = true; // 选择类型，默认单选，true:多选，false:单选
		systemCommonUtil.checkStaffMation = [].concat(checkStaffList); // 选择时返回的对象
		systemCommonUtil.openSysAllUserStaffChoosePage(function (checkStaffMation){
			// 重置数据
			checkStaffList = [].concat(systemCommonUtil.tagEditorResetData('userStaff', checkStaffMation));
		});
	});

	// 新增行
	$("body").on("click", "#addRow", function() {
		addRow();
	});

	// 删除行
	$("body").on("click", "#deleteRow", function() {
		deleteRow();
	});

	// 新增行
	function addRow() {
		var par = {
			id: "row" + rowNum.toString(), //checkbox的id
			trId: "tr" + rowNum.toString(), //行的id
			fieldId: "fieldId" + rowNum.toString(), //字段id
			fieldType: "fieldType"  + rowNum.toString(), //字段类型id
			defaultMoney: "defaultMoney" + rowNum.toString(), //默认金额id
			formula: "formula" + rowNum.toString(), //公式id
			remark: "remark" + rowNum.toString() //备注id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	// 删除行
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

	// 字段选择
	$("body").on("click", ".chooseFieldBtn", function (e) {
		var trId = $(this).parent().parent().attr("trcusid");
		_openNewWindows({
			url: "../../tpl/wagesFieldType/wagesFieldTypeChoose.html",
			title: "选择薪资字段",
			pageId: "productlist",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				// 获取表格行号
				var thisRowNum = trId.replace("tr", "");
				// 表格名称赋值
				$("#fieldId" + thisRowNum.toString()).val(fieldMation.nameCn + '(' + fieldMation.key + ')');
				$("#fieldId" + thisRowNum.toString()).attr("rowKey", fieldMation.key);
			}});
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});