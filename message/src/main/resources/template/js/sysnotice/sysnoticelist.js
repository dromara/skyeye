
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate', 'form', 'soulTable'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		soulTable = layui.soulTable,
		table = layui.table;
	
	//公告上线时间时间段表格
	laydate.render({
		elem: '#upTime',
		range: '~'
	});
	
	authBtn('1561973831412');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'notice001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
		limit: getLimit(),
	    overflow: {
            type: 'tips',
            hoverTime: 300, // 悬停时间，单位ms, 悬停 hoverTime 后才会显示，默认为 0
            minWidth: 150, // 最小宽度
            maxWidth: 500 // 最大宽度
        },
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '公告标题', align: 'left', width: 180, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
		    }},
	        { field: 'state', title: '状态', width: 80, align: 'center', templet: function (d) {
	        	if(d.state == '3'){
	        		return "<span class='state-down'>下线</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>上线</span>";
	        	}else if(d.state == '1'){
	        		return "<span class='state-new'>新建</span>";
	        	}
	        }},
	        { field: 'typeName', title: '一级分类', align: 'center', width: 120 },
	        { field: 'secondTypeName', title: '二级分类', align: 'center', width: 120 },
	        { field: 'whetherEmail', title: '邮件通知', width: 80, align: 'center', templet: function (d) {
	        	if(d.whetherEmail == '1'){
	        		return "否";
	        	}else if(d.whetherEmail == '2'){
	        		return "是";
	        	}
	        }},
	        { field: 'timeSend', title: '定时任务', width: 80, align: 'center', templet: function (d) {
	        	if(d.timeSend == '1'){
	        		return "-";
	        	}else if(d.timeSend == '2'){
	        		return d.delayedTime;
	        	}else if(d.timeSend == '3'){
	        		return "已失效";
	        	}else if(d.timeSend == '4'){
	        		return "已执行";
	        	}
	        }},
	        { field: 'sendType', title: '群发类型', width: 80, align: 'center', templet: function (d) {
	        	if(d.sendType == '1'){
	        		return "群发所有人";
	        	}else if(d.sendType == '2'){
	        		return "选择性群发";
	        	}
	        }},
	        { field: 'realLinesType', title: '上线类型', width: 80, align: 'center', templet: function (d) {
	        	if(d.realLinesType == '1'){
	        		return "手动上线";
	        	}else if(d.realLinesType == '2'){
	        		return "定时上线";
	        	} else {
	        		return "";
	        	}
	        }},
	        { field: 'realLinesTime', title: '上线时间', align: 'center', width: 150 },
	        { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 370, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    	soulTable.render(this);
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'delet') { //删除
        	delet(data);
        }else if (layEvent === 'up') { //上线
        	up(data);
        }else if (layEvent === 'down') { //下线
        	down(data);
        }else if (layEvent === 'upMove') { //上移
        	upMove(data);
        }else if (layEvent === 'downMove') { //下移
        	downMove(data);
        }else if (layEvent === 'timeup') { //定时上线
        	timeup(data);
        }else if (layEvent === 'details') { //公告详情
        	details(data);
        }
    });
	
	//添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysnotice/sysnoticeadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysnoticeadd",
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
	
	//定时上线
	function timeup(data){
		rowId = data.id;
		title = data.title;
		_openNewWindows({
			url: "../../tpl/sysnotice/sysnoticetimeup.html", 
			title: "定时上线",
			pageId: "sysnoticetimeup",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}
		});
	}
	
	//删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], { icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType] }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "notice003", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//上线
	function up(data){
		var msg = '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '上线公告' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "notice004", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg("上线成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//下线
	function down(data){
		var msg = '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '下线公告' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "notice005", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg("下线成功", {icon: 1, time: 2000});
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
			url: "../../tpl/sysnotice/sysnoticeedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysnoticeedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}
		});
	}
	
	//上移
	function upMove(data){
        AjaxPostUtil.request({url: reqBasePath + "notice008", params:{rowId: data.id}, type: 'json', callback: function (json) {
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//下移
	function downMove(data){
        AjaxPostUtil.request({url: reqBasePath + "notice009", params:{rowId: data.id}, type: 'json', callback: function (json) {
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//公告详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysnotice/sysnoticedetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "sysnoticedetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	form.render();

	$("body").on("click", "#formSearch", function() {
		loadTable();
	});

	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

    //上线时间搜索条件
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
		var startTime = "", endTime = "";
		if(!isNull($("#upTime").val())){
			startTime = $("#upTime").val().split('~')[0].trim() + ' 00:00:00';
			endTime = $("#upTime").val().split('~')[1].trim() + ' 23:59:59';
		}
		return {
			firstTime: startTime,
			lastTime: endTime,
			title:$("#title").val(),
			state: $("#state").val(),
			realLinesType: $("#realLinesType").val(),
			whetherEmail: $("#whetherEmail").val()
		};
	}
    
    exports('sysnoticelist', {});
});
