
var rowId = "", parentRowId = "";
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

	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'syseveusernotice001',
	    where: {},
	    even: true,
	    page: true,
	    limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { type: 'checkbox', fixed: 'left'},
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '标题', width: 120 },
	        { field: 'desc', title: '信息', width: 300 },
	        { field: 'state', title: '状态', width: 100 },
	        { field: 'createTime', title: '提醒时间', width: 180 },
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
        	del(data);
        }else if (layEvent === 'sel') { //查看
        	sel(data);
        }
    });
	
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//查看
	function sel(data){
		rowId = data.id;
		parentRowId = data.id;
		AjaxPostUtil.request({url: reqBasePath + "syseveusernotice003", params:{rowId: data.id}, type: 'json', callback: function (json) {
   			if (json.returnCode == 0) {
   				_openNewWindows({
					url: "../../tpl/index/noticeDetail.html", 
					title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
					pageId: "noticeDetail" + (new Date()).valueOf(),
					area: ['600px', '400px'],
					shade: false,
					skin: 'msg-skin-message',
					callBack: function(refreshCode){
				}});
	   		} else {
   				winui.window.msg(json.returnMessage, {shift: 6, skin: 'msg-skin-message'});
   			}
   		}});
	}
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {id: "delNoticeList", icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType], skin: 'msg-skin-message', success: function(layero, index){
			var times = $("#delNoticeList").parent().attr("times");
			var zIndex = $("#delNoticeList").parent().css("z-index");
			$("#layui-layer-shade" + times).css({'z-index': zIndex});
		}}, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "syseveusernotice004", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//设置已读选中
	$("body").on("click", "#designReadBeans", function() {
		var checkStatus = table.checkStatus('messageTable');
		var data = checkStatus.data;
		var idsStr = '';
		$.each(data, function(i, item){
			if(item.state == '未读'){
				idsStr += item.id + ",";
			}
		});
		if(!isNull(idsStr)){
			AjaxPostUtil.request({url: reqBasePath + "syseveusernotice005", params:{rowIds: idsStr}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		} else {
			winui.window.msg('选中数据中没有可操作的未读信息', {icon: 5,time: 2000});
		}
    });
	
	//删除选中
	$("body").on("click", "#delBeans", function() {
		var checkStatus = table.checkStatus('messageTable');
		var data = checkStatus.data;
		var idsStr = '';
		$.each(data, function(i, item){
			idsStr += item.id + ",";
		});
		if(!isNull(idsStr)){
			layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {id: "delAllNoticeList", icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType], skin: 'msg-skin-message', success: function(layero, index){
				var times = $("#delAllNoticeList").parent().attr("times");
				var zIndex = $("#delAllNoticeList").parent().css("z-index");
				$("#layui-layer-shade" + times).css({'z-index': zIndex});
			}}, function (index) {
				layer.close(index);
				AjaxPostUtil.request({url: reqBasePath + "syseveusernotice006", params:{rowIds: idsStr}, type: 'json', callback: function (json) {
					if (json.returnCode == 0) {
						winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
						loadTable();
					} else {
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			});
		} else {
			winui.window.msg('请选择要删除的消息', {icon: 5,time: 2000});
		}
    });
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{}});
    }
    
    exports('mynoticelist', {});
});
