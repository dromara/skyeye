
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
	var s = "属性选择规则：";
	s += '1.单选，双击指定行数据即可选中；';
	s += '如没有查到要选择的属性，请检查属性信息是否满足当前规则。';
	$("#showInfo").html(s);

	initTable();
	function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reportBasePath + 'reportproperty007',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: 'radio'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
				{ field: 'title', title: '标题', align: 'left', width: 150, templet: function (d) {
					return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
				}},
				{ field: 'code', title: '属性', align: 'left', width: 150 },
				{ field: 'optional', title: '属性值是否可选', align: 'center', width: 150, templet: function (d) {
					if(d.optional == 1){
						return '可选';
					} else if(d.optional == 2){
						return '不可选';
					}
				}},
				{ field: 'defaultValue', title: '默认值', align: 'left', width: 140 },
	            { field: 'createName', title: '创建人', align: 'left', width: 100 },
	            { field: 'createTime', title: '创建时间', align: 'center', width: 140 },
	            { field: 'lastUpdateName', title: '最后修改人', align: 'left', width: 100 },
	            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 140}
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
				$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
					var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
					dubClick.find("input[type='radio']").prop("checked", true);
					form.render();
					var chooseIndex = JSON.stringify(dubClick.data('index'));
					var obj = res.rows[chooseIndex];
					parent.reportProperty = obj;

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
	}

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'details') { // 详情
			details(data);
		}
	});

	// 详情
	function details(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/reportProperty/reportPropertyDetails.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "reportPropertyDetails",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}
		});
	}

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
    
    function loadTable() {
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams() {
		return {
			title: $("#title").val(),
			attrCode: $("#attrCode").val()
		};
	}
	
    exports('reportPropertyChoose', {});
});