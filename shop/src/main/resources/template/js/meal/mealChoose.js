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
	var s = "套餐选择规则：1.单选，双击指定行数据即可选中";
	s += '如没有查到要选择的套餐，请检查套餐信息是否满足当前规则。';
	$("#showInfo").html(s);

	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: shopBasePath + 'meal001',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: 'radio'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
				{ field: 'title', title: '套餐名称', align: 'left', width: 200, templet: function (d) {
						return '<a lay-event="select" class="notice-title-click">' + d.title + '</a>';
					}},
				{ field: 'logo', title: 'LOGO', align: 'center', width: 60, templet: function (d) {
						return '<img src="' + fileBasePath + d.logo + '" class="photo-img" lay-event="logo">';
					}},
				{ field: 'mealNum', title: '可使用次数', width: 120 },
				{ field: 'type', title: '套餐分类', width: 100, align: "center", templet: function (d) {
					if(d.type == 1){
						return "保养套餐";
					}
					return "-";
				}},
				{ field: 'price', title: '指导价', width: 100 },
				{ field: 'showPrice', title: '展示价', width: 100 },
				{ field: 'lowPrice', title: '底价', width: 100 }
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.mealMation = obj;

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
	        if (layEvent === 'select') { //详情
	        	details(data);
	        }
	    });
		
		form.render();
	}
	
	// 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/meal/mealInfo.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "mealInfo",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
			}
		});
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
		return {
			title: $("#title").val(),
			storeId: parent.storeId,
			state: 2
		};
	}
	
    exports('mealChoose', {});
});