layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;
	
	// 选择的部门信息
	var departmentList = parent.departmentList;
	
	// 设置提示信息
	var s = "部门选择规则：1.多选；如没有查到要选择的部门，请检查部门信息是否满足当前规则。";
	$("#showInfo").html(s);

	initTable();
	function initTable(){
		// 初始化值
		var ids = [];
		$.each(departmentList, function(i, item) {
			ids.push(item.id);
		});
		tableCheckBoxUtil.setIds({
			gridId: 'messageTable',
			fieldName: 'id',
			ids: ids
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'id'
		});

		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'companydepartment008',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
				{ field: 'departmentName', title: '部门名称', width: 300 },
				{ field: 'id', title: '部门简介', width: 80, align: 'center', templet: function (d) {
					return '<i class="fa fa-fw fa-html5 cursor" lay-event="departmentDesc"></i>';
				}},
				{ field: 'jobNum', title: '职位数', width: 120 },
				{ field: 'userNum', title: '员工数', width: 120 }
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
	    		//设置选中
	    		tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'id'
				});
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
			if (layEvent === 'departmentDesc') { //部门简介
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
	}

	// 保存
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		if(selectedData.length == 0){
			winui.window.msg("请选择部门", {icon: 2, time: 2000});
			return false;
		}
		AjaxPostUtil.request({url: reqBasePath + "companydepartment009", params: {ids: selectedData.toString()}, type: 'json', callback: function (json) {
			parent.departmentList = [].concat(json.rows);
			parent.layer.close(index);
			parent.refreshCode = '0';
   		}});
	});

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });
	
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
			companyName: $("companyName").val(),
			departmentName: $("departmentName").val()
		};
	}
	
    exports('companyDepartmentChooseList', {});
});