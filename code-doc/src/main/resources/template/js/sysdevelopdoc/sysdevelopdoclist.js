
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		tableTree = layui.tableTreeDj;
	
	authBtn('1553740371701');

	tableTree.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'sysdevelopdoc001',
        where: {typeName: $("#typeName").val()},
        cols: [[
            {field:'title', width:200, title: '目录'},
            {field:'contentNum', width:80, title: '文档数'},
            {field:'stateName', width: 80, align: 'center', title: '状态'},
            {field:'createTime', width: 130, align: 'center', title: systemLanguage["com.skyeye.createTime"][languageType]},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar'}
        ]],
	    done: function(){
	    	matchingLanguage();
	    },
    }, {
		keyId: 'id',
		keyPid: 'pId',
		title: 'title',
	});

	tableTree.getTable().on('tool(messageTable)', function (obj) {
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
        }else if (layEvent === 'docconsole') { //文档管理
        	docconsole(data);
        }
    });
    
    form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysdevelopdoc005", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//上线
	function up(data, obj){
		var msg = obj ? '确认上线目录【' + obj.data.title + '】吗？' : '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '目录上线' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysdevelopdoc007", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("上线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//下线
	function down(data, obj){
		var msg = obj ? '确认下线目录【' + obj.data.title + '】吗？' : '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '目录下线' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysdevelopdoc008", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("下线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//上移
	function upmove(data){
        AjaxPostUtil.request({url: reqBasePath + "sysdevelopdoc009", params:{rowId: data.id}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	//下移
	function downmove(data){
        AjaxPostUtil.request({url: reqBasePath + "sysdevelopdoc010", params:{rowId: data.id}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysdevelopdoc/sysdevelopdocedit.html", 
			title: "编辑目录信息",
			pageId: "sysdevelopdocedit",
			area: ['600px', '50vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//文档管理
	function docconsole(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysdevelopdoc/sysdevelopdocconsolelist.html", 
			title: "文档管理",
			pageId: "sysdevelopdocconsole",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode) {
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
			url: "../../tpl/sysdevelopdoc/sysdevelopdocadd.html", 
			title: "新增目录信息",
			pageId: "sysdevelopdocadd",
			area: ['600px', '50vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	function loadTable(){
		tableTree.reload("messageTable", {where:{typeName: $("#typeName").val()}});
    }
    
    exports('sysdevelopdoclist', {});
});
