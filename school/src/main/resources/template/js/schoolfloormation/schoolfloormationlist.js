
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
	
	authBtn('1586146946646');
	
	//初始化学校
	showGrid({
	 	id: "schoolId",
	 	url: reqBasePath + "schoolmation008",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option-must.tpl'),
	 	ajaxSendLoadBefore: function(hdb){},
	 	ajaxSendAfter:function(json){
	 		initTable();
	 	}
    });
	
    function initTable(){
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'schoolfloormation001',
		    where: {name: $("#typeName").val(), schoolId: $("#schoolId").val()},
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'name', title: '名称', align: 'left', width: 160 },
		        { field: 'schoolName', title: '所属学校', align: 'left', width: 150 },
		        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 257, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'edit') { //编辑
	        	edit(data);
	        }else if (layEvent === 'delet') { //删除
	        	delet(data);
	        }
	    });
    }
	
	form.render();
	
	
	$("body").on("click", "#formSearch", function(){
		refreshTable();
	});
	
	//添加
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/schoolfloormation/schoolfloormationadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "schoolfloormationadd",
			area: ['60vw', '40vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	//删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "schoolfloormation005", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolfloormation/schoolfloormationedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "schoolfloormationedit",
			area: ['60vw', '40vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}
		});
	}
    
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{name:$("#typeName").val(), schoolId: $("#schoolId").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{name:$("#typeName").val(), schoolId: $("#schoolId").val()}});
    }
    
    exports('schoolfloormationlist', {});
});
