var startTime = "";
var endTime = "";
var typeId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate,
		table = layui.table;
	
	//公告上线时间时间段表格
	laydate.render({
		elem: '#upTime', //指定元素
		range: '~'
	});
	
	function showList(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: sysMainMation.noticeBasePath + 'notice013',
		    where: {typeId: typeId, firstTime: startTime, lastTime: endTime, title:$("#titleName").val()},
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '主题', align: 'left', width: 400, templet: function (d) {
		        	return '<a lay-event="titleName" class="notice-title-click">' + d.title + '</a>';
		        }},
		        { field: 'typeName', title: '类别', align: 'left', width: 120 },
		        { field: 'linesTime', title: '发送时间', align: 'center', width: 150 },
		        { field: 'createName', title: '发送人', align: 'left', width: 90 }
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
    }
	
	//监听行单击事件
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'titleName') { //点击主题弹出详情
        	details(data);
        }
    });
	
	//公告详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysnoticefront/sysnoticefrontdetails.html", 
			title: "公告详情",
			pageId: "sysnoticefrontdetails",
			area: ['80vw', '80vh'],
			callBack: function(refreshCode){
			}});
	}
	
	//初始化左侧菜单数据
    showGrid({
	 	id: "setting",
	 	url: sysMainMation.noticeBasePath + "noticetype011",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/sysnoticefront/sysnoticefrontTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function (json) {
	 		//初始化所有上线列表数据
	 	    showList();
	 	}	
    });
    
	$("body").on("click", ".setting a", function (e) {
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		
		var clickName = $(this).attr("rowname");
		$("#title").text(clickName);
		typeId = $(this).attr("rowid");
		loadTable();
	});
	
	form.render();
	
	$("body").on("click", "#formSearch", function() {
		loadTable();
	});
	//上线时间搜索条件
    function loadTable(){
    	if(isNull($("#upTime").val())){
    		startTime = "";
    		endTime = "";
    	} else {
    		startTime = $("#upTime").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#upTime").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	table.reload("messageTable", {where:{typeId: typeId, firstTime: startTime, lastTime: endTime, title:$("#titleName").val()}});
    }
    exports('sysnoticefrontlist', {});
});
