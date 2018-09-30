
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	//搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	initLoadTable();
	
	function initLoadTable(){
		//表格渲染
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'database001',
		    where:{tableName:$("#tableName").val(), tableDesc:$("#tableDesc").val()},
		    even:true,  //隔行变色
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: '序号', type: 'numbers'},
		        { field: 'tableName', title: '表名', width: 180 },
		        { field: 'tableComment', title: '表备注', width: 180 },
		        { field: 'columnName', title: '字段名', width: 180 },
		        { field: 'ordinalPosition', title: '字段顺序', width: 120 },
		        { field: 'columnType', title: '数据类型', width: 120 },
		        { field: 'columnDefault', title: '字段默认值', width: 120 },
		        { field: 'isNullable', title: '允许非空', width: 180 },
		        { field: 'extra', title: '其他选项', width: 180 },
		        { field: 'columnKey', title: '主键约束', width: 180 },
		        { field: 'columnComment', title: '备注', width: 180 }
		    ]]
		});
		
		showGrid({
		 	id: "tableName",
		 	url: reqBasePath + "database002",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		showGrid({
				 	id: "tableDesc",
				 	url: reqBasePath + "database003",
				 	params: {},
				 	pagination: false,
				 	template: getFileContent('tpl/template/select-option.tpl'),
				 	ajaxSendLoadBefore: function(hdb){
				 	},
				 	ajaxSendAfter:function(json){
				 		form.render('select');
				 	}
				});
		 	}
		});
		
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{tableName:$("#tableName").val(), tableDesc:$("#tableDesc").val()}});
    }
    
    exports('sysdatabaselist', {});
});
