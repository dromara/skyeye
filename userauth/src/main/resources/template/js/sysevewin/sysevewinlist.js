
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
	
	authBtn('1552961221651');
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'sysevewin001',
	    where:{sysName: $("#sysName").val()},
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'sysName', title: '系统名称', width: 180 },
	        { field: 'sysPic', title: '系统图片', align: 'center', width: 100, templet: function (d) {
	        	if(isNull(d.sysPic)){
	        		return '<img src="../../assets/images/os_windows.png" class="photo-img">';
	        	} else {
	        		return '<img src="' + fileBasePath + d.sysPic + '" class="photo-img" lay-event="sysPic">';
	        	}
	        }},
	        { field: 'sysUrl', title: '演示地址', width: 240 },
	        { field: 'firstTypeName', title: '一级分类', width: 120 },
	        { field: 'secondTypeName', title: '二级分类', width: 120 },
	        { field: 'menuNum', title: '菜单数量', width: 100 },
	        { field: 'useNum', title: '客户数量', width: 100 },
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 180, toolbar: '#tableBar'}
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
        }else if (layEvent === 'authorization'){//授权
        	authorization(data, obj);
        }else if (layEvent === 'cancleauthorization'){//取消授权
        	cancleauthorization(data, obj);
        }else if (layEvent === 'sysPic'){
        	layer.open({
        		type:1,
        		title:false,
        		closeBtn:0,
        		skin: 'demo-class',
        		shadeClose:true,
        		content:'<img src="' + fileBasePath + data.sysPic + '" style="max-height:600px;max-width:100%;">',
        		scrollbar:false
            });
        }
    });
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除系统【' + obj.data.sysName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除系统' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysevewin005", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//授权
	function authorization(data, obj){
		var msg = '确认授权于该商户吗？';
		layer.confirm(msg, { icon: 3, title: '系统授权' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysevewin006", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg("授权成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//取消授权
	function cancleauthorization(data, obj){
		var msg = '确认取消该商户的授权吗？';
		layer.confirm(msg, { icon: 3, title: '取消授权' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "sysevewin007", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg("取消授权成功", {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/sysevewin/sysevewinedit.html", 
			title: "编辑系统",
			pageId: "sysevewinedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//新增系统
	$("body").on("click", "#addBean", function() {
		_openNewWindows({
			url: "../../tpl/sysevewin/sysevewinadd.html", 
			title: "新增系统",
			pageId: "sysevewinadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{sysName: $("#sysName").val()}});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where:{sysName: $("#sysName").val()}});
    }
    
    exports('sysevewinlist', {});
});
