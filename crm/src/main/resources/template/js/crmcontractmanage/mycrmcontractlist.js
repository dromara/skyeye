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
	
	authBtn('1573888009852');//列表
	authBtn('1573888019541');//新增
	
	// 客户
	showGrid({
	 	id: "customer",
	 	url: flowableBasePath + "customer007",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(j){
	 		form.render('select');
	 	}
	});
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'mycrmcontract001',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '合同名称', align: 'left', width: 300, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'num', title: '合同编号', align: 'left', width: 120 },
	        { field: 'price', title: '合同金额（元）', align: 'left', width: 120 },
	        { field: 'signingTime', title: '签约日期', align: 'center', width: 100 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
	        { field: 'processInstanceId', title: '流程ID', align: 'center', width: 100, templet: function (d) {
	        	if(!isNull(d.processInstanceId)){
	        		return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
	        	} else {
	        		return "";
	        	}
	        }},
	        { field: 'stateName', title: '状态', width: 90, templet: function (d) {
	        	if(d.state == '0'){
	        		return "<span>" + d.stateName + "</span>";
	        	}else if(d.state == '1'){
	        		return "<span class='state-new'>" + d.stateName + "</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>" + d.stateName + "</span>";
	        	}else if(d.state == '11'){
	        		return "<span class='state-up'>" + d.stateName + "</span>";
	        	}else if(d.state == '12'){
	        		return "<span class='state-down'>" + d.stateName + "</span>";
	        	}else if(d.state == '3'){
	        		return "<span class='state-error'>" + d.stateName + "</span>";
	        	}else if(d.state == '4'){
	        		return "<span class='state-error'>" + d.stateName + "</span>";
	        	}else if(d.state == '5'){
	        		return "<span class='state-error'>" + d.stateName + "</span>";
	        	}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'details'){ //详情
        	details(data);
        }else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        }else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data);
        }else if (layEvent === 'del') { //删除
        	del(data);
        }else if (layEvent === 'perform') { //执行
        	perform(data);
        }else if (layEvent === 'close') { //关闭
        	close(data);
        }else if (layEvent === 'shelve') { //搁置
        	shelve(data);
        }else if (layEvent === 'recovery') { //恢复
        	recovery(data);
        }else if (layEvent === 'revoke') { //撤销
        	revoke(data);
        }
    });
	
	// 新增
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/crmcontractmanage/mycrmcontractadd.html", 
			title: "新增合同",
			pageId: "crmcontractadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmcontractmanage/mycrmcontractedit.html", 
			title: "编辑合同",
			pageId: "crmcontractedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmcontractmanage/mycrmcontractdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "crmcontractdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	// 提交审批
	function subApproval(data){
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["myCrmContract"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract009", params: params, type: 'json', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}
	
	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract015", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 执行
	function perform(data){
		var msg = '确认执行该合同吗？';
		layer.confirm(msg, { icon: 3, title: '执行申请提交' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract010", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 关闭
	function close(data){
		var msg = '确认关闭该合同吗？';
		layer.confirm(msg, { icon: 3, title: '关闭申请提交' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract011", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 搁置
	function shelve(data){
		var msg = '确认搁置该合同吗？';
		layer.confirm(msg, { icon: 3, title: '搁置申请提交' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract012", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 恢复
	function recovery(data){
		var msg = '确认恢复该合同吗？';
		layer.confirm(msg, { icon: 3, title: '恢复申请提交' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract013", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 撤销
	function revoke(data){
		var msg = '确认撤销该合同吗？';
		layer.confirm(msg, { icon: 3, title: '撤销申请提交' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "mycrmcontract016", params:{processInstanceId: data.processInstanceId}, type: 'json', callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable(){
		table.reload("messageTable", {where: getTableParams()});
	}

	function getTableParams(){
		return {
			title: $("#title").val(),
			customer: $("#customer").val(),
			state: $("#state").val(),
			createTime: $("#createTime").val()
		}
	}

    exports('mycrmcontractlist', {});
});