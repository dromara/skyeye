var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		table = layui.table;
	
	// 新增会议室
	authBtn('1565139164446');
	
	// 会议室列表开始
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'conferenceroom001',
	    where: {roomName:$("#roomName").val(), state:$("#state").val()},
	    even: true,
	    page: true,
	    limits: getLimits(),
    	limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'roomName', title: '名称', align: 'left', width: 170, templet: function (d) {
	        	return '<a lay-event="details" class="notice-title-click">' + d.roomName + '</a>';
	        }},
	        { field: 'roomImg', title: '图片', align: 'center', width: 60, templet: function (d) {
	        	if(isNull(d.roomImg)){
	        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
	        	} else {
	        		return '<img src="' + fileBasePath + d.roomImg + '" class="photo-img" lay-event="roomImg">';
	        	}
	        }},
	        { field: 'roomNum', title: '会议室编号', align: 'center', width: 170 },
	        { field: 'state', title: '状态', width: 80, align: 'center', templet: function (d) {
	        	if(d.state == '1'){
	        		return "<span class='state-up'>正常</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-down'>维修</span>";
	        	}else if(d.state == '3'){
	        		return "<span class='state-down'>报废</span>";
	        	} else {
	        		return "参数错误";
	        	}
	        }},
	        { field: 'roomCapacity', title: '会议室容量', align: 'center', width: 100 },
	        { field: 'roomPosition', title: '会议室位置', align: 'center', width: 100 },
	        { field: 'company', title: '所属公司', align: 'center', width: 170 },
	        { field: 'roomAdmin', title: '管理人员', align: 'center', width: 100 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
		if (layEvent === 'repair') { // 修复
			repair(data);
		} else if (layEvent === 'roomImg') { // 图片预览
			systemCommonUtil.showPicImg(fileBasePath + data.roomImg);
		} else if (layEvent === 'details') { // 详情
			details(data);
		} else if (layEvent === 'scrap') { // 报废
			scrap(data);
		} else if (layEvent === 'normal') { // 恢复正常
			normal(data);
		} else if (layEvent === 'delet') { // 删除
			delet(data);
		} else if (layEvent === 'edit') {	// 编辑
			edit(data);
		}
    });
	
	form.render();
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/conFerenceRoom/conFerenceRoomDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "conFerenceRoomDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
			}});
	}
	
	// 维修
	function repair(data){
		var msg = '确认维修该会议室吗？';
		layer.confirm(msg, { icon: 3, title: '维修会议室' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "conferenceroom005", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 报废
	function scrap(data){
		var msg = '确认报废该会议室吗？';
		layer.confirm(msg, { icon: 3, title: '报废会议室' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "conferenceroom006", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 恢复正常
	function normal(data){
		var msg = '确认对该会议室恢复正常吗？';
		layer.confirm(msg, { icon: 3, title: '恢复正常操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "conferenceroom004", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "conferenceroom003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	// 新增会议室
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/conFerenceRoom/conFerenceRoomAdd.html", 
			title: "新增会议室",
			pageId: "conFerenceRoomAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/conFerenceRoom/conFerenceRoomEdit.html", 
			title: "编辑会议室",
			pageId: "conFerenceRoomEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 搜索表单
	$("body").on("click", "#formSearch", function() {
		refreshTable();
	});
	
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		roomName:$("#roomName").val(),
    		state:$("#state").val()
    	};
    }
    
    exports('conFerenceRoomList', {});
});
