
var rowId = "";

var parentId = "";

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
	
	parentId = parent.rowId;
	authBtn('1553755578433');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sysdevelopdoc011',
	    where:{title: $("#title").val(), parentId: parentId},
	    even:true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '文档标题', width: 400 },
	        { field: 'stateName', title: '状态', width: 120 },
	        { field: 'createTime', title: '创建时间', width: 180 },
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
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'up') { //上线
        	up(data, obj);
        }else if (layEvent === 'down') { //下线
        	down(data, obj);
        }else if (layEvent === 'upmove') { //上移
        	upmove(data);
        }else if (layEvent === 'downmove') { //下移
        	downmove(data);
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
            AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc015", params:{rowId: data.id}, type:'json', callback:function(json){
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
			url: "../../tpl/sysdevelopdoc/sysdevelopdocconsoleedit.html", 
			title: "编辑文档",
			pageId: "sysdocconsoleedit",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
	}
	
	//上线
	function up(data, obj){
		var msg = obj ? '确认上线文档【' + obj.data.title + '】吗？' : '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '文档上线' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc016", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("上线成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//下线
	function down(data, obj){
		var msg = obj ? '确认下线文档【' + obj.data.title + '】吗？' : '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '文档下线' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc017", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				winui.window.msg("下线成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
		});
	}
	
	//上移
	function upmove(data){
        AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc018", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	//下移
	function downmove(data){
        AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc019", params:{rowId: data.id}, type:'json', callback:function(json){
			if(json.returnCode == 0){
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
				loadTable();
			}else{
				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			}
		}});
	}
	
	//新增
	$("body").on("click", "#addBean", function(){
		_openNewWindows({
			url: "../../tpl/sysdevelopdoc/sysdevelopdocconsoleadd.html", 
			title: "新增文档",
			pageId: "sysdocconsoleadd",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{title: $("#title").val(), parentId: parentId}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{title: $("#title").val(), parentId: parentId}});
    }
    
    exports('sysdevelopdocconsolelist', {});
});
