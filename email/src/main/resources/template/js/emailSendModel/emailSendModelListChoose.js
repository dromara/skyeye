var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
		
	// 设置提示信息
	$("#showInfo").html("邮件模板选择规则：双击指定行即可选中。");
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: reqBasePath + 'emailsendmodel001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio'},
			{ field: 'title', title: '主题', width: 180, templet: function(d){
				return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
		]],
		done: function(res, curr, count){
			matchingLanguage();
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var chooseIndex = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[chooseIndex];
				parent.emailSendModel = obj;

				parent.refreshCode = '0';
				parent.layer.close(index);
			});

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				click.find("input[type='radio']").prop("checked", true);
				form.render();
			})
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'details') { //详情
			details(data);
		}
	});

	form.render();

	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/emailSendModel/emailSendModelDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "emailSendModelDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}

	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams(){
		return {
			title: $("#title").val()
		};
	}
	
    exports('emailSendModelListChoose', {});
});