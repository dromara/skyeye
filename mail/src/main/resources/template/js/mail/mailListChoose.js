
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;

	tableCheckBoxUtil.init({
		gridId: 'messageTable',
		filterId: 'messageTable',
		fieldName: 'id'
	});

	var tableList = [];
	var seePageNumList = [];
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'maillist001',
	    where: getTableParams(),
		even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	    	{ type: 'checkbox', fixed: 'left'},
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'userName', title: '姓名', width: 100, templet: function (d) {
					return '<a lay-event="details" class="notice-title-click">' + d.userName + '</a>';
				}},
			{ field: 'phone', title: '手机', width: 100 },
			{ field: 'telePhone', title: '电话', width: 100 },
			{ field: 'email', title: '个人邮箱', width: 180 },
			{ field: 'unitName', title: '单位', width: 270 },
	    ]],
	    done: function(res, curr, count){
			matchingLanguage();
			if($.inArray(curr, seePageNumList) < 0){
				tableList = tableList.concat(res.rows);
				seePageNumList.push(curr);
			}

			tableCheckBoxUtil.checkedDefault({
				gridId: 'messageTable',
				fieldName: 'id'
			});
	    }
	});
	
	// 保存
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		var result = [];
		$.each(selectedData, function(i, item){
			result.push(getInPoingArr(tableList, "id", item, ""));
		});

		parent.mailUtil.mailChooseList = [].concat(result);
		parent.layer.close(index);
		parent.refreshCode = '0';
	});

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});
	
	$("body").on("click", "#reloadTable", function() {
		table.reload("messageTable", {where: getTableParams()});
    });

	function getTableParams(){
		return {
			category: $("#category").val(),
			userName: $("#userName").val(),
			phone: $("#phone").val(),
			email: $("#email").val()
		};
	}
	
    exports('mailListChoose', {});
});