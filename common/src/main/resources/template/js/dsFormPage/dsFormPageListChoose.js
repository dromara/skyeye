
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'fsTree'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		fsTree = layui.fsTree;
	var objectId = 'temp';

	fsTree.render({
		id: "treeDemo",
		url: reqBasePath + "queryServiceClassForTree",
		checkEnable: false,
		loadEnable: false,
		showLine: false,
		showIcon: true,
		addDiyDom: ztreeUtil.addDiyDom,
		clickCallback: onClickTree,
		onDblClick: onClickTree
	}, function(id) {
		fuzzySearch(id, '#name', null, true);
		ztreeUtil.initEventListener(id);
	});

	function onClickTree(event, treeId, treeNode) {
		if (treeNode.level != 3) {
			return false;
		}
		objectId = treeNode.id;
		loadTable();
	}

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'queryDsFormPageList',
	    where: getTableParams(),
		even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	    	{ type: 'radio', fixed: 'left' },
			{ field: 'name', title: '名称', align: 'left', width: 120 },
			{ field: 'remark', title: '简介', align: 'left', width: 200 },
			{ field: 'numCode', title: '页面编号', align: 'center', width: 150 },
			{ field: 'type', title: '类型', align: 'left', width: 120, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("dsFormPageType", 'id', d.type, 'name');
			}}
	    ]],
	    done: function(res, curr, count) {
			matchingLanguage();
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
	});
	
	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return {className: objectId};
	}
	
    exports('dsFormPageListChoose', {});
});