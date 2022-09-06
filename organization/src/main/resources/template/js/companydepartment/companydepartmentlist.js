
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	var selTemplate = getFileContent('tpl/template/select-option-must.tpl');
	
	authBtn('1552961771842');
	
	systemCommonUtil.getSysCompanyList(function (json) {
		// 加载企业数据
		$("#companyId").html(getDataUseHandlebars(selTemplate, json));
		// 初始化加载表格
		initLoatTable();
	});

	function initLoatTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'companydepartment001',
		    where: getTableParams(),
		    even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'departmentName', title: '部门名称', width: 180 },
		        { field: 'id', title: '部门简介', width: 80, align: 'center', templet: function (d) {
		        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="departmentDesc"></i>';
		        }},
				{ field: 'overtimeSettlementTypeName', title: '加班结算方式', width: 120 },
		        { field: 'jobNum', title: '职位数', width: 120 },
		        { field: 'userNum', title: '员工数', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: "center", width: 150 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
		    ]],
		    done: function(json) {
		    	matchingLanguage();
		    }
		});
	}

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { //删除
			del(data);
		} else if (layEvent === 'edit') { //编辑
			edit(data);
		} else if (layEvent === 'departmentDesc') { //部门简介
			layer.open({
				id: '部门简介',
				type: 1,
				title: '部门简介',
				shade: 0.3,
				area: ['90vw', '90vh'],
				content: data.departmentDesc
			});
		}
	});
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	// 删除
	function del(data) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "companydepartment003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/companydepartment/companydepartmentedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "companydepartmentedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
    // 新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/companydepartment/companydepartmentadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "companydepartmentadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });

	// 刷新数据
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams() {
    	return {
			departmentName: $("#departmentName").val(),
			companyId: $("#companyId").val(),
			overtimeSettlementType: $("#overtimeSettlementType").val()
		}
	}
    
    exports('companydepartmentlist', {});
});
