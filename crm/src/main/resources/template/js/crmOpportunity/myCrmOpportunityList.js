
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

	authBtn('1572936662194');

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'opportunity010',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'title', title: '商机全称', width: 200, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'processInstanceId', title: '流程ID', align: 'center', width: 80 , templet: function (d) {
	        	return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
	        }},
	        { field: 'state', title: '商机阶段', width: 85, templet: function (d) {
	        	if (d.state == '0'){
	        		return "草稿";
	        	} else if (d.state == '1'){
	        		return "<span class='state-new'>审核中</span>";
	        	} else if (d.state == '2'){
	        		return "<span class='state-new'>初期沟通</span>";
	        	} else if (d.state == '3'){
	        		return "<span class='state-new'>方案与报价</span>";
	        	} else if (d.state == '4'){
	        		return "<span class='state-new'>竞争与投标</span>";
	        	} else if (d.state == '5'){
	        		return "<span class='state-new'>商务谈判</span>";
	        	} else if (d.state == '6'){
	        		return "<span class='state-up'>成交</span>";
	        	} else if (d.state == '7'){
	        		return "<span class='state-down'>丢单</span>";
	        	} else if (d.state == '8'){
	        		return "<span class='state-down'>搁置</span>";
	        	} else if (d.state == '11'){
	        		return "<span class='state-up'>审核通过</span>";
	        	} else if (d.state == '12'){
	        		return "<span class='state-down'>审核不通过</span>";
	        	} 
	        }},
	        { field: 'estimatePrice', title: '预计成交金额', width: 120 },
	        { field: 'responsId', title: '负责人', width: 80 },
	        { field: 'partId', title: '参与人', width: 120 },
	        { field: 'followId', title: '关注人', width: 120 },
	        { field: 'createId', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { field: 'documentaryTime', title: '最后跟单时间', align: 'center', width: 130 },
	        { field: 'discussNum', title: '讨论版', align: 'center', width: 70, templet: function (d) {
	        	return '<a lay-event="discussDetails" class="notice-title-click">' + d.discussNum + '</a>';
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 230, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});

	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'delete'){ //删除
        	del(data, obj);
        } else if (layEvent === 'discussDetails'){ //讨论版数详情
        	discussDetails(data);
        } else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data);
        } else if (layEvent === 'processDetails') {//流程详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'stateChange') {//审核通过后的状态变更
        	stateChange(data);
        } else if (layEvent === 'revoke') {//撤销商机审批申请
        	revoke(data);
        }
    });

	// 撤销商机审批申请
	function revoke(data) {
		layer.confirm('确认撤销该商机审批申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "opportunity027", params: {rowId: data.id, processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/crmOpportunity/crmopportunityadd.html",
			title: "新增商机",
			pageId: "crmopportunityadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 商机提交审批
	function subApproval(data) {
		layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
			layer.close(index);
			activitiUtil.startProcess(sysActivitiModel["crmOpportUnity"]["key"], function (approvalId) {
				var params = {
					rowId: data.id,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "opportunity017", params: params, type: 'json', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000});
					loadTable();
				}});
			});
		});
	}
	
	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmOpportunity/crmopportunitydetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "crmopportunitydetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}
	
	// 讨论版数详情
	function discussDetails(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmdiscuss/discussnumlist.html", 
			title: data.title + "-讨论版",
			pageId: "discussDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	}

	// 编辑
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/crmOpportunity/crmopportunityedit.html",
			title: "编辑商机",
			pageId: "crmopportunityedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 状态变更
	function stateChange(data) {
		stateChangeData = data;
		_openNewWindows({
			url: "../../tpl/crmOpportunity/stateChange.html",
			title: "状态变更",
			pageId: "stateChange",
			area: ['80vw', '50vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "opportunity013", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
    	return {
    		myRole: $("#myRole").val(),
    		documentaryState: $("#documentaryState").val(),
    		createTime: $("#createTime").val(),
    		state: $("#state").val()
    	};
    }

    exports('myCrmOpportunityList', {});
});
