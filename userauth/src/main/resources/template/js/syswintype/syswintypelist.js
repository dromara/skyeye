
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'treeGrid', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		treeGrid = layui.treeGrid;
	
	authBtn('1552962225700');
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	treeGrid.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        idField: 'id',
        url: reqBasePath + 'sysevewintype001',
        cellMinWidth: 100,
        where: {typeName: $("#typeName").val(), state: $("#state").val()},
        treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'name',//以树形式显示的字段
        cols: [[
            { field:'name', width:300, title: '分类名称'},
            { field:'stateName', width:100, title: '状态'},
            { field:'winNum', width:100, title: '项目数量'},
            { field:'orderBy', width:100, title: '排序'},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
        ]],
        isPage:false,
	    done: function(){
	    	matchingLanguage();
	    }
    });
	
	treeGrid.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'upMove') { //上移
        	upMove(data);
        }else if (layEvent === 'downMove') { //下移
        	downMove(data);
        }else if (layEvent === 'upState') { //上线
        	upState(data, obj);
        }else if (layEvent === 'downState') { //下线
        	downState(data, obj);
        }
    });
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/syswintype/syswintypeedit.html", 
			title: "编辑分类",
			pageId: "sysevewintypeedit",
			area: ['500px', '350px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "sysevewintype007", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//上移
	function upMove(data){
        AjaxPostUtil.request({url:reqBasePath + "sysevewintype008", params:{rowId: data.id}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//下移
	function downMove(data){
        AjaxPostUtil.request({url:reqBasePath + "sysevewintype009", params:{rowId: data.id}, type: 'json', callback: function(json){
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//上线
	function upState(data, obj){
		var msg = obj ? '确认上线该分类【' + obj.data.name + '】吗？' : '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '系统分类上线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "sysevewintype010", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg("上线成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//下线
	function downState(data, obj){
		var msg = obj ? '确认下线该分类【' + obj.data.name + '】吗？' : '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '系统分类下线操作' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url:reqBasePath + "sysevewintype011", params:{rowId: data.id}, type: 'json', callback: function(json){
    			if (json.returnCode == 0) {
    				winui.window.msg("下线成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//添加分类
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/syswintype/syswintypeadd.html", 
			title: "新增分类",
			pageId: "sysevewintypeadd",
			area: ['500px', '350px'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
	
	function loadTable(){
    	treeGrid.query("messageTable", {where:{typeName: $("#typeName").val(), state: $("#state").val()}});
    }
    
    exports('syswintypelist', {});
});
