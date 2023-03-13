
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
		
	$("#showInfo").html("会计科目选择规则：双击指定行即可选中。");

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.ifsBasePath + 'ifsaccountsubject001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio'},
			{ field: 'num', title: '编号', align: 'left', width: 180 },
			{ field: 'name', title: '名称', align: 'left', width: 200 },
			{ field: 'type', title: '类型', align: 'center', width: 120, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("accountSubjectType", 'id', d.type, 'name');
			}},
			{ field: 'amountDirection', title: '余额方向', align: 'center', width: 80, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("amountDirection", 'id', d.amountDirection, 'name');
			}},
			{ field: 'remark', title: '备注', align: 'left', width: 200 }
		]],
		done: function(res, curr, count){
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});

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

	form.render();
	$("body").on("click", "#reloadTable", function() {
		table.reloadData("messageTable", {where: getTableParams()});
    });
    
	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('ifsAccountSubjectListChoose', {});
});