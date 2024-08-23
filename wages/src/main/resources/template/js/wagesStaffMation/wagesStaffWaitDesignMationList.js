
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable;

	// 待设定薪资员工列表
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.reqBasePath + 'querySysUserStaffList',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    overflow: {
            type: 'tips',
            header: true,
            total: true
        },
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '3', fixed: 'left', type: 'numbers' },
	        { field: 'userName', title: '姓名', rowspan: '3', align: 'left', width: 150, fixed: 'left', templet: function (d) {
	        	return d.jobNumber + '_' + d.userName;
	        }},
	        { field: 'type', title: '类型', rowspan: '3', align: 'left', width: 90, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("userStaffType", 'id', d.type, 'name');
	        }},
	        { field: 'email', title: '邮箱', rowspan: '3', align: 'left', width: 170 },
	        { field: 'userPhoto', title: '头像', rowspan: '3', align: 'center', width: 60, templet: function (d) {
	        	if(isNull(d.userPhoto)){
	        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
	        	} else {
	        		return '<img src="' + fileBasePath + d.userPhoto + '" class="photo-img" lay-event="userPhoto">';
	        	}
	        }},
	        { field: 'userSex', title: '性别', width: 60, rowspan: '3', align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("sexEnum", 'id', d.userSex, 'name');
			}},
	        { field: 'state', title: '状态', rowspan: '3', width: 60, align: 'center', templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("userStaffState", 'id', d.state, 'name');
	        }},
	        { title: '公司信息', align: 'center', colspan: '3'},
	        { field: 'phone', title: '手机号', rowspan: '3', align: 'center', width: 100},
	        { field: 'homePhone', title: '家庭电话', rowspan: '3', align: 'center', width: 100},
	        { field: 'qq', title: 'QQ', rowspan: '3', align: 'left', width: 100},
	        { title: systemLanguage["com.skyeye.operation"][languageType], rowspan: '3', fixed: 'right', align: 'center', width: 120, toolbar: '#tableBar'}
	    ],[
	    	{ field: 'companyName', title: '公司', align: 'left', width: 120},
	        { field: 'departmentName', title: '部门', align: 'left', width: 120},
	        { field: 'jobName', title: '职位', align: 'left', width: 120}
	       ]
	    ],
	    done: function(res) {
	    	soulTable.render(this);
    		matchingLanguage();

			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入姓名，工号", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'wagesDesign') { // 薪资设定
			wagesDesign(data);
		} else if (layEvent === 'userPhoto') { // 头像预览
			systemCommonUtil.showPicImg(fileBasePath + data.userPhoto);
		}
    });

	// 薪资设定
	function wagesDesign(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/wagesStaffMation/wagesStaffMationDesign.html",
			title: "薪资设定",
			pageId: "wagesStaffMationDesign",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {designWages: 1}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('wagesStaffWaitDesignMationList', {});
});
