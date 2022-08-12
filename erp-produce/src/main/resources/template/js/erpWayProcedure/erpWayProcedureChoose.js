
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
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'erpwayprocedure009',
	    where: getTablePatams(),
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	    	{ type: 'radio'},
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			{ field: 'wayNumber', title: '工艺编号', align: 'left', width: 100, templet: function (d) {
					return '<a lay-event="details" class="notice-title-click">' + d.wayNumber + '</a>';
				}},
			{ field: 'wayName', title: '工艺名称', align: 'left', width: 250},
	    ]],
	    done: function(res, curr, count){
	    	matchingLanguage();
	    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var id = JSON.stringify(dubClick.data('index'));
				// 工艺信息
				parent.wayProcedureMation = res.rows[id];
				parent.layer.close(index);
				parent.refreshCode = '0';
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
        if (layEvent === 'details'){ //详情
        	details(data);
        }
    });

	// 搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reloadData("messageTable", {page: {curr: 1}, where: getTablePatams()})
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTablePatams()});
    }

	function getTablePatams(){
		return {
			wayNumber: $("#wayNumber").val(),
			wayName: $("#wayName").val()
		};
	}

    exports('erpWayProcedureChoose', {});
});