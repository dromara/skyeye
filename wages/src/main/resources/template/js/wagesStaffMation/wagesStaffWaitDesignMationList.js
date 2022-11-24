
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
	var selTemplate = getFileContent('tpl/template/select-option.tpl')

	// 待设定薪资员工列表
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.wagesBasePath + 'wagesstaff001',
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
	        { field: 'staffType', title: '类型', rowspan: '3', align: 'left', width: 90, templet: function (d) {
	        	if(d.staffType == 1){
	        		return '普通员工';
	        	} else if (d.staffType == 2){
	        		return '教职工';
	        	} else {
	        		return '参数错误';
	        	}
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
	        	if(d.userSex == '0'){
	        		return "保密";
	        	} else if (d.userSex == '1'){
	        		return "男";
	        	} else if (d.userSex == '2'){
	        		return "女";
	        	} else {
	        		return "参数错误";
	        	}
	        }},
	        { field: 'state', title: '状态', rowspan: '3', width: 60, align: 'center', templet: function (d) {
	        	return getStaffStateName(d);
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
	    done: function(json) {
	    	if(!loadCompany){
	    		initCompany();
	    	}
	    	soulTable.render(this);
    		matchingLanguage();
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

	var loadCompany = false;
	// 初始化公司
	function initCompany(){
		loadCompany = true;
		systemCommonUtil.getSysCompanyList(function (json) {
			// 加载企业数据
			$("#companyList").html(getDataUseHandlebars(selTemplate, json));
		});
	}

	// 初始化部门
	function initDepartment(){
		showGrid({
			id: "departmentList",
			url: reqBasePath + "companydepartment007",
			params: {companyId: $("#companyList").val()},
			pagination: false,
			method: 'POST',
			template: selTemplate,
			ajaxSendLoadBefore: function(hdb) {},
			ajaxSendAfter:function (json) {
				form.render('select');
			}
		});
	}

	function initJob(){
		// 根据部门id获取岗位集合
		systemCommonUtil.queryJobListByDepartmentId($("#departmentList").val(), function(data) {
			$("#jobList").html(getDataUseHandlebars(selTemplate, data));
			form.render('select');
		});
	}

	// 公司监听事件
	form.on('select(companyList)', function(data) {
		initDepartment();
		initJob();
	});

	// 部门监听事件
	form.on('select(departmentList)', function(data) {
		initJob();
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
			companyId: $("#companyList").val(),
			departmentId: $("#departmentList").val(),
			jobId: $("#jobList").val(),
			userName: $("#userName").val(),
			jobNumber: $("#jobNumber").val(),
			userSex: $("#userSex").val()
		};
    }
    
    exports('wagesStaffWaitDesignMationList', {});
});
