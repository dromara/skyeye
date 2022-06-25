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
	$("#showInfo").html("会计科目选择规则：双击指定行即可选中。");

	var selTemplate = getFileContent('tpl/template/select-option.tpl');
	$("#type").html(getDataUseHandlebars(selTemplate, {rows: accountSubjectUtil.accountSubjectType}));
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: flowableBasePath + 'ifsaccountsubject001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio'},
			{ field: 'num', title: '编号', align: 'left', width: 180},
			{ field: 'name', title: '会计科目名称', align: 'left', width: 200, templet: function(d){
				return '<a lay-event="select" class="notice-title-click">' + d.name + '</a>';
			}},
			{ field: 'type', title: '类型', align: 'center', width: 120, templet: function(d){
				return getInPoingArr(accountSubjectUtil.accountSubjectType, "id", d.type, "name");
			}},
			{ field: 'amountDirection', title: '余额方向', align: 'center', width: 80, templet: function(d){
				return sysIfsUtil.getAmountDirectionById(d.amountDirection);
			}},
			{ field: 'remark', title: '备注', align: 'left', width: 200}
		]],
		done: function(res, curr, count){
			matchingLanguage();
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var chooseIndex = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[chooseIndex];
				parent.sysIfsUtil.chooseAccountSubjectMation = obj;

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
	});

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshTable();
		}
		return false;
	});
	
	$("body").on("click", "#reloadTable", function() {
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
			name: $("#name").val(),
			state: 1, // 已启用
			type: $("#type").val()
		};
	}
	
    exports('ifsAccountSubjectListChoose', {});
});