
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table,
		form = layui.form;
	
	// 我启动的流程
	table.render({
	    id: 'messageMyStartTable',
	    elem: '#messageMyStartTable',
	    method: 'post',
	    url: flowableBasePath + 'activitimode013',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'processInstanceId', title: '流程ID', width: 280, templet: function (d) {
				return '<a lay-event="details" class="notice-title-click">' + getNotUndefinedVal(d.processInstanceId) + '</a>';
			}},
			{ field: 'taskType', title: '类型', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.title);
			}},
			{ field: 'createTime', title: '申请时间', align: 'center', width: 150, templet: function (d) {
				return getNotUndefinedVal(d.processMation?.createTime);
			}},
			{ field: 'assigneeList', title: '当前审批人', align: 'left', width: 150, templet: function (d) {
				if (!isNull(d.assigneeList)) {
					var str = "";
					$.each(d.assigneeList, function(i, item) {
						str += '<span class="layui-badge layui-bg-blue">' + item.name + '</span><br>';
					});
					return str;
				}
				return '';
			}},
	        { field: 'suspended', title: '状态<i id="stateDesc" class="fa fa-question-circle" style="margin-left: 5px"></i>', align: 'center', width: 130, templet: function (d) {
	        	if (d.suspended) {
	        		return "<span class='state-down'>挂起</span>";
	        	} else {
	        		return "<span class='state-up'>正常</span>";
	        	}
	        }},
	        { field: 'weatherEnd', title: '审批进度', align: 'left', width: 80, templet: function (d) {
				if (d.weatherEnd == 0) {
					return "<span class='state-down'>进行中</span>";
				} else {
					return "<span class='state-up'>已完成</span>";
				}
	        }},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#myStartTableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入流程ID", function () {
				table.reloadData("messageMyStartTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageMyStartTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
			activitiUtil.activitiDetails(data);
        } else if (layEvent === 'refreshPic') { //刷新流程图
        	refreshPic(data);
        }
    });
	
	// 刷新流程图
	function refreshPic(data) {
		layer.confirm('确认重新生成流程图吗？', { icon: 3, title: '刷新流程图操作' }, function (i) {
			layer.close(i);
            AjaxPostUtil.request({url: flowableBasePath + "activitimode027", params: {processInstanceId: data.processInstanceId}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
 	   		}});
		});
	}

	form.render();
	$("body").on("click", "#reloadMyStartTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageMyStartTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageMyStartTable"));
	}
    
    exports('initiatedProcess', {});
});
