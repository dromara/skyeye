
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
	
	authBtn('1554952601470');
	
	showGrid({
	 	id: "rmTypeId",
	 	url: reqBasePath + "common001",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function (json) {
	 		form.render();
	 		form.on('select(selectParent)', function(data){
	 			
	 		});
	 		form.on('submit(formSearch)', function (data) {
	 	        if (winui.verifyForm(data.elem)) {
	 	        	refreshTable();
	 	        }
	 	        return false;
	 		});
	 	}
    });
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'rmxcx008',
	    where:{rmGroupName:$("#rmGroupName").val(), rmTypeId:$("#rmTypeId").val()},
	    even:true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'rmGroupName', title: '分组名称', width: 120 },
	        { field: 'icon', title: '图标码', width: 520 },
	        { field: 'icon', title: '图标', width: 120, templet: function(d){
	        	return '<i class="fa fa-fw ' + d.icon + '"></i>';
	        }},
	        { field: 'typeName', title: '所属分类', width: 120 },
	        { field: 'groupMemberNum', title: '组件数量', width: 120 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 150 },
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
        }else if (layEvent === 'top') { //上移
        	topOne(data);
        }else if (layEvent === 'lower') { //下移
        	lowerOne(data);
        }
    });
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除分组【' + obj.data.rmGroupName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除分组' }, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url: reqBasePath + "rmxcx010", params:{rowId: data.id}, type: 'json', callback: function (json) {
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
	function topOne(data){
		AjaxPostUtil.request({url: reqBasePath + "rmxcx013", params:{rowId: data.id}, type: 'json', callback: function (json) {
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveUpOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//下移
	function lowerOne(data){
		AjaxPostUtil.request({url: reqBasePath + "rmxcx014", params:{rowId: data.id}, type: 'json', callback: function (json) {
			if (json.returnCode == 0) {
				winui.window.msg(systemLanguage["com.skyeye.moveDownOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/rmgroup/rmgroupedit.html", 
			title: "编辑分组",
			pageId: "rmgroupedit",
			area: ['600px', '50vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/rmgroup/rmgroupadd.html", 
			title: "新增分组",
			pageId: "rmgroupadd",
			area: ['600px', '50vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{rmGroupName:$("#rmGroupName").val(), rmTypeId:$("#rmTypeId").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{rmGroupName:$("#rmGroupName").val(), rmTypeId:$("#rmTypeId").val()}});
    }
    
    exports('rmgrouplist', {});
});
