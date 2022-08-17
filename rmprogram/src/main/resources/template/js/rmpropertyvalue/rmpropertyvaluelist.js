
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1560828440572');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.rmprogramBasePath + 'rmpropertyvalue001',
	    where: {title: $("#title").val(), propertyValue: $("#propertyValue").val(), propertyId: $("#propertyId").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '属性值别名', width: 180 },
	        { field: 'propertyValue', title: '属性值', width: 180 },
	        { field: 'propertyTitle', title: '所属标签', width: 180 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        } else if (layEvent === 'edit') { //编辑
        	edit(data);
        }
    });
	
	//属性标签
	showGrid({
	 	id: "propertyId",
	 	url: sysMainMation.rmprogramBasePath + "rmproperty006",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function (json) {
	 		form.render('select');
	 	}
	});

	form.render();
	form.on('submit(formSearch)', function (data) {
    	
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除标签属性值【' + obj.data.title + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除标签属性值' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmpropertyvalue003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑分类
	function edit(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/rmpropertyvalue/rmpropertyvalueedit.html", 
			title: "编辑标签属性值",
			pageId: "rmpropertyvalueedit",
			area: ['700px', '60vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/rmpropertyvalue/rmpropertyvalueadd.html", 
			title: "新增标签属性值",
			pageId: "rmpropertyvalueadd",
			area: ['700px', '60vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable() {
    	table.reloadData("messageTable", {where:{title: $("#title").val(), propertyValue: $("#propertyValue").val(), propertyId: $("#propertyId").val()}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{title: $("#title").val(), propertyValue: $("#propertyValue").val(), propertyId: $("#propertyId").val()}});
    }
    
    exports('rmpropertyvaluelist', {});
});
