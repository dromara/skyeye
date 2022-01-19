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
	
	authBtn('1574130121952');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'proprojectdiscusstype001',
	    where: {typeName: $("#typeName").val(), state: $("#state").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'typeName', title: '类型名称', align: 'center', width: 120 },
	        { field: 'state', title: '状态', width: 100, align: 'center', templet: function(d){
	        	if(d.state == '1'){
	        		return "<span class='state-new'>新建</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>上线</span>";
	        	}else if(d.state == '3'){
	        		return "<span class='state-down'>下线</span>";
	        	}else{
	        		return "参数错误";
	        	}
	        }},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
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
        }else if (layEvent === 'delete'){ //删除
        	del(data);
        }else if (layEvent === 'up') { //上线
        	up(data);
        }else if (layEvent === 'down') { //下线
        	down(data);
        }
    });
	
	form.render();

	$("body").on("click", "#formSearch", function(){
		refreshTable();
	});
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: {typeName: $("#typeName").val(), state: $("#state").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: {typeName: $("#typeName").val(), state: $("#state").val()}});
    }

	//新增
	$("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/proprojectdiscusstype/proprojectdiscusstypeadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "proprojectdiscusstypeadd",
			area: ['40vw', '40vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/proprojectdiscusstype/proprojectdiscusstypeedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "proprojectdiscusstypeedit",
			area: ['40vw', '40vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "proprojectdiscusstype005", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//上线
	function up(data, obj){
		var msg = obj ? '确认将【' + obj.data.typeName + '】上线吗？' : '确认将选中数据上线吗？';
		layer.confirm(msg, {icon: 3, title: '上线操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "proprojectdiscusstype006", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("上线成功", {icon: 1, time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//下线
	function down(data, obj){
		var msg = obj ? '确认将【' + obj.data.typeName + '】下线吗？' : '确认将选中数据下线吗？';
		layer.confirm(msg, {icon: 3, title: '下线操作'}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "proprojectdiscusstype007", params: {rowId: data.id}, type: 'json', callback: function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("下线成功", {icon: 1, time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
    exports('proprojectdiscusstypelist', {});
});