
var rowId = "";

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'table'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'profile011',
	    where: getTableParams(),
	    even: true,
	    page: true,
		imits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '名称', align: 'left', width: 250, templet: function(d){
	        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
	        }},
	        { field: 'projectName', title: '所属项目', align: 'left', width: 100},
	        { field: 'stateName', title: '状态', width: 90, templet: function(d){
	        	if(d.state == 0){
	        		return "<span>草稿</span>";
	        	}else if(d.state == 1){
	        		return "<span class='state-new'>审核中</span>";
	        	}else if(d.state == 11){
	        		return "<span class='state-up'>审核通过</span>";
	        	}else if(d.state == 12){
	        		return "<span class='state-down'>审核失败</span>";
	        	}else if(d.state == 2){
	        		return "<span class='state-down'>作废</span>";
	        	}else if(d.state == 3){
	        		return "<span class='state-error'>撤销</span>";
	        	}
	        }},
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120},
	        { field: 'updateTime', title: '更新时间', align: 'center', width: 140},
	        { field: 'processInstanceId', title: '流程ID', align: 'center', width: 100, templet: function(d){
	        	if(!isNull(d.processInstanceId)){
	        		return '<a lay-event="processDetails" class="notice-title-click">' + d.processInstanceId + '</a>';
	        	} else {
	        		return "";
	        	}
	        }}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details'){ //详情
        	details(data);
        }else if (layEvent === 'processDetails') { //流程详情
			activitiUtil.activitiDetails(data);
        }
    });

	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/profile/profiledetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "profiledetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });

    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
    		projectName: $("#proName").val(),
			title: $("#fileName").val(),
			state: $("#state").val()
    	};
	}

    exports('profilealllist', {});
});
