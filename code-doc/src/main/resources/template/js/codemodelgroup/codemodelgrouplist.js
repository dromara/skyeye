
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
	
	authBtn('1560216118308');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'codemodel001',
	    where:{groupName:$("#groupName").val(), groupNum:$("#groupNum").val()},
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'groupNum', title: '分组编号', width: 120 },
	        { field: 'groupName', title: '分组名称', width: 120 },
	        { field: 'id', title: '分组简介', width: 120, templet: function(d){
	        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="groupDesc"></i>';
	        }},
	        { field: 'modelNum', title: '模板数量', width: 120 },
	        { field: 'useNum', title: '调用次数', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 280, toolbar: '#tableBar'}
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
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'modelConcle') { //模板管理
        	modelConcle(data);
        }else if (layEvent === 'useModelGroup') { //使用模板
        	useModelGroup(data);
        }else if (layEvent === 'groupDesc') { //查看分组简介
        	layer.open({
	            id: '分组简介',
	            type: 1,
	            title: '分组简介',
	            shade: 0.3,
	            area: ['700px', '90vh'],
	            content: data.groupDesc
	        });
        }else if (layEvent === 'createHistory') {//生成历史
        	createHistory(data);
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
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "codemodel003", params:{rowId: data.id}, type:'json', callback:function(json){
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
			url: "../../tpl/codemodelgroup/codemodelgroupedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "codemodelgroupedit",
			area: ['700px', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//模板管理
	function modelConcle(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/codemodel/codemodellist.html", 
			title: "模板管理",
			maxmin: true,
			pageId: "codemodelgroupmodelconcle"
		});
	}
	
	//生成历史
	function createHistory(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/codemodelhistory/codemodelhistorylist.html", 
			title: "生成历史",
			maxmin: true,
			pageId: "codemodelhistorylist"
		});
	}
	
	//使用模板
	function useModelGroup(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/codemodelgroup/usemodelgroup.html", 
			title: "代码生成",
			maxmin: true,
			pageId: "usemodelgroup"
		});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/codemodelgroup/codemodelgroupadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "codemodelgroupadd",
			area: ['700px', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{groupName:$("#groupName").val(), groupNum:$("#groupNum").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{groupName:$("#groupName").val(), groupNum:$("#groupNum").val()}});
    }
    
    exports('codemodelgrouplist', {});
});
