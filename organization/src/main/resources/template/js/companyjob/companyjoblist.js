
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'treeGrid', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		treeGrid = layui.treeGrid;
	var selTemplate = getFileContent('tpl/template/select-option.tpl')
	
	authBtn('1552962689111');

	systemCommonUtil.getSysCompanyList(function (json) {
		// 加载企业数据
		$("#companyId").html(getDataUseHandlebars(selTemplate, json));
		initLoadTable();
	});

	//公司监听事件
	form.on('select(companyId)', function(data){
		if(isNull(data.value) || data.value === '请选择'){
			$("#departmentId").html("");
			form.render('select');
		} else {
			initDepartment();
		}
	});
	
	//初始化部门
	function initDepartment(){
		showGrid({
		 	id: "departmentId",
		 	url: reqBasePath + "companydepartment007",
		 	params: {companyId: $("#companyId").val()},
		 	pagination: false,
		 	template: selTemplate,
		 	ajaxSendLoadBefore: function(hdb){},
		 	ajaxSendAfter:function (json) {
		 		form.render('select');
		 	}
	    });
	}
	
	function initLoadTable(){
		treeGrid.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    idField: 'id',
		    url: reqBasePath + 'companyjob001',
		    where: getTableParams(),
		    treeId: 'id',//树形id字段名称
        	treeUpId: 'pId',//树形父id字段名称
        	treeShowName: 'jobName',//以树形式显示的字段
		    cols: [[
		        { field: 'jobName', title: '职位名称', width: 180 },
		        { field: 'id', title: '职位简介', width: 80, align: 'center', templet: function(d){
		        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="jobDesc"></i>';
		        }},
		        { field: 'userNum', title: '员工数', width: 100 },
		        { field: 'companyName', title: '所属公司', width: 150 },
		        { field: 'departmentName', title: '所属部门', width: 100 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 150 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
		    ]],
		    isPage:false,
		    done: function(){
		    	matchingLanguage();
		    }
		});
	}

	treeGrid.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { //删除
			del(data, obj);
		}else if (layEvent === 'edit') { //编辑
			edit(data);
		}else if (layEvent === 'jobDesc') { //职位简介
			layer.open({
				id: '职位简介',
				type: 1,
				title: '职位简介',
				shade: 0.3,
				area: ['90vw', '90vh'],
				content: data.jobDesc
			});
		}else if (layEvent === 'jobScore') { //岗位定级
			jobScore(data);
		}
	});
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "companyjob003", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/companyjob/companyjobedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "companyjobedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}

	// 岗位定级
	function jobScore(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/companyJobScore/companyJobScoreList.html",
			title: '岗位定级',
			pageId: "companyJobScoreList",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    // 新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/companyjob/companyjobadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "companyjobadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
    
    function loadTable(){
    	treeGrid.query("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	treeGrid.query("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		companyId: $("#companyId").val(),
			departmentId: $("#departmentId").val(),
			jobName: $("#jobName").val()
    	};
	}
    
    exports('companyjoblist', {});
});
