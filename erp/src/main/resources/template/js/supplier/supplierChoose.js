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
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'supplier009',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	    	{ type: 'radio'},
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'supplierName', title: '供应商名称', align: 'left', width: 140,templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.supplierName + '</a>';
            }},
            { field: 'contacts', title: '联系人', align: 'left', width: 100},
            { field: 'phonenum', title: '联系电话', align: 'center', width: 100},
            { field: 'email', title: '电子邮箱', align: 'left', width: 120},
            { field: 'telephone', title: '手机号码', align: 'center', width: 100},
            { field: 'fax', title: '传真', align: 'left', width: 100},
            { field: 'advanceIn', title: '预收款', align: 'left',width: 100},
            { field: 'beginNeedGet', title: '期初应收', align: 'left', width: 100},
            { field: 'beginNeedPay', title: '期初应付', align: 'left', width: 100},
            { field: 'allNeedGet', title: '累计应收', align: 'left', width: 100},
            { field: 'allNeedPay', title: '累计应付', align: 'left', width: 100},
            { field: 'taxRate', title: '税率(%)',  align: 'left',width: 100},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 140 }
	    ]],
	    done: function(res, curr, count){
	    	matchingLanguage();
	    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
				var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
				dubClick.find("input[type='radio']").prop("checked", true);
				form.render();
				var id = JSON.stringify(dubClick.data('index'));
				var obj = res.rows[id];
				parent.sysSupplierUtil.supplierMation = obj;
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
        if(layEvent == 'select'){ //详情
            selectSupplier(data)
        }
    });

	// 详情
	function selectSupplier(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/supplier/supplierinfo.html",
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "supplierinfo",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}
		});
	}
	
	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
	function getTableParams(){
		return {
			supplierName:$("#supplierName").val(),
            telephone: $("#telephone").val(),
            email: $("#email").val(),
            fax: $("#fax").val()
		};
	}
	
    exports('supplierChoose', {});
});