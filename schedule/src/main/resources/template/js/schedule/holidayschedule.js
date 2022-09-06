
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
	
	authBtn('1552958799527');
	authBtn('1552958811619');
	authBtn('1552958778313');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.scheduleBasePath + 'syseveschedule008',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
	        { field: 'title', title: '标题', width: 180 },
	        { field: 'start', title: '开始日期', width: 150, align: 'center' },
	        { field: 'end', title: '结束日期', width: 150, align: 'center' },
	        { field: 'scheduleRemindTime', title: '提醒日期', width: 180 },
	        { field: 'userName', title: '录入人', width: 120 },
	        { field: 'scheduleCreateTime', title: systemLanguage["com.skyeye.entryTime"][languageType], width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 160, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        } else if (layEvent === 'cancleRemind') { // 取消提醒
        	cancleRemind(data);
        } else if (layEvent === 'addRemind') { //添加提醒
        	addRemind(data);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        }
    });
	
	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.scheduleBasePath + "syseveschedule011", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 取消提醒
	function cancleRemind(data) {
		var msg = '确认取消该节假日的提醒吗？';
		layer.confirm(msg, { icon: 3, title: '取消日程提醒' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.scheduleBasePath + "syseveschedule014", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("取消成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	// 添加提醒
	function addRemind(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schedule/setreminder.html", 
			title: "设置节假日提醒",
			pageId: "sysevewinedit",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 编辑节假日
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schedule/editschedule.html", 
			title: "编辑节假日",
			pageId: "editschedule",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	// 新增节假日
	$("body").on("click", "#addSchedule", function() {
    	_openNewWindows({
			url: "../../tpl/schedule/scheduleadd.html", 
			title: "新增节假日",
			pageId: "scheduleadd",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	// 清空本年度节假日日程
	$("body").on("click", "#deleteThisYear", function() {
		layer.confirm('确认清空本年度节假日日程吗？', { icon: 3, title: '删除节假日' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.scheduleBasePath + "syseveschedule012", params: {}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	});
	
	// 下载模板
	$("body").on("click", "#download", function() {
		postDownLoadFile({
			url : sysMainMation.scheduleBasePath + 'syseveschedule009',
			method : 'post'
		});
    });
	
	// 导入数据
	$("body").on("click", "#exploreExcel", function() {
		$("#upfile").val("");
		$("#upfile").click();
    });
	$("body").on("change", "#upfile", function() {
		var formData = new FormData();
	    var name = $("#upfile").val();
	    formData.append("file", $("#upfile")[0].files[0]);
	    formData.append("name", name);
	    $.ajax({
	        url : sysMainMation.scheduleBasePath + 'syseveschedule010',
	        type : 'POST',
	        async : false,
	        data : formData,
			headers: getRequestHeaders(),
	        // 告诉jQuery不要去处理发送的数据
	        processData : false,
	        // 告诉jQuery不要去设置Content-Type请求头
	        contentType : false,
	        dataType:"json",
	        beforeSend:function(){
	        	winui.window.msg("正在进行，请稍候", { shift: 1 });
	        },
	        success : function(json) {
	            if(json.returnCode == "0"){
	            	winui.window.msg("成功导入" + json.bean.uploadNum + "条日程。", { shift: 1 });
	                loadTable();
	            } else {
	            	winui.window.msg("导入失败", {icon: 2, time: 2000});
	            }
	        }
	    });
    });

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
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
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams() {
    	return {
    		scheduleTitle: $("#scheduleTitle").val()
    	};
	}
    
    exports('holidayschedule', {});
});
