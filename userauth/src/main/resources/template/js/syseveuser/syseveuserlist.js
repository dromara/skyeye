
var companyId = "";
var departmentId = "";
var jobId = "";

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
	var selTemplate = getFileContent('tpl/template/select-option.tpl')
	
	authBtn('1552960199302');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sys001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'userCode', title: '账号', width: 120 },
	        { field: 'userName', title: '员工姓名', width: 150, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.jobNumber + ' ' + d.userName + '</a>';
	        }},
	        { field: 'email', title: '邮箱', width: 100 },
	        { field: 'sexName', title: '性别', width: 60, templet: function(d){
	        	if(d.sexName == '0'){
	        		return "保密";
	        	}else if(d.sexName == '1'){
	        		return "男";
	        	}else if(d.sexName == '2'){
	        		return "女";
	        	}else{
	        		return "参数错误";
	        	}
	        }},
	        { field: 'companyName', title: '所属公司', width: 150},
	        { field: 'departmentName', title: '所属部门', width: 120},
	        { field: 'jobName', title: '担任职位', width: 120},
	        { field: 'userLock', title: '是否锁定', align: 'center', width: 90, templet: '#checkboxTpl', unresize: true},
	        { field: 'roleName', title: '角色', width: 120},
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	if(!loadCompany){
	    		initCompany();
	    	}
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'unlock') { //解锁
        	unlock(data);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'bindRole') { //绑定角色
        	bindRole(data);
        }else if (layEvent === 'userPhoto') { //头像预览
        	layer.open({
        		type:1,
        		title:false,
        		closeBtn:0,
        		skin: 'demo-class',
        		shadeClose:true,
        		content:'<img src="' + fileBasePath + data.userPhoto + '" style="max-height:600px;max-width:100%;">',
        		scrollbar:false
            });
        }else if (layEvent === 'details') { //员工详情
        	details(data);
        }
    });
	
	var loadCompany = false;
	// 初始化公司
	function initCompany(){
		loadCompany = true;
		systemCommonUtil.getSysCompanyList(function(json){
			// 加载企业数据
			$("#companyList").html(getDataUseHandlebars(selTemplate, json));
		});
	}
	
	// 初始化部门
	function initDepartment(){
		showGrid({
		 	id: "departmentList",
		 	url: reqBasePath + "companydepartment007",
		 	params: {companyId: companyId},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
	    });
	}
	
	function initJob(){
		// 根据部门id获取岗位集合
		systemCommonUtil.queryJobListByDepartmentId(departmentId, function(data) {
			$("#jobList").html(getDataUseHandlebars(selTemplate, data));
			form.render('select');
		});
	}
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	// 公司监听事件
	form.on('select(companyList)', function(data){
		companyId = data.value;
		departmentId = '';
		jobId = '';
		initDepartment();
		initJob();
	});
	
	// 部门监听事件
	form.on('select(departmentList)', function(data){
		departmentId = data.value;
		jobId = '';
		initJob();
	});
	
	// 职位监听事件
	form.on('select(jobList)', function(data){
		jobId = data.value;
	});
	
	// 监听锁定操作
	form.on('checkbox(lockDemo)', function(obj) {
		if(obj.elem.checked){
			// 锁定
			lock(obj.value);
		}else{
			// 解锁
			unlock(obj.value);
		}
	});
	
	// 锁定
	function lock(id){
		AjaxPostUtil.request({url: reqBasePath + "sys002", params: {rowId: id}, type: 'json', method: "PUT", callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg("已成功锁定，该账号目前无法登录.", {icon: 1, time: 2000});
			}else{
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	// 解锁
	function unlock(id){
		AjaxPostUtil.request({url: reqBasePath + "sys003", params: {rowId: id}, type: 'json', method: "PUT", callback: function(json){
			if(json.returnCode == 0){
				winui.window.msg("账号恢复正常.", {icon: 1, time: 2000});
			}else{
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuser/sysEveUserPasswordEdit.html",
			title: "重置密码",
			pageId: "syseveuseredit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	// 绑定角色
	function bindRole(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syseveuser/syseveuserrolebind.html", 
			title: "绑定角色",
			pageId: "syseveuserrolebind",
			area: ['450px', '300px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	// 添加用户
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/syseveuser/syseveuseradd.html", 
			title: "新增用户",
			pageId: "syseveuseradd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
    
    // 员工详情
	function details(data){
		rowId = data.staffId;
		_openNewWindows({
			url: "../../tpl/syseveuserstaff/syseveuserstaffdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "syseveuserstaffdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		userCode:$("#userCode").val(),
    		userName:$("#userName").val(),
    		sexName:$("#userSex").val(),
    		companyName:companyId,
    		departmentName:departmentId,
    		jobName:jobId
    	};
    }
    
    exports('syseveuserlist', {});
});
