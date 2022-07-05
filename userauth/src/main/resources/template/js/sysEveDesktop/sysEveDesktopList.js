
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
	
	authBtn('1563780633455');
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'desktop001',
	    where: getTableParams(),
	    even: true,
	    page: true,
	    limits: getLimits(),
    	limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'desktopName', title: '桌面名称', align: 'left', width: 120 },
	        { field: 'desktopCnName', title: '英文名称', align: 'left', width: 120 },
	        { field: 'allNum', title: '菜单数量', align: 'center', width: 120 },
	        { field: 'state', title: '状态', width: 120, align: 'center', templet: function (d) {
	        	if(d.state == '3'){
	        		return "<span class='state-down'>下线</span>";
	        	}else if(d.state == '2'){
	        		return "<span class='state-up'>上线</span>";
	        	}else if(d.state == '1'){
	        		return "<span class='state-new'>新建</span>";
	        	}
	        }},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 357, toolbar: '#tableBar'}
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
        }else if (layEvent === 'up') { //上线
        	up(data);
        }else if (layEvent === 'down') { //下线
        	down(data);
        }else if (layEvent === 'upMove') { //上移
        	upMove(data);
        }else if (layEvent === 'downMove') { //下移
        	downMove(data);
        }else if (layEvent === 'remove') { //一键移除菜单
            remove(data);
        }
    });

	//添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/sysEveDesktop/sysEveDesktopAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "sysEveDesktopAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	//删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "desktop003", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//一键移除菜单
    function remove(data){
        var msg = '确认一键移除菜单选择该桌面的所有菜单吗？';
        layer.confirm(msg, { icon: 3, title: '一键移除菜单' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "desktop012", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("移除成功", {icon: 1, time: 2000});
				loadTable();
            }});
        });
    }
	
	//上线
	function up(data){
		var msg = '确认上线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '上线桌面名称' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "desktop004", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("上线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//下线
	function down(data){
		var msg = '确认下线选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '下线桌面名称' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "desktop005", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("下线成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysEveDesktop/sysEveDesktopEdit.html",
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "sysEveDesktopEdit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	//上移
	function upMove(data){
        AjaxPostUtil.request({url: reqBasePath + "desktop008", params:{rowId: data.id}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}
	
	//下移
	function downMove(data){
        AjaxPostUtil.request({url: reqBasePath + "desktop009", params:{rowId: data.id}, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
			loadTable();
		}});
	}

	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
		}
		return false;
	});

	function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
		return {
			desktopName: $("#desktopName").val()
		};
	}

    exports('sysEveDesktopList', {});
});
