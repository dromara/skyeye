
var rowId = "";//用户提交的表单数据的id

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table,
		laydate = layui.laydate,
		form = layui.form;
	
	//'申请时间'页面的选取时间段表格
	laydate.render({
		elem: '#createTime', //指定元素
		range: '~'
	});
	
	// 我启动的流程
	table.render({
	    id: 'messageMyStartTable',
	    elem: '#messageMyStartTable',
	    method: 'post',
	    url: flowableBasePath + 'pagesequence001',
	    where: getTableParams(),
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '流程名称', width: 100 },
	        { field: 'typeTitle', title: '流程类型', width: 100 },
	        { field: 'createTime', title: '申请时间', align: 'center', width: 140},
	        { field: 'state', title: '状态', align: 'center', width: 80, templet: function (d) {
	        	if(d.state == 1){
	        		return "<span class='state-down'>草稿</span>";
	        	} else {
	        		return "<span class='state-up'>正常</span>";
	        	}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#myStartTableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageMyStartTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        } else if (layEvent === 'details') { //详情
        	details(data);
        } else if (layEvent === 'subApproval') { //提交审批
        	subApproval(data);
        } else if (layEvent === 'deleteRow') { //删除
        	deleteRow(data);
        }
    });
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: '../../tpl/dsFormPageSequence/dsFormPageSequenceDraftProcessEdit.html',
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "dsFormPageSequenceDraftProcessEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			}
		});
	}
	
	// 表单详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/dsFormPageSequence/dsFormPageSequenceDraftProcessDetail.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "dsFormPageSequenceDraftProcessDetail",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}
	
	// 提交审批
	function subApproval(data){
		layer.confirm('确认提交进行审批吗？', { icon: 3, title: '提交审批' }, function (i) {
			layer.close(i);
			activitiUtil.startProcess(data.pageId, function (approvalId) {
				var params = {
					rowId: data.id,
					pageId: data.pageId,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "pagesequence005", params: params, type: 'json', callback: function (json) {
					if (json.returnCode == 0) {
						winui.window.msg("申请提交成功，等待审核...", {icon: 1, time: 2000});
						reloadMyStartTable();
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		});
	}
	
	// 删除
	function deleteRow(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "pagesequence002", params: {rowId: data.id}, type: 'json', callback: function (json) {
 	   			if (json.returnCode == 0) {
                	winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                	reloadMyStartTable();
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
		});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			searchMyStartTable();
		}
		return false;
	});

    // 刷新表单草稿列表
	$("body").on("click", "#reloadMyStartTable", function() {
		reloadMyStartTable();
	});
	
    function reloadMyStartTable(){
    	table.reload("messageMyStartTable", {where: getTableParams()});
    }
    
    function searchMyStartTable(){
    	table.reload("messageMyStartTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams(){
		var startTime = "";
		var endTime = "";
		if(!isNull($("#createTime").val())){//一定要记得，当createTime为空时
			startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
			endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
		}
		return {
			startTime: startTime,
			endTime: endTime
		};
	}
    
    exports('dsFormPageSequenceDraftProcessList', {});
});
