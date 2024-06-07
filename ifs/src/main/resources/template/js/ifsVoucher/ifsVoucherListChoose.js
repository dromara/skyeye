
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table;

	$("#showInfo").html("凭证选择规则：双击指定行即可选中。");
	
	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.ifsBasePath + 'ifsVoucher001',
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ type: 'radio'},
			{ field: 'name', title: '名称', width: 150 },
			{ field: 'path', title: '展示', width: 80, align: 'center', templet: function (d) {
				var fileExt = sysFileUtil.getFileExt(d.path);
				if($.inArray(fileExt[0], imageType) >= 0){
					return '<img src="' + fileBasePath + d.path + '" class="photo-img" lay-event="logo">';
				}
				return '<img src="../../assets/images/cloud/doc.png" class="photo-img" lay-event="logo">';
			}},
			{ field: 'state', title: '状态', width: 80, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("voucherState", 'id', d.state, 'name');
			}},
		]],
		done: function(res, curr, count){
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, res.searchFilter, form, "请输入名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});

			for (var i = 0; i < res.rows.length; i++) {
				// 已整理的不可选中
				if(res.rows[i].state != 1){
					systemCommonUtil.disabledRow(res.rows[i].LAY_TABLE_INDEX, 'radio');
				}
			}

			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var chooseIndex = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[chooseIndex];
				parent.sysIfsUtil.chooseVoucherMation = obj;

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
    
    exports('ifsVoucherListChoose', {});
});
