
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', "form"], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sealseservice022',
	    where: {},
	    even: true,
	    page: false,
	    cols: [[
	        { type: 'radio'},
	        { field: 'orderNum', title: '工单号', align: 'center', width: 220},
	        { field: 'serviceTypeName', title: '服务类型', align: 'left', width: 100 },
	        { field: 'declarationTime', title: '报单时间', align: 'center', width: 140 },
	        { field: 'customerName', title: '客户名称', align: 'left', width: 120 },
	        { field: 'contacts', title: '联系人', align: 'left', width: 80 },
	        { field: 'phone', title: '联系电话', align: 'center', width: 100 }
	    ]],
	    done: function(res, curr, count){
	    	matchingLanguage();
			$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var id = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[id];
				parent.chooseOrderNum = obj.orderNum;
				parent.chooseOrderId = obj.id;
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
	
    exports('mytocompletedlist', {});
});