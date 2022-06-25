
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
	
	authBtn('1567731484858');

	dsFormUtil.loadDsFormPageTypeByPId("firstTypeId", "0");
	form.on('select(firstTypeId)', function(data) {
		var thisRowValue = data.value;
		dsFormUtil.loadDsFormPageTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
		form.render('select');
	});

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
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'pageName', title: '页面名称', align: 'left', width: 120 },
			{ field: 'firstTypeName', title: '一级分类', align: 'left', width: 120 },
			{ field: 'secondTypeName', title: '二级分类', align: 'left', width: 120 },
	        { field: 'pageDesc', title: '页面简介', align: 'left', width: 350 },
	        { field: 'pageNum', title: '页面编号', align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'control') { // 表单设计
        	control(data);
        }else if(layEvent === 'delet'){ // 删除
        	delet(data);
        }else if(layEvent === 'edit'){ // 编辑
        	edit(data);
        }
    });

	// 添加
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/dsFormPage/pageAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "pageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });

	// 表单控件
	function control(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/dsFormPage/dsFormPageDesign.html",
			title: "表单设计",
			pageId: "dsFormPageDesign",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
			}});
	}

	// 删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "dsformpage005", params:{rowId: data.id}, type: 'json', method: "DELETE", callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}

	// 编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/dsFormPage/pageEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "pageEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	// 刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams(){
    	return {
			pageName: $("#pageName").val(),
			firstTypeId: $("#firstTypeId").val(),
			secondTypeId: $("#secondTypeId").val()
    	};
	}

    exports('pageList', {});
});
