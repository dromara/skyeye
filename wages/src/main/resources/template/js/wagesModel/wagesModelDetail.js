
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;
	var rowNum = 1; //表格的序号
	var usetableTemplate = $("#usetableTemplate").html();

	showGrid({
		id: "showForm",
		url: sysMainMation.wagesBasePath + "wagesmodel003",
		params: {rowId: parent.rowId},
		pagination: false,
		method: "GET",
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb) {},
		ajaxSendAfter:function (json) {
			laydate.render({elem: '#executeMonth', type: 'month', range: '~'});

			$('#company').html(getNameByList(json.bean.company).toString());
			$('#department').html(getNameByList(json.bean.departMent).toString());
			$('#userStaff').html(getNameByList(json.bean.userStaff).toString());

			// 加载列表项
			$.each(json.bean.modelField, function(i, item) {
				addRow();
				$("#fieldId" + (rowNum - 1)).html(item.nameCn + '(' + item.fieldKey + ')');
				$("#fieldType" + (rowNum - 1)).html(getFileType(item.fieldType));
				$("#defaultMoney" + (rowNum - 1)).html(item.defaultMoney);
				$("#formula" + (rowNum - 1)).html(item.formula);
				$("#remark" + (rowNum - 1)).html(item.remark);
			});

			matchingLanguage();
			form.render();
		}
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

	function getFileType(fieldType) {
		var str = "";
		if(fieldType == 1){
			str = "字段";
		} else if (fieldType == 2){
			str = "增加";
		} else if (fieldType == 3){
			str = "减少";
		} else if (fieldType == 4){
			str = "仅实发增加";
		} else if (fieldType == 5){
			str = "仅实发减少";
		} else if (fieldType == 6){
			str = "仅应发增加";
		} else if (fieldType == 7){
			str = "仅应发减少";
		}
		return str;
	}

	function getNameByList(array){
		var name = [];
		if(isNull(array)){
			return name;
		}
		$.each(array, function(i, item) {
			name.push(item.name)
		});
		return name;
	}

});