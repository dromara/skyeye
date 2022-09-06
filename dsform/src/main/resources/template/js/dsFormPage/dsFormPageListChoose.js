
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

	if(parent.dsFormUtil.chooseType) {
		// 多选
		var ids = [];
		$.each(parent.dsFormUtil.dsFormChooseList, function (i, item) {
			ids.push(item.id);
		});
		tableCheckBoxUtil.setIds({
			gridId: 'messageTable',
			fieldName: 'farmId',
			ids: ids
		});

		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'id'
		});
	} else {
		// 单选
		$("#saveCheckBox").hide();
	}

	dsFormUtil.loadDsFormPageTypeByPId("firstTypeId", "0");

	form.on('select(firstTypeId)', function(data) {
		var thisRowValue = data.value;
		dsFormUtil.loadDsFormPageTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
		form.render('select');
	});

	var tableList = [];
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'dsformpage001',
	    where: getTableParams(),
		even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	    	{ type: parent.dsFormUtil.chooseType ? 'checkbox' : 'radio', fixed: 'left'},
			{ field: 'pageName', title: '页面名称', align: 'left', width: 120 },
			{ field: 'firstTypeName', title: '一级分类', align: 'left', width: 120 },
			{ field: 'secondTypeName', title: '二级分类', align: 'left', width: 120 },
			{ field: 'pageDesc', title: '页面简介', align: 'left', width: 350 },
			{ field: 'pageNum', title: '页面编号', align: 'center', width: 150 },
	    ]],
	    done: function(res, curr, count){
			matchingLanguage();
			$.each(res.rows, function (i, item) {
				if(isNull(getInPoingArr(tableList, "id", item.id, null))){
					tableList.push(item);
				}
			});

			if(parent.dsFormUtil.chooseType) {
				// 多选
				tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'id'
				});
			} else {
				// 单选
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.dsFormUtil.dsFormChooseMation = obj;

					parent.refreshCode = '0';
					parent.layer.close(index);
				});

				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
					var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					click.find("input[type='radio']").prop("checked", true);
					form.render();
				});
			}
	    }
	});
	
	// 保存
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		if(selectedData.length == 0){
			winui.window.msg("请选择表单", {icon: 2, time: 2000});
			return false;
		}
		var result = [];
		$.each(selectedData, function(i, item) {
			result.push(getInPoingArr(tableList, "id", item, ""));
		});

		parent.dsFormUtil.dsFormChooseList = [].concat(result);
		parent.layer.close(index);
		parent.refreshCode = '0';
	});

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});
	
	$("body").on("click", "#reloadTable", function() {
		table.reloadData("messageTable", {where: getTableParams()});
    });

	function getTableParams() {
		return {
			pageName: $("#pageName").val(),
			firstTypeId: $("#firstTypeId").val(),
			secondTypeId: $("#secondTypeId").val()
		};
	}
	
    exports('dsFormPageListChoose', {});
});