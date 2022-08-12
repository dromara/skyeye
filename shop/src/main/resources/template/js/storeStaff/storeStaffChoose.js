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
	var s = "店员选择规则：1.单选，双击指定行数据即可选中";
	s += '如没有查到要选择的店员，请检查店员信息是否满足当前规则。';
	$("#showInfo").html(s);

	// 加载我所在的门店
	shopUtil.queryStaffBelongStoreList(function (json){
		$("#storeId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
	});

	form.on('select(storeId)', function(data) {
		table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
	});

	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: shopBasePath + 'storeStaff001',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: 'radio', fixed: 'left'},
				{ field: 'jobNumber', title: '工号', align: 'left', width: 140 },
				{ field: 'userName', title: '姓名', width: 120 },
				{ field: 'companyName', title: '企业', width: 150 },
				{ field: 'departmentName', title: '部门', width: 140 },
				{ field: 'jobName', title: '职位', width: 140 },
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.shopUtil.staffMation = obj;

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
	}
	
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});
	
	 $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
	function getTableParams(){
    	var storeId = "-";
    	if(!isNull($("#storeId").val())){
			storeId = $("#storeId").val();
		}
		return {
			userName: $("#userName").val(),
			storeId: storeId
		};
	}
	
    exports('storeStaffChoose', {});
});