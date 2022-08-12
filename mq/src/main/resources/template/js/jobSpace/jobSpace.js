
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'table', 'cookie'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
    	element = layui.element;
    	
    var tabIndex = GetUrlParam("tabIndex");

    var tabTable = {
    	tab0: {load: true, initMethod: myOutputList}, // 我的输出
    	tab1: {load: false, initMethod: mySendList}, // 我的发送
    	tab2: {load: false, initMethod: myAccessList} // 我的获取
    };
	
	$(document).attr("title", sysMainMation.mationTitle);
	$(".sys-logo").html(sysMainMation.mationTitle);

	element.on('tab(jobDetail)', function(obj){
		var mation = tabTable["tab" + obj.index];
		if(!isNull(mation)){
			if(!mation.load){
				tabTable["tab" + obj.index].load = true;
				mation.initMethod();
			}
		}
	});

	// 获取当前登录员工信息
	systemCommonUtil.getSysCurrentLoginUserMation(function (data){
		var str = '<img alt="' + data.bean.userName + '" src="' + fileBasePath + data.bean.userPhoto + '"/>'
			+ '<font>' + data.bean.userName + '</font>'
			+ '<font id="consoleDesk">控制台</font>'
			+ '<font id="exitBtn">退出</font>';
		$("#operatorBtn").html(str);
	});
	// 默认加载我的输出
	myOutputList();

	// 我的输出列表
	function myOutputList(){
		table.render({
		    id: 'outputTable',
		    elem: '#outputTable',
		    method: 'post',
		    url: reqBasePath + 'jobmatemation001',
		    where: {bigType: 1},
		    even: true,
		    page: true,
		    limits: [15, 30, 45, 60, 100, 150],
		    limit: 15,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '标题', align: 'left', width: 250, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
		        }},
		        { field: 'jobTypeName', title: '任务类型', align: 'left', width: 120 },
		        { field: 'status', title: '状态', align: 'left', width: 100, templet: function (d) {
		        	if(d.status == '1'){
		        		return "等待处理";
		        	}else if(d.status == '2'){
		        		return "处理中";
		        	}else if(d.status == '3'){
		        		return "<span class='state-down'>执行失败</span>";
		        	}else if(d.status == '4'){
		        		return "<span class='state-up'>执行成功</span>";
		        	}else if(d.status == '5'){
		        		return "<span class='state-new'>部分完成</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'test', title: '备注', align: 'left', width: 200, templet: function (d) {
		        	if(d.status == '4'){
		        		var js = JSON.parse(d.responseBody);
		        		var point = js.filePath.lastIndexOf(".");
     					var type = js.filePath.substr(point);
		        		return '<a class="notice-title-click download" download="' + js.filePath + '" fileName="' + d.title + '.' + type + '">下载</a>';
		        	}
		        	return '';
		        }},
		        { field: 'userName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
		        { field: 'complateTime', title: '完成时间', align: 'center', width: 150 }
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(outputTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details'){ // 详情
	        	
	        }
	    });
		
		// 我的输出刷新表单
		$("body").on("click", "#refreshMyOutput", function() {
			table.reloadData("outputTable", {page: {curr: 1}, where: {bigType: 1}});
		});
		
		// 我的输出下载
		$("body").on("click", ".download", function() {
			var filePath = $(this).attr("download");
			var fileName = $(this).attr("fileName");
			fileDownloadByCloud(fileName, filePath);
		});
	}
	
	/**
	 * 文件下载
	 */
	function fileDownloadByCloud(fileName, fileAddress){
		const link = document.createElement('a');
        link.style.display = 'none';
        link.href = fileBasePath + fileAddress;
        link.setAttribute(
          'download',
          fileName
        );
        document.body.appendChild(link);
        link.click();
	}
	
	// 我的发送列表
	function mySendList(){
		table.render({
		    id: 'sendTable',
		    elem: '#sendTable',
		    method: 'post',
		    url: reqBasePath + 'jobmatemation001',
		    where: {bigType: 2},
		    even: true,
		    page: true,
		    limits: [15, 30, 45, 60, 100, 150],
		    limit: 15,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '标题', align: 'left', width: 250, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
		        }},
		        { field: 'jobTypeName', title: '任务类型', align: 'left', width: 120 },
		        { field: 'status', title: '状态', align: 'left', width: 100, templet: function (d) {
		        	if(d.status == '1'){
		        		return "等待处理";
		        	}else if(d.status == '2'){
		        		return "处理中";
		        	}else if(d.status == '3'){
		        		return "<span class='state-down'>执行失败</span>";
		        	}else if(d.status == '4'){
		        		return "<span class='state-up'>执行成功</span>";
		        	}else if(d.status == '5'){
		        		return "<span class='state-new'>部分完成</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'test', title: '备注', align: 'left', width: 200, templet: function (d) {
		        	return '';
		        }},
		        { field: 'userName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
		        { field: 'complateTime', title: '完成时间', align: 'center', width: 150 }
		    ]]
		});
		
		table.on('tool(sendTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details'){ // 详情
	        	
	        }
	    });
		
		// 我的发送刷新表单
		$("body").on("click", "#refreshMySend", function() {
			table.reloadData("sendTable", {page: {curr: 1}, where: {bigType: 2}});
		});
	}
	
	// 我的获取列表
	function myAccessList(){
		table.render({
		    id: 'accessTable',
		    elem: '#accessTable',
		    method: 'post',
		    url: reqBasePath + 'jobmatemation001',
		    where: {bigType: 3},
		    even: true,
		    page: true,
		    limits: [15, 30, 45, 60, 100, 150],
		    limit: 15,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '标题', align: 'left', width: 250, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
		        }},
		        { field: 'jobTypeName', title: '任务类型', align: 'left', width: 120 },
		        { field: 'status', title: '状态', align: 'left', width: 100, templet: function (d) {
		        	if(d.status == '1'){
		        		return "等待处理";
		        	}else if(d.status == '2'){
		        		return "处理中";
		        	}else if(d.status == '3'){
		        		return "<span class='state-down'>执行失败</span>";
		        	}else if(d.status == '4'){
		        		return "<span class='state-up'>执行成功</span>";
		        	}else if(d.status == '5'){
		        		return "<span class='state-new'>部分完成</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'test', title: '备注', align: 'left', width: 200, templet: function (d) {
		        	return '';
		        }},
		        { field: 'userName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
		        { field: 'complateTime', title: '完成时间', align: 'center', width: 150 }
		    ]]
		});
		
		table.on('tool(accessTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'details'){ // 详情
	        	
	        }
	    });
		
		// 我的获取刷新表单
		$("body").on("click", "#refreshMyAccess", function() {
			table.reloadData("accessTable", {page: {curr: 1}, where: {bigType: 3}});
		});
	}
	
	// 退出
    $("body").on("click", "#exitBtn", function() {
        winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index){
            var times = $("#exit-confim").parent().attr("times");
            var zIndex = $("#exit-confim").parent().css("z-index");
            $("#layui-layer-shade" + times).css({'z-index': zIndex});
        }}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "login003", params: {}, type: 'json', method: "POST", callback: function (json) {
				localStorage.setItem('userToken', "");
                location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/note/shareNote.html?id=" + rowId);
            }});
        });
    });
	
    // 控制台
    $("body").on("click", "#consoleDesk", function() {
        location.href = "../../tpl/index/index.html";
    });
	
});

