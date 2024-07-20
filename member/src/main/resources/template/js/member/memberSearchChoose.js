var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'fsCommon'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
		
	// 设置提示信息
	var s = "会员选择规则：1.单选，双击指定行数据即可选中；2.只有启用状态的会员可以选择";
	s += '如没有查到要选择的会员，请检查会员信息是否满足当前规则。';
	$("#showInfo").html(s);

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: shopBasePath + 'member001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: [8, 16, 24, 32, 40, 48, 56],
		limit: 8,
		cols: [[
			{ type: 'radio', fixed: 'left'},
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
			{ field: 'name', title: '会员称呼', align: 'left', width: 140, fixed: 'left' },
			{ field: 'phone', title: '联系电话', align: 'center', width: 100 },
			{ field: 'email', title: '电子邮箱', align: 'left', width: 120 },
			{ field: 'address', title: '地址', align: 'left', width: 100 },
			{ field: 'enabled', title: '状态', align: 'center', width: 80, templet: function (d) {
					return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
			}},
		]],
		done: function(res, curr, count){
			matchingLanguage();

			for (var i = 0; i < res.rows.length; i++) {
				// 禁用的不可选中
				if(res.rows[i].enabled == 2){
					systemCommonUtil.disabledRow(res.rows[i].LAY_TABLE_INDEX, 'radio');
				}
			}

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if(!dubClick.find("input[type='radio']").prop("disabled")) {
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.sysMemberUtil.memberMation = obj;

					parent.refreshCode = '0';
					parent.layer.close(index);
				}
			});

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
				var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				if(!click.find("input[type='radio']").prop("disabled")) {
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				}
			})

			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入联系电话", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});
	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
	
    exports('memberSearchChoose', {});
});